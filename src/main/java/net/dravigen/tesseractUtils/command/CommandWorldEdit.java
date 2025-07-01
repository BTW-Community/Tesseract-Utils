package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.command.UtilsCommand.*;

public class CommandWorldEdit extends CommandBase {


    @Override
    public String getCommandName() {
        return "/";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "// <set> <id/metadata> [hollow:wall] [thickness] OR // <setblock> <x> <y> <z> <id/metadata> OR // <replace> <id/metadata> [id/metadata replaced] OR // <move> <to:add> <x> <y> <z> OR // <undo> OR // <copy> OR // <paste> [x,y,z] OR // <pos1> OR // <pos2> OR // <reach> <distance> OR // <disablePlaceCooldown> <True:False> OR // <disableBreakCooldown> <True:False> OR // <disableMomentum> <True:False> OR // <enableClickReplace> <True:False> OR // <enableNoClip> <True:False> OR // <enableExtraDebugInfo> <True:False>";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] strings) {
        if (strings.length==1) {
            return getListOfStringsMatchingLastWord(strings,"set", "setblock", "shape", "replace", "move", "undo", "redo", "copy", "paste", "pos1", "pos2","tool");
        }
        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(par1ICommandSender);
        if (strings[0].equalsIgnoreCase("pos1") || strings[0].equalsIgnoreCase("pos2")) {
            if (blockCoord != null) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 2) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            } else return null;
        }
        else if (strings[0].equalsIgnoreCase("setblock")) {
            if (strings.length == 2) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }
            if (blockCoord != null) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length == 5) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            }
        }
        else if (strings[0].equalsIgnoreCase("shape")) {
            if (strings.length == 2) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }
            if (blockCoord != null) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 5) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 6) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length ==7) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            }
            if (strings.length==3){
                return getListOfStringsMatchingLastWord(strings, "sphere","cylinder", "cube");
            }
        }
        else if (strings[0].equalsIgnoreCase("set")) {
            if (strings.length == 2) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            } else if (strings.length == 3) {
                return getListOfStringsMatchingLastWord(strings, "hollow", "wall");
            }
        }
        else if (strings[0].equalsIgnoreCase("replace")) {
            if (strings.length == 2 || strings.length == 3) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }
        }
        else if (strings[0].equalsIgnoreCase("copy")) return getListOfStringsMatchingLastWord(strings, "ignoreAir");
        else if (strings[0].equalsIgnoreCase("paste")){
            if (blockCoord != null) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 2) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            } else return null;
        }
        else if (strings[0].equalsIgnoreCase("move")) {
            if (strings.length == 2) {
                return getListOfStringsMatchingLastWord(strings, "to", "add");
            }
            if (blockCoord != null&&strings[1].equalsIgnoreCase("to")) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length == 5) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            }
        }
        else if (strings[0].equalsIgnoreCase("tool")) {
            if (strings.length==2){
                return getListOfStringsMatchingLastWord(strings, "sphere", "cylinder","cube");
            } else if (strings.length==3){
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }else if (strings.length == 4) {
                return getListOfStringsMatchingLastWord(strings, "1:1:1:1");
            } else if (strings.length == 5) {
                return getListOfStringsMatchingLastWord(strings,"replace");
            }else if (strings.length == 6) {
                return getListOfStringsMatchingLastWord(strings, "hollow", "open");
            }
        }
        return null;
    }

    private static int numBlock=0;

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        World world = iCommandSender.getEntityWorld();
        numBlock = 0;
        boolean ignoreAir = false;
        int flag = 2;
        for (String string : strings) {
            if (!ignoreAir) {
                ignoreAir = string.equalsIgnoreCase("ignoreAir");
            }
            if (flag == 2) {
                flag = string.equalsIgnoreCase("causeUpdate") ? 3 : flag;
            }
        }
        if (strings.length==0)throw new WrongUsageException("Invalid command");
        else switch (strings[0]) {
            case "setblock" -> {
                try {
                    List<SavedBlock> list = new ArrayList<>();
                    int x;
                    int y;
                    int z;
                    try {
                        x = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[2]));
                        y = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[3]));
                        z = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[4]));
                    } catch (Exception ignored) {
                        x = Integer.parseInt(strings[2]);
                        y = Integer.parseInt(strings[3]);
                        z = Integer.parseInt(strings[4]);
                    }
                    @NotNull List<BlockInfo> results = getBlockInfo(strings[1]);
                    BlockInfo result = results.get(0);
                    if (result.id() != 0) {
                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (result.meta() > subtype.size() - 1) {
                            getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                            return;
                        }
                    }

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    undoSaved.add(list);
                    redoSaved.clear();
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dPlaced " + result.blockName().replace("_", " ") + " at: " + x + ", " + y + ", " + z));
                } catch (Exception e) {
                    throw new WrongUsageException("// setblock <id/metadata> <x> <y> <z>");
                }
            }
            case "set" -> {
                try {
                    boolean hollow = strings.length > 2 && strings[2].equalsIgnoreCase("hollow");
                    boolean wall = strings.length > 2 && strings[2].equalsIgnoreCase("wall");
                    if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999) {
                        List<SavedBlock> list = new ArrayList<>();
                        if (hollow || wall) {
                            int thickness = 1;
                            thickness = strings.length > 3 ? strings[3].equalsIgnoreCase("ignoreAir") || strings[3].equalsIgnoreCase("causeUpdate") ? thickness : Integer.parseInt(strings[3]) : thickness;
                            for (int l = 0; l < thickness; l++) {
                                int maxX = Math.max(x1, x2) - l;
                                int minX = Math.min(x1, x2) + l;
                                int maxY = Math.max(y1, y2) - l;
                                int minY = Math.min(y1, y2) + l;
                                int maxZ = Math.max(z1, z2) - l;
                                int minZ = Math.min(z1, z2) + l;
                                int absX = MathHelper.abs_int(x1 - x2) - l * 2;
                                int absY = wall ? MathHelper.abs_int(y1 - y2) : MathHelper.abs_int(y1 - y2) - l * 2;
                                int absZ = MathHelper.abs_int(z1 - z2) - l * 2;
                                @NotNull List<BlockInfo> results = getBlockInfo(strings[1]);
                                for (int j = 0; j <= absY; j++) {
                                    for (int i = 0; i <= absX; i++) {
                                        for (int k = 0; k <= absZ; k++) {
                                            BlockInfo result = getRandomBlockFromOdds(results);
                                            int x = minX + i;
                                            int y = wall ? Math.min(y1, y2) + j : minY + j;
                                            int z = minZ + k;
                                            if (result.id() != 0) {
                                                ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                                                List<ItemStack> subtype = new ArrayList<>();
                                                itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                                if (result.meta() > subtype.size() - 1) {
                                                    getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                                    return;
                                                }
                                            }
                                            if (x == maxX || z == maxZ || x == minX || z == minZ) {
                                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                world.setBlock(x, y, z, result.id(), result.meta(), flag);
                                                numBlock++;
                                            } else if ((y == maxY || y == minY) && hollow) {
                                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                world.setBlock(x, y, z, result.id(), result.meta(), flag);
                                                numBlock++;
                                            } else if (!ignoreAir) {
                                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                world.setBlock(x, y, z, 0, 0, flag);
                                                numBlock++;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            @NotNull List<BlockInfo> results = getBlockInfo(strings[1]);
                            for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                                    for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                        BlockInfo result = getRandomBlockFromOdds(results);
                                        int x = Math.min(x1, x2) + i;
                                        int y = Math.min(y1, y2) + j;
                                        int z = Math.min(z1, z2) + k;
                                        if (result.id() != 0) {
                                            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                                            List<ItemStack> subtype = new ArrayList<>();
                                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                            if (result.meta() > subtype.size() - 1) {
                                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                                return;
                                            }
                                        }
                                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                        world.setBlock(x, y, z, result.id(), result.meta(), flag);
                                        numBlock++;
                                    }
                                }
                            }
                        }
                        if (!list.isEmpty()) {
                            undoSaved.add(list);
                            redoSaved.clear();
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                    } else throw new WrongUsageException("You need to select an area");
                } catch (Exception e) {
                    throw new WrongUsageException("// set <id/metadata>");
                }
            }
            case "shape" -> {
                try {
                    int centerX = 999999;
                    int centerY = 999999;
                    int centerZ = 999999;
                    String[] parameters = strings[3].split(":");
                    float var1 = Float.parseFloat(parameters[0]);
                    int var2 = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                    int var3 = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                    int var4 = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                    boolean hollow = false;
                    boolean open = false;
                    boolean replace = false;
                    for (String s : strings) {
                        switch (s) {
                            case "hollow" -> hollow = true;
                            case "open" -> open = true;
                            case "replace" -> replace = true;
                        }
                    }
                    if (strings.length >= 7) {
                        try {
                            centerX = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[4]));
                            centerY = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[5]));
                            centerZ = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[6]));
                        } catch (Exception ignored) {
                            centerX = Integer.parseInt(strings[4]);
                            centerY = Integer.parseInt(strings[5]);
                            centerZ = Integer.parseInt(strings[6]);
                        }
                    }
                    if (centerX == 999999 || centerY == 999999 || centerZ == 999999) {
                        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(iCommandSender);
                        centerX = blockCoord.blockX;
                        centerY = blockCoord.blockY;
                        centerZ = blockCoord.blockZ;
                    }

                    centerY = var2 < 0 ? centerY + var2 : centerY;
                    var2 = var2 < 0 ? var2 * (-1) : var2;
                    List<SavedBlock> list;
                    switch (strings[2].toLowerCase()) {
                        case "sphere" -> {
                            if (hollow) {
                                list = generateHollowSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace);
                            } else
                                list = generateSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, replace);
                            if (!list.isEmpty()) {
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                        }
                        case "cylinder" -> {

                            if (hollow)
                                list = generateHollowCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace);
                            else if (open)
                                list = generateOpenCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace);
                            else
                                list = generateCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace);
                            if (!list.isEmpty()) {
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                        }
                        case "cube" -> {
                            if (hollow)
                                list = generateHollowCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace);
                            else if (open)
                                list = generateOpenCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace);
                            else
                                list = generateCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, replace);
                            if (!list.isEmpty()) {
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));

                        }
                    }
                } catch (Exception e) {
                    throw new WrongUsageException("// shape <type> <id> <parameters> <x> <y> <z> [hollow|open] [replace]");
                }
            }
            case "replace" -> {
                try {
                    if (strings.length > 1) {
                        if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999) {
                            List<SavedBlock> list = new ArrayList<>();
                            @NotNull List<BlockInfo> results1 = getBlockInfo(strings[1]);
                            @NotNull List<BlockInfo> results2 = null;
                            if (strings.length > 2) {
                                results2 = getBlockInfo(strings[2]);
                            }
                            for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                                    for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                        BlockInfo result1 = getRandomBlockFromOdds(results1);
                                        if (result1.id() != 0) {
                                            ItemStack itemStack = new ItemStack(result1.id(), 0, 0);
                                            List<ItemStack> subtype = new ArrayList<>();
                                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                            if (result1.meta() > subtype.size() - 1) {
                                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result1.meta() + " out of bounds for length " + (subtype.size())));
                                                return;
                                            }
                                        }
                                        int x = Math.min(x1, x2) + i;
                                        int y = Math.min(y1, y2) + j;
                                        int z = Math.min(z1, z2) + k;
                                        if (strings.length > 2) {
                                            for (BlockInfo result2:results2) {
                                                if (result2 != null && world.getBlockId(x, y, z) == result2.id() && world.getBlockMetadata(x, y, z) == result2.meta()) {
                                                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                    world.setBlock(x, y, z, result1.id(), result1.meta(), flag);
                                                    numBlock++;
                                                }
                                            }
                                        }else if (!world.isAirBlock(x, y, z)) {
                                            list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                            world.setBlock(x, y, z, result1.id(), result1.meta(), flag);
                                            numBlock++;
                                        }
                                    }
                                }
                            }
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) was replaced"));
                            if (!list.isEmpty()) {
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                        } else throw new WrongUsageException("You need to select an area");
                    } else throw new WrongUsageException("// replace <id> [id replaced]");
                } catch (Exception e) {
                    throw new WrongUsageException("// replace <id> [id replaced]");
                }
            }
            case "move" -> {
                try {
                    if (strings.length > 1) {
                        List<SavedBlock> list = new ArrayList<>();
                        List<SavedBlock> list1 = new ArrayList<>();
                        int xPos = 0;
                        int yPos = 0;
                        int zPos = 0;
                        if (strings[1].equalsIgnoreCase("to")) {
                            if (strings.length > 4) {
                                try {
                                    xPos = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[2]));
                                    yPos = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[3]));
                                    zPos = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[4]));
                                } catch (Exception ignored) {
                                    xPos = Integer.parseInt(strings[2]);
                                    yPos = Integer.parseInt(strings[3]);
                                    zPos = Integer.parseInt(strings[4]);
                                }
                            } else {
                                xPos = iCommandSender.getPlayerCoordinates().posX;
                                yPos = iCommandSender.getPlayerCoordinates().posY;
                                zPos = iCommandSender.getPlayerCoordinates().posZ;
                            }
                        } else if (strings[1].equalsIgnoreCase("add")) {
                            if (strings.length > 4) {
                                xPos = Integer.parseInt(strings[2]);
                                yPos = Integer.parseInt(strings[3]);
                                zPos = Integer.parseInt(strings[4]);
                            }
                        }
                        if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999 && (strings[1].equalsIgnoreCase("to") || strings[1].equalsIgnoreCase("add"))) {
                            for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                                for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                    for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                        int x = Math.min(x1, x2) + i;
                                        int y = Math.min(y1, y2) + j;
                                        int z = Math.min(z1, z2) + k;
                                        if (ignoreAir) {
                                            if (!world.isAirBlock(x, y, z)) {
                                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                world.setBlock(x, y, z, 0, 0, flag);
                                                numBlock++;
                                            }
                                        } else {
                                            list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                            world.setBlock(x, y, z, 0, 0, flag);
                                            numBlock++;
                                        }
                                    }
                                }
                            }
                            if (!list.isEmpty()) {
                                int xa = 9999999;
                                int ya = 9999999;
                                int za = 9999999;
                                int yb = -9999999;
                                int xb = -9999999;
                                int zb = -9999999;
                                for (SavedBlock block : list) {
                                    int finalX = xPos + block.x();
                                    int finalY = yPos + block.y();
                                    int finalZ = zPos + block.z();
                                    if (strings[1].equalsIgnoreCase("to")) {
                                        finalX -= Math.max(x1, x2);
                                        finalY -= Math.min(y1, y2);
                                        finalZ -= Math.max(z1, z2);
                                    }
                                    list1.add(new SavedBlock(finalX, finalY, finalZ, world.getBlockId(finalX, finalY, finalZ), world.getBlockMetadata(finalX, finalY, finalZ)));
                                    world.setBlock(finalX, finalY, finalZ, block.id(), block.meta(), flag);
                                    xa = Math.min(finalX, xa);
                                    ya = Math.min(finalY, ya);
                                    za = Math.min(finalZ, za);
                                    xb = Math.max(finalX, xb);
                                    yb = Math.max(finalY, yb);
                                    zb = Math.max(finalZ, zb);
                                }
                                setCoord1(xa,ya,za);
                                setCoord2(xb,yb,zb);
                            }
                            list1.addAll(list);
                            if (!list1.isEmpty()) {
                                undoSaved.add(list1);
                                redoSaved.clear();
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been moved"));
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new WrongUsageException("// move <to|add> <x> <y> <z>");
                }
            }
            case "copy" -> {
                try {
                    copySaved.clear();
                    if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999) {
                        for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                            for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                    int x = Math.min(x1, x2) + i;
                                    int y = Math.min(y1, y2) + j;
                                    int z = Math.min(z1, z2) + k;

                                    if (ignoreAir) {
                                        if (!world.isAirBlock(x, y, z)) {
                                            copySaved.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                            numBlock++;
                                        }
                                    } else {
                                        copySaved.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                        numBlock++;
                                    }
                                }
                            }
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been copied"));
                    } else throw new WrongUsageException("You need to select an area");
                } catch (Exception e) {
                    throw new WrongUsageException("// copy");
                }
            }
            case "paste" -> {
                try {
                    List<SavedBlock> list = new ArrayList<>();
                    int x;
                    int y;
                    int z;
                    if (strings.length > 1) {
                        try {
                            x = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[1]));
                            y = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[2]));
                            z = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[3]));
                        } catch (Exception ignored) {
                            x = Integer.parseInt(strings[1]);
                            y = Integer.parseInt(strings[2]);
                            z = Integer.parseInt(strings[3]);
                        }
                    } else {
                        x = iCommandSender.getPlayerCoordinates().posX;
                        y = iCommandSender.getPlayerCoordinates().posY;
                        z = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    if (!copySaved.isEmpty()) {
                        for (SavedBlock block : copySaved) {
                            if (ignoreAir) {
                                if (block.id() != 0) {
                                    list.add(new SavedBlock(block.x() + x, block.y() + y, block.z() + z, world.getBlockId(block.x() + x, block.y() + y, block.z() + z), world.getBlockMetadata(block.x() + x, block.y() + y, block.z() + z)));
                                    world.setBlock(block.x() + x, block.y() + y, block.z() + z, block.id(), block.meta(), flag);
                                    numBlock++;
                                }
                            } else {
                                list.add(new SavedBlock(block.x() + x, block.y() + y, block.z() + z, world.getBlockId(block.x() + x, block.y() + y, block.z() + z), world.getBlockMetadata(block.x() + x, block.y() + y, block.z() + z)));
                                world.setBlock(block.x() + x, block.y() + y, block.z() + z, block.id(), block.meta(), flag);
                                numBlock++;
                            }
                        }
                    }
                    if (!list.isEmpty()) {
                        undoSaved.add(list);
                        redoSaved.clear();
                    }
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been pasted"));
                } catch (Exception e) {
                    throw new WrongUsageException("// paste [x] [y] [z]");
                }
            }
            case "undo" -> {
                try {
                    if (!undoSaved.isEmpty()) {
                        int count = 0;
                        for (int i = 0; i < (strings.length > 1 ? Integer.parseInt(strings[1]) : 1); i++) {
                            if (undoSaved.isEmpty())break;
                            int xa = 9999999;
                            int ya = 9999999;
                            int za = 9999999;
                            int yb = -9999999;
                            int xb = -9999999;
                            int zb = -9999999;
                            List<SavedBlock> list = new ArrayList<>();
                            for (SavedBlock block : undoSaved.get(undoSaved.size() - 1)) {
                                list.add(new SavedBlock(block.x(), block.y(), block.z(), world.getBlockId(block.x(), block.y(), block.z()), world.getBlockMetadata(block.x(), block.y(), block.z())));
                                world.setBlock(block.x(), block.y(), block.z(), block.id(), block.meta(), flag);
                                xa = Math.min(block.x(), xa);
                                ya = Math.min(block.y(), ya);
                                za = Math.min(block.z(), za);
                                xb = Math.max(block.x(), xb);
                                yb = Math.max(block.y(), yb);
                                zb = Math.max(block.z(), zb);
                                numBlock++;
                            }
                            x1 = xa;
                            y1 = ya;
                            z1 = za;
                            x2 = xb;
                            y2 = yb;
                            z2 = zb;
                            redoSaved.add(list);
                            undoSaved.remove(undoSaved.size() - 1);
                            count++;
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dThe last " + (count>1 ? count + " ":"") + "action" + (count>1 ?"s have been" : " has been") + " reverted"));
                    }
                } catch (Exception e) {
                    throw new WrongUsageException("// undo [count]");
                }
            }
            case "redo" -> {
                try {
                    int count=0;
                    if (!redoSaved.isEmpty()) {
                        for (int j = 0; j < (strings.length > 1 ? Integer.parseInt(strings[1]) : 1); j++) {
                            if (redoSaved.isEmpty())break;
                            int xa = 9999999;
                            int ya = 9999999;
                            int za = 9999999;
                            int yb = -9999999;
                            int xb = -9999999;
                            int zb = -9999999;
                            List<SavedBlock> list = new ArrayList<>();
                            for (SavedBlock undoBlock : redoSaved.get(redoSaved.size() - 1)) {
                                list.add(new SavedBlock(undoBlock.x(), undoBlock.y(), undoBlock.z(), world.getBlockId(undoBlock.x(), undoBlock.y(), undoBlock.z()), world.getBlockMetadata(undoBlock.x(), undoBlock.y(), undoBlock.z())));
                            }
                            for (int i = redoSaved.get(redoSaved.size() - 1).size() - 1; i >= 0; i--) {
                                SavedBlock block = redoSaved.get(redoSaved.size() - 1).get(i);
                                world.setBlock(block.x(), block.y(), block.z(), block.id(), block.meta(), flag);
                                xa = Math.min(block.x(), xa);
                                ya = Math.min(block.y(), ya);
                                za = Math.min(block.z(), za);
                                xb = Math.max(block.x(), xb);
                                yb = Math.max(block.y(), yb);
                                zb = Math.max(block.z(), zb);
                            }
                            x1 = xa;
                            y1 = ya;
                            z1 = za;
                            x2 = xb;
                            y2 = yb;
                            z2 = zb;
                            undoSaved.add(list);
                            redoSaved.remove(redoSaved.size() - 1);
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dThe last " + (count > 1 ? count + " " : "") + "action" + (count > 1 ? "s have been" : " has been") + " redone"));
                    }
                } catch (Exception e) {
                    throw new WrongUsageException("// redo [count]");
                }
            }
            case "pos1" -> {
                try {
                    if (strings.length == 4) {
                        x1 = Integer.parseInt(strings[1]);
                        y1 = Integer.parseInt(strings[2]);
                        z1 = Integer.parseInt(strings[3]);
                    } else {
                        x1 = iCommandSender.getPlayerCoordinates().posX;
                        y1 = iCommandSender.getPlayerCoordinates().posY;
                        z1 = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dFirst position set to (" + x1 + ", " + y1 + ", " + z1 + ")."));
                } catch (Exception e) {
                    throw new WrongUsageException("// pos1 [x] [y] [z]");
                }
            }
            case "pos2" -> {
                try {
                    if (strings.length == 4) {
                        x2 = Integer.parseInt(strings[1]);
                        y2 = Integer.parseInt(strings[2]);
                        z2 = Integer.parseInt(strings[3]);
                    } else {
                        x2 = iCommandSender.getPlayerCoordinates().posX;
                        y2 = iCommandSender.getPlayerCoordinates().posY;
                        z2 = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dSecond position set to (" + x2 + ", " + y2 + ", " + z2 + ")."));
                }catch (Exception e) {
                    throw new WrongUsageException("// pos2 [x] [y] [z]");
                }
            }
            case "tool" -> {
                try {
                    ItemStack itemStack = getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).getHeldItem();
                    if (itemStack == null) return;
                    if (strings.length == 1 && itemStack.hasTagCompound()) {
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        NBTTagCompound buildingParamsNBT;
                        if (nbt.hasKey("BuildingParams")) {
                            buildingParamsNBT = nbt.getCompoundTag("BuildingParams");
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dCurrent item is a building tool with the characteristics:"));
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-shape= §a" + buildingParamsNBT.getString("shape")));
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed")));
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-parameters= §a" + buildingParamsNBT.getString("parameters")));
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-type of action= §a" + buildingParamsNBT.getString("actionType")));
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-volume= §a" + buildingParamsNBT.getString("volume")));
                        }
                    } else if (strings.length >= 4) {
                        if (!itemStack.hasTagCompound()) {
                            itemStack.setTagCompound(new NBTTagCompound());
                        }
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        NBTTagCompound buildingParamsNBT;
                        if (nbt.hasKey("BuildingParams")) {
                            buildingParamsNBT = nbt.getCompoundTag("BuildingParams");
                        } else {
                            buildingParamsNBT = new NBTTagCompound();
                            nbt.setCompoundTag("BuildingParams", buildingParamsNBT);
                        }

                        buildingParamsNBT.setString("shape", strings[1]);
                        buildingParamsNBT.setString("blockUsed", strings[2]);
                        buildingParamsNBT.setString("parameters", strings[3]);
                        buildingParamsNBT.setString("actionType", "place");
                        buildingParamsNBT.setString("volume", "full");

                        for (String s : strings) {
                            switch (s) {
                                case "replace" -> buildingParamsNBT.setString("actionType", "replace");
                                case "hollow" -> buildingParamsNBT.setString("volume", "hollow");
                                case "open" -> buildingParamsNBT.setString("volume", "open");
                            }
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dCurrent item got transformed into a building tool with the characteristics:"));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-shape= §a" + buildingParamsNBT.getString("shape")));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed")));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-parameters= §a" + buildingParamsNBT.getString("parameters")));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-type of action= §a" + buildingParamsNBT.getString("actionType")));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-volume= §a" + buildingParamsNBT.getString("volume")));
                    } else throw new WrongUsageException("// tool <shape> <id> <parameters> [replace] [hollow|open]");
                } catch (Exception e) {
                    throw new WrongUsageException("// tool <shape> <id> <parameters> [replace] [hollow|open]");
                }
            }
        }
    }

    public static List<SavedBlock> generateSphere(World world, int centerX, int centerY, int centerZ, String string, int flag, float radius, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0) {
            System.err.println("Cannot generate sphere: Invalid world or radius.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        @NotNull List<BlockInfo> results = getBlockInfo(string);
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
                        BlockInfo result = getRandomBlockFromOdds(results);
                        if (result.id() != 0) {
                            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                            List<ItemStack> subtype = new ArrayList<>();
                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                            if (result.meta() > subtype.size() - 1) {
                                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                return list;
                            }
                        }
                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowSphere(World world, int centerX, int centerY, int centerZ, String string, int flag, float radius, int thickness, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || thickness <= 0 || thickness > radius) {
            System.err.println("Cannot generate hollow sphere: Invalid world, radius, or thickness.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        List<BlockInfo> results = getBlockInfo(string);
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
                        BlockInfo result = getRandomBlockFromOdds(results);
                        if (result.id() != 0) {
                            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                            List<ItemStack> subtype = new ArrayList<>();
                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                            if (result.meta() > subtype.size() - 1) {
                                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                return list;
                            }
                        }
                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateCylinder(World world, int centerX, int baseCenterY, int centerZ, String string, int flag, float radius, int height, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0) {
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x - 0.5;
                double dz = z - 0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared) {
                    for (int y = 0; y < height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = baseCenterY + y;
                        if (replace&&world.getBlockId(currentBlockX, currentBlockY, currentBlockZ)==0)continue;
                        BlockInfo result = getRandomBlockFromOdds(results);
                        if (result.id() != 0) {
                            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                            List<ItemStack> subtype = new ArrayList<>();
                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                            if (result.meta() > subtype.size() - 1) {
                                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                return list;
                            }
                        }
                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowCylinder(World world, int centerX, int baseCenterY, int centerZ, String string, int flag, float radius, int height, int thickness, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0 || thickness <= 0 || thickness >= radius) {
            System.err.println("Cannot generate hollow cylinder: Invalid parameters.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x - 0.5;
                double dz = z - 0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared) {
                    for (int y = 0; y < height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = baseCenterY + y;
                        boolean isTopOrBottomLayer = (y == 0 || y == height - 1);
                        boolean isWallLayer = (y > 0 && y < height - 1);
                        if (isTopOrBottomLayer || (isWallLayer && distanceSquared >= innerRadiusSquared)) {
                            if (replace && world.getBlockId(currentBlockX, currentBlockY, currentBlockZ) == 0) continue;
                            BlockInfo result = getRandomBlockFromOdds(results);
                            if (result.id() != 0) {
                                ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                                List<ItemStack> subtype = new ArrayList<>();
                                itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                if (result.meta() > subtype.size() - 1) {
                                    Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                    return list;
                                }
                            }
                            list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                            world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                            numBlock++;
                        }
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateOpenCylinder(World world, int centerX, int baseCenterY, int centerZ, String string, int flag, float radius, int height, int thickness, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || radius <= 0 || height <= 0 || thickness <= 0 || thickness >= radius) {
            System.err.println("Cannot generate hollow cylinder: Invalid parameters.");
            return list;
        }
        radius+=0.5f;
        double radiusSquared = (double) radius * radius;
        float innerRadius = radius - thickness;
        double innerRadiusSquared = (double) innerRadius * innerRadius;
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                double dx = x -0.5;
                double dz = z -0.5;
                double distanceSquared = (dx * dx) + (dz * dz);
                if (distanceSquared < radiusSquared && distanceSquared >= innerRadiusSquared) {
                    for (int y = 0; y < height; y++) {
                        int currentBlockX = MathHelper.floor_float (centerX + x);
                        int currentBlockZ = MathHelper.floor_float (centerZ + z);
                        int currentBlockY = baseCenterY + y;
                        if (replace && world.getBlockId(currentBlockX, currentBlockY, currentBlockZ) == 0) continue;
                        BlockInfo result = getRandomBlockFromOdds(results);
                        if (result.id() != 0) {
                            ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                            List<ItemStack> subtype = new ArrayList<>();
                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                            if (result.meta() > subtype.size() - 1) {
                                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                                return list;
                            }
                        }
                        list.add(new SavedBlock(currentBlockX, currentBlockY, currentBlockZ, world.getBlockId(currentBlockX, currentBlockY, currentBlockZ), world.getBlockMetadata(currentBlockX, currentBlockY, currentBlockZ)));
                        world.setBlock(currentBlockX, currentBlockY, currentBlockZ, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateCube(World world, int centerX, int centerY, int centerZ, String string, int flag, int sizeX, int sizeY, int sizeZ, boolean replace) {
        List<SavedBlock> list = new ArrayList<>();
        if (world == null || sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            System.err.println("Cannot generate cube: Invalid world or dimensions.");
            return list;
        }
        int actualStartX = centerX - (sizeX / 2);
        int actualStartY = centerY - (sizeY / 2);
        int actualStartZ = centerZ - (sizeZ / 2);
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    if (replace&&world.getBlockId(x, y, z)==0)continue;
                    BlockInfo result = getRandomBlockFromOdds(results);
                    if (result.id() != 0) {
                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (result.meta() > subtype.size() - 1) {
                            Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                            return list;
                        }
                    }
                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    numBlock++;
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateHollowCube(World world, int centerX, int centerY, int centerZ, String string, int flag, int sizeX, int sizeY, int sizeZ, int thickness, boolean replace) {
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
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    boolean isOutsideInnerSpace = (x < innerStartX || x >= innerEndX) ||
                            (y < innerStartY || y >= innerEndY) ||
                            (z < innerStartZ || z >= innerEndZ);
                    if (replace&&world.getBlockId(x, y, z)==0)continue;
                    BlockInfo result = getRandomBlockFromOdds(results);
                    if (result.id() != 0) {
                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (result.meta() > subtype.size() - 1) {
                            Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                            return list;
                        }
                    }
                    if (isOutsideInnerSpace) {
                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                        world.setBlock(x, y, z, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

    public static List<SavedBlock> generateOpenCube(World world, int centerX, int centerY, int centerZ, String string, int flag, int sizeX, int sizeY, int sizeZ, int thickness, boolean replace) {
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
        @NotNull List<BlockInfo> results = getBlockInfo(string);
        for (int x = actualStartX; x < actualStartX + sizeX; x++) {
            for (int y = actualStartY; y < actualStartY + sizeY; y++) {
                for (int z = actualStartZ; z < actualStartZ + sizeZ; z++) {
                    boolean isPartOfSideWall = (x < innerStartX || x >= innerEndX) || (z < innerStartZ || z >= innerEndZ);
                    if (replace&&world.getBlockId(x, y, z)==0)continue;
                    BlockInfo result = getRandomBlockFromOdds(results);
                    if (result.id() != 0) {
                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (result.meta() > subtype.size() - 1) {
                            Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                            return list;
                        }
                    }
                    if (isPartOfSideWall) {
                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                        world.setBlock(x, y, z, result.id(), result.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        return list;
    }

}
