package net.dravigen.tesseractUtils.utils;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RayTracingUtils {

    /**
     * Represents the result of a ray-box intersection test.
     */
    public static class HitResult {
        public boolean hit; // True if the ray hit the box
        public double distance; // Distance from ray origin to hit point
        public int hitFace;     // 0: NX, 1: PX, 2: NY, 3: PY, 4: NZ, 5: PZ

        // Face constants (consistent with Minecraft's EnumFacing side values often)
        public static final int SIDE_W = 0; // -X (West)
        public static final int SIDE_E = 1; // +X (East)
        public static final int SIDE_D = 2; // -Y (Down)
        public static final int SIDE_U = 3; // +Y (Up)
        public static final int SIDE_N = 4; // -Z (North)
        public static final int SIDE_S = 5; // +Z (South)

        public HitResult(boolean hit) {
            this.hit = hit;
            this.distance = Double.POSITIVE_INFINITY;
            this.hitFace = -1; // No face hit
        }

        public HitResult(double distance, int hitFace) {
            this.hit = true;
            this.distance = distance;
            this.hitFace = hitFace;
        }
    }

    /**
     * Performs a ray-Axis-Aligned Bounding Box (AABB) intersection test.
     * Implements the Slab Method.
     *
     * @param rayOrigin   The starting point of the ray.
     * @param rayDirection The direction vector of the ray (should be normalized).
     * @param boxMinX     Minimum X coordinate of the box.
     * @param boxMinY     Minimum Y coordinate of the box.
     * @param boxMinZ     Minimum Z coordinate of the box.
     * @param boxMaxX     Maximum X coordinate of the box.
     * @param boxMaxY     Maximum Y coordinate of the box.
     * @param boxMaxZ     Maximum Z coordinate of the box.
     * @return A HitResult object indicating if a hit occurred, distance, and hit face.
     */
    public static HitResult intersectAABB(Vec3 rayOrigin, Vec3 rayDirection,
                                          double boxMinX, double boxMinY, double boxMinZ,
                                          double boxMaxX, double boxMaxY, double boxMaxZ) {

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;
        int hitFace = -1; // Stores the face that caused tMin to be updated

        // Check X-axis slab
        if (Math.abs(rayDirection.xCoord) < 1e-6) { // Ray is parallel to XZ planes
            if (rayOrigin.xCoord < boxMinX || rayOrigin.xCoord > boxMaxX) {
                return new HitResult(false); // Ray is outside the X slab
            }
        } else {
            double t1 = (boxMinX - rayOrigin.xCoord) / rayDirection.xCoord;
            double t2 = (boxMaxX - rayOrigin.xCoord) / rayDirection.xCoord;

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; } // Swap if t1 > t2

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            // Determine which face caused tMin
            if (tMin == t1) { // If tMin came from t1 (minX plane)
                hitFace = (rayDirection.xCoord > 0) ? HitResult.SIDE_W : HitResult.SIDE_E;
            } else { // If tMin came from t2 (maxX plane)
                hitFace = (rayDirection.xCoord > 0) ? HitResult.SIDE_E : HitResult.SIDE_W;
            }

            if (tMin > tMax || tMax < 0) return new HitResult(false); // No intersection
        }

        // Check Y-axis slab
        if (Math.abs(rayDirection.yCoord) < 1e-6) { // Ray is parallel to XZ planes
            if (rayOrigin.yCoord < boxMinY || rayOrigin.yCoord > boxMaxY) {
                return new HitResult(false);
            }
        } else {
            double t1 = (boxMinY - rayOrigin.yCoord) / rayDirection.yCoord;
            double t2 = (boxMaxY - rayOrigin.yCoord) / rayDirection.yCoord;

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }

            // Important: if current tMin comes from a plane further than this t2, no intersection
            if (tMin > t2 || t1 > tMax) return new HitResult(false);

            if (tMin < t1) { // If original tMin was before this slab's entry
                tMin = t1;
                hitFace = (rayDirection.yCoord > 0) ? HitResult.SIDE_D : HitResult.SIDE_U;
            }

            tMax = Math.min(tMax, t2);

            if (tMin > tMax || tMax < 0) return new HitResult(false);
        }

        // Check Z-axis slab
        if (Math.abs(rayDirection.zCoord) < 1e-6) { // Ray is parallel to XY planes
            if (rayOrigin.zCoord < boxMinZ || rayOrigin.zCoord > boxMaxZ) {
                return new HitResult(false);
            }
        } else {
            double t1 = (boxMinZ - rayOrigin.zCoord) / rayDirection.zCoord;
            double t2 = (boxMaxZ - rayOrigin.zCoord) / rayDirection.zCoord;

            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }

            // Important: if current tMin comes from a plane further than this t2, no intersection
            if (tMin > t2 || t1 > tMax) return new HitResult(false);

            if (tMin < t1) { // If original tMin was before this slab's entry
                tMin = t1;
                hitFace = (rayDirection.zCoord > 0) ? HitResult.SIDE_N : HitResult.SIDE_S;
            }

            tMax = Math.min(tMax, t2);

            if (tMin > tMax || tMax < 0) return new HitResult(false);
        }

        // If we made it here, there's an intersection
        return new HitResult(tMin, hitFace);
    }
    /**
     * Performs a ray trace from an entity's eye position to find the closest hit entity.
     *
     * @param sourceEntity The entity from which to cast the ray (e.g., the player).
     * @param maxDistance The maximum distance for the ray trace.
     * @param partialTicks For smooth interpolation (use 1.0F for current position/look).
     * @return The closest Entity hit by the ray, or null if no entity is found.
     */

    public static Entity getEntityFromRayTrace(EntityPlayer sourceEntity, double maxDistance, float partialTicks) {
        if (sourceEntity == null || sourceEntity.worldObj == null) {
            return null;
        }

        World world = sourceEntity.worldObj;
        Vec3 startVec = world.getWorldVec3Pool().getVecFromPool(sourceEntity.posX, sourceEntity.posY + sourceEntity.getEyeHeight(), sourceEntity.posZ);
        Vec3 lookVec = sourceEntity.getLook(partialTicks);
        Vec3 endVec = startVec.addVector(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance);

        MovingObjectPosition blockHit = world.rayTraceBlocks_do_do(startVec, endVec, false,true);

        if (blockHit != null) {endVec = world.getWorldVec3Pool().getVecFromPool(blockHit.hitVec.xCoord, blockHit.hitVec.yCoord, blockHit.hitVec.zCoord);}

        double expandAmount = 1.0D;
        AxisAlignedBB searchBox = sourceEntity.boundingBox.expand(maxDistance, maxDistance, maxDistance).expand(expandAmount, expandAmount, expandAmount);

        List<Entity> possibleEntities = world.getEntitiesWithinAABBExcludingEntity(sourceEntity, searchBox);

        Entity foundEntity = null;
        double closestDistance = maxDistance;
        for (Entity currentEntity : possibleEntities) {
            if (currentEntity instanceof EntityPlayer || !(currentEntity instanceof EntityLivingBase)) {
                continue;
            }
            AxisAlignedBB entityBoundingBox = currentEntity.boundingBox.expand(
                    currentEntity.getCollisionBorderSize(),
                    currentEntity.getCollisionBorderSize(),
                    currentEntity.getCollisionBorderSize()
            );

            MovingObjectPosition entityIntercept = entityBoundingBox.calculateIntercept(startVec, endVec);

            if (entityIntercept != null) {
                double distanceToIntercept = startVec.distanceTo(entityIntercept.hitVec);

                if (distanceToIntercept < closestDistance) {
                    foundEntity = currentEntity;
                    closestDistance = distanceToIntercept;
                }
            }
        }
        return foundEntity;
    }
    public static @NotNull RayTracingUtils.BlockFromRayTrace getBlockFromRayTrace(EntityPlayer player) {
        Vec3 var3 = player.getPosition(TesseractUtilsAddon.partialTick);
        Vec3 var4 = player.getLook(TesseractUtilsAddon.partialTick);
        Vec3 var5 = var3.addVector(var4.xCoord * 256, var4.yCoord * 256, var4.zCoord * 256);
        MovingObjectPosition block = player.worldObj.clip(var3, var5);
        int sideHit = 0;
        int x = 0;
        int y = 0;
        int z = 0;
        Vec3 hitVec = Vec3.createVectorHelper(0,0,0);
        if (block != null) {
            x = block.blockX;
            y = block.blockY;
            z = block.blockZ;
            sideHit = block.sideHit;
            hitVec = block.hitVec;
        }
        return new BlockFromRayTrace(block, sideHit, x, y, z, hitVec);
    }

    public record BlockFromRayTrace(MovingObjectPosition block, int sideHit, int x, int y, int z, Vec3 hitVec3) {
    }
}
