package net.dravigen.tesseractUtils.GUI;

import net.dravigen.tesseractUtils.TessUConfig;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.TessUConfig.*;

public class GuiTUSettings extends GuiScreen {
    private int buttonId = -1;
    private final GuiScreen parentScreen;
    private static final EnumConfig[] enumConfigs;
    private static GuiButton currentButton;
    static {
        enumConfigs = new EnumConfig[]{EnumConfig.PLACING_COOLDOWN,EnumConfig.BREAKING_COOLDOWN,EnumConfig.FLIGHT_MOMENTUM,EnumConfig.CLICK_REPLACE,EnumConfig.NO_CLIP,EnumConfig.EXTRA_DEBUG,EnumConfig.REACH, EnumConfig.FLIGHT_SPEED};
    }
    private static final ResourceLocation customButtonBar = new ResourceLocation("tesseract_utils:textures/gui/button.png");
    private static final ResourceLocation customButtonReset = new ResourceLocation("tesseract_utils:textures/gui/buttonReset.png");
    public static List<GuiButton> buttons = new ArrayList<>();


    public GuiTUSettings(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        StringTranslate stringTranslate = StringTranslate.getInstance();

        // Add buttons
        int count = 1;
        int ordinal=0;
        for (EnumConfig item : enumConfigs) {
            // Get info
             ordinal= item.returnEnumOrdinal();

            // Add buttons
            if (item.getEnumBoolean()) {
                // Is boolean button
                GuiButton button = new GuiButtonCustom(300+ordinal,
                        this.width / 2 +25,
                        this.height / 6 + 29 * (count) ,
                        150,
                        20,
                        200,
                        boolToString(ordinal,getBoolValue(ordinal)),
                        customButtonBar);
                buttons.add(button);
                this.buttonList.add(button);
            } else if (item.getEnumInt()) {
                // Is int button
                GuiSliderConfig slider = new GuiSliderConfig(300 + ordinal,
                        (int) (this.width / 2f - 200+25),
                        this.height / 6 + (29 * count),
                        (int) ((this.width / 2f + 150+25)-(this.width / 2f - 200+25)),
                        item.getEnumProperty(),
                        GuiUtils.getInstance().getSliderDisplay(item.getEnumProperty()),
                        getFloatValue(ordinal), 0);
                buttons.add(slider);
                this.buttonList.add(slider);
                /*
                this.buttonList.add(new GuiButton(300 + ordinal, this.width / 2 + 2,
                        this.height / 6 + 24 + (24 * count), 150, 20,
                        intToString(ordinal, getIntValue(ordinal))));*/
            } else {
                // Is float button
                GuiSliderConfig slider = new GuiSliderConfig(300 + ordinal,
                        this.width / 6 ,
                        this.height / 6 + (29 * count),
                        150,
                        item.getEnumProperty(),
                        GuiUtils.getInstance().getSliderDisplay(item.getEnumProperty()),
                        getFloatValue(ordinal), item.getEnumFloatNotches());
                buttons.add(slider);
                this.buttonList.add(slider);

            }
            this.buttonList.add(new GuiButtonCustom(400+ordinal,
                    this.width / 2 +25 + 154 ,
                    this.height / 6 + 29 * (count) ,
                    20,
                    20,
                    20,
                    "",
                    customButtonReset));
            count++;
        }
        for (int i = 0; i < modBinds.length; i++) {
            this.buttonList.add(new GuiButtonCustom(300+i+ordinal+1,
                    this.width / 2 +25,
                    this.height / 6 + 29 * (count) ,
                    150,
                    20,
                    200,
                    GameSettings.getKeyDisplayString(modBinds[i].keyCode),
                    customButtonBar));
            this.buttonList.add(new GuiButtonCustom(400+i+ordinal+1,
                    this.width / 2 +25 + 154 ,
                    this.height / 6 + 29 * (count) ,
                    20,
                    20,
                    20,
                    "",
                    customButtonReset));
            count++;
        }
        // Add a "Done" button to return to the parent screen
        this.buttonList.add(new GuiButtonCustom(100,
                this.width / 2 - 100,
                this.height / 6 + (29 * count)+10,
                200,
                20,
                200,
                stringTranslate.translateKey("gui.done"),
                customButtonBar));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        currentButton=button;
        switch (button.id) {
            case 300 -> {
                TessUConfig.disablePlaceCooldown = !TessUConfig.disablePlaceCooldown;
                button.displayString = TessUConfig.disablePlaceCooldown ? "Disabled" : "Enabled";
            }
            case 301 -> {
                TessUConfig.disableBreakCooldown = !TessUConfig.disableBreakCooldown;
                button.displayString = TessUConfig.disableBreakCooldown ? "Disabled" : "Enabled";
            }
            case 302 -> {
                TessUConfig.disableMomentum = !TessUConfig.disableMomentum;
                button.displayString = TessUConfig.disableMomentum ? "Disabled" : "Enabled";
            }
            case 303 -> {
                TessUConfig.enableClickReplace = !TessUConfig.enableClickReplace;
                button.displayString = !TessUConfig.enableClickReplace ? "Disabled" : "Enabled";
            }
            case 304 -> {
                TessUConfig.enableNoClip = !TessUConfig.enableNoClip;
                button.displayString = !TessUConfig.enableNoClip ? "Disabled" : "Enabled";
            }
            case 305 -> {
                TessUConfig.enableExtraDebugInfo = !TessUConfig.enableExtraDebugInfo;
                button.displayString = !TessUConfig.enableExtraDebugInfo ? "Disabled" : "Enabled";
            }
            case 308, 309 -> {
                this.buttonId = button.id;
                String var10001 = button.displayString;
                button.displayString = "> " + var10001 + " <";
            }
            case 400 -> {
                if (TessUConfig.disablePlaceCooldown) {
                    TessUConfig.disablePlaceCooldown=false;
                }
            }
            case 401 -> {
                if (TessUConfig.disableBreakCooldown) {
                    TessUConfig.disableBreakCooldown=false;
                }
            }
            case 402 -> {
                if (TessUConfig.disableMomentum) {
                    TessUConfig.disableMomentum=false;
                }
            }
            case 403 -> {
                if (TessUConfig.enableClickReplace) {
                    TessUConfig.enableClickReplace=false;
                }
            }
            case 404 -> {
                if (TessUConfig.enableNoClip) {
                    TessUConfig.enableNoClip = false;
                }
            }
            case 405 -> {
                if (TessUConfig.enableExtraDebugInfo) {
                    TessUConfig.enableExtraDebugInfo = false;
                }
            }
            case 406 -> {
                if (TessUConfig.reach!=5) {
                    TessUConfig.reach=5;
                }
            }
            case 407 -> {
                if (TessUConfig.flySpeed!=2) {
                    TessUConfig.flySpeed=2;
                }
            }
            case 408 -> {
                modBinds[0].keyCode = Keyboard.KEY_F6;
                button.displayString = GameSettings.getKeyDisplayString(configMenu.keyCode);
                properties.put(configMenu.keyDescription,Keyboard.KEY_F6);
                KeyBinding.resetKeyBindingArrayAndHash();
            }
            case 409 -> {
                modBinds[1].keyCode = Keyboard.KEY_H;
                button.displayString = GameSettings.getKeyDisplayString(hotbarSwap.keyCode);
                properties.put(hotbarSwap.keyDescription,Keyboard.KEY_H);
                KeyBinding.resetKeyBindingArrayAndHash();
            }
        }
        if (button.id!=306&&button.id!=307&&button.id!=308&&button.id!=309) {
            this.mc.displayGuiScreen(new GuiTUSettings(parentScreen));
        }

        TessUConfig.saveConfig();

        if (button.id == 100) {
            if (this.parentScreen==null){
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                this.mc.sndManager.resumeAllSounds();
            }else this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        if (this.buttonId == 308 || this.buttonId ==309) {
            this.updateBind(par3);
        } else {
            super.mouseClicked(par1, par2, par3);
        }

    }
    @Override
    protected void keyTyped(char par1, int par2) {
        if (this.buttonId == 308 || this.buttonId ==309) {
            this.updateBind(par2);
        } else {
            super.keyTyped(par1, par2);
        }

    }

    public void updateBind(int scancode) {
        this.buttonId-=308;
        modBinds[this.buttonId].keyCode = scancode;
        currentButton.displayString = GameSettings.getKeyDisplayString(modBinds[this.buttonId].keyCode);
        properties.put(modBinds[this.buttonId].keyDescription,modBinds[this.buttonId].keyCode);
        this.buttonId = -1;
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    private boolean getBoolValue(int ordinal) {
        return switch (ordinal) {
            case 0 -> TessUConfig.disablePlaceCooldown;
            case 1 -> TessUConfig.disableBreakCooldown;
            case 2 -> TessUConfig.disableMomentum;
            case 3 -> TessUConfig.enableClickReplace;
            case 4 -> TessUConfig.enableNoClip;
            case 5 -> TessUConfig.enableExtraDebugInfo;
            default -> false;
        };
    }

    private int getIntValue(int ordinal) {
        if (ordinal==EnumConfig.REACH.returnEnumOrdinal())
            return (int) (TessUConfig.reach/128);
        if (ordinal==EnumConfig.FLIGHT_SPEED.returnEnumOrdinal())
            return (int) (TessUConfig.flySpeed/32);
        return -1;
    }

    private float getFloatValue(int ordinal) {
        if (ordinal==EnumConfig.REACH.returnEnumOrdinal())
            return  (TessUConfig.reach/128);
        if (ordinal==EnumConfig.FLIGHT_SPEED.returnEnumOrdinal())
            return  ((TessUConfig.flySpeed-1)/32);

        return 1.0f;
    }

    private String boolToString(int ordinal, boolean bool) {
        return switch (ordinal){
            case 0,1,2 -> bool ? "Disabled" : "Enabled";
            case 3,4,5 -> bool ? "Enabled" : "Disabled";
            default -> throw new IllegalStateException("Unexpected value: " + ordinal);
        };
    }

    private String intToString(int ordinal, int value) {
        String res = "";

        return StatCollector.translateToLocal(res);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        //this.drawCenteredString(this.fontRenderer, "TesseractUtils Settings", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawWorldBackground(int par1) {
        if (this.mc.theWorld != null) {
            this.drawBackground(0);

        } else {
            this.drawBackground(0);
        }
    }

    @Override
    public void drawBackground(int par1) {
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator var2 = Tessellator.instance;
        if (par1==0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/custom background.png"));
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
            var2.startDrawingQuads();
            //var2.setColorOpaque_I(0x888888);
            var2.addVertexWithUV(0.0, this.height, 0.0, 0, 1);
            var2.addVertexWithUV(this.width, this.height, 0.0, 1, 1);
            var2.addVertexWithUV(this.width, 0.0, 0.0, 1, 0);
            var2.addVertexWithUV(0.0, 0.0, 0.0, 0, 0);
            var2.draw();
            GL11.glDisable(GL11.GL_BLEND);
/*
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            var2.startDrawingQuads();
            var2.setColorOpaque_I(0x555555);
            var2.addVertexWithUV(0,       this.height/8f*7, 0, 0, 1/8f*7);
            var2.addVertexWithUV(this.width, this.height/8f*7, 0, 1, 1/8f*7);
            var2.addVertexWithUV(this.width, this.height/5.25f,   0, 1, 1/5.25f);
            var2.addVertexWithUV(0.0,     this.height/5.25f,   0, 0, 1/5.25f);
            var2.draw();

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            var2.startDrawingQuads();
            var2.setColorOpaque_I(0xb400ff);
            var2.addVertexWithUV(0,       this.height/5.25f+4, 0, 0, 0.01);
            var2.addVertexWithUV(this.width, this.height/5.25f+4, 0, 0.01, 0.01);
            var2.addVertexWithUV(this.width, this.height/5.25f,   0, 0.01, 0);
            var2.addVertexWithUV(0.0,     this.height/5.25f,   0, 0, 0);
            var2.draw();

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            var2.startDrawingQuads();
            var2.setColorOpaque_I(0xb400ff);
            var2.addVertexWithUV(0,       this.height/8f*7, 0, 0, 1/8f*7);
            var2.addVertexWithUV(this.width, this.height/8f*7, 0, 1, 1/8f*7);
            var2.addVertexWithUV(this.width, this.height/8f*7-4,   0, 1, 1/8f*7-4f/this.height);
            var2.addVertexWithUV(0.0,this.height/8f*7-4,   0, 0, 1/8f*7-4f/this.height);
            var2.draw();*/

        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/title.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var2.startDrawingQuads();
        var2.addVertexWithUV(this.width/2f-717/3f, 128/1.5, 0.0, 0, 1);
        var2.addVertexWithUV(this.width/2f+717/3f, 128/1.5, 0.0, 1 , 1);
        var2.addVertexWithUV(this.width/2f+717/3f, 0.0/1.5, 0.0, 1 , 0);
        var2.addVertexWithUV(this.width/2f-717/3f, 0.0/1.5, 0.0, 0, 0);
        var2.draw();

        List<String> optionNames = new ArrayList<>();
        optionNames.add("Placing Cooldown");
        optionNames.add("Breaking Cooldown");
        optionNames.add("Flight Momentum");
        optionNames.add("Replacing Click");
        optionNames.add("No Clip");
        optionNames.add("Extra Debug Info");
        optionNames.add("");
        optionNames.add("");
        optionNames.add("Config Menu");
        optionNames.add("Hotbar Swap");

        FontRenderer var4 = Minecraft.getMinecraft().fontRenderer;
       // int var6 = 0xff00fb;
        int var6 = 0xffffff;

        for (String name : optionNames){
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/custom background.png"));
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.75f);
            var2.startDrawingQuads();
            //var2.setColorOpaque_I(0x373737);
            var2.addVertexWithUV(this.width / 2f - 200+25, this.height / 6f + 30 + 29 * optionNames.indexOf(name)+20, 0.0, (this.width / 2f - 200+25)/this.width, (this.height / 6f + 30 + 29 * optionNames.indexOf(name)+20)/this.height);
            var2.addVertexWithUV(this.width / 2f + 150+25, this.height / 6f + 30 + 29 * optionNames.indexOf(name)+20, 0.0, (this.width / 2f + 175)/this.width, (this.height / 6f + 30 + 29 * optionNames.indexOf(name)+20)/this.height);
            var2.addVertexWithUV(this.width / 2f + 150+25, this.height / 6f + 28 + 29 * optionNames.indexOf(name), 0.0, (this.width / 2f + 175)/this.width, (this.height / 6f + 30 + 29 * optionNames.indexOf(name))/this.height);
            var2.addVertexWithUV(this.width / 2f - 200+25, this.height / 6f + 28 + 29 * optionNames.indexOf(name), 0.0, (this.width / 2f - 200+25)/this.width, (this.height / 6f + 30 + 29 * optionNames.indexOf(name))/this.height);
            var2.draw();
            GL11.glDisable(GL11.GL_BLEND);

            this.drawString(var4, name, this.width / 2 - 200+30, this.height / 6 + 34 + 29 * optionNames.indexOf(name), var6);

        }

    }



}