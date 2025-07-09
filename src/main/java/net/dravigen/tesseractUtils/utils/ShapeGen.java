package net.dravigen.tesseractUtils.utils;

import net.dravigen.tesseractUtils.command.CommandWorldEdit;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.utils.ListsUtils.*;
import static net.dravigen.tesseractUtils.utils.ListsUtils.getBlockInfo;
import static net.dravigen.tesseractUtils.utils.ListsUtils.getRandomBlockFromOdds;

public class ShapeGen {

    public static List<SavedBlock> generateSphere(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, float radius, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0) {
            System.err.println("Cannot generate sphere: Invalid world or radius.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                for (float z = -radius; z <= radius; z++) {
                    double dx = x-0.5;
                    double dy = y-0.5;
                    double dz = z-0.5;
                    double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
                    if (distanceSquared <= radiusSquared) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockY = MathHelper.floor_float (centerY + y);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        if (replace&&world.getBlockId(currentBlockX, currentBlockY, currentBlockZ)==0)continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowSphere(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, float radius, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || thickness <= 0 || thickness > radius) {
            System.err.println("Cannot generate hollow sphere: Invalid world, radius, or thickness.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                for (float z = -radius; z <= radius; z++) {
                    double dx = x - 0.5;
                    double dy = y - 0.5;
                    double dz = z - 0.5;
                    double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
                    if (distanceSquared < radiusSquared && distanceSquared >= innerRadiusSquared) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockY = MathHelper.floor_float (centerY + y);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        if (replace&&world.getBlockId(currentBlockX, currentBlockY, currentBlockZ)==0)continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateCylinder(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, float radius, float height, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0) {
            return list;
        }
        radius+=0.5f;
        height/=2;
        height-=0.5f;
        double radiusSquared = (double) radius * radius;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x - 0.5;
                double dz = z - 0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared) {
                    for (float y = -height; y <= height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = MathHelper.floor_float (centerY + y);
                        if (replace&&world.getBlockId(currentBlockX, currentBlockY, currentBlockZ)==0)continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowCylinder(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, float radius, float height, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0 || thickness <= 0 || thickness >= radius) {
            System.err.println("Cannot generate hollow cylinder: Invalid parameters.");
            return list;
        }
        radius+=0.5f;
        height/=2;
        height-=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x - 0.5;
                double dz = z - 0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared) {
                    for (float y = -height; y <= height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = MathHelper.floor_float (centerY + y);
                        boolean isTopOrBottomLayer = ((y >= -height && y<-height+thickness) || (y <= height && y>height-thickness));
                        boolean isWallLayer = (y > -height && y < height);
                        if (isTopOrBottomLayer || (isWallLayer && distanceSquared >= innerRadiusSquared)) {
                            if (replace && world.getBlockId(currentBlockX, currentBlockY, currentBlockZ) == 0) continue;
                            BlockInfo result = getInfo(sender, results);

                            list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                            world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                            CommandWorldEdit.numBlock++;
                        }
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateOpenCylinder(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, float radius, float height, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0 || thickness <= 0 || thickness >= radius) {
            System.err.println("Cannot generate hollow cylinder: Invalid parameters.");
            return list;
        }
        radius+=0.5f;
        height/=2;
        height-=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x -0.5;
                double dz = z -0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared && distanceSquared >= innerRadiusSquared) {
                    for (float y = -height; y <= height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = MathHelper.floor_float (centerY + y);
                        if (replace && world.getBlockId(currentBlockX, currentBlockY, currentBlockZ) == 0) continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateCube(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, int sizeX, int sizeY, int sizeZ, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            System.err.println("Cannot generate cube: Invalid world or dimensions.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartY = centerY - (sizeY / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    if (replace&&world.getBlockId(x, y, z)==0)continue;
                    BlockInfo result = getInfo(sender, results);

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    CommandWorldEdit.numBlock++;
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowCube(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, int sizeX, int sizeY, int sizeZ, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeY <= 0 || sizeZ <= 0 || thickness <= 0) {
            System.err.println("Cannot generate hollow cube: Invalid world, dimensions, or thickness.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartY = centerY - (sizeY / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        int innerStartX = actualStartX + thickness;
        int innerStartY = actualStartY + thickness;
        int innerStartZ = actualStartZ + thickness;
        int innerEndX = (actualStartX + sizeX) - thickness;
        int innerEndY = (actualStartY + sizeY) - thickness;
        int innerEndZ = (actualStartZ + sizeZ) - thickness;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    boolean isOutsideInnerSpace = (x < innerStartX || x >= innerEndX) ||
                            (y < innerStartY || y >= innerEndY) ||
                            (z < innerStartZ || z >= innerEndZ);
                    if (replace && world.getBlockId(x, y, z) == 0 || !isOutsideInnerSpace) continue;
                    BlockInfo result = getInfo(sender, results);

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    CommandWorldEdit.numBlock++;
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateOpenCube(World world, int centerX, int centerY, int centerZ, String blocksUsed, int flag, int sizeX, int sizeY, int sizeZ, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeY <= 0 || sizeZ <= 0 || thickness <= 0) {
            System.err.println("Cannot generate hollow cube: Invalid world, dimensions, or thickness.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartY = centerY - (sizeY / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        int innerStartX = actualStartX + thickness;
        int innerStartZ = actualStartZ + thickness;
        int innerEndX = (actualStartX + sizeX) - thickness;
        int innerEndZ = (actualStartZ + sizeZ) - thickness;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    boolean isPartOfSideWall = (x < innerStartX || x >= innerEndX) || (z < innerStartZ || z >= innerEndZ);
                    if (replace && world.getBlockId(x, y, z) == 0 || !isPartOfSideWall) continue;
                    BlockInfo result = getInfo(sender, results);

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    CommandWorldEdit.numBlock++;
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generatePyramid(World world, int centerX, int baseY, int centerZ, String blocksUsed, int flag, int sizeX, int sizeY, int sizeZ, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeZ <= 0) {
            System.err.println("Cannot generate cube: Invalid world or dimensions.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        if (sizeY>0) {
            for (int y = baseY; y < baseY + sizeY; y++) {
                int ratioWH = MathHelper.floor_float(((float) Math.min(sizeX, sizeZ) / (float) sizeY) * ((float) y - baseY) / 2);
                int startX = actualStartX + ratioWH;
                int startZ = actualStartZ + ratioWH;
                int sizedX = sizeX - ratioWH * 2;
                int sizedZ = sizeZ - ratioWH * 2;

                for (int x = startX; x < startX + sizedX; x++) {
                    for (int z = startZ; z < startZ + sizedZ; z++) {
                        if (replace && world.getBlockId(x, y, z) == 0) continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                        world.setBlock(x, y, z, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }else {
            for (int y = baseY; y > baseY + sizeY; y--) {
                int ratioWH = Math.round((float) ((Math.min(sizeX, sizeZ) / Math.abs(sizeY)) * Math.abs(y - baseY)) / 2);
                int startX = actualStartX + ratioWH;
                int startZ = actualStartZ + ratioWH;
                int sizedX = sizeX - ratioWH * 2;
                int sizedZ = sizeZ - ratioWH * 2;

                for (int x = startX; x < startX + sizedX; x++) {
                    for (int z = startZ; z < startZ + sizedZ; z++) {
                        if (replace && world.getBlockId(x, y, z) == 0) continue;
                        BlockInfo result = getInfo(sender, results);

                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                        world.setBlock(x, y, z, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }

        }

        return list;
    }

    public static List<SavedBlock> generateHollowPyramid(World world, int centerX, int baseY, int centerZ, String blocksUsed, int flag, int sizeX, int sizeY, int sizeZ, int thickness, boolean replace, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            System.err.println("Cannot generate cube: Invalid world or dimensions.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        int innerStartY = baseY + thickness;
        int innerEndY = (baseY + sizeY) - thickness;
        List<BlockInfo> results = getBlockInfo(blocksUsed,sender.getCommandSenderName());
        if (results==null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        for (int y = baseY; y < baseY + sizeY; y++) {
            int ratioWH = MathHelper.floor_float(((float)Math.min(sizeX, sizeZ) /(float)sizeY) * ((float)y- baseY)/2);
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
                    if (replace && world.getBlockId(x, y, z) == 0 || !isOutsideInnerSpace) continue;
                    BlockInfo result = getInfo(sender, results);

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    CommandWorldEdit.numBlock++;

                }
            }
        }
        return list;
    }

    public static List<SavedBlock> buildLine(World world, String[] strings, int x1, int y1, int z1, int x2, int y2, int z2, int thickness, int flag, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        if (thickness <= 0) {
            System.err.println("Error: Line thickness must be a positive integer.");
            return list;
        }
        if (strings.length < 2) {
            System.err.println("Error: block id needed");
            return list;
        }
        List<BlockInfo> results = getBlockInfo(strings[1], sender.getCommandSenderName());
        if (results == null) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return list;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        double steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));

        double xIncrement = dx / steps;
        double yIncrement = dy / steps;
        double zIncrement = dz / steps;

        int halfThicknessOffset = (thickness - 1) / 2;
        for (int i = 0; i <= steps; i++) {
            int currentX = (int) Math.round(x1 + i * xIncrement);
            int currentY = (int) Math.round(y1 + i * yIncrement);
            int currentZ = (int) Math.round(z1 + i * zIncrement);
            for (int ox = -halfThicknessOffset; ox < thickness - halfThicknessOffset; ox++) {
                for (int oy = -halfThicknessOffset; oy < thickness - halfThicknessOffset; oy++) {
                    for (int oz = -halfThicknessOffset; oz < thickness - halfThicknessOffset; oz++) {
                        list.add(new SavedBlock(currentX + ox, currentY + oy, currentZ + oz, world.getBlockId(currentX + ox, currentY + oy, currentZ + oz), world.getBlockMetadata(currentX + ox, currentY + oy, currentZ + oz)));
                    }
                }
            }
        }
        for (int i = 0; i <= steps; i++) {
            int currentX = (int) Math.round(x1 + i * xIncrement);
            int currentY = (int) Math.round(y1 + i * yIncrement);
            int currentZ = (int) Math.round(z1 + i * zIncrement);
            for (int ox = -halfThicknessOffset; ox < thickness - halfThicknessOffset; ox++) {
                for (int oy = -halfThicknessOffset; oy < thickness - halfThicknessOffset; oy++) {
                    for (int oz = -halfThicknessOffset; oz < thickness - halfThicknessOffset; oz++) {
                        BlockInfo result = getInfo(sender, results);
                        world.setBlock(currentX + ox, currentY + oy, currentZ + oz, result.id(), result.meta(), flag);
                        CommandWorldEdit.numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> buildPlane(World world, String blocksUsed, int xa, int ya, int za, int xb, int yb, int zb, int thickness, int side, boolean replace, int flag, EntityPlayer sender) {
        List<SavedBlock> list = new ArrayList<>();
        List<BlockInfo> results = getBlockInfo(blocksUsed, sender.getCommandSenderName());
        if (results == null || results.isEmpty()) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong block name or ID"));
            return list;
        }
        boolean slopeAlongX = side == 0;
        boolean slopeAlongZ = side == 1;

        int minX = Math.min(xa, xb);
        int maxX = Math.max(xa, xb);
        int minZ = Math.min(za, zb);
        int maxZ = Math.max(za, zb);

        int minY_overall = Math.min(ya, yb);
        int maxY_overall = Math.max(ya, yb);

        double dx_horizontal = Math.abs(xa - xb);
        double dz_horizontal = Math.abs(za - zb);

        if (dx_horizontal == 0 && dz_horizontal == 0) {
            for (int y = minY_overall; y <= maxY_overall; y++) {
                if (replace && world.getBlockId(xa, y, za) == 0) continue;
                BlockInfo blockToPlace = getInfo(sender, results);
                list.add(new SavedBlock(xa, y, za, world.getBlockId(xa, y, za), world.getBlockMetadata(xa, y, za)));
                world.setBlock(xa, y, za, blockToPlace.id(), blockToPlace.meta(), flag);
                CommandWorldEdit.numBlock++;
            }
            return list;
        }

        if (slopeAlongX) {
            int currentX = xa;
            int currentY = ya;

            int dx = xb - xa;
            int dy = yb - ya;

            int x_inc = (dx > 0) ? 1 : -1;
            int y_inc = (dy > 0) ? 1 : -1;

            dx = Math.abs(dx);
            dy = Math.abs(dy);

            if (dx >= dy) {
                int error = 2 * dy - dx;
                for (int i = 0; i <= dx; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if (replace && world.getBlockId(currentX, yFill, z) == 0) continue;
                            list.add(new SavedBlock(currentX, yFill, z, world.getBlockId(currentX, yFill, z), world.getBlockMetadata(currentX, yFill, z)));
                        }
                    }
                }
                for (int i = 0; i <= dx; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if (replace && world.getBlockId(currentX, yFill, z) == 0) continue;
                            BlockInfo blockToPlace = getInfo(sender, results);
                            world.setBlock(currentX, yFill, z, blockToPlace.id(), blockToPlace.meta(), flag);
                            CommandWorldEdit.numBlock++;
                        }
                    }

                    if (i < dx) {
                        if (error < 0) {
                            error += 2 * dy;
                        } else {
                            currentY += y_inc;
                            error += 2 * (dy - dx);
                        }
                        currentX += x_inc;
                    }
                }
            } else {
                int error = 2 * dx - dy;
                for (int i = 0; i <= dy; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if (replace && world.getBlockId(currentX, yFill, z) == 0) continue;
                            list.add(new SavedBlock(currentX, yFill, z, world.getBlockId(currentX, yFill, z), world.getBlockMetadata(currentX, yFill, z)));
                        }
                    }
                }
                for (int i = 0; i <= dy; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if (replace && world.getBlockId(currentX, yFill, z) == 0) continue;
                            BlockInfo blockToPlace = getInfo(sender, results);
                            world.setBlock(currentX, yFill, z, blockToPlace.id(), blockToPlace.meta(), flag);
                            CommandWorldEdit.numBlock++;
                        }
                    }
                    if (i < dy) {
                        if (error < 0) {
                            error += 2 * dx;
                        } else {
                            currentX += x_inc;
                            error += 2 * (dx - dy);
                        }
                        currentY += y_inc;
                    }
                }
            }
        } else if (slopeAlongZ){

            int currentZ = za;
            int currentY = ya;

            int dz = zb - za;
            int dy = yb - ya;

            int z_inc = (dz > 0) ? 1 : -1;
            int y_inc = (dy > 0) ? 1 : -1;

            dz = Math.abs(dz);
            dy = Math.abs(dy);

            if (dz >= dy) {
                int error = 2 * dy - dz;
                for (int i = 0; i <= dz; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int x = minX; x <= maxX; x++) {
                            if (replace && world.getBlockId(x, yFill, currentZ) == 0) continue;
                            list.add(new SavedBlock(x, yFill, currentZ, world.getBlockId(x, yFill, currentZ), world.getBlockMetadata(x, yFill, currentZ)));
                        }
                    }
                }
                for (int i = 0; i <= dz; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int x = minX; x <= maxX; x++) {
                            if (replace && world.getBlockId(x, yFill, currentZ) == 0) continue;
                            BlockInfo blockToPlace = getInfo(sender, results);
                            world.setBlock(x, yFill, currentZ, blockToPlace.id(), blockToPlace.meta(), flag);
                            CommandWorldEdit.numBlock++;
                        }
                    }

                    if (i < dz) {
                        if (error < 0) {
                            error += 2 * dy;
                        } else {
                            currentY += y_inc;
                            error += 2 * (dy - dz);
                        }
                        currentZ += z_inc;
                    }
                }
            } else {
                int error = 2 * dz - dy;
                for (int i = 0; i <= dy; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int x = minX; x <= maxX; x++) {
                            if (replace && world.getBlockId(x, yFill, currentZ) == 0) continue;
                            list.add(new SavedBlock(x, yFill, currentZ, world.getBlockId(x, yFill, currentZ), world.getBlockMetadata(x, yFill, currentZ)));
                        }
                    }
                }
                for (int i = 0; i <= dy; i++) {
                    for (int yFill = currentY; yFill >= currentY - (thickness - 1); yFill--) {
                        for (int x = minX; x <= maxX; x++) {
                            if (replace && world.getBlockId(x, yFill, currentZ) == 0) continue;
                            BlockInfo blockToPlace = getInfo(sender, results);
                            world.setBlock(x, yFill, currentZ, blockToPlace.id(), blockToPlace.meta(), flag);
                            CommandWorldEdit.numBlock++;
                        }
                    }

                    if (i < dy) {
                        if (error < 0) {
                            error += 2 * dz;
                        } else {
                            currentZ += z_inc;
                            error += 2 * (dz - dy);
                        }
                        currentY += y_inc;
                    }
                }
            }
        }
        return list;
    }

    private static @NotNull BlockInfo getInfo(EntityPlayer sender, List<BlockInfo> results) {
        BlockInfo result = getRandomBlockFromOdds(results);
        if (result.id() != 0) {
            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
            List<ItemStack> subtype = new ArrayList<>();
            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
            if (result.meta() > subtype.size() - 1) {
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
            }
        }
        return result;
    }
}
