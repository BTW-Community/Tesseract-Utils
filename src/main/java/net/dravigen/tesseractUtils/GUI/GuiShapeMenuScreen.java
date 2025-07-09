package net.dravigen.tesseractUtils.GUI;

import btw.item.items.PlaceAsBlockItem;
import net.dravigen.tesseractUtils.enums.EnumShape;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.dravigen.tesseractUtils.utils.GuiUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import static net.dravigen.tesseractUtils.enums.EnumShape.*;


public class GuiShapeMenuScreen extends GuiScreen {
    private static final ResourceLocation customButtonBar = new ResourceLocation("tesseract_utils:textures/gui/button.png");
    private static final ResourceLocation customButtonReset = new ResourceLocation("tesseract_utils:textures/gui/buttonReset.png");
    private static GuiButton currentButton;
    public final GuiScreen parentScreen;
    public static final EnumShape[] enumCustomShape = values();
    public static final EnumShape[] enumSphereConfig;
    public static final EnumShape[] enumCylinderConfig;
    public static final EnumShape[] enumCubeConfig;

    static {
        enumSphereConfig = new EnumShape[]{RADIUS};
        enumCylinderConfig = new EnumShape[]{RADIUS, HEIGHT};
        enumCubeConfig = new EnumShape[]{SIZE_X, SIZE_Y, SIZE_Z};
    }

    public GuiShapeMenuScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        StringTranslate stringTranslate = StringTranslate.getInstance();
        int count = 1;
        GuiButton shape = new GuiButtonCustom(SHAPE.getIndex(),
                (int) ((this.width / 2f)-(350/2f)),
                fontRenderer.FONT_HEIGHT*2 + 29,
                346/2,
                20,
                200,
                20,
                intToString(SHAPE.getIndex()),
                customButtonBar
        );
        this.buttonList.add(shape);
        GuiButton volume = new GuiButtonCustom(VOLUME.getIndex(),
                (int) (this.width / 2f)+3,
                fontRenderer.FONT_HEIGHT*2+ 29,
                346/2,
                20,
                200,
                20,
                intToString(VOLUME.getIndex()),
                customButtonBar
        );
        count++;
        this.buttonList.add(volume);

