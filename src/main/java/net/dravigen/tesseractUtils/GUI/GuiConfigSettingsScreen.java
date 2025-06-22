package net.dravigen.tesseractUtils.GUI;

import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.TessUConfig.*;

public class GuiConfigSettingsScreen extends GuiScreen {

    private static final ResourceLocation customButtonBar = new ResourceLocation("tesseract_utils:textures/gui/button.png");
    private static final ResourceLocation customButtonReset = new ResourceLocation("tesseract_utils:textures/gui/buttonReset.png");
    private static GuiButton currentButton;
    private int buttonId = -1;
    private final GuiScreen parentScreen;
    private final List<GuiButton> buttons = new ArrayList<>();
    private final List<GuiButton> resetButtons = new ArrayList<>();
    private final List<GuiButton> miscButtons = new ArrayList<>();
    private static int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int scrollAreaPosY, scrollAreaHeight;
    private boolean isScrolling = false;
    private static final EnumConfig[] enumConfigs;

    static {
        enumConfigs = new EnumConfig[]{EnumConfig.PLACING_COOLDOWN, EnumConfig.BREAKING_COOLDOWN, EnumConfig.FLIGHT_MOMENTUM, EnumConfig.CLICK_REPLACE, EnumConfig.NO_CLIP, EnumConfig.EXTRA_DEBUG, EnumConfig.VANILLA_NIGHTVIS, EnumConfig.FUZZY_EXTRUDER,
                EnumConfig.REACH, EnumConfig.FLIGHT_SPEED, EnumConfig.EXTRUDE_LIMIT,
                EnumConfig.CONFIG_MENU_KEY, EnumConfig.BAR_SWAP_KEY};
    }

    public final int placeIndex = EnumConfig.PLACING_COOLDOWN.returnEnumOrdinal();
    public final int breakIndex = EnumConfig.BREAKING_COOLDOWN.returnEnumOrdinal();
    public final int momentumIndex = EnumConfig.FLIGHT_MOMENTUM.returnEnumOrdinal();
    public final int replaceIndex = EnumConfig.CLICK_REPLACE.returnEnumOrdinal();
    public final int clipIndex = EnumConfig.NO_CLIP.returnEnumOrdinal();
    public final int debugIndex = EnumConfig.EXTRA_DEBUG.returnEnumOrdinal();
    public final int nightIndex = EnumConfig.VANILLA_NIGHTVIS.returnEnumOrdinal();
    public final int fuzzyIndex = EnumConfig.FUZZY_EXTRUDER.returnEnumOrdinal();

    public final int reachIndex = EnumConfig.REACH.returnEnumOrdinal();
    public final int flyIndex = EnumConfig.FLIGHT_SPEED.returnEnumOrdinal();
    public final int extrudeIndex = EnumConfig.EXTRUDE_LIMIT.returnEnumOrdinal();

    public final int menuKeyIndex = EnumConfig.CONFIG_MENU_KEY.returnEnumOrdinal();
    public final int barKeyIndex = EnumConfig.BAR_SWAP_KEY.returnEnumOrdinal();
    private int dragStartOffset;

