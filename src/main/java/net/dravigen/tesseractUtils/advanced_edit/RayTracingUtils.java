package net.dravigen.tesseractUtils.advanced_edit;

import net.minecraft.src.Vec3;

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
}
