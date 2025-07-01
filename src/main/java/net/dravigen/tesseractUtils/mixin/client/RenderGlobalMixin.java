package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.advanced_edit.EnumBuildMode;
import net.dravigen.tesseractUtils.command.UtilsCommand;
import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.advanced_edit.BlockSelectionManager.*;
import static net.dravigen.tesseractUtils.advanced_edit.RayTracingUtils.*;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Shadow
    private Minecraft mc;

    @Inject(method = "renderEntities", at = @At("HEAD"))
    private void renderDisplay(Vec3 cameraPosition, ICamera camera, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        TesseractUtilsAddon.partialTick = partialTicks;
        if (mc.theWorld == null || mc.renderViewEntity == null) return;
        double playerRenderX = cameraPosition.xCoord;
        double playerRenderY = cameraPosition.yCoord;
        double playerRenderZ = cameraPosition.zCoord;
        GL11.glPushMatrix();
        GL11.glTranslated(-playerRenderX, -playerRenderY, -playerRenderZ);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (isBlock1Selected) {
            renderTransparentCube(block1.xCoord, block1.yCoord, block1.zCoord, 1F, 0.0F, 0.0F, 0.5F);
        }
        if (isBlock2Selected) {
            renderTransparentCube(block2.xCoord, block2.yCoord, block2.zCoord, 0.0F, 0.0F, 1.0F, 0.5F);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (currentSelectionState == SelectionState.TWO_SELECTED) {
            double minX = Math.min(block1.xCoord, block2.xCoord) - 0.01;
            double minY = Math.min(block1.yCoord, block2.yCoord) - 0.01;
            double minZ = Math.min(block1.zCoord, block2.zCoord) - 0.01;
            double maxX = Math.max(block1.xCoord, block2.xCoord) + 1.01;
            double maxY = Math.max(block1.yCoord, block2.yCoord) + 1.01;
            double maxZ = Math.max(block1.zCoord, block2.zCoord) + 1.01;
            HitResult hitResult;
            EntityPlayer player = mc.thePlayer;
            Vec3 rayOriginWorld = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            boolean inverted = false;
            Vec3 rayDirection = player.getLook(partialTicks);
            if (player.isUsingSpecialKey()) {
                rayOriginWorld = Vec3.createVectorHelper(
                        0,
                        0,
                        0
                );
                rayDirection.xCoord *= -1;
                rayDirection.yCoord *= -1;
                rayDirection.zCoord *= -1;
                inverted = true;
                hitResult = intersectAABB(rayOriginWorld, rayDirection, -1, -1, -1, 1, 1, 1);
            } else {
                if (rayOriginWorld.xCoord <= maxX && rayOriginWorld.xCoord >= minX && rayOriginWorld.yCoord <= maxY && rayOriginWorld.yCoord >= minY && rayOriginWorld.zCoord <= maxZ && rayOriginWorld.zCoord >= minZ) {
                    rayDirection.xCoord *= -1;
                    rayDirection.yCoord *= -1;
                    rayDirection.zCoord *= -1;
                    inverted = true;
                }
                hitResult = intersectAABB(rayOriginWorld, rayDirection, minX, minY, minZ, maxX, maxY, maxZ);
            }
            if (!EnumBuildMode.getEnumFromIndex(TesseractUtilsAddon.currentBuildingMode).getCanSelect())hitResult.hitFace=-1;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_CULL_FACE);
            float sin = ((float) Math.sin(this.mc.thePlayer.ticksExisted / 20f) + 0.25f) / 1.25f;
            sin = sin < 0 ? 0 : sin;

            GL11.glLineWidth(0.75F);
            GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.1001f + (0.5f - 0.1001f) * sin);
            drawWireframeBox(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, false);

            GL11.glLineWidth(3F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0F, 1.0F, 0.0F, 1f);
            drawWireframeBox(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, false);
            for (int i = 0; i < 6; i++) {
                if (i != hitResult.hitFace) {

                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(0.47F, 0.77F, 1.0F, 0.1001f);
                    drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, i, inverted, hitResult.hitFace);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(0.47F, 0.77F, 1.0F, 0.3f + (0.3f) * sin);
                    drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, i, inverted, hitResult.hitFace);
                }
            }
            if (hitResult.hit && EnumBuildMode.getEnumFromIndex(TesseractUtilsAddon.currentBuildingMode).getCanSelect()) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5f);
                drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, inverted, hitResult.hitFace);

                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.101f);
                drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, inverted, hitResult.hitFace);
                GL11.glColor4f(1.0F, 1.0F, 0.0F, 1f);
                drawWireframeBox(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, true);
            }
            GL11.glDepthMask(true);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1.0F);
        GL11.glPopMatrix();
    }

    @Unique
    private void renderTransparentCube(double x, double y, double z, float r, float g, float b, float a) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(r, g, b, a);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
        drawCubeFaces();
        GL11.glCullFace(GL11.GL_BACK);
        drawCubeFaces();
        GL11.glPopMatrix();
    }
    @Unique
    private void drawCubeFaces() {
        double min = -0.01;
        double max = 1.01;
        GL11.glBegin(GL11.GL_QUADS);
        // Front face (-Z)
        GL11.glVertex3f((float) min, (float) min, (float) min);
        GL11.glVertex3f((float) max, (float) min, (float) min);
        GL11.glVertex3f((float) max, (float) max, (float) min);
        GL11.glVertex3f((float) min, (float) max, (float) min);
        // Back face (+Z)
        GL11.glVertex3f((float) min, (float) max, (float) max);
        GL11.glVertex3f((float) max, (float) max, (float) max);
        GL11.glVertex3f((float) max, (float) min, (float) max);
        GL11.glVertex3f((float) min, (float) min, (float) max);
        // Bottom face (-Y)
        GL11.glVertex3f((float) min, (float) min, (float) min);
        GL11.glVertex3f((float) min, (float) min, (float) max);
        GL11.glVertex3f((float) max, (float) min, (float) max);
        GL11.glVertex3f((float) max, (float) min, (float) min);
        // Top face (+Y)
        GL11.glVertex3f((float) min, (float) max, (float) min);
        GL11.glVertex3f((float) max, (float) max, (float) min);
        GL11.glVertex3f((float) max, (float) max, (float) max);
        GL11.glVertex3f((float) min, (float) max, (float) max);
        // Left face (-X)
        GL11.glVertex3f((float) min, (float) min, (float) max);
        GL11.glVertex3f((float) min, (float) min, (float) min);
        GL11.glVertex3f((float) min, (float) max, (float) min);
        GL11.glVertex3f((float) min, (float) max, (float) max);
        // Right face (+X)
        GL11.glVertex3f((float) max, (float) min, (float) min);
        GL11.glVertex3f((float) max, (float) min, (float) max);
        GL11.glVertex3f((float) max, (float) max, (float) max);
        GL11.glVertex3f((float) max, (float) max, (float) min);
        GL11.glEnd();
    }
    @Unique
    private void drawWireframeBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int skipFace, boolean onlyHitFace) {
        GL11.glBegin(GL11.GL_LINES);
        // Bottom square
        drawEdgeIfCondition(minX, minY, minZ, maxX, minY, minZ, HitResult.SIDE_D, HitResult.SIDE_N, skipFace, onlyHitFace); // minX-maxX, minY, minZ
        drawEdgeIfCondition(maxX, minY, minZ, maxX, minY, maxZ, HitResult.SIDE_D, HitResult.SIDE_E, skipFace, onlyHitFace); // maxX, minY, minZ-maxZ
        drawEdgeIfCondition(maxX, minY, maxZ, minX, minY, maxZ, HitResult.SIDE_D, HitResult.SIDE_S, skipFace, onlyHitFace); // maxX-minX, minY, maxZ
        drawEdgeIfCondition(minX, minY, maxZ, minX, minY, minZ, HitResult.SIDE_D, HitResult.SIDE_W, skipFace, onlyHitFace); // minX, minY, maxZ-minZ

        // Top square
        drawEdgeIfCondition(minX, maxY, minZ, maxX, maxY, minZ, HitResult.SIDE_U, HitResult.SIDE_N, skipFace, onlyHitFace); // minX-maxX, maxY, minZ
        drawEdgeIfCondition(maxX, maxY, minZ, maxX, maxY, maxZ, HitResult.SIDE_U, HitResult.SIDE_E, skipFace, onlyHitFace); // maxX, maxY, minZ-maxZ
        drawEdgeIfCondition(maxX, maxY, maxZ, minX, maxY, maxZ, HitResult.SIDE_U, HitResult.SIDE_S, skipFace, onlyHitFace); // maxX-minX, maxY, maxZ
        drawEdgeIfCondition(minX, maxY, maxZ, minX, maxY, minZ, HitResult.SIDE_U, HitResult.SIDE_W, skipFace, onlyHitFace); // minX, maxY, maxZ-minZ

        // Vertical edges connecting top and bottom squares
        drawEdgeIfCondition(minX, minY, minZ, minX, maxY, minZ, HitResult.SIDE_W, HitResult.SIDE_N, skipFace, onlyHitFace); // minX, minZ
        drawEdgeIfCondition(maxX, minY, minZ, maxX, maxY, minZ, HitResult.SIDE_E, HitResult.SIDE_N, skipFace, onlyHitFace); // maxX, minZ
        drawEdgeIfCondition(maxX, minY, maxZ, maxX, maxY, maxZ, HitResult.SIDE_E, HitResult.SIDE_S, skipFace, onlyHitFace); // maxX, maxZ
        drawEdgeIfCondition(minX, minY, maxZ, minX, maxY, maxZ, HitResult.SIDE_W, HitResult.SIDE_S, skipFace, onlyHitFace); // minX, maxZ

        GL11.glEnd();
    }
    @Unique
    private void drawEdgeIfCondition(double x1, double y1, double z1, double x2, double y2, double z2, int face1, int face2, int checkFace, boolean onlyHitFace) {
        boolean belongsToFace = (face1 == checkFace || face2 == checkFace);
        if (onlyHitFace == belongsToFace) {
            GL11.glVertex3f((float)x1, (float)y1, (float)z1);
            GL11.glVertex3f((float)x2, (float)y2, (float)z2);
        }
    }
    @Unique
    private void drawHighlightFace(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int face, boolean inverted, int hitFace) {
        GL11.glBegin(GL11.GL_QUADS);
        int increment = 0;
        int ctrlIncrement = 0;
        if (face == hitFace) {
            if (this.mc.currentScreen==null) {
                int dWheel = Mouse.getDWheel();
                dWheel = dWheel>120 ? 0 : dWheel<-120 ? 0 : dWheel;
                if (dWheel != 0) {
                    int i = dWheel > 0 ? inverted ? -1 : 1 : inverted ? 1 : -1;
                    increment = i;
                    if (TesseractUtilsAddon.currentBuildingMode==EnumBuildMode.MOVE_MODE.getIndex()) {
                        ctrlIncrement = i;
                    }
                }
            }
        }

        switch (face) {
            case HitResult.SIDE_W: // -X (West) face
                if (block1.xCoord>block2.xCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord + increment, (int) block1.yCoord, (int) block1.zCoord);
                UtilsCommand.setCoord2((int) block2.xCoord + ctrlIncrement, (int) block2.yCoord, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
            case HitResult.SIDE_E: // +X (East) face
                if (block1.xCoord>block2.xCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord - ctrlIncrement, (int) block1.yCoord, (int) block1.zCoord);
                UtilsCommand.setCoord2((int) block2.xCoord - increment, (int) block2.yCoord, (int) block2.zCoord);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                break;
            case HitResult.SIDE_D: // -Y (Bottom) face
                if (block1.yCoord>block2.yCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord, (int) block1.yCoord + increment, (int) block1.zCoord);
                UtilsCommand.setCoord2((int) block2.xCoord, (int) block2.yCoord + ctrlIncrement, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                break;
            case HitResult.SIDE_U: // +Y (Top) face
                if (block1.yCoord>block2.yCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord, (int) block1.yCoord - ctrlIncrement, (int) block1.zCoord);
                UtilsCommand.setCoord2((int) block2.xCoord, (int) block2.yCoord - increment, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
            case HitResult.SIDE_N: // -Z (Front) face
                if (block1.zCoord>block2.zCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord, (int) block1.yCoord, (int) block1.zCoord + increment);
                UtilsCommand.setCoord2((int) block2.xCoord, (int) block2.yCoord, (int) block2.zCoord + ctrlIncrement);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                break;
            case HitResult.SIDE_S: // +Z (Back) face
                if (block1.zCoord>block2.zCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                UtilsCommand.setCoord1((int) block1.xCoord, (int) block1.yCoord, (int) block1.zCoord - ctrlIncrement);
                UtilsCommand.setCoord2((int) block2.xCoord, (int) block2.yCoord, (int) block2.zCoord - increment);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
        }
        GL11.glEnd();
    }
}
