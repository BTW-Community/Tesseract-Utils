package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.GUI.GuiTUSettings;
import net.dravigen.tesseractUtils.TessUConfig;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {
    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderGameOverlay",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;drawString(Lnet/minecraft/src/FontRenderer;Ljava/lang/String;III)V",ordinal = 1))
    private void addTargetBlockInfo(float par1, boolean par2, int par3, int par4, CallbackInfo ci){
        EntityClientPlayerMP player = this.mc.thePlayer;
        if (TessUConfig.enableExtraDebugInfo &&player.capabilities.isCreativeMode) {
            FontRenderer fontRenderer = this.mc.fontRenderer;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);

            int var6 = scaledResolution.getScaledWidth();

            Vec3 var3 = player.getPosition(1);
            Vec3 var4 = player.getLook(1);
            Vec3 var5 = var3.addVector(var4.xCoord * TessUConfig.reach, var4.yCoord * TessUConfig.reach, var4.zCoord * TessUConfig.reach);
            MovingObjectPosition blockLookedAt = player.worldObj.clip(var3, var5);
            if (blockLookedAt != null) {
                int xBlock = blockLookedAt.blockX;
                int yBlock = blockLookedAt.blockY;
                int zBlock = blockLookedAt.blockZ;
                String face = "";

                switch (blockLookedAt.sideHit) {
                    case 0 -> face = "Down";
                    case 1 -> face = "Up";
                    case 2 -> face = "North";
                    case 3 -> face = "South";
                    case 4 -> face = "West";
                    case 5 -> face = "East";
                }
                String var20 = "Targeted Block: " + xBlock + " / " + yBlock + " / " + zBlock;
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32, 0xE0E0E0);
                var20 = "-ID: " + mc.theWorld.getBlockId(xBlock, yBlock, zBlock) + ", Metadata: " + mc.theWorld.getBlockMetadata(xBlock, yBlock, zBlock);
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 42, 0xE0E0E0);
                var20 = "-Name: " + Block.blocksList[mc.theWorld.getBlockId(xBlock, yBlock, zBlock)].getLocalizedName();
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 52, 0xE0E0E0);
                var20 = "-Face: " + blockLookedAt.sideHit + " = " + face;
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 62, 0xE0E0E0);
            }
        }
    }
    @Unique
    private static boolean logic = false;
    @Unique
    private static boolean F4 = false;
    @Unique
    private static int chosenMode =TesseractUtilsAddon.modeState;

    @Unique
    private static int previousMode;
    @Unique
    private static boolean previousCalled=false;
    @Unique
    private static boolean F3= false;
    @Unique
    private static boolean F4Foolpressed = false;
    @Unique
    private static boolean F4pressed = false;


    @Inject(method = "renderGameOverlay",at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",ordinal = 4,shift = At.Shift.BEFORE),remap = false)
    private void modeSwapOverlay(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int width = var5.getScaledWidth();
        int height = var5.getScaledHeight();

        this.mc.mcProfiler.startSection("modeSwap");

        if(!Keyboard.isKeyDown(61) && Keyboard.isKeyDown(62)){
            F4Foolpressed =true;
        }
        if (Keyboard.isKeyDown(61)&&!F4Foolpressed) {
            F3 = true;
            if (Keyboard.isKeyDown(62)) {
                F4pressed=true;
            }
            if (F4pressed){
                GL11.glDisable(2896);
                GL11.glDisable(2912);
                Tessellator var2 = Tessellator.instance;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/gamemodeBox.png"));
                GL11.glColor4f(1f, 1f, 1f, 0.5f);
                var2.startDrawingQuads();
                var2.addVertexWithUV(width / 2f - 64, height / 2f + 38, 0.0, 0, 1);
                var2.addVertexWithUV(width / 2f + 64, height / 2f + 38, 0.0, 1, 1);
                var2.addVertexWithUV(width / 2f + 64, height / 2f - 38, 0.0, 1, 43 / 192f);
                var2.addVertexWithUV(width / 2f - 64, height / 2f - 38, 0.0, 0, 43 / 192f);
                var2.draw();
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/gamemodeBox.png"));
                GL11.glColor4f(1f, 1f, 1f, 1f);
                var2.startDrawingQuads();
                var2.addVertexWithUV(width / 2f - 64, height / 2f - 38 + 22, 0.0, 0, 43 / 192f);
                var2.addVertexWithUV(width / 2f + 64, height / 2f - 38 + 22, 0.0, 1, 43 / 192f);
                var2.addVertexWithUV(width / 2f + 64, height / 2f - 38, 0.0, 1, 0);
                var2.addVertexWithUV(width / 2f - 64, height / 2f - 38, 0.0, 0, 0);
                var2.draw();
                if (!Keyboard.isKeyDown(62)) {
                    F4 = false;
                }
                if (!F4) {
                    logic = Keyboard.isKeyDown(62);
                }
                if (logic) {
                    if (previousCalled) {
                        chosenMode++;
                        chosenMode = chosenMode > 2 ? 0 : chosenMode;
                    } else {
                        chosenMode = previousMode;
                        previousCalled = true;
                    }
                    logic = false;
                    F4 = true;
                }
                for (int i = 0; i < 3; i++) {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/gamemode.png"));
                    GL11.glColor4f(1f, 1f, 1f, 1f);
                    var2.startDrawingQuads();
                    var2.addVertexWithUV(width / 2f - 12 - 32 + 32 * i, height / 2f + 12, 0.0, 1 / 3f * i, 0.5 + (chosenMode == i ? 0.5 : 0));
                    var2.addVertexWithUV(width / 2f + 12 - 32 + 32 * i, height / 2f + 12, 0.0, 1 / 3f * (i + 1), 0.5 + (chosenMode == i ? 0.5 : 0));
                    var2.addVertexWithUV(width / 2f + 12 - 32 + 32 * i, height / 2f - 12, 0.0, 1 / 3f * (i + 1), 0 + (chosenMode == i ? 0.5 : 0));
                    var2.addVertexWithUV(width / 2f - 12 - 32 + 32 * i, height / 2f - 12, 0.0, 1 / 3f * i, 0 + (chosenMode == i ? 0.5 : 0));
                    var2.draw();
                }
                FontRenderer font = Minecraft.getMinecraft().fontRenderer;
                font.drawString("[ F4 ]       ", (int) (width / 2f) - font.getStringWidth("[ F4 ]      ") / 2, (int) (height / 2f + 38 - 15), 0x00E1FF, false);
                font.drawString("         Next", (int) (width / 2f) - font.getStringWidth("        Next") / 2, (int) (height / 2f + 38 - 15), 0xffffff, false);

                drawCenteredString(font, chosenMode == 0 ? "Creative Mode" : chosenMode == 1 ? "Survival Mode" : "No Clip Mode", (int) (width / 2f), (int) (height / 2f - 38 + 7), 0xffffff);

                GL11.glDisable(GL11.GL_BLEND);
            }
        }else if (F3&&!Keyboard.isKeyDown(61)){
            F4pressed=false;
            F3=false;
            previousCalled=false;
            if (TesseractUtilsAddon.modeState != chosenMode){
                previousMode=TesseractUtilsAddon.modeState;
            }
            chosenMode =TesseractUtilsAddon.modeState;
        }else {
            chosenMode =TesseractUtilsAddon.modeState;
        }
        if (!Keyboard.isKeyDown(61)&&!Keyboard.isKeyDown(62)){
            F4Foolpressed =false;
        }
        this.mc.mcProfiler.startSection("TesseractUtilsOverlay");
        if (Keyboard.isKeyDown(64)) {
            this.mc.displayGuiScreen(new GuiTUSettings(null ));
        }
    }


    @Inject(method = "addChunkBoundaryDisplay",at = @At("TAIL"))
    private void addCoordsToScreen(int iYPos, CallbackInfo ci){
        EntityClientPlayerMP player = this.mc.thePlayer;
        if (TessUConfig.enableExtraDebugInfo &&player.capabilities.isCreativeMode) {
            FontRenderer fontRenderer = this.mc.fontRenderer;
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY);
            int z = MathHelper.floor_double(player.posZ);
            Vec3 direction = player.getLookVec();
            String facing = "";
            if (direction.zCoord>0&&direction.xCoord>0){
                if (direction.zCoord>direction.xCoord){
                    facing = "south";
                }else facing = "east";
            } else if (direction.zCoord < 0 && direction.xCoord < 0) {
                if (direction.zCoord>direction.xCoord){
                    facing = "west";
                }else facing = "north";
            } else if (direction.zCoord > 0 && direction.xCoord < 0) {
                if (direction.zCoord>-direction.xCoord) {
                    facing = "south";
                }
            }else if (direction.zCoord < direction.xCoord) {
                facing = "north";
            }


            this.drawString(fontRenderer, String.format("XYZ: " + x + " / " + y + " / " + z), 2, iYPos + 55, 0xE0E0E0);
            this.drawString(fontRenderer, String.format("Biome: " + player.worldObj.getBiomeGenForCoords(player.chunkCoordX,player.chunkCoordZ).biomeName), 2, iYPos + 65, 0xE0E0E0);
            this.drawString(fontRenderer, String.format("Light: " + player.worldObj.getBlockLightValue(x,y,z) + " (" + player.worldObj.getBlockNaturalLightValue(x,y,z) + " sky, " + player.worldObj.getBlockLightValueNoSky(x,y,z) + " block)"), 2, iYPos + 75, 0xE0E0E0);
            this.drawString(fontRenderer, String.format("Facing: " + facing ), 2, iYPos + 85, 0xE0E0E0);

        }
    }
}
