package net.dravigen.tesseractUtils.utils;

import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.*;
import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.*;
import static net.dravigen.tesseractUtils.enums.EnumBuildMode.*;

public class RenderUtils {

    public static void renderTransparentCube(double x, double y, double z, float r, float g, float b, float a) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(r, g, b, a);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
        drawCubeFacesDisplay();
        GL11.glCullFace(GL11.GL_BACK);
        drawCubeFacesDisplay();
        GL11.glPopMatrix();
    }

    public static void drawCubeFacesDisplay() {
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

    public static void drawTrueCubeFaces(boolean drawNX, boolean drawPX, boolean drawNY, boolean drawPY, boolean drawNZ, boolean drawPZ) {
        double min = 0;
        double max = 1;
        GL11.glBegin(GL11.GL_QUADS);

        // Front face (-Z)
        if (drawNZ) {
            GL11.glVertex3f((float) min, (float) min, (float) min);
            GL11.glVertex3f((float) max, (float) min, (float) min);
            GL11.glVertex3f((float) max, (float) max, (float) min);
            GL11.glVertex3f((float) min, (float) max, (float) min);
        }
        // Back face (+Z)
        if (drawPZ) {
            GL11.glVertex3f((float) min, (float) max, (float) max);
            GL11.glVertex3f((float) max, (float) max, (float) max);
            GL11.glVertex3f((float) max, (float) min, (float) max);
            GL11.glVertex3f((float) min, (float) min, (float) max);
        }
        // Bottom face (-Y)
        if (drawNY) {
            GL11.glVertex3f((float) min, (float) min, (float) min);
            GL11.glVertex3f((float) min, (float) min, (float) max);
            GL11.glVertex3f((float) max, (float) min, (float) max);
            GL11.glVertex3f((float) max, (float) min, (float) min);
        }
        // Top face (+Y)
        if (drawPY) {
            GL11.glVertex3f((float) min, (float) max, (float) min);
            GL11.glVertex3f((float) max, (float) max, (float) min);
            GL11.glVertex3f((float) max, (float) max, (float) max);
            GL11.glVertex3f((float) min, (float) max, (float) max);
        }
        // Left face (-X)
        if (drawNX) {
            GL11.glVertex3f((float) min, (float) min, (float) max);
            GL11.glVertex3f((float) min, (float) min, (float) min);
            GL11.glVertex3f((float) min, (float) max, (float) min);
            GL11.glVertex3f((float) min, (float) max, (float) max);
        }
        // Right face (+X)
        if (drawPX) {
            GL11.glVertex3f((float) max, (float) min, (float) min);
            GL11.glVertex3f((float) max, (float) min, (float) max);
            GL11.glVertex3f((float) max, (float) max, (float) max);
            GL11.glVertex3f((float) max, (float) max, (float) min);
        }
        GL11.glEnd();
    }

    public static void drawWireframeBoxConditions(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int skipFace, boolean onlyHitFace) {
        GL11.glBegin(GL11.GL_LINES);
        // Bottom square
        drawEdgeIfCondition(minX, minY, minZ, maxX, minY, minZ, RayTracingUtils.HitResult.SIDE_D, RayTracingUtils.HitResult.SIDE_N, skipFace, onlyHitFace); // minX-maxX, minY, minZ
        drawEdgeIfCondition(maxX, minY, minZ, maxX, minY, maxZ, RayTracingUtils.HitResult.SIDE_D, RayTracingUtils.HitResult.SIDE_E, skipFace, onlyHitFace); // maxX, minY, minZ-maxZ
        drawEdgeIfCondition(maxX, minY, maxZ, minX, minY, maxZ, RayTracingUtils.HitResult.SIDE_D, RayTracingUtils.HitResult.SIDE_S, skipFace, onlyHitFace); // maxX-minX, minY, maxZ
        drawEdgeIfCondition(minX, minY, maxZ, minX, minY, minZ, RayTracingUtils.HitResult.SIDE_D, RayTracingUtils.HitResult.SIDE_W, skipFace, onlyHitFace); // minX, minY, maxZ-minZ

        // Top square
        drawEdgeIfCondition(minX, maxY, minZ, maxX, maxY, minZ, RayTracingUtils.HitResult.SIDE_U, RayTracingUtils.HitResult.SIDE_N, skipFace, onlyHitFace); // minX-maxX, maxY, minZ
        drawEdgeIfCondition(maxX, maxY, minZ, maxX, maxY, maxZ, RayTracingUtils.HitResult.SIDE_U, RayTracingUtils.HitResult.SIDE_E, skipFace, onlyHitFace); // maxX, maxY, minZ-maxZ
        drawEdgeIfCondition(maxX, maxY, maxZ, minX, maxY, maxZ, RayTracingUtils.HitResult.SIDE_U, RayTracingUtils.HitResult.SIDE_S, skipFace, onlyHitFace); // maxX-minX, maxY, maxZ
        drawEdgeIfCondition(minX, maxY, maxZ, minX, maxY, minZ, RayTracingUtils.HitResult.SIDE_U, RayTracingUtils.HitResult.SIDE_W, skipFace, onlyHitFace); // minX, maxY, maxZ-minZ

        // Vertical edges connecting top and bottom squares
        drawEdgeIfCondition(minX, minY, minZ, minX, maxY, minZ, RayTracingUtils.HitResult.SIDE_W, RayTracingUtils.HitResult.SIDE_N, skipFace, onlyHitFace); // minX, minZ
        drawEdgeIfCondition(maxX, minY, minZ, maxX, maxY, minZ, RayTracingUtils.HitResult.SIDE_E, RayTracingUtils.HitResult.SIDE_N, skipFace, onlyHitFace); // maxX, minZ
        drawEdgeIfCondition(maxX, minY, maxZ, maxX, maxY, maxZ, RayTracingUtils.HitResult.SIDE_E, RayTracingUtils.HitResult.SIDE_S, skipFace, onlyHitFace); // maxX, maxZ
        drawEdgeIfCondition(minX, minY, maxZ, minX, maxY, maxZ, RayTracingUtils.HitResult.SIDE_W, RayTracingUtils.HitResult.SIDE_S, skipFace, onlyHitFace); // minX, maxZ

        GL11.glEnd();
    }

    private static void drawEdgeIfCondition(double x1, double y1, double z1, double x2, double y2, double z2, int face1, int face2, int checkFace, boolean onlyHitFace) {
        boolean belongsToFace = (face1 == checkFace || face2 == checkFace);
        if (onlyHitFace == belongsToFace) {
            GL11.glVertex3f((float)x1, (float)y1, (float)z1);
            GL11.glVertex3f((float)x2, (float)y2, (float)z2);
        }
    }

    public static void drawWireframeBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        GL11.glBegin(GL11.GL_LINES);
        // Bottom square
        drawEdge(minX, minY, minZ, maxX, minY, minZ); // minX-maxX, minY, minZ
        drawEdge(maxX, minY, minZ, maxX, minY, maxZ); // maxX, minY, minZ-maxZ
        drawEdge(maxX, minY, maxZ, minX, minY, maxZ); // maxX-minX, minY, maxZ
        drawEdge(minX, minY, maxZ, minX, minY, minZ); // minX, minY, maxZ-minZ

        // Top square
        drawEdge(minX, maxY, minZ, maxX, maxY, minZ); // minX-maxX, maxY, minZ
        drawEdge(maxX, maxY, minZ, maxX, maxY, maxZ); // maxX, maxY, minZ-maxZ
        drawEdge(maxX, maxY, maxZ, minX, maxY, maxZ); // maxX-minX, maxY, maxZ
        drawEdge(minX, maxY, maxZ, minX, maxY, minZ); // minX, maxY, maxZ-minZ

        // Vertical edges connecting top and bottom squares
        drawEdge(minX, minY, minZ, minX, maxY, minZ); // minX, minZ
        drawEdge(maxX, minY, minZ, maxX, maxY, minZ); // maxX, minZ
        drawEdge(maxX, minY, maxZ, maxX, maxY, maxZ); // maxX, maxZ
        drawEdge(minX, minY, maxZ, minX, maxY, maxZ); // minX, maxZ

        GL11.glEnd();
    }

    private static void drawEdge(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glVertex3f((float) x1, (float) y1, (float) z1);
        GL11.glVertex3f((float) x2, (float) y2, (float) z2);

    }

    public static void drawHighlightFace(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int face, boolean inverted, int hitFace, boolean canIncrement) {
        GL11.glBegin(GL11.GL_QUADS);
        int increment = 0;
        int ctrlIncrement = 0;
        if (canIncrement) {
            if (face == hitFace) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    int dWheel = Mouse.getDWheel();
                    dWheel = dWheel > 120 ? 0 : dWheel < -120 ? 0 : dWheel;
                    if (dWheel != 0) {
                        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                        int i = dWheel > 0 ? inverted ? -1 : 1 : inverted ? 1 : -1;
                        increment = i;
                        if (((currentBuildingMode == COPY_MODE.getIndex() || currentBuildingMode == BLOCK_SET_MODE.getIndex()) && !player.isSneaking()) || (player.isSneaking() && currentBuildingMode == SELECTION_MODE.getIndex())) {
                            ctrlIncrement = i;
                        }
                    }
                }
            }
        }

        switch (face) {
            case RayTracingUtils.HitResult.SIDE_W: // -X (West) face
                if (block1.xCoord>block2.xCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord + increment, (int) block1.yCoord, (int) block1.zCoord,(int) block2.xCoord + ctrlIncrement, (int) block2.yCoord, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
            case RayTracingUtils.HitResult.SIDE_E: // +X (East) face
                if (block1.xCoord>block2.xCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord - ctrlIncrement, (int) block1.yCoord, (int) block1.zCoord,(int) block2.xCoord - increment, (int) block2.yCoord, (int) block2.zCoord);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                break;
            case RayTracingUtils.HitResult.SIDE_D: // -Y (Bottom) face
                if (block1.yCoord>block2.yCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord, (int) block1.yCoord + increment, (int) block1.zCoord,(int) block2.xCoord, (int) block2.yCoord + ctrlIncrement, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                break;
            case RayTracingUtils.HitResult.SIDE_U: // +Y (Top) face
                if (block1.yCoord>block2.yCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord, (int) block1.yCoord - ctrlIncrement, (int) block1.zCoord,(int) block2.xCoord, (int) block2.yCoord - increment, (int) block2.zCoord);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
            case RayTracingUtils.HitResult.SIDE_N: // -Z (Front) face
                if (block1.zCoord>block2.zCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord, (int) block1.yCoord, (int) block1.zCoord + increment,(int) block2.xCoord, (int) block2.yCoord, (int) block2.zCoord + ctrlIncrement);
                GL11.glVertex3f((float) minX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) minZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) minZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) minZ);
                break;
            case RayTracingUtils.HitResult.SIDE_S: // +Z (Back) face
                if (block1.zCoord>block2.zCoord){
                    int a = increment;
                    increment=ctrlIncrement;
                    ctrlIncrement=a;
                }
                if (increment!=0||ctrlIncrement!=0) sendPosUpdatePackets((int) block1.xCoord, (int) block1.yCoord, (int) block1.zCoord - ctrlIncrement,(int) block2.xCoord, (int) block2.yCoord, (int) block2.zCoord - increment);
                GL11.glVertex3f((float) minX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) minY, (float) maxZ);
                GL11.glVertex3f((float) maxX, (float) maxY, (float) maxZ);
                GL11.glVertex3f((float) minX, (float) maxY, (float) maxZ);
                break;
        }
        GL11.glEnd();
    }

    public static void sendPosUpdatePackets(int x1,int y1,int z1,int x2,int y2,int z2){
        setBlock1(x1,y1,z1);
        setBlock2(x2,y2,z2);
    }

    private static boolean isBlockInSphere(float relX, float relY, float relZ, double radiusSquared) {
        double dx = relX - 0.5;
        double dy = relY - 0.5;
        double dz = relZ - 0.5;
        double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);

        return distanceSquared <= radiusSquared ;
    }
    private static boolean isBlockInSphereOuter(float relX, float relY, float relZ, double radiusSquared, double innerRadiusSquared) {
        double dx = relX - 0.5;
        double dy = relY - 0.5;
        double dz = relZ - 0.5;
        double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);

        return distanceSquared < radiusSquared && distanceSquared >= innerRadiusSquared;
    }

    private static int sphereDisplayListID = 0; // 0 means not generated yet

    public static void renderSphere(World world, float radius, int centerX, int centerY, int centerZ, int hollowOpen, int thickness) {
        if (world == null || radius <= 0) {
            return;
        }
        boolean hollow = hollowOpen == 0;
        boolean open = hollowOpen == 1;
        if (sphereDisplayListID == 0) {
            /*sphereDisplayListID = GL11.glGenLists(1);
            if (sphereDisplayListID == 0) {
                System.err.println("Failed to generate OpenGL display list!");
                return;
            }

            GL11.glNewList(sphereDisplayListID, GL11.GL_COMPILE);*/
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase renderViewEntity = mc.renderViewEntity;
            double cameraX = renderViewEntity.posX;
            double cameraY = renderViewEntity.posY + renderViewEntity.getEyeHeight();
            double cameraZ = renderViewEntity.posZ;
            double distCenterFromCameraX = (centerX + 0.5) - cameraX;
            double distCenterFromCameraY = (centerY + 0.5) - cameraY;
            double distCenterFromCameraZ = (centerZ + 0.5) - cameraZ;
            double distanceSquaredToCameraFromCenter = (distCenterFromCameraX * distCenterFromCameraX) + (distCenterFromCameraY * distCenterFromCameraY) + (distCenterFromCameraZ * distCenterFromCameraZ);

            List<TransparentCubeInfo> transparentCubesToRender = new ArrayList<>();
            radius += 0.5f;
            double radiusSquared = (double) radius * radius;
            float innerRadius = radius - thickness;
            double innerRadiusSquared = (double) innerRadius * innerRadius;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_FRONT);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            for (float x = -radius; x <= radius; x++) {
                for (float y = -radius; y <= radius; y++) {
                    for (float z = -radius; z <= radius; z++) {
                        if (isBlockInSphereOuter(x, y, z, radiusSquared, innerRadiusSquared)||((!hollow&&!open) &&player.isUsingSpecialKey()&&isBlockInSphere(x,y,z,radiusSquared))) {
                            int currentBlockX = MathHelper.floor_float(centerX + x);
                            int currentBlockY = MathHelper.floor_float(centerY + y);
                            int currentBlockZ = MathHelper.floor_float(centerZ + z);
                            if (player.isUsingSpecialKey()&&world.getBlockId(currentBlockX,currentBlockY,currentBlockZ)==0)continue;
                            double distFromCameraX = currentBlockX + 0.5 - cameraX;
                            double distFromCameraY = currentBlockY + 0.5 - cameraY;
                            double distFromCameraZ = currentBlockZ + 0.5 - cameraZ;
                            double distanceSquaredToCameraFromBlock = (distFromCameraX * distFromCameraX) + (distFromCameraY * distFromCameraY) + (distFromCameraZ * distFromCameraZ);
                            if (distanceSquaredToCameraFromBlock>distanceSquaredToCameraFromCenter&&!player.isUsingSpecialKey())continue;
                            boolean drawFaceNX;
                            boolean drawFacePX;
                            boolean drawFaceNY;
                            boolean drawFacePY;
                            boolean drawFaceNZ;
                            boolean drawFacePZ;
                            if (!player.isUsingSpecialKey()) {
                                drawFaceNX = !isBlockInSphere(x - 1, y, z, radiusSquared) && distCenterFromCameraX > 0;
                                drawFacePX = !isBlockInSphere(x + 1, y, z, radiusSquared) && distCenterFromCameraX < 0;
                                drawFaceNY = !isBlockInSphere(x, y - 1, z, radiusSquared) && distCenterFromCameraY > 0;
                                drawFacePY = !isBlockInSphere(x, y + 1, z, radiusSquared) && distCenterFromCameraY < 0;
                                drawFaceNZ = !isBlockInSphere(x, y, z - 1, radiusSquared) && distCenterFromCameraZ > 0;
                                drawFacePZ = !isBlockInSphere(x, y, z + 1, radiusSquared) && distCenterFromCameraZ < 0;
                            }else {
                                drawFaceNX = world.isAirBlock((int) (x-1), (int) y, (int) z) && distCenterFromCameraX > 0;
                                drawFacePX = world.isAirBlock((int) (x+1), (int) y, (int) z) && distCenterFromCameraX < 0;
                                drawFaceNY = world.isAirBlock((int) x, (int) (y-1), (int) z) && distCenterFromCameraY > 0;
                                drawFacePY = world.isAirBlock((int) x, (int) (y+1), (int) z) && distCenterFromCameraY < 0;
                                drawFaceNZ = world.isAirBlock((int) x, (int) y, (int) (z-1)) && distCenterFromCameraZ > 0;
                                drawFacePZ = world.isAirBlock((int) x, (int) y, (int) (z+1)) && distCenterFromCameraZ < 0;
                            }
                            if (!drawFaceNX && !drawFacePX && !drawFaceNY && !drawFacePY && !drawFaceNZ && !drawFacePZ) continue;
                            transparentCubesToRender.add(new TransparentCubeInfo(currentBlockX, currentBlockY, currentBlockZ, distanceSquaredToCameraFromBlock, drawFaceNX, drawFacePX, drawFaceNY, drawFacePY, drawFaceNZ, drawFacePZ));
                        }
                    }
                }
            }
            transparentCubesToRender.sort((c1, c2) -> Double.compare(c2.distanceSquaredToCamera, c1.distanceSquaredToCamera));
            for (TransparentCubeInfo cubeInfo : transparentCubesToRender) {
                GL11.glPushMatrix();
                GL11.glTranslated(cubeInfo.x, cubeInfo.y, cubeInfo.z);
                GL11.glColor4f(1F, 0.0F, 0.0F, 0.5F);

                drawTrueCubeFaces(cubeInfo.drawFaceNX, cubeInfo.drawFacePX, cubeInfo.drawFaceNY, cubeInfo.drawFacePY, cubeInfo.drawFaceNZ, cubeInfo.drawFacePZ);
                GL11.glPopMatrix();
            }
           // GL11.glEndList();
        }

       // GL11.glCallList(sphereDisplayListID);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static boolean isBlockInCylinder(float relX, float relY, float relZ, double radiusSquared) {
        double dx = relX - 0.5;
        double dz = relZ - 0.5;
        double distanceSquared = (dx * dx) + (dz * dz);

        return distanceSquared <= radiusSquared ;
    }

    public static void renderCylinder(World world, float radius, int centerX, int centerY, int centerZ, float height, int hollowOpen, int thickness) {
        if (world == null || radius <= 0 || height <= 0) {
            return;
        }
        boolean hollow = hollowOpen == 0;
        boolean open = hollowOpen == 1;

        if (sphereDisplayListID == 0) {
            /*sphereDisplayListID = GL11.glGenLists(1);
            if (sphereDisplayListID == 0) {
                System.err.println("Failed to generate OpenGL display list!");
                return;
            }

            GL11.glNewList(sphereDisplayListID, GL11.GL_COMPILE);*/
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase renderViewEntity = mc.renderViewEntity;
            double cameraX = renderViewEntity.posX;
            double cameraY = renderViewEntity.posY + renderViewEntity.getEyeHeight();
            double cameraZ = renderViewEntity.posZ;

            List<TransparentCubeInfo> transparentCubesToRender = new ArrayList<>();
            radius += 0.5f;
            height/=2;
            height -= 0.5f;
            double radiusSquared = (double) radius * radius;
            float innerRadius = radius - thickness;
            double innerRadiusSquared = (double) innerRadius * innerRadius;
            double distCenterFromCameraX = (centerX + 0.5) - cameraX;
            double distCenterFromCameraY = (centerY + 0.5) - cameraY;
            double distCenterFromCameraZ = (centerZ + 0.5) - cameraZ;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_CULL_FACE);

            GL11.glCullFace(GL11.GL_FRONT);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            for (float x = -radius; x <= radius; x++) {
                for (float z = -radius; z <= radius; z++) {
                    for (float y = -height; y <= height; y++) {
                        double dx = x - 0.5;
                        double dz = z - 0.5;
                        double distanceSquared = (dx * dx) + (dz * dz);
                        boolean isTopOrBottomLayer = ((y >= -height && y<-height+thickness) || (y <= height && y>height-thickness));
                        if (distanceSquared < radiusSquared && (distanceSquared >= innerRadiusSquared||isTopOrBottomLayer||((!hollow&&!open)&&player.isUsingSpecialKey()))) {
                            if (isTopOrBottomLayer&&!(distanceSquared < radiusSquared && distanceSquared >= innerRadiusSquared)&&open&&player.isUsingSpecialKey())continue;
                            int currentBlockX = MathHelper.floor_float(centerX + x);
                            int currentBlockZ = MathHelper.floor_float(centerZ + z);
                            int currentBlockY = MathHelper.floor_float(centerY + y);
                            if (player.isUsingSpecialKey() && world.getBlockId(currentBlockX, currentBlockY, currentBlockZ) == 0)
                                continue;
                            double distFromCameraX = currentBlockX + 0.5 - cameraX;
                            double distFromCameraY = currentBlockY + 0.5 - cameraY;
                            double distFromCameraZ = currentBlockZ + 0.5 - cameraZ;
                            double distanceSquaredToCameraFromBlock = (distFromCameraX * distFromCameraX) + (distFromCameraY * distFromCameraY) + (distFromCameraZ * distFromCameraZ);
                            boolean drawFaceNX = true;
                            boolean drawFacePX = true;
                            boolean drawFaceNY = y==-height&& distCenterFromCameraY > 0;
                            boolean drawFacePY = y==height&& distCenterFromCameraY < 0;
                            boolean drawFaceNZ = true;
                            boolean drawFacePZ = true;
                            if (!player.isUsingSpecialKey()) {
                                drawFaceNX = !isBlockInCylinder(x - 1, y, z, radiusSquared)&& distCenterFromCameraX > 0;
                                drawFacePX = !isBlockInCylinder(x + 1, y, z, radiusSquared)&& distCenterFromCameraX < 0;
                                drawFaceNZ = !isBlockInCylinder(x, y, z - 1, radiusSquared)&& distCenterFromCameraZ > 0;
                                drawFacePZ = !isBlockInCylinder(x, y, z + 1, radiusSquared)&& distCenterFromCameraZ < 0;

                            }
                            if (!drawFaceNX && !drawFacePX && !drawFaceNY && !drawFacePY && !drawFaceNZ && !drawFacePZ) continue;
                            transparentCubesToRender.add(new TransparentCubeInfo(currentBlockX, currentBlockY, currentBlockZ, distanceSquaredToCameraFromBlock, drawFaceNX, drawFacePX, drawFaceNY, drawFacePY, drawFaceNZ, drawFacePZ));
                        }
                    }
                }
            }
            transparentCubesToRender.sort((c1, c2) -> Double.compare(c2.distanceSquaredToCamera, c1.distanceSquaredToCamera));
            for (TransparentCubeInfo cubeInfo : transparentCubesToRender) {
                GL11.glPushMatrix();
                GL11.glTranslated(cubeInfo.x, cubeInfo.y, cubeInfo.z);
                GL11.glColor4f(1F, 0.0F, 0.0F, 0.5F);

                drawTrueCubeFaces(cubeInfo.drawFaceNX, cubeInfo.drawFacePX, cubeInfo.drawFaceNY, cubeInfo.drawFacePY, cubeInfo.drawFaceNZ, cubeInfo.drawFacePZ);
                GL11.glPopMatrix();
            }
            // GL11.glEndList();
        }

        // GL11.glCallList(sphereDisplayListID);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static boolean isBlockInCube(int x, int y, int z, int startX, int startY, int startZ, int sizeX, int sizeY, int sizeZ) {
        return x>=startX && x<startX+sizeX && y>=startY && y<startY+sizeY && z>=startZ && z<startZ+sizeZ ;
    }

    public static void renderCube(World world,int centerX, int centerY, int centerZ, int sizeX, int sizeY, int sizeZ, int hollowOpen, int thickness) {
        if (world == null) {
            return;
        }
        boolean hollow = hollowOpen == 0;
        boolean open = hollowOpen == 1;
        if (sphereDisplayListID == 0) {
            /*sphereDisplayListID = GL11.glGenLists(1);
            if (sphereDisplayListID == 0) {
                System.err.println("Failed to generate OpenGL display list!");
                return;
            }

            GL11.glNewList(sphereDisplayListID, GL11.GL_COMPILE);*/
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase renderViewEntity = mc.renderViewEntity;
            double cameraX = renderViewEntity.posX;
            double cameraY = renderViewEntity.posY + renderViewEntity.getEyeHeight();
            double cameraZ = renderViewEntity.posZ;

            int actualStartX = centerX - (sizeX / 2);
            int actualStartY = centerY - (sizeY / 2);
            int actualStartZ = centerZ - (sizeZ / 2);
            int innerStartX = actualStartX + thickness;
            int innerStartY = actualStartY + thickness;
            int innerStartZ = actualStartZ + thickness;
            int innerEndX = (actualStartX + sizeX) - thickness;
            int innerEndY = (actualStartY + sizeY) - thickness;
            int innerEndZ = (actualStartZ + sizeZ) - thickness;


            List<TransparentCubeInfo> transparentCubesToRender = new ArrayList<>();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glFrontFace(GL11.GL_CCW);
            GL11.glCullFace(GL11.GL_FRONT);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            for (int x = actualStartX; x < actualStartX + sizeX; x++) {
                for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                    for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                        boolean isOutsideInnerSpace = (x < innerStartX || x >= innerEndX) ||
                                (y < innerStartY || y >= innerEndY) ||
                                (z < innerStartZ || z >= innerEndZ);
                        boolean isTopBottom = isOutsideInnerSpace&&x>innerStartX&&x<=innerEndX&&z>innerStartZ&&z<=innerEndZ;
                        if ((open||hollow)&&player.isUsingSpecialKey()&&!isOutsideInnerSpace)continue;
                        if (open&&player.isUsingSpecialKey()&&isTopBottom)continue;
                        if (player.isUsingSpecialKey() && world.getBlockId(x, y, z) == 0) continue;
                        double distFromCameraX = x + 0.5 - cameraX;
                        double distFromCameraY = y + 0.5 - cameraY;
                        double distFromCameraZ = z + 0.5 - cameraZ;
                        double distanceSquaredToCameraFromBlock = (distFromCameraX * distFromCameraX) + (distFromCameraY * distFromCameraY) + (distFromCameraZ * distFromCameraZ);
                        boolean drawFaceNX = true;
                        boolean drawFacePX = true;
                        boolean drawFaceNY = true;
                        boolean drawFacePY = true;
                        boolean drawFaceNZ = true;
                        boolean drawFacePZ = true;

                        if (!player.isUsingSpecialKey()) {
                            drawFaceNX = !isBlockInCube(x - 1, y, z, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                            drawFacePX = !isBlockInCube(x + 1, y, z, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                            drawFaceNY = !isBlockInCube(x, y - 1, z, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                            drawFacePY = !isBlockInCube(x, y + 1, z, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                            drawFaceNZ = !isBlockInCube(x, y, z - 1, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                            drawFacePZ = !isBlockInCube(x, y, z + 1, actualStartX,actualStartY,actualStartZ,sizeX,sizeY,sizeZ);
                        }
                        if (!drawFaceNX && !drawFacePX && !drawFaceNY && !drawFacePY && !drawFaceNZ && !drawFacePZ)
                            continue;
                        transparentCubesToRender.add(new TransparentCubeInfo(x, y, z, distanceSquaredToCameraFromBlock, drawFaceNX, drawFacePX, drawFaceNY, drawFacePY, drawFaceNZ, drawFacePZ));
                    }

                }
            }
            transparentCubesToRender.sort((c1, c2) -> Double.compare(c2.distanceSquaredToCamera, c1.distanceSquaredToCamera));
            for (TransparentCubeInfo cubeInfo : transparentCubesToRender) {
                GL11.glPushMatrix();
                GL11.glTranslated(cubeInfo.x, cubeInfo.y, cubeInfo.z);
                GL11.glColor4f(1F, 0.0F, 0.0F, 0.5F);

                drawTrueCubeFaces(cubeInfo.drawFaceNX, cubeInfo.drawFacePX, cubeInfo.drawFaceNY, cubeInfo.drawFacePY, cubeInfo.drawFaceNZ, cubeInfo.drawFacePZ);
                GL11.glPopMatrix();
            }
            // GL11.glEndList();
        }

        // GL11.glCallList(sphereDisplayListID);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static boolean isBlockInPyramid(int x, int y, int z, int actualStartX, int baseY, int actualStartZ, int sizeX, int sizeY, int sizeZ) {
        int ratioWH = MathHelper.floor_float(((float) Math.min(sizeX, sizeZ) / (float) sizeY) * ((float) y - baseY) / 2);
        int startX = actualStartX + ratioWH;
        int startZ = actualStartZ + ratioWH;
        int sizedX = sizeX - ratioWH*2;
        int sizedZ = sizeZ - ratioWH*2;
        int endX = startX+sizedX;
        int endZ = startZ+sizedZ;

        return x>=startX&&x<endX&&z>=startZ&&z<endZ&&y>=baseY&&y<sizeY+baseY;
    }


    public static void renderPyramid(World world, int centerX, int baseY, int centerZ, int sizeX, int sizeY, int sizeZ, int hollowOpen, int thickness) {
        if (world == null || sizeX <= 0 || sizeZ <= 0) {
            System.err.println("Cannot generate pyramid: Invalid world or dimensions.");
            return;
        }
        boolean hollow = hollowOpen == 0;
        if (sphereDisplayListID == 0) {
            /*sphereDisplayListID = GL11.glGenLists(1);
            if (sphereDisplayListID == 0) {
                System.err.println("Failed to generate OpenGL display list!");
                return;
            }

            GL11.glNewList(sphereDisplayListID, GL11.GL_COMPILE);*/
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase renderViewEntity = mc.renderViewEntity;
            double cameraX = renderViewEntity.posX;
            double cameraY = renderViewEntity.posY + renderViewEntity.getEyeHeight();
            double cameraZ = renderViewEntity.posZ;

            int actualStartX = centerX - (sizeX / 2);
            int actualStartZ = centerZ - (sizeZ / 2);
            int innerStartY = baseY + thickness;
            int innerEndY = (baseY + sizeY) - thickness;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glFrontFace(GL11.GL_CCW);
            GL11.glCullFace(GL11.GL_FRONT);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            List<TransparentCubeInfo> transparentCubesToRender = new ArrayList<>();
            if (sizeY>0) {
                for (int y = baseY; y < baseY + sizeY; y++) {
                    renderPyramidSec(world, baseY, sizeX, sizeY, sizeZ, thickness, y, actualStartX, actualStartZ, innerStartY, innerEndY, hollow, player, cameraX, cameraY, cameraZ, transparentCubesToRender);
                }
            }else for (int y = baseY; y > baseY + sizeY; y--) {
                renderPyramidSec(world, baseY, sizeX, sizeY, sizeZ, thickness, y, actualStartX, actualStartZ, innerStartY, innerEndY, hollow, player, cameraX, cameraY, cameraZ, transparentCubesToRender);
            }


            transparentCubesToRender.sort((c1, c2) -> Double.compare(c2.distanceSquaredToCamera, c1.distanceSquaredToCamera));
            for (TransparentCubeInfo cubeInfo : transparentCubesToRender) {
                GL11.glPushMatrix();
                GL11.glTranslated(cubeInfo.x, cubeInfo.y, cubeInfo.z);
                GL11.glColor4f(1F, 0.0F, 0.0F, 0.5F);

                drawTrueCubeFaces(cubeInfo.drawFaceNX, cubeInfo.drawFacePX, cubeInfo.drawFaceNY, cubeInfo.drawFacePY, cubeInfo.drawFaceNZ, cubeInfo.drawFacePZ);
                GL11.glPopMatrix();
            }
            // GL11.glEndList();
        }

        // GL11.glCallList(sphereDisplayListID);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void renderPyramidSec(World world, int baseY, int sizeX, int sizeY, int sizeZ, int thickness, int y, int actualStartX, int actualStartZ, int innerStartY, int innerEndY, boolean hollow, EntityClientPlayerMP player, double cameraX, double cameraY, double cameraZ, List<TransparentCubeInfo> transparentCubesToRender) {
        int ratioWH = MathHelper.floor_float(((float)Math.min(sizeX, sizeZ) /(float) sizeY) * ((float) y - baseY)/2);
        int startX = actualStartX + ratioWH;
        int startZ = actualStartZ + ratioWH;
        int sizedX = sizeX - ratioWH*2;
        int sizedZ = sizeZ - ratioWH*2;
        int innerStartX = startX + thickness;
        int innerStartZ = startZ + thickness;
        int innerEndX = (startX + sizedX) - thickness;
        int innerEndZ = (startZ + sizedZ) - thickness;
        for (int x = startX; x < startX + sizedX; x++) {
            for (int z = startZ; z < startZ + sizedZ; z++) {
                boolean isOutsideInnerSpace = (x < innerStartX || x >= innerEndX) ||
                        (y < innerStartY || y >= innerEndY) ||
                        (z < innerStartZ || z >= innerEndZ);
                if (hollow && player.isUsingSpecialKey()&&!isOutsideInnerSpace)continue;
                if (player.isUsingSpecialKey() && world.getBlockId(x, y, z) == 0) continue;

                double distFromCameraX = x + 0.5 - cameraX;
                double distFromCameraY = y + 0.5 - cameraY;
                double distFromCameraZ = z + 0.5 - cameraZ;
                double distanceSquaredToCameraFromBlock = (distFromCameraX * distFromCameraX) + (distFromCameraY * distFromCameraY) + (distFromCameraZ * distFromCameraZ);
                boolean drawFaceNX = true;
                boolean drawFacePX = true;
                boolean drawFaceNY = true;
                boolean drawFacePY = true;
                boolean drawFaceNZ = true;
                boolean drawFacePZ = true;

                if (!player.isUsingSpecialKey()) {
                    drawFaceNX = !isBlockInPyramid(x - 1, y, z, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                    drawFacePX = !isBlockInPyramid(x + 1, y, z, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                    drawFaceNY = !isBlockInPyramid(x, y - 1, z, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                    drawFacePY = !isBlockInPyramid(x, y + 1, z, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                    drawFaceNZ = !isBlockInPyramid(x, y, z - 1, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                    drawFacePZ = !isBlockInPyramid(x, y, z + 1, actualStartX, baseY, actualStartZ, sizeX, sizeY, sizeZ);
                }
                if (!drawFaceNX && !drawFacePX && !drawFaceNY && !drawFacePY && !drawFaceNZ && !drawFacePZ)
                    continue;
                transparentCubesToRender.add(new TransparentCubeInfo(x, y, z, distanceSquaredToCameraFromBlock, drawFaceNX, drawFacePX, drawFaceNY, drawFacePY, drawFaceNZ, drawFacePZ));
            }
        }
    }


    public static void deleteSphereDisplayList() {
        if (sphereDisplayListID != 0) {
            GL11.glDeleteLists(sphereDisplayListID, 1);
            sphereDisplayListID = 0;
        }
    }

}
