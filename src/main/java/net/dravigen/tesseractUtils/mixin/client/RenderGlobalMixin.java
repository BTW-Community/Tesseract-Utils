package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.utils.RenderUtils;
import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.enums.EnumShape;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.*;
import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.*;
import static net.dravigen.tesseractUtils.enums.EnumBuildMode.*;
import static net.dravigen.tesseractUtils.utils.RayTracingUtils.*;
import static net.dravigen.tesseractUtils.utils.RenderUtils.*;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Shadow
    private Minecraft mc;

    @Inject(method = "renderEntities", at = @At("HEAD"))
    private void renderDisplay(Vec3 cameraPosition, ICamera camera, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player.capabilities.isCreativeMode && PacketUtils.isPlayerOPClient) {
            partialTick = partialTicks;
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
                renderTransparentCube(block1.xCoord, block1.yCoord, block1.zCoord, 1F, 0.0F, 0.0F, 0.25F);
            }
            if (isBlock2Selected) {
                renderTransparentCube(block2.xCoord, block2.yCoord, block2.zCoord, 0.0F, 0.0F, 1.0F, 0.25F);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            ItemStack heldItem = player.getHeldItem();

            boolean shapeMode = EnumConfig.SHAPE_DISPLAY.getBoolValue() && (currentBuildingMode == SHAPE_MODE.getIndex() || ((heldItem != null && heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams")) && heldItem.getTagCompound().getCompoundTag("BuildingParams").getString("shape")!=null && currentBuildingMode == 8));
            boolean needHitHighlight = shapeMode||((heldItem != null && (heldItem.itemID == Item.axeWood.itemID || heldItem.itemID == Item.shovelWood.itemID)) || currentBuildingMode != 8);
            boolean deleteMode = (heldItem != null && heldItem.itemID == Item.swordWood.itemID) || currentBuildingMode == DELETE_MODE.getIndex();
            try {
                /// render hit block
                if (needHitHighlight) {
                    Vec3 var3 = player.getPosition(partialTicks);
                    Vec3 var4 = player.getLook(partialTicks);
                    Vec3 var5 = var3.addVector(var4.xCoord * 256, var4.yCoord * 256, var4.zCoord * 256);
                    MovingObjectPosition block = player.worldObj.clip(var3, var5);
                    if (block != null) {
                        GL11.glLineWidth(3f);
                        GL11.glColor4f(0.2F, 1.0F, 0.2F, 1);
                        RenderUtils.drawWireframeBox(block.blockX - 0.01, block.blockY - 0.01, block.blockZ - 0.01, block.blockX + 1.01, block.blockY + 1.01, block.blockZ + 1.01);
                    }
                }
                /// render shape display
                if (shapeMode) {
                    Vec3 var3 = player.getPosition(partialTicks);
                    Vec3 var4 = player.getLook(partialTicks);
                    Vec3 var5 = var3.addVector(var4.xCoord * 256, var4.yCoord * 256, var4.zCoord * 256);
                    MovingObjectPosition block = player.worldObj.clip(var3, var5);
                    if (block != null) {
                        boolean replace = player.isUsingSpecialKey();
                        String shape = EnumShape.SHAPE.getShape();
                        String[] parameters = new String[]{};
                        String actionType = EnumShape.VOLUME.getVolume();
                        switch (shape.toLowerCase()) {
                            case "sphere" ->
                                    parameters = new String[]{String.valueOf(EnumShape.RADIUS.getIntValue()), String.valueOf(EnumShape.THICKNESS.getIntValue())};
                            case "cylinder" ->
                                    parameters = new String[]{String.valueOf(EnumShape.RADIUS.getIntValue()), String.valueOf(EnumShape.HEIGHT.getIntValue()), String.valueOf(EnumShape.THICKNESS.getIntValue())};
                            case "cube", "pyramid" ->
                                    parameters = new String[]{String.valueOf(EnumShape.SIZE_X.getIntValue()), String.valueOf(EnumShape.SIZE_Y.getIntValue()), String.valueOf(EnumShape.SIZE_Z.getIntValue()), String.valueOf(EnumShape.THICKNESS.getIntValue())};

                        }
                        int radius = 0;
                        if (currentBuildingMode != SHAPE_MODE.getIndex() && heldItem != null && heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams")) {
                            NBTTagCompound buildingParamsNBT = heldItem.getTagCompound().getCompoundTag("BuildingParams");
                            shape = buildingParamsNBT.getString("shape");
                            parameters = buildingParamsNBT.getString("parameters").split(":");
                            actionType = buildingParamsNBT.getString("volume");
                        }

                        int hollowOpen = actionType.equalsIgnoreCase("hollow") ? 0 : actionType.equalsIgnoreCase("open") ? 1 : 2;
                        radius = Integer.parseInt(parameters[0]);
                        int thickness = 1;
                        switch (shape.toLowerCase()) {
                            case "sphere" -> {
                                int y = block.blockY;
                                thickness = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                                if (player.isSneaking()) y = y + radius;
                                renderSphere(player.worldObj, radius, block.blockX, y, block.blockZ, hollowOpen, thickness);
                            }
                            case "cylinder" -> {
                                int height = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                                thickness = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                                int y = block.blockY;
                                if (player.isSneaking()) y = y + height / 2;
                                renderCylinder(player.worldObj, radius, block.blockX, y, block.blockZ, height, hollowOpen, thickness);
                            }
                            case "cube" -> {
                                int sizeX = Integer.parseInt(parameters[0]);
                                int sizeY = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                                int sizeZ = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                                thickness = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                                int y = block.blockY;
                                if (player.isSneaking()) y = y + sizeY / 2;
                                renderCube(player.worldObj, block.blockX, y, block.blockZ, sizeX, sizeY, sizeZ, hollowOpen, thickness);
                            }
                            case "pyramid" -> {
                                int sizeX = Integer.parseInt(parameters[0]);
                                int sizeY = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                                int sizeZ = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                                thickness = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                                int y = block.blockY;
                                renderPyramid(player.worldObj, block.blockX, y, block.blockZ, sizeX, sizeY, sizeZ, hollowOpen, thickness);

                            }
                        }
                    }//else RenderUtils.deleteSphereDisplayList();
                }
                /// render delete entity box
                if (deleteMode) {
                    Entity entityHit = getEntityFromRayTrace(player, 256, partialTicks);
                    if (entityHit != null) {
                        GL11.glLineWidth(3f);
                        GL11.glColor4f(1.0F, 0.0F, 0.0F, 1);
                        RenderUtils.drawWireframeBox(entityHit.boundingBox.minX, entityHit.boundingBox.minY, entityHit.boundingBox.minZ, entityHit.boundingBox.maxX, entityHit.boundingBox.maxY, entityHit.boundingBox.maxZ);
                        if (player.isUsingSpecialKey()) {
                            BlockFromRayTrace block = getBlockFromRayTrace(player);
                            List<Entity> entityList = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(entityHit.posX - 2, entityHit.posY - 2, entityHit.posZ - 2, entityHit.posX + 2, entityHit.posY + 2, entityHit.posZ + 2));
                            for (Entity entity : entityList) {
                                if (entity != entityHit) {
                                    GL11.glLineWidth(3f);
                                    GL11.glColor4f(1.0F, 1.0F, 0.0F, 1);
                                    RenderUtils.drawWireframeBox(entity.boundingBox.minX, entity.boundingBox.minY, entity.boundingBox.minZ, entity.boundingBox.maxX, entity.boundingBox.maxY, entity.boundingBox.maxZ);
                                }
                            }
                        }
                    } else if (player.isUsingSpecialKey()) {
                        BlockFromRayTrace block = getBlockFromRayTrace(player);
                        List<Entity> entityList = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(block.x() - 2, block.y() - 2, block.z() - 2, block.x() + 2, block.y() + 2, block.z() + 2));
                        for (Entity entity : entityList) {
                            GL11.glLineWidth(3f);
                            GL11.glColor4f(1.0F, 1.0F, 0.0F, 1);
                            RenderUtils.drawWireframeBox(entity.boundingBox.minX, entity.boundingBox.minY, entity.boundingBox.minZ, entity.boundingBox.maxX, entity.boundingBox.maxY, entity.boundingBox.maxZ);
                        }
                    }
                }
            } catch (Exception e) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cSomething went wrong but idk why"));
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            if (EnumConfig.SELECTION_DISPLAY.getBoolValue()&&currentSelectionState == SelectionState.TWO_SELECTED) {
                double minX = Math.min(block1.xCoord, block2.xCoord) - 0.05;
                double minY = Math.min(block1.yCoord, block2.yCoord) - 0.05;
                double minZ = Math.min(block1.zCoord, block2.zCoord) - 0.05;
                double maxX = Math.max(block1.xCoord, block2.xCoord) + 1.05;
                double maxY = Math.max(block1.yCoord, block2.yCoord) + 1.05;
                double maxZ = Math.max(block1.zCoord, block2.zCoord) + 1.05;
                HitResult hitResult;
                Vec3 rayOriginWorld = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                boolean inverted = false;
                Vec3 rayDirection = player.getLook(partialTicks);
                if (player.isUsingSpecialKey()) {
                    rayOriginWorld = Vec3.createVectorHelper(0, 0, 0);
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
                if (!getEnumFromIndex(currentBuildingMode).getCanSelect()) hitResult.hitFace = -1;
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_CULL_FACE);
                float sin = ((float) Math.sin(this.mc.thePlayer.ticksExisted / 20f) + 0.25f) / 1.25f;
                sin = sin < 0 ? 0 : sin;

                GL11.glLineWidth(0.75F);
                GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.1001f + (0.3f - 0.1001f) * sin);
                drawWireframeBoxConditions(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, false);

                GL11.glLineWidth(3F);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0F, 1.0F, 0.0F, 1f);
                drawWireframeBoxConditions(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, false);
                for (int i = 0; i < 6; i++) {
                    if (i != hitResult.hitFace) {

                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glColor4f(0.47F, 0.77F, 1.0F, 0.1001f);
                        drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, i, inverted, hitResult.hitFace);

                        sin = ((float) -Math.sin(this.mc.thePlayer.ticksExisted / 20f) + 0.25f) / 1.25f;
                        sin = sin < 0 ? 0 : sin;

                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glColor4f(0.47F, 0.77F, 1.0F, 0.15f + (0.1f) * sin);
                        drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, i, inverted, hitResult.hitFace);
                    }
                }
                if (hitResult.hit && getEnumFromIndex(currentBuildingMode).getCanSelect()) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5f);
                    drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, inverted, hitResult.hitFace);

                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.101f);
                    drawHighlightFace(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, inverted, hitResult.hitFace);
                    GL11.glColor4f(1.0F, 1.0F, 0.0F, 1f);
                    drawWireframeBoxConditions(minX, minY, minZ, maxX, maxY, maxZ, hitResult.hitFace, true);
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
    }
}
