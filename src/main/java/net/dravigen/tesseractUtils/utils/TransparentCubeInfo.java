package net.dravigen.tesseractUtils.utils;

public class TransparentCubeInfo {
    public int x, y, z;
    public double distanceSquaredToCamera;

    public boolean drawFaceNX, drawFacePX; // Negative X, Positive X
    public boolean drawFaceNY, drawFacePY; // Negative Y, Positive Y
    public boolean drawFaceNZ, drawFacePZ; // Negative Z, Positive Z

    public TransparentCubeInfo(int x, int y, int z, double distSq,
                               boolean nx, boolean px, boolean ny, boolean py, boolean nz, boolean pz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distanceSquaredToCamera = distSq;
        this.drawFaceNX = nx;
        this.drawFacePX = px;
        this.drawFaceNY = ny;
        this.drawFacePY = py;
        this.drawFaceNZ = nz;
        this.drawFacePZ = pz;
    }
}
