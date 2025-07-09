package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.GUI.GuiBuildingModeScreen;
import net.dravigen.tesseractUtils.GUI.GuiConfigSettingsScreen;
import net.dravigen.tesseractUtils.GUI.GuiShapeMenuScreen;
import net.dravigen.tesseractUtils.enums.EnumBuildMode;
import net.dravigen.tesseractUtils.utils.RayTracingUtils;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
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

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.*;
import static net.dravigen.tesseractUtils.TesseractUtilsAddon.mspt;
import static net.dravigen.tesseractUtils.TesseractUtilsAddon.tps;
import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {
    @Shadow @Final private Minecraft mc;

    @Unique private static boolean ranOnce = false;
    @Unique private static int previousEntityID = -1;

    @Inject(method = "renderGameOverlay",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;drawString(Lnet/minecraft/src/FontRenderer;Ljava/lang/String;III)V",ordinal = 1))
    private void addTargetInfo(float par1, boolean par2, int par3, int par4, CallbackInfo ci)  {
        EntityClientPlayerMP player = this.mc.thePlayer;
        if (EXTRA_DEBUG.getBoolValue()&&player.capabilities.isCreativeMode&&PacketUtils.isPlayerOPClient) {
            FontRenderer fontRenderer = this.mc.fontRenderer;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);

            int var6 = scaledResolution.getScaledWidth();

            Vec3 var3 = player.getPosition(partialTick);
            Vec3 var4 = player.getLook(partialTick);
            Vec3 var5 = var3.addVector(var4.xCoord * REACH.getIntValue(), var4.yCoord * REACH.getIntValue(), var4.zCoord * REACH.getIntValue());
            MovingObjectPosition blockLookedAt = player.worldObj.clip(var3, var5);
            Entity entityHit = RayTracingUtils.getEntityFromRayTrace(player,REACH.getIntValue(), partialTick);
            int count=0;

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
                String var20 = "Targeted Block: §a" + xBlock + " §f/ §a" + yBlock + " §f/ §a" + zBlock;
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-Name: §a" + Block.blocksList[player.worldObj.getBlockId(xBlock, yBlock, zBlock)].getLocalizedName();
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-ID: §a" + player.worldObj.getBlockId(xBlock, yBlock, zBlock) + "§f, Metadata: §a" + player.worldObj.getBlockMetadata(xBlock, yBlock, zBlock);
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-Face: §a" + face + " §f(§a" + blockLookedAt.sideHit + "§f)";
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                count++;

            }
            if (entityHit instanceof EntityLiving entity) {
                String var20 = "Targeted Entity: §a" + entity.getTranslatedEntityName();
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-Health: §a" + entity.getHealth() + " §f/ §a" + entity.getMaxHealth();
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-Armor: §a" + entity.getTotalArmorValue();
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                var20 = "-Coords:§a " + String.format("%.1f",entity.posX) + " §f/ §a" + String.format("%.1f",entity.posY) + " §f/ §a" + String.format("%.1f",entity.posZ);
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                if (entity.entityId!=previousEntityID) {
                    if (!ranOnce) {
                        PacketSender.sendClientToServerMessage("isPermanent:" + entity.entityId);
                        ranOnce = true;
                        previousEntityID=entity.entityId;
                    }
                }

                var20 = "-IsPermanent: §a" + (PacketUtils.isLookedAtEntityPermanentClient);
                this.drawString(fontRenderer, String.format(var20), var6 - fontRenderer.getStringWidth(var20) - 2, 32+10*count, 0xFFFFFF);
                count++;
                count++;
            }else ranOnce=false;
        }
    }
    @Unique
    private static boolean logic = false;
    @Unique
    private static boolean F4 = false;
    @Unique
    private static int chosenMode = modeState;

    @Unique
    private static int previousMode=-1;
    @Unique
    private static boolean previousCalled=false;
    @Unique
    private static boolean F3= false;
    @Unique
    private static boolean F4Foolpressed = false;
    @Unique
    private static boolean F4pressed = false;
    @Unique
    private static boolean isModifyingConfig = false;


    @Inject(method = "renderGameOverlay",at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",ordinal = 9,remap = false))
    private void render(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int width = var5.getScaledWidth();
        int height = var5.getScaledHeight();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        this.mc.mcProfiler.startSection("TesseractUtilsOverlay");
        if (mc.currentScreen==null&&Keyboard.isKeyDown((int) CONFIG_MENU_KEY.getValue())) {
            this.mc.displayGuiScreen(new GuiConfigSettingsScreen(null));
        }
        if (mc.currentScreen==null&&Keyboard.isKeyDown((int) SHAPE_MENU_KEY.getValue())) {
            this.mc.displayGuiScreen(new GuiShapeMenuScreen(null));
        }
        if (this.mc.thePlayer != null&&this.mc.thePlayer.capabilities.isCreativeMode && PacketUtils.isPlayerOPClient) {
            if (this.mc.currentScreen instanceof GuiConfigSettingsScreen) {
                isModifyingConfig = true;
            } else if (isModifyingConfig) {
                PacketSender.sendClientToServerMessage("updatePlayerInfo:" + PacketUtils.playerInfoClient(modeState));
                isModifyingConfig = false;
            }
            if (heldItem != null && heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams")&&currentBuildingMode==8){
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if (KEY_HELP.getBoolValue()) {
                    String[] shapeModeHelp = EnumBuildMode.getEnumFromIndex(EnumBuildMode.SHAPE_MODE.getIndex()).getActionsHelp();
                    int stringWidth = 0;
                    for (String s : shapeModeHelp) stringWidth = Math.max(stringWidth, font.getStringWidth(s));
                    int stringHeight = font.FONT_HEIGHT * (shapeModeHelp.length);
                    for (int i = 0; i < shapeModeHelp.length; i++) {
                        String action = shapeModeHelp[i];
                        font.drawStringWithShadow(action, width - font.getStringWidth(action) - 4, height - font.FONT_HEIGHT - font.FONT_HEIGHT * (shapeModeHelp.length - i - 1) - 24, 0xFFFFFF);
                    }
                }
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (currentBuildingMode != 8) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                String var1 = "§cBuilder Mode ON: §f" + EnumBuildMode.getEnumFromIndex(currentBuildingMode).getName();
                font.drawString(var1,width / 2 - font.getStringWidth(var1)/ 2,4,0xFFFFFF);
                if (KEY_HELP.getBoolValue()) {
                    String[] actions = EnumBuildMode.getEnumFromIndex(currentBuildingMode).getActionsHelp();
                    int stringWidth = 0;
                    for (String s : actions) stringWidth = Math.max(stringWidth, font.getStringWidth(s));
                    int stringHeight = font.FONT_HEIGHT * (actions.length);
                    for (int i = 0; i < actions.length; i++) {
                        String action = actions[i];
                        font.drawStringWithShadow(action, width - font.getStringWidth(action) - 4, height - font.FONT_HEIGHT - font.FONT_HEIGHT * (actions.length - i - 1) - 24, 0xFFFFFF);
                    }
                }
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (Keyboard.isKeyDown(56)) {
                if (!(this.mc.currentScreen instanceof GuiBuildingModeScreen)) {
                    this.mc.displayGuiScreen(new GuiBuildingModeScreen());
                }
            }

        }
    }

    @Inject(method = "renderGameOverlay",at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",ordinal = 9,remap = false))
    private void modeSwapOverlay(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int width = var5.getScaledWidth();
        int height = var5.getScaledHeight();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        this.mc.mcProfiler.startSection("modeSwap");
        if (PacketUtils.isPlayerOPClient) {
            if (!Keyboard.isKeyDown(61) && Keyboard.isKeyDown(62)) {
                F4Foolpressed = true;
            }
            if (Keyboard.isKeyDown(61) && !F4Foolpressed) {
                F3 = true;
                if (Keyboard.isKeyDown(62)) {
                    F4pressed = true;
                }
                if (F4pressed) {
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
                            chosenMode = previousMode==-1 ? modeState : previousMode;
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
                    font.drawString("[ F4 ]       ", (int) (width / 2f) - font.getStringWidth("[ F4 ]      ") / 2, (int) (height / 2f + 38 - 15), 0x00E1FF, false);
                    font.drawString("         Next", (int) (width / 2f) - font.getStringWidth("        Next") / 2, (int) (height / 2f + 38 - 15), 0xffffff, false);

                    drawCenteredString(font, chosenMode == 0 ? "Creative Mode" : chosenMode == 1 ? "Survival Mode" : "No Clip Mode", (int) (width / 2f), (int) (height / 2f - 38 + 7), 0xffffff);

                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
            else if (F3 && !Keyboard.isKeyDown(61)) {
                F4pressed = false;
                F3 = false;
                previousCalled = false;
                if (modeState != chosenMode) {
                    previousMode = modeState;
                    modeState=chosenMode;
                    EntityClientPlayerMP player = this.mc.thePlayer;
                    player.setGameType(chosenMode == 1 ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
                    PacketSender.sendClientToServerMessage("updatePlayerInfo:"+PacketUtils.playerInfoClient(chosenMode));
                }
                chosenMode = modeState;
            } else {
                chosenMode = modeState;
            }
            if (!Keyboard.isKeyDown(61) && !Keyboard.isKeyDown(62)) {
                F4Foolpressed = false;
            }
        }
    }

    @Inject(method = "addChunkBoundaryDisplay",at = @At("TAIL"))
    private void addExtraInfo(int iYPos, CallbackInfo ci) {
        EntityClientPlayerMP player = this.mc.thePlayer;
        if ((boolean) EXTRA_DEBUG.getValue() && player.capabilities.isCreativeMode && PacketUtils.isPlayerOPClient) {
            FontRenderer fontRenderer = this.mc.fontRenderer;
            String x = String.format("%.1f", player.posX);
            String y = String.format("%.1f", player.boundingBox.minY);
            String z = String.format("%.1f", player.posZ);
            int xInt = MathHelper.floor_double(player.posX);
            int yInt = MathHelper.floor_double(player.boundingBox.minY);
            int zInt = MathHelper.floor_double(player.posZ);
            float var2 = MathHelper.cos(-player.rotationYaw * ((float) Math.PI / 180) - (float) Math.PI);
            float var3 = MathHelper.sin(-player.rotationYaw * ((float) Math.PI / 180) - (float) Math.PI);
            float var5 = MathHelper.sin(-player.rotationPitch * ((float) Math.PI / 180));
            World world = player.worldObj;
            Vec3 direction = world.getWorldVec3Pool().getVecFromPool(-var3, var5, -var2);
            String facing;
            facing = direction.xCoord >= 0 ? (direction.zCoord <= 0 ? (direction.xCoord <= 0.7 ? "north" : "east") : direction.xCoord <= 0.7 ? "south" : "east") : (direction.zCoord <= 0 ? (direction.xCoord >= -0.7 ? "north" : "west") : (direction.xCoord <= -0.7 ? "west" : "south"));
            this.drawString(fontRenderer, String.format("XYZ: " + x + " / " + y + " / " + z), 2, iYPos + 55, 0xFFFFFF);
            this.drawString(fontRenderer, String.format("Biome: " + world.getBiomeGenForCoords(player.chunkCoordX, player.chunkCoordZ).biomeName), 2, iYPos + 65, 0xFFFFFF);
            this.drawString(fontRenderer, String.format("Light: " + world.getBlockLightValue(xInt, yInt, zInt) + " (" + world.getBlockNaturalLightValue(xInt, yInt, zInt) + " sky, " + world.getBlockLightValueNoSky(xInt, yInt, zInt) + " block)"), 2, iYPos + 75, 0xFFFFFF);
            this.drawString(fontRenderer, String.format("Facing: " + facing), 2, iYPos + 85, 0xFFFFFF);
            this.drawString(fontRenderer, "TPS: " + String.format("%.2f", 1000 / tps) + ", MSPT: " + mspt, 2, iYPos + 95, 0xFFFFFF);

            if (this.mc.thePlayer.ticksExisted%20==0) PacketSender.sendClientToServerMessage("mobCapUpdate");

            String[] infos = PacketUtils.mobCapInfosClient;
            if (infos != null) {
                int hostile = Integer.parseInt(infos[0]);
                int creature = Integer.parseInt(infos[1]);
                int ambient = Integer.parseInt(infos[2]);
                int water = Integer.parseInt(infos[3]);
                float constant = Float.parseFloat(infos[4]);

                int maxHostile = (int) (90 * constant);
                int maxCreature = (int) (10 * constant);
                int maxAmbient = (int) (15 * constant);
                int maxWater = (int) (5 * constant);

                this.drawString(fontRenderer, "Mobs cap:", 2, iYPos + 115, 0xFFFFFF);
                this.drawString(fontRenderer, "-Hostile: " + hostile + "/" + maxHostile, 2, iYPos + 125, 0xFFFFFF);
                this.drawString(fontRenderer, "-Creature: " + creature + "/" + maxCreature, 2, iYPos + 135, 0xFFFFFF);
                this.drawString(fontRenderer, "-Ambient: " + ambient + "/" + maxAmbient, 2, iYPos + 145, 0xFFFFFF);
                this.drawString(fontRenderer, "-Water: " + water + "/" + maxWater, 2, iYPos + 155, 0xFFFFFF);
            }
        }
    }
}