    public GuiConfigSettingsScreen(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        StringTranslate stringTranslate = StringTranslate.getInstance();
        scrollAreaPosY = this.height / 6 + 29;
        scrollAreaHeight = 11 * 29;
        int count = 1;
        for (EnumConfig item : enumConfigs) {
            int ordinal = item.returnEnumOrdinal();
            if (item.getEnumBoolean()) {
                // Is boolean button
                GuiButton button = new GuiButtonCustom(ordinal,
                        this.width / 2 + 25,
                        this.height / 6 + 29 * (count),
                        150,
                        20,
                        200,
                        boolToString(ordinal, getBoolValue(ordinal)),
                        customButtonBar);
                buttons.add(button);
                this.buttonList.add(button);
            } else if (item.getEnumInt()) {
                // Is int button
                GuiSliderConfig slider = new GuiSliderConfig(ordinal,
                        (int) (this.width / 2f - 200 + 25),
                        this.height / 6 + (29 * count),
                        (int) ((this.width / 2f + 150 + 25) - (this.width / 2f - 200 + 25)),
                        item.getEnumProperty(),
                        GuiUtils.getInstance().getSliderDisplay(item.getEnumProperty()),
                        getFloatValue(ordinal), 0);
                buttons.add(slider);
                this.buttonList.add(slider);
            } else if (item.getKeyBind()) {
                // Is keyBind button
                GuiButton button = new GuiButtonCustom(ordinal,
                        this.width / 2 + 25,
                        this.height / 6 + 29 * (count),
                        150,
                        20,
                        200,
                        GameSettings.getKeyDisplayString(modBinds[ordinal - 375].keyCode),
                        customButtonBar);
                buttons.add(button);
                this.buttonList.add(button);
            } else {
                // Is float button
                GuiSliderConfig slider = new GuiSliderConfig(ordinal,
                        this.width / 6,
                        this.height / 6 + (29 * count),
                        150,
                        item.getEnumProperty(),
                        GuiUtils.getInstance().getSliderDisplay(item.getEnumProperty()),
                        getFloatValue(ordinal), item.getEnumFloatNotches());
                buttons.add(slider);
                this.buttonList.add(slider);
            }
            // Add corresponding reset button
            GuiButton resetButton = new GuiButtonCustom(ordinal + 100,
                    this.width / 2 + 25 + 154,
                    this.height / 6 + 29 * (count),
                    20,
                    20,
                    20,
                    "",
                    customButtonReset);
            resetButtons.add(resetButton);
            this.buttonList.add(resetButton);
            count++;
        }
        // Add a "Done" button
        GuiButton doneButton = new GuiButtonCustom(100,
                this.width / 2 - 100,
                this.height / 6 + (29 * 13) + 10,
                200,
                20,
                200,
                stringTranslate.translateKey("gui.done"),
                customButtonBar);
        miscButtons.add(doneButton);
        this.buttonList.add(doneButton);
        int totalContentHeight = buttons.size() * 29;
        maxScrollOffset = Math.max(0, totalContentHeight - scrollAreaHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        for (int i = 0; i < buttons.size(); i++) {
            GuiButton button = buttons.get(i);
            GuiButton resetButton = resetButtons.get(i);
            int buttonDrawY = scrollAreaPosY + (i * 29) - scrollOffset;
            button.yPosition = buttonDrawY;
            resetButton.yPosition = buttonDrawY;
            if (buttonDrawY + button.height >= scrollAreaPosY && buttonDrawY < scrollAreaPosY + scrollAreaHeight) {
                button.drawButton(this.mc, mouseX, mouseY);
                resetButton.drawButton(this.mc, mouseX, mouseY);
            }
        }
        for (GuiButton miscButton : miscButtons) {
            miscButton.drawButton(this.mc, mouseX, mouseY);
        }
        if (maxScrollOffset > 0) {
            this.mc.getTextureManager().bindTexture(customButtonBar);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(width - 14, scrollAreaPosY, 246, 0, 10 , 195);
            this.drawTexturedModalRect(width - 14, scrollAreaPosY+ 195, 246, 200-124, 10 , 124);
            int thumbY = getCalculatedThumbY(scrollOffset);
            int thumbHeight = getCalculatedThumbHeight();
            this.drawTexturedModalRect(width - 15, thumbY, 233, 0, 14, thumbHeight/2);
            this.drawTexturedModalRect(width - 15, thumbY+thumbHeight/2, 233, thumbHeight/2, 14 , thumbHeight/2);
        }
    }

    @Override
    public void drawWorldBackground(int par1) {
        this.drawBackground(0);
    }

    @Override
    public void drawBackground(int par1) {
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator var2 = Tessellator.instance;
        if (par1 == 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/custom background.png"));
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
            var2.startDrawingQuads();
            var2.addVertexWithUV(0.0, this.height, 0.0, 0, 1);
            var2.addVertexWithUV(this.width, this.height, 0.0, 1, 1);
            var2.addVertexWithUV(this.width, 0.0, 0.0, 1, 0);
            var2.addVertexWithUV(0.0, 0.0, 0.0, 0, 0);
            var2.draw();
            GL11.glDisable(GL11.GL_BLEND);
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/title.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var2.startDrawingQuads();
        var2.addVertexWithUV(this.width / 2f - 717 / 3f, 128 / 1.5, 0.0, 0, 1);
        var2.addVertexWithUV(this.width / 2f + 717 / 3f, 128 / 1.5, 0.0, 1, 1);
        var2.addVertexWithUV(this.width / 2f + 717 / 3f, 0.0 / 1.5, 0.0, 1, 0);
        var2.addVertexWithUV(this.width / 2f - 717 / 3f, 0.0 / 1.5, 0.0, 0, 0);
        var2.draw();

        FontRenderer var4 = Minecraft.getMinecraft().fontRenderer;
        int var6 = 0xffffff;
        int count = 0;
        for (EnumConfig config : enumConfigs) {
            int buttonDrawY = scrollAreaPosY + (count * 29) - scrollOffset;
            if (buttonDrawY + buttons.get(count).height >= scrollAreaPosY && buttonDrawY < scrollAreaPosY + scrollAreaHeight) {
                String name = config.getEnumName();
                GL11.glEnable(3042);
                GL11.glDisable(3553);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(0f, 0f, 0f, 0.5f);
                var2.startDrawingQuads();
                var2.addVertexWithUV(this.width / 2f - 200 + 25, scrollAreaPosY + (count * 29) - scrollOffset + 20, 0.0, (this.width / 2f - 200 + 25) / this.width, (this.height / 6f + 30 + 29 * count + 20) / this.height);
                var2.addVertexWithUV(this.width / 2f + 150 + 25, scrollAreaPosY + (count * 29) - scrollOffset + 20, 0.0, (this.width / 2f + 175) / this.width, (this.height / 6f + 30 + 29 * count + 20) / this.height);
                var2.addVertexWithUV(this.width / 2f + 150 + 25, scrollAreaPosY + (count * 29) - scrollOffset, 0.0, (this.width / 2f + 175) / this.width, (this.height / 6f + 30 + 29 * count) / this.height);
                var2.addVertexWithUV(this.width / 2f - 200 + 25, scrollAreaPosY + (count * 29) - scrollOffset, 0.0, (this.width / 2f - 200 + 25) / this.width, (this.height / 6f + 30 + 29 * count) / this.height);
                var2.draw();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(3553);
                GL11.glDisable(3042);
                this.drawString(var4, name, this.width / 2 - 200 + 30, scrollAreaPosY + (count * 29) - scrollOffset + 6, var6);
            }
            count++;
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            scrollOffset -= dWheel > 0 ? 29 : -29;
            scrollOffset = MathHelper.clamp_int(scrollOffset, 0, maxScrollOffset);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && maxScrollOffset > 0 && mouseX >= (width - 14) && mouseX <= (width - 4) && mouseY >= scrollAreaPosY && mouseY <= scrollAreaPosY + scrollAreaHeight) {
            int thumbY = getCalculatedThumbY(scrollOffset);
            int thumbHeight = getCalculatedThumbHeight();
            if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                isScrolling = true;
                dragStartOffset = mouseY - thumbY;
            } else {
                float relativeClickPos = (float) (mouseY - scrollAreaPosY - (thumbHeight / 2)) / (scrollAreaHeight - thumbHeight);
                scrollOffset = (int) (relativeClickPos * maxScrollOffset);
                scrollOffset = MathHelper.clamp_int(scrollOffset, 0, maxScrollOffset);
            }
        }
        if (this.buttonId == menuKeyIndex || this.buttonId == barKeyIndex) {
            this.updateBind(mouseButton);
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private int getCalculatedThumbHeight() {
        //int totalContentHeight = buttons.size() * 29*2;
        return Math.max(10, (int) ((float) scrollAreaHeight /11)); //scrollAreaHeight / (float) totalContentHeight));
    }

    private int getCalculatedThumbY(int currentScrollOffset) {
        if (maxScrollOffset <= 0) return scrollAreaPosY;
        int thumbHeight = getCalculatedThumbHeight();
        float scrollProgress = (float) currentScrollOffset / maxScrollOffset;
        return scrollAreaPosY + (int) (scrollProgress * (scrollAreaHeight - thumbHeight));
    }

    @Override
    protected void mouseMovedOrUp(int par1, int par2, int par3) {
        super.mouseMovedOrUp(par1, par2, par3);
        if (par3 == 0) {
            isScrolling = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long par4) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, par4);
        float scrollProgress = (float) scrollOffset / maxScrollOffset;
        int thumbHeight = Math.max(10, (int) ((float) scrollAreaHeight * scrollAreaHeight / (float) (buttons.size() * 29)));
        int thumbY = scrollAreaPosY + (int) (scrollProgress * (scrollAreaHeight - thumbHeight));
        if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight && mouseX >= (width - 14) && mouseX <= (width - 4)) {
            isScrolling = true;
        }
        if (isScrolling) {
            int desiredThumbY = mouseY - dragStartOffset;
            int minThumbY = scrollAreaPosY;
            int maxThumbY = scrollAreaPosY + scrollAreaHeight - getCalculatedThumbHeight();
            desiredThumbY = MathHelper.clamp_int(desiredThumbY, minThumbY, maxThumbY);
            scrollProgress = (float) (desiredThumbY - scrollAreaPosY) / (float) (scrollAreaHeight - getCalculatedThumbHeight());
            scrollOffset = (int) (scrollProgress * maxScrollOffset);
            scrollOffset = MathHelper.clamp_int(scrollOffset, 0, maxScrollOffset);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        currentButton = button;
        if (button.id == placeIndex) {
            disablePlaceCooldown = !disablePlaceCooldown;
            button.displayString = disablePlaceCooldown ? "Disabled" : "Enabled";
        }
        if (button.id == breakIndex) {
            disableBreakCooldown = !disableBreakCooldown;
            button.displayString = disableBreakCooldown ? "Disabled" : "Enabled";
        }
        if (button.id == momentumIndex) {
            disableMomentum = !disableMomentum;
            button.displayString = disableMomentum ? "Disabled" : "Enabled";
        }
        if (button.id == replaceIndex) {
            enableClickReplace = !enableClickReplace;
            button.displayString = !enableClickReplace ? "Disabled" : "Enabled";
        }
        if (button.id == clipIndex) {
            enableNoClip = !enableNoClip;
            button.displayString = !enableNoClip ? "Disabled" : "Enabled";
        }
        if (button.id == debugIndex) {
            enableExtraDebugInfo = !enableExtraDebugInfo;
            button.displayString = !enableExtraDebugInfo ? "Disabled" : "Enabled";
        }
        if (button.id == nightIndex) {
            enableVanillaNightVis = !enableVanillaNightVis;
            button.displayString = !enableVanillaNightVis ? "Disabled" : "Enabled";
        }
        if (button.id == fuzzyIndex) {
            enableFuzzyExtruder = !enableFuzzyExtruder;
            button.displayString = !enableFuzzyExtruder ? "Disabled" : "Enabled";
        }
        if (button.id == menuKeyIndex || button.id == barKeyIndex) {
            this.buttonId = button.id;
            String var10001 = button.displayString;
            button.displayString = "> " + var10001 + " <";
        }
        if (button.id == placeIndex + 100) {
            if (disablePlaceCooldown) {
                disablePlaceCooldown = false;
            }
        }
        if (button.id == breakIndex + 100) {
            if (disableBreakCooldown) {
                disableBreakCooldown = false;
            }
        }
        if (button.id == momentumIndex + 100) {
            if (disableMomentum) {
                disableMomentum = false;
            }
        }
        if (button.id == replaceIndex + 100) {
            if (enableClickReplace) {
                enableClickReplace = false;
            }
        }
        if (button.id == clipIndex + 100) {
            if (enableNoClip) {
                enableNoClip = false;
            }
        }
        if (button.id == debugIndex + 100) {
            if (enableExtraDebugInfo) {
                enableExtraDebugInfo = false;
            }
        }
        if (button.id == nightIndex + 100) {
            if (enableVanillaNightVis) {
                enableVanillaNightVis = false;
            }
        }
        if (button.id == fuzzyIndex + 100) {
            if (enableFuzzyExtruder) {
                enableFuzzyExtruder = false;
            }
        }
        if (button.id == reachIndex + 100) {
            if (reach != 5) {
                reach = 5;
            }
        }
        if (button.id == flyIndex + 100) {
            if (flySpeed != 2) {
                flySpeed = 2;
            }
        }
        if (button.id == extrudeIndex+100){
            if (extrudeLimit != 128) {
                extrudeLimit = 128;
            }
        }
        if (button.id == menuKeyIndex + 100) {
            modBinds[0].keyCode = Keyboard.KEY_F6;
            button.displayString = GameSettings.getKeyDisplayString(configMenu.keyCode);
            properties.put(configMenu.keyDescription, Keyboard.KEY_F6);
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        if (button.id == barKeyIndex + 100) {
            modBinds[1].keyCode = Keyboard.KEY_H;
            button.displayString = GameSettings.getKeyDisplayString(hotbarSwap.keyCode);
            properties.put(hotbarSwap.keyDescription, Keyboard.KEY_H);
            KeyBinding.resetKeyBindingArrayAndHash();
        }

        if (button.id != reachIndex && button.id != extrudeIndex && button.id != flyIndex && button.id != menuKeyIndex && button.id != barKeyIndex) {
            this.mc.displayGuiScreen(new GuiConfigSettingsScreen(parentScreen));
        }

        saveConfig();

        if (button.id == 100) {
            if (this.parentScreen == null) {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                this.mc.sndManager.resumeAllSounds();
            } else this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (this.buttonId == menuKeyIndex || this.buttonId == barKeyIndex) {
            this.updateBind(par2);
        } else {
            super.keyTyped(par1, par2);
        }

    }

    public void updateBind(int scancode) {
        this.buttonId -= menuKeyIndex;
        modBinds[this.buttonId].keyCode = scancode;
        currentButton.displayString = GameSettings.getKeyDisplayString(modBinds[this.buttonId].keyCode);
        properties.put(modBinds[this.buttonId].keyDescription, modBinds[this.buttonId].keyCode);
        this.buttonId = -1;
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    private boolean getBoolValue(int ordinal) {
        int var1 = ordinal - 300;
        return switch (var1) {
            case 0 -> disablePlaceCooldown;
            case 1 -> disableBreakCooldown;
            case 2 -> disableMomentum;
            case 3 -> enableClickReplace;
            case 4 -> enableNoClip;
            case 5 -> enableExtraDebugInfo;
            case 6 -> enableVanillaNightVis;
            case 7 -> enableFuzzyExtruder;
            default -> false;
        };
    }

    private int getIntValue(int ordinal) {

        return -1;
    }

    private float getFloatValue(int ordinal) {
        if (ordinal == reachIndex)
            return (reach / 128);
        if (ordinal == flyIndex)
            return ((flySpeed - 1) / 32);
        if (ordinal == extrudeIndex)
            return ((extrudeLimit-1)/4096);
        return 1.0f;
    }

    private String boolToString(int ordinal, boolean bool) {
        int var1 = ordinal - 300;
        return switch (var1) {
            case 0, 1, 2 -> bool ? "Disabled" : "Enabled";
            case 3, 4, 5, 6,7 -> bool ? "Enabled" : "Disabled";
            default -> throw new IllegalStateException("Unexpected value: " + ordinal);
        };
    }

    private String intToString(int ordinal, int value) {
        String res = "";

        return StatCollector.translateToLocal(res);
    }

}
