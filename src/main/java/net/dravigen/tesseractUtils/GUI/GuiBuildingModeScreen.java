package net.dravigen.tesseractUtils.GUI;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.enums.EnumBuildMode;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public class GuiBuildingModeScreen extends GuiScreen {
    @Unique
    private static double angle = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!Keyboard.isKeyDown(56)){
            TesseractUtilsAddon.currentBuildingMode = (int) (-1*((angle-360)/45f));
            PacketSender.sendClientToServerMessage("updatePlayerInfo:"+PacketUtils.playerInfoClient(TesseractUtilsAddon.modeState));
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int width = var5.getScaledWidth();
        int height = var5.getScaledHeight();
        if (PacketUtils.isPlayerOPClient &&this.mc.thePlayer.capabilities.isCreativeMode) {
            if (Keyboard.isKeyDown(56)) {
                int dWheel = Mouse.getDWheel();
                if (dWheel != 0) {
                    if (dWheel<0) {
                        angle = angle + 45F;
                    }else angle = angle - 45F;
                    if (angle >= 360.0F) {
                        angle -= 360.0F;
                    } else if (angle < 0.0F) {
                        angle += 360.0F;
                    }
                }
                GL11.glPushMatrix();
                GL11.glDisable(2896);
                GL11.glDisable(2912);
                Tessellator var2 = Tessellator.instance;
                GL11.glTranslatef(width+48,height / 2f,0);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.mc.getTextureManager().bindTexture(new ResourceLocation("tesseract_utils:textures/gui/buildingMode.png"));
                GL11.glColor4f(1f, 1f, 1f, 0.75f);
                var2.startDrawingQuads();
                var2.addVertexWithUV(-120, 120, 0.0, 0, 1);
                var2.addVertexWithUV(120, 120, 0.0, 1, 1);
                var2.addVertexWithUV(120, -120, 0.0, 1, 0);
                var2.addVertexWithUV(-120, -120, 0.0, 0, 0);
                GL11.glRotated(angle, 0, 0, 1);
                var2.draw();
                GL11.glPopMatrix();

                EnumBuildMode enumBuildMode = EnumBuildMode.getEnumFromIndex((int) (-1*((angle-360)/45f)));
                String desc = enumBuildMode.getDescription();
                List<String> list = fontRenderer.listFormattedStringToWidth(desc,width/3);
                for (String s:list){
                    fontRenderer.drawStringWithShadow(s,width+48- 120-fontRenderer.getStringWidth(s),height/2- fontRenderer.FONT_HEIGHT + fontRenderer.FONT_HEIGHT*list.indexOf(s), 0xffffff);
                }

            }
        }
    }
}