        for (EnumShape item : SHAPE.getShape().equalsIgnoreCase("sphere") ? enumSphereConfig : SHAPE.getShape().equalsIgnoreCase("cylinder") ? enumCylinderConfig : enumCubeConfig ) {
            if (Minecraft.getMinecraft().thePlayer != null && (!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode || !PacketUtils.isPlayerOPClient))
                continue;
            int ordinal = item.getIndex();
            count = addButtons(item, ordinal, count);

        }
        if (VOLUME.getVolume().equalsIgnoreCase("hollow")|| VOLUME.getVolume().equalsIgnoreCase("open")) {
            count = addButtons(THICKNESS, THICKNESS.getIndex(), count);
        }
        GuiButton saveToItem = new GuiButtonCustom(499,
                width - fontRenderer.getStringWidth("Save to item") - 12,
                height - 24,
                fontRenderer.getStringWidth("Save to item")+8,
                20,
                200,
                20,
                "Save to item",
                customButtonBar);
        this.buttonList.add(saveToItem);
        // Add a "Done" button
        GuiButton doneButton = new GuiButtonCustom(100,
                width / 2 - 100,
                height - 24,
                200,
                20,
                200,
                20,
                stringTranslate.translateKey("gui.done"),
                customButtonBar);
        this.buttonList.add(doneButton);
    }

    private int addButtons(EnumShape item, int ordinal, int count) {
        if (item.isBool()) {
            // Is boolean button
            GuiButton button = new GuiButtonCustom(ordinal,
                    this.width / 2 + 25,
                    fontRenderer.FONT_HEIGHT*2 + 29 * count,
                    150,
                    20,
                    200,
                    20,
                    boolToString(getBoolValue(ordinal)),
                    customButtonBar);
            this.buttonList.add(button);
        } else if (item.isIntSlider()) {
            // Is int button
            GuiSliderConfig slider = new GuiSliderConfig(ordinal,
                    (int) (this.width / 2f - 200 + 25),
                    fontRenderer.FONT_HEIGHT*2 + (29 * count),
                    350,
                    20,
                    item.getProperty(),
                    GuiUtils.getInstance().getSliderDisplay(item.getProperty()),
                    getFloatValue(ordinal));
            this.buttonList.add(slider);
        } else if (item.isKeybind()) {
            // Is keyBind button
            GuiButton button = new GuiButtonCustom(ordinal,
                    this.width / 2 + 25,
                    fontRenderer.FONT_HEIGHT*2 + 29 * count,
                    150,
                    20,
                    200,
                    20,
                    GameSettings.getKeyDisplayString((int) item.getValue()),
                    customButtonBar);
            this.buttonList.add(button);
        } else {
            // Is float button
            GuiSliderConfig slider = new GuiSliderConfig(ordinal,
                    this.width / 6,
                    fontRenderer.FONT_HEIGHT*2 + (29 * count),
                    150,
                    20,
                    item.getProperty(),
                    GuiUtils.getInstance().getSliderDisplay(item.getProperty()),
                    getFloatValue(ordinal));
            this.buttonList.add(slider);
        }
        count++;
        return count;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        for (EnumShape customShape: enumCustomShape){
            if (customShape.getIndex()==button.id&&customShape.isInt()){
                int value = customShape.getIntValue();
                value++;
                if (!customShape.getHasOpen()){
                    if (value > customShape.getMaxValue()-1) value = 0;
                }else if (!customShape.getHasHollow()){
                    if (value > customShape.getMaxValue()-2) value = 0;
                }else if (value > customShape.getMaxValue()) value = 0;

                customShape.setValue(value);
                //button.displayString = (boolean) config.getValue() ? "Enabled" : "Disabled";
                this.mc.displayGuiScreen(new GuiShapeMenuScreen(parentScreen));
            }
        }
        if (button.id == 499) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            ItemStack heldItem = player.getHeldItem();
            if (heldItem == null) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou're not holding any item"));
                return;
            }
            String parameters = "";
            switch (SHAPE.getShape().toLowerCase()) {
                case "sphere" -> parameters = RADIUS.getIntValue() + ":" + THICKNESS.getIntValue();
                case "cylinder" ->
                        parameters = RADIUS.getIntValue() + ":" + HEIGHT.getIntValue() + ":" + THICKNESS.getIntValue();
                case "cube", "pyramid" ->
                        parameters = SIZE_X.getIntValue() + ":" + SIZE_Y.getIntValue() + ":" + SIZE_Z.getIntValue() + ":" + THICKNESS.getIntValue();
            }
            if (parameters.equalsIgnoreCase("")) return;
            ItemStack[] hotbarItems = new ItemStack[9];
            System.arraycopy(player.inventory.mainInventory, 0, hotbarItems, 0, 9);
            StringBuilder blocks = new StringBuilder();
            for (ItemStack item : hotbarItems) {
                if (item == null || item == heldItem) continue;
                if (!(item.getItem() instanceof PlaceAsBlockItem blockItem)) continue;
                blocks.append(blockItem.getBlockIDToPlace(item.getItemDamage(), 0, 0, 0, 0)).append("/").append(item.getItemDamage()).append("!").append(item.stackSize).append(";");
            }

            String blockUsed = String.valueOf(blocks);
            if (blockUsed == null||blockUsed.equalsIgnoreCase("")) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou don't have any blocks in your hotbar"));
                return;
            }
            PacketSender.sendClientToServerMessage("saveShapeTool:" + "a"+ "," +SHAPE.getShape()+ "," +blockUsed+ "," +parameters.replace(":","!")+ "," +VOLUME.getVolume());
        }
        if (button.id == 100) {
            if (this.parentScreen == null) {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                this.mc.sndManager.resumeAllSounds();
            } else this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawBackground(0);
        super.drawScreen(par1, par2, par3);

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
        GL11.glPushMatrix();
        GL11.glTranslatef(width/2f, fontRenderer.FONT_HEIGHT*2,0);
        GL11.glScalef(2, 2, 0);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int var6 = 0xffffff;
        this.drawCenteredString(font,"Custom Shape Config",0,0,var6);
        GL11.glPopMatrix();
    }

    private boolean getBoolValue(int ordinal) {
        for(EnumShape config : enumCustomShape){
            if (config.getIndex()==ordinal){
                return (boolean) config.getValue();
            }
        }
        return false;
    }

    private float getFloatValue(int ordinal) {
        for (EnumShape config:enumCustomShape){
            if (ordinal== config.getIndex()){
                if (config.getIntValue()>config.getMaxValue())config.setValue(config.getMaxValue());
                if (config.getIntValue()<0)config.setValue(0);
                return (float) config.getIntValue() /config.getMaxValue();
            }
        }
        return 1.0f;
    }

    private String boolToString(boolean bool) {
        return bool ? "Enabled" : "Disabled";
    }

    private String intToString(int ordinal){
        String display="";
        if (ordinal== SHAPE.getIndex()){
            display = SHAPE.getShape();
        } else if (ordinal == VOLUME.getIndex()) {
            display = VOLUME.getVolume();
        }
        return display;
    }
}
