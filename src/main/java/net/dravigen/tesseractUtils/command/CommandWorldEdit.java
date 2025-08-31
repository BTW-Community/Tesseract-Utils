package net.dravigen.tesseractUtils.command;

import btw.block.blocks.ButtonBlock;
import net.dravigen.tesseractUtils.configs.BlockSelectionManager;
import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.utils.ShapeGen;
import net.dravigen.tesseractUtils.utils.interfaces.IClientStatusCallback;
import net.dravigen.tesseractUtils.packet.ClientRequestManager;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;

import java.util.*;

import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

public class CommandWorldEdit extends CommandBase {


    @Override
    public String getCommandName() {
        return "edit";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/edit <set> <id/metadata> [hollow:wall] [thickness] OR /edit <setblock> <x> <y> <z> <id/metadata> OR /edit <replace> <id/metadata> [id/metadata replaced] OR /edit <stack> <side> <count> OR /edit <move> <to:add> <x> <y> <z> OR /edit <rotate> [reverse] OR /edit <undo> OR /edit <copy> OR /edit <paste> [x,y,z] OR /edit <pos1> OR /edit <pos2> OR /edit <reach> <distance> OR /edit <disablePlaceCooldown> <True:False> OR /edit <disableBreakCooldown> <True:False> OR /edit <disableMomentum> <True:False> OR /edit <enableClickReplace> <True:False> OR /edit <enableNoClip> <True:False> OR /edit <enableExtraDebugInfo> <True:False>";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        String username = sender.getCommandSenderName();
        int var1 = strings.length;
        if (var1 ==1) {
            return getListOfStringsMatchingLastWord(strings,"set", "setblock", "shape", "replace", "line", "plane", "stack", "move", "rotate", "undo", "redo", "copy", "paste", "pos1", "pos2", "posAll","tool");
        }
        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(sender);
        switch (strings[0].toLowerCase()){
            case "pos1","pos2"->{
                if (blockCoord != null) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 2) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 3) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 == 4) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                } else return null;
            }
            case "posall"->{
                if (blockCoord != null) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 2||var1==5) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 3||var1==6) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 == 4||var1==7) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                } else return null;
            }
            case "setblock"->{
                if (var1 == 2) {
                    return getBlockNameList(strings);
                }
                if (blockCoord != null) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 3) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 4) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 == 5) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                }
            }
            case "shape"->{
                if (var1 == 2) {
                    return getBlockNameList(strings);
                }
                if (blockCoord != null) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 5) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 6) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 ==7) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                }
                if (var1 ==3){
                    return getListOfStringsMatchingLastWord(strings, "sphere","cylinder", "cube", "pyramid");
                }
            }
            case "set"->{
                if (var1 == 2) {
                    return getBlockNameList(strings);
                } else if (var1 == 3) {
                    return getListOfStringsMatchingLastWord(strings, "hollow", "wall");
                }
            }
            case "replace"->{
                if (var1 == 2 || var1 == 3) {
                    return getBlockNameList(strings);
                }
            }
            case "copy"->{
                return getListOfStringsMatchingLastWord(strings, "ignoreAir");
            }
            case "paste"->{
                if (blockCoord != null) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 2) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 3) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 == 4) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                } else return null;
            }
            case "move"->{
                if (var1 == 2) {
                    return getListOfStringsMatchingLastWord(strings, "to", "add");
                }
                if (blockCoord != null&&strings[1].equalsIgnoreCase("to")) {
                    int x = blockCoord.blockX;
                    int y = blockCoord.blockY;
                    int z = blockCoord.blockZ;
                    if (var1 == 3) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (var1 == 4) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (var1 == 5) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                }
            }
            case "tool"->{
                if (var1 ==2){
                    return getListOfStringsMatchingLastWord(strings, "sphere", "cylinder","cube", "pyramid", "plane");
                } else if (var1 ==3){
                    return getBlockNameList(strings);
                }else if (var1 == 4) {
                    return getListOfStringsMatchingLastWord(strings, "1:1:1:1");
                } else if (var1 == 5) {
                    return getListOfStringsMatchingLastWord(strings, "hollow", "open");
                }
            }
            case "line"->{
                if (var1 == 2) {
                    return getBlockNameList(strings);
                }
            }
            case "plane"->{
                if (var1 == 2) {
                    return getBlockNameList(strings);
                } else if (var1 == 3) {
                    return getListOfStringsMatchingLastWord(strings, "x", "z");
                }
            }
            case "rotate"->{
                if (var1 ==2){
                    return getListOfStringsMatchingLastWord(strings, "reverse");
                }
            }
            case "stack"->{
                if (var1==2){
                    return getListOfStringsMatchingLastWord(strings, "N", "E", "S", "W", "U", "D");
                }
            }
        }

        return null;
    }

    public static int numBlock=0;

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {


        World world = sender.getEntityWorld();
        numBlock = 0;
        if (strings.length == 0) sendMsg(sender, "§cInvalid command",false);
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));
        boolean ignoreAir = Boolean.parseBoolean(PacketUtils.playersInfoServer.get(sender.getCommandSenderName()).get(EnumConfig.IGNORE_AIR.ordinal()));
        int flag = Boolean.parseBoolean(PacketUtils.playersInfoServer.get(sender.getCommandSenderName()).get(EnumConfig.NO_UPDATE.ordinal())) ? 2 : 3;
        sendMsg(sender, "§dProcessing command...",mute);


        switch (strings[0]) {
            /// doesn't need pos update
            case "setblock" -> {
                try {
                    List<SavedBlock> list = new ArrayList<>();
                    int x;
                    int y;
                    int z;
                    try {
                        x = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[2]));
                        y = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[3]));
                        z = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[4]));
                    } catch (Exception ignored) {
                        x = Integer.parseInt(strings[2]);
                        y = Integer.parseInt(strings[3]);
                        z = Integer.parseInt(strings[4]);
                    }
                    List<BlockInfo> results = getBlockInfo(strings[1],sender.getCommandSenderName());
                    if (results==null){

                        sendMsg(sender, "§cWrong name or id",mute);
                        return;
                    }
                    BlockInfo result = results.get(0);
                    if (result.id() != 0) {

                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (!itemStack.getHasSubtypes()&&result.meta() > subtype.size() - 1) {
                            sendMsg(sender, "§cIndex " + result.meta() + " out of bounds for length " + (subtype.size()),mute);
                            return;
                        }
                    }

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    addToUndoListServer(list, sender);
                    clearRedoListServer(sender);
                    sendMsg(sender, "§dPlaced " + result.blockName().replace("_", " ") + " at: " + x + ", " + y + ", " + z,mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // setblock <id/metadata> <x> <y> <z>",mute);
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
                            centerX = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[4]));
                            centerY = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[5]));
                            centerZ = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[6]));
                        } catch (Exception ignored) {
                            centerX = Integer.parseInt(strings[4]);
                            centerY = Integer.parseInt(strings[5]);
                            centerZ = Integer.parseInt(strings[6]);
                        }
                    }
                    if (centerX == 999999 || centerY == 999999 || centerZ == 999999) {
                        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(sender);
                        centerX = blockCoord.blockX;
                        centerY = blockCoord.blockY;
                        centerZ = blockCoord.blockZ;
                    }
                    List<SavedBlock> list = new ArrayList<>();
                    final int replaceYIfNegative = var2 < 0 ? centerY + var2 : centerY;
                    switch (strings[2].toLowerCase()) {
                        case "sphere" -> {
                            if (hollow) {
                                list = ShapeGen.generateHollowSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace, sender);
                            } else
                                list = ShapeGen.generateSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, replace, sender);
                        }
                        case "cylinder" -> {
                            centerY = replaceYIfNegative;
                            var2 = var2 < 0 ? var2 * (-1) : var2;
                            if (hollow)
                                list = ShapeGen.generateHollowCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace, sender);
                            else if (open)
                                list = ShapeGen.generateOpenCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace, sender);
                            else
                                list = ShapeGen.generateCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace, sender);
                        }
                        case "cube" -> {
                            centerY = replaceYIfNegative;
                            var2 = var2 < 0 ? var2 * (-1) : var2;
                            if (hollow)
                                list = ShapeGen.generateHollowCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, sender);
                            else if (open)
                                list = ShapeGen.generateOpenCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, sender);
                            else
                                list = ShapeGen.generateCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, replace, sender);
                        }
                        case "pyramid" -> {
                            if (hollow)
                                list = ShapeGen.generateHollowPyramid(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, sender);
                            else
                                list = ShapeGen.generatePyramid(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, replace, sender);
                        }
                    }
                    if (!list.isEmpty()) {
                        addToUndoListServer(list, sender);
                        clearRedoListServer(sender);
                        sendMsg(sender, "§d" + numBlock + " block(s) have been placed",mute);
                    }
                } catch (Exception e) {
                    sendMsg(sender, "§c// shape <type> <id> <parameters> <x> <y> <z> [hollow|open] [replace]",mute);
                }
            }
            case "paste" -> {
                try {
                    paste(sender, strings, ignoreAir, world, flag);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // paste [x] [y] [z]",mute);
                }
            }
            case "undo" -> {
                try {
                    int num = 1;
                    for (String s:strings){
                        try {
                           num = Integer.parseInt(s);
                           break;
                        }catch (Exception ignored){
                        }
                    }

                    undo(num, sender, world, flag,mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // undo [count]",mute);
                }
            }
            case "redo" -> {
                try {
                    int num = 1;
                    for (String s:strings){
                        try {
                            num = Integer.parseInt(s);
                            break;
                        }catch (Exception ignored){
                        }
                    }

                    redo(num, sender, world, flag,mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // redo [count]",mute);
                }
            }
            case "pos1" -> {
                try {
                    int x;
                    int y;
                    int z;
                    if (strings.length >= 4) {
                        x = Integer.parseInt(strings[1]);
                        y = Integer.parseInt(strings[2]);
                        z = Integer.parseInt(strings[3]);
                    } else {
                        ChunkCoordinates coords = sender.getPlayerCoordinates();
                        x =coords.posX;
                        y =coords.posY;
                        z =coords.posZ;
                    }
                    try {
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + x + "," + y + "," + z);
                        sendMsg(sender, "§dFirst position set to (" + x + ", " + y + ", " + z + ").",mute);
                    }
                    catch (Exception e) {
                        BlockSelectionManager.setServBlock1(x,y,z);
                        sendMsg(sender, "§dFirst position set to (" + x + ", " + y + ", " + z + ").",mute);
                    }
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // pos1 [x] [y] [z]",mute);
                }
            }
            case "pos2" -> {
                try {
                    int x;
                    int y;
                    int z;
                    if (strings.length >= 4) {
                        x = Integer.parseInt(strings[1]);
                        y = Integer.parseInt(strings[2]);
                        z = Integer.parseInt(strings[3]);
                    } else {
                        ChunkCoordinates coords = sender.getPlayerCoordinates();
                        x =coords.posX;
                        y =coords.posY;
                        z =coords.posZ;
                    }
                    try {
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + x + "," + y + "," + z);
                        sendMsg(sender, "§dSecond position set to (" + x + ", " + y + ", " + z + ").",mute);
                    } catch (Exception e) {
                        BlockSelectionManager.setServBlock2(x,y,z);
                        sendMsg(sender, "§dSecond position set to (" + x + ", " + y + ", " + z + ").",mute);
                    }
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // pos2 [x] [y] [z]",mute);
                }
            }
            case "posAll" -> {
                try {
                    int x1;
                    int y1;
                    int z1;

                    x1 = Integer.parseInt(strings[1]);
                    y1 = Integer.parseInt(strings[2]);
                    z1 = Integer.parseInt(strings[3]);

                    int x2;
                    int y2;
                    int z2;

                    x2 = Integer.parseInt(strings[4]);
                    y2 = Integer.parseInt(strings[5]);
                    z2 = Integer.parseInt(strings[6]);

                    String msg = "§dPositions set to (" + x1 + ", " + y1 + ", " + z1 + ") and (" + x2 + ", " + y2 + ", " + z2 + ").";
                    try {
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + x1 + "," + y1 + "," + z1);
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + x2 + "," + y2 + "," + z2);
                        sendMsg(sender, msg,mute);
                    }
                    catch (Exception e) {
                        BlockSelectionManager.setServBlock1(x1,y1,z1);
                        BlockSelectionManager.setServBlock2(x2,y2,z2);
                        sendMsg(sender, msg,mute);
                    }
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // posAll [x1] [y1] [z1] [x2] [y2] [z2]",mute);
                }
            }
            case "tool" -> {
                try {
                    ItemStack itemStack = getPlayer(sender, sender.getCommandSenderName()).getHeldItem();
                    if (itemStack == null) return;
                    if (strings.length == 1 && itemStack.hasTagCompound()) {
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        NBTTagCompound buildingParamsNBT;
                        if (nbt.hasKey("BuildingParams")) {
                            buildingParamsNBT = nbt.getCompoundTag("BuildingParams");
                            sendMsg(sender, "§dCurrent item is a building tool with the characteristics:",mute);
                            sendMsg(sender, " §f-shape= §a" + buildingParamsNBT.getString("shape"),mute);
                            sendMsg(sender, " §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed"),mute);
                            sendMsg(sender, " §f-parameters= §a" + buildingParamsNBT.getString("parameters"),mute);
                            sendMsg(sender, " §f-volume= §a" + buildingParamsNBT.getString("volume"),mute);
                        }
                    } else if (strings.length >= 4) {
                        saveShapeTool(sender, strings, itemStack);
                    } else sendMsg(sender, "§c// tool <shape> <id> <parameters> [hollow|open]",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // tool <shape> <id> <parameters> [hollow|open]",mute);
                }
            }

            /// need pos update
            case "set", "move", "replace", "copy", "line", "rotate", "plane", "stack" -> {
                if (sender.getCommandSenderName().equalsIgnoreCase("@")){
                    areaEdit(sender, strings, BlockSelectionManager.servBlock1, BlockSelectionManager.servBlock2, world, flag, ignoreAir, mute);

                }
                else {
                    IClientStatusCallback continuationCode = (int[] block1, int[] block2) -> areaEdit(sender, strings, block1, block2, world, flag, ignoreAir, mute);
                    int requestId = ClientRequestManager.registerStatusCallback(getPlayer(sender, sender.getCommandSenderName()), continuationCode);
                    PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updateAllPos:" + requestId);

                }
            }
        }
    }

    private static void areaEdit(ICommandSender sender, String[] strings, int[] block1, int[] block2, World world, int flag, boolean ignoreAir, boolean mute) {
        final int x1 = block1[0];
        final int y1 = block1[1];
        final int z1 = block1[2];
        final int x2 = block2[0];
        final int y2 = block2[1];
        final int z2 = block2[2];
        final boolean isPosValid = x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999;
        List<SavedBlock> undoListFinal = new ArrayList<>();
        boolean replace=false;
        for (String s : strings) {
            if (s.equalsIgnoreCase("replace")) {
                replace = true;
                break;
            }
        }
        switch (strings[0]) {
            case "set" -> {
                try {
                    boolean hollow = strings.length > 2 && strings[2].equalsIgnoreCase("hollow");
                    boolean wall = strings.length > 2 && strings[2].equalsIgnoreCase("wall");
                    if (isPosValid) {
                        if (hollow || wall) {
                            int thickness = 1;
                            thickness = strings.length > 3 ? strings[3].equalsIgnoreCase("ignoreAir") || strings[3].equalsIgnoreCase("causeUpdate") ? thickness : Integer.parseInt(strings[3]) : thickness;
                            List<SavedBlock> firstPass = new ArrayList<>();
                            List<SavedBlock> secondPass = new ArrayList<>();
                            List<SavedBlock> undoOldOther = new ArrayList<>();
                            List<SavedBlock> undoOldBlock = new ArrayList<>();


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
                                List<BlockInfo> results = getBlockInfo(strings[1], sender.getCommandSenderName());
                                if (results == null) {
                                    sendMsg(sender, "§cWrong name or id",mute);
                                    return;
                                }
                                for (int j = 0; j <= absY; j++) {
                                    for (int i = 0; i <= absX; i++) {
                                        for (int k = 0; k <= absZ; k++) {
                                            BlockInfo result = getRandomBlockFromOdds(results);
                                            int x = minX + i;
                                            int y = wall ? (Math.min(y1, y2) + j) : minY + j;
                                            int z = minZ + k;
                                            if (result.id() != 0) {
                                                ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                                                List<ItemStack> subtype = new ArrayList<>();
                                                itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                                if (!itemStack.getHasSubtypes()&&result.meta() > subtype.size() - 1) {
                                                    sendMsg(sender, "§cIndex " + result.meta() + " out of bounds for length " + (subtype.size()),mute);
                                                }
                                            }
                                            SavedBlock savedBlock = new SavedBlock(x, y, z, result.id(), result.meta());
                                            if (x == maxX || z == maxZ || x == minX || z == minZ) {
                                                //world.setBlock(x, y, z, result.id(), result.meta(), flag);
                                                numBlock++;
                                                Block block = Block.blocksList[result.id()];
                                                if (block != null && !block.isOpaqueCube()) {
                                                    secondPass.add(savedBlock);
                                                    undoOldOther.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));

                                                } else {
                                                    firstPass.add(savedBlock);
                                                    undoOldBlock.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));

                                                }
                                            } else if ((y == maxY || y == minY) && hollow) {
                                                //world.setBlock(x, y, z, result.id(), result.meta(), flag);
                                                numBlock++;
                                                Block block = Block.blocksList[result.id()];
                                                if (block != null && !block.isOpaqueCube()) {
                                                    secondPass.add(savedBlock);
                                                    undoOldOther.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));

                                                } else {
                                                    firstPass.add(savedBlock);
                                                    undoOldBlock.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));

                                                }
                                            } else if (!ignoreAir) {
                                                undoOldOther.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                //world.setBlock(x, y, z, 0, 0, flag);
                                                secondPass.add(new SavedBlock(x, y, z, 0, 0));

                                                numBlock++;
                                            }
                                        }
                                    }
                                }
                            }
                            List<SavedBlock> blocks = new ArrayList<>();
                            blocks.addAll(firstPass);
                            blocks.addAll(secondPass);

                            for (SavedBlock block:blocks)world.setBlock(block.x(),block.y(),block.z(),block.id(),block.meta(),flag);

                            undoListFinal.addAll(undoOldBlock);
                            undoListFinal.addAll(undoOldOther);

                            if (!undoListFinal.isEmpty()) {
                                addToUndoListServer(undoListFinal, sender);
                                clearRedoListServer(sender);
                            }
                            String msg = "§d" + numBlock + " block(s) have been placed";
                            sendMsg(sender, msg,mute);
                        } else setArea(strings, y1, y2, x1, x2, z1, z2, sender, world, flag);
                    } else
                        sendMsg(sender, "§cYou need to select an area",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // set <id/metadata:odds",mute);
                }
            }
            case "move" -> {
                try {
                    if (strings.length > 1&& (strings[1].equalsIgnoreCase("to") || strings[1].equalsIgnoreCase("add"))) {
                        int xPos = 0;
                        int yPos = 0;
                        int zPos = 0;
                        if (strings[1].equalsIgnoreCase("to")) {
                            if (strings.length > 4) {
                                try {
                                    xPos = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[2]));
                                    yPos = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[3]));
                                    zPos = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[4]));
                                } catch (Exception ignored) {
                                    xPos = Integer.parseInt(strings[2]);
                                    yPos = Integer.parseInt(strings[3]);
                                    zPos = Integer.parseInt(strings[4]);
                                }
                            } else {
                                xPos = sender.getPlayerCoordinates().posX;
                                yPos = sender.getPlayerCoordinates().posY;
                                zPos = sender.getPlayerCoordinates().posZ;
                            }
                        } else if (strings[1].equalsIgnoreCase("add")) {
                            if (strings.length > 4) {
                                xPos = Integer.parseInt(strings[2]);
                                yPos = Integer.parseInt(strings[3]);
                                zPos = Integer.parseInt(strings[4]);
                            }
                        }
                        if (isPosValid) {
                            List<SavedBlock> undoOldBlock = new ArrayList<>();
                            List<SavedBlock> undoOldOther = new ArrayList<>();

                            int absY = MathHelper.abs_int(y1 - y2);
                            int absZ = MathHelper.abs_int(z1 - z2);
                            int absX = MathHelper.abs_int(x1 - x2);

                            for (int i = 0; i <= absX; i++) {
                                for (int k = 0; k <= absZ; k++) {
                                    for (int j = 0; j <= absY; j++) {
                                        int x = Math.min(x1, x2) + i;
                                        int y = Math.min(y1, y2) + j;
                                        int z = Math.min(z1, z2) + k;
                                        int id = world.getBlockId(x, y, z);
                                        int meta = world.getBlockMetadata(x, y, z);
                                        SavedBlock savedBlock = new SavedBlock(x, y, z, id, meta);
                                        Block block = Block.blocksList[id];

                                        if (!ignoreAir || !world.isAirBlock(x, y, z)) {

                                            if (block != null && !block.isOpaqueCube()) {
                                                undoOldOther.add(savedBlock);

                                            } else {
                                                undoOldBlock.add(savedBlock);

                                            }
                                            numBlock++;

                                        }
                                    }
                                }
                            }

                            List<SavedBlock> undoNewBlock = new ArrayList<>();
                            List<SavedBlock> undoNewOther = new ArrayList<>();

                            if (!undoOldOther.isEmpty()) for (SavedBlock block:undoOldOther) world.setBlock(block.x(), block.y(), block.z(),0,0,2);
                            if (!undoOldBlock.isEmpty()) for (SavedBlock block:undoOldBlock) world.setBlock(block.x(), block.y(), block.z(),0,0,2);

                            int xa = 9999999;
                            int ya = 9999999;
                            int za = 9999999;
                            int xb = -9999999;
                            int yb = -9999999;
                            int zb = -9999999;


                            if (!undoOldBlock.isEmpty()) {
                                for (SavedBlock block:undoOldBlock){
                                    int newX = xPos + block.x();
                                    int newY = yPos + block.y();
                                    int newZ = zPos + block.z();

                                    if (strings[1].equalsIgnoreCase("to")) {
                                        newX -= Math.max(x1, x2);
                                        newY -= Math.min(y1, y2);
                                        newZ -= Math.max(z1, z2);
                                    }
                                    undoNewBlock.add(new SavedBlock(newX,newY,newZ,world.getBlockId(newX,newY,newZ),world.getBlockMetadata(newX,newY,newZ)));
                                    world.setBlock(newX,newY,newZ,block.id(),block.meta(),flag);

                                    xa = Math.min(newX, xa);
                                    ya = Math.min(newY, ya);
                                    za = Math.min(newZ, za);
                                    xb = Math.max(newX, xb);
                                    yb = Math.max(newY, yb);
                                    zb = Math.max(newZ, zb);

                                }
                            }
                            if (!undoOldOther.isEmpty()) {
                                for (SavedBlock block : undoOldOther) {
                                    int newX = xPos + block.x();
                                    int newY = yPos + block.y();
                                    int newZ = zPos + block.z();

                                    if (strings[1].equalsIgnoreCase("to")) {
                                        newX -= Math.max(x1, x2);
                                        newY -= Math.min(y1, y2);
                                        newZ -= Math.max(z1, z2);
                                    }
                                    undoNewOther.add(new SavedBlock(newX,newY,newZ,world.getBlockId(newX,newY,newZ),world.getBlockMetadata(newX,newY,newZ)));
                                    world.setBlock(newX,newY,newZ,block.id(),block.meta(),flag);

                                    xa = Math.min(newX, xa);
                                    ya = Math.min(newY, ya);
                                    za = Math.min(newZ, za);
                                    xb = Math.max(newX, xb);
                                    yb = Math.max(newY, yb);
                                    zb = Math.max(newZ, zb);


                                }
                            }

                            try {
                                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + xa + "," + ya + "," + za);
                                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + xb + "," + yb + "," + zb);

                            } catch (Exception e) {
                                BlockSelectionManager.setServBlock1(xa, ya,za);
                                BlockSelectionManager.setServBlock2(xb, yb,zb);
                            }

                            undoListFinal.addAll(undoNewOther);
                            undoListFinal.addAll(undoNewBlock);
                            undoListFinal.addAll(undoOldBlock);
                            undoListFinal.addAll(undoOldOther);

                            if (!undoListFinal.isEmpty()) {
                                addToUndoListServer(undoListFinal, sender);
                                clearRedoListServer(sender);
                                sendMsg(sender, "§d" + numBlock + " block(s) have been moved",mute);
                            }
                        }else sendMsg(sender, "§cYou need to select an area",mute);
                    }else sendMsg(sender, "§cWrong usage: // newSelection <to|add> <x> <y> <z>",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // newSelection <to|add> <x> <y> <z>",mute);
                }
            }
            case "replace" -> {
                try {
                    if (strings.length > 1) {
                        if (isPosValid)
                            replaceArea(sender, strings, y1, y2, x1, x2, z1, z2, world, flag);
                        else
                            sendMsg(sender, "§cYou need to select an area",mute);
                    } else
                        sendMsg(sender, "§cWrong usage: // replace <id> [id replaced]",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // replace <id> [id replaced]",mute);
                }
            }
            case "copy" -> {
                try {
                    copy(sender, isPosValid, x1, x2, y1, y2, z1, z2, ignoreAir, world,mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // copy",mute);
                }
            }
            case "line" -> {
                try {
                    if (strings.length == 1) throw new Exception();
                    if (isPosValid) {
                        undoListFinal = ShapeGen.buildLine(world, strings, x1, y1, z1, x2, y2, z2, strings.length > 2 ? Integer.parseInt(strings[2]) : 1, flag, sender);
                        if (!undoListFinal.isEmpty()) {
                            addToUndoListServer(undoListFinal, sender);
                            clearRedoListServer(sender);
                        }
                        sendMsg(sender, "§d" + numBlock + " block(s) have been placed",mute);

                    } else
                        sendMsg(sender, "§cYou need to select an area",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // line <id> [thickness]",mute);
                }
            }
            case "plane" -> {
                try {
                    if (strings.length == 1) throw new Exception();
                    if (isPosValid) {
                        int side = strings.length > 2 ? strings[2].equalsIgnoreCase("z") ? 1 : 0 : 0;
                        int thickness = strings.length > 3 ? Integer.parseInt(strings[3]) : 1;

                        undoListFinal = ShapeGen.buildPlane(world, strings[1], x1, y1, z1, x2, y2, z2, thickness, side, replace, flag, sender);

                        if (!undoListFinal.isEmpty()) {
                            addToUndoListServer(undoListFinal, sender);
                            clearRedoListServer(sender);
                        }
                        sendMsg(sender, "§d" + numBlock + " block(s) have been placed",mute);
                    } else
                        sendMsg(sender, "§cYou need to select an area",mute);
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // plane <id> [x:z] [thickness]",mute);
                }
            }
            case "rotate" -> {
                try {
                    boolean clockwise;
                    clockwise = strings.length == 1 || !strings[1].equalsIgnoreCase("reverse");

                    List<SavedBlock> solidBlockInitial = new ArrayList<>();
                    List<SavedBlock> nonSolidBlockInitial = new ArrayList<>();

                    List<SavedBlock> undoList = new ArrayList<>();

                    int absY = MathHelper.abs_int(y1 - y2);
                    int absZ = MathHelper.abs_int(z1 - z2);
                    int absX = MathHelper.abs_int(x1 - x2);


                    List<SavedBlock> undoOldOther = new ArrayList<>();
                    List<SavedBlock> undoOldBlock = new ArrayList<>();

                    for (int i = 0; i <= absX; i++) {
                        for (int k = 0; k <= absZ; k++) {
                            for (int j = 0; j <= absY; j++) {
                                int x = Math.min(x1, x2) + i;
                                int y = Math.min(y1, y2) + j;
                                int z = Math.min(z1, z2) + k;
                                int id = world.getBlockId(x, y, z);
                                int meta = world.getBlockMetadata(x,y,z);
                                Block block = Block.blocksList[id];

                                SavedBlock savedBlock = new SavedBlock(x, y, z, id, meta);
                                SavedBlock newSavedBlock = new SavedBlock(x - Math.min(x1, x2), y - Math.min(y1, y2), z - Math.min(z1, z2), id, meta);
                                if (block!=null&&!block.isOpaqueCube()){
                                    nonSolidBlockInitial.add(newSavedBlock);
                                    undoOldOther.add(savedBlock);

                                }

                                else {
                                    solidBlockInitial.add(newSavedBlock);
                                    undoOldBlock.add(savedBlock);

                                }

                                numBlock++;
                            }
                        }
                    }

                    if (!undoOldOther.isEmpty()) for (SavedBlock block:undoOldOther)world.setBlock(block.x(), block.y(), block.z(),0,0,2);

                    if (!undoOldBlock.isEmpty()) for (SavedBlock block:undoOldBlock)world.setBlock(block.x(), block.y(), block.z(),0,0,2);

                    List<SavedBlock> nonSolidBlockEndList = new ArrayList<>();
                    List<SavedBlock> solidBlockEndList = new ArrayList<>();

                    if (!solidBlockInitial.isEmpty())
                        for (SavedBlock block : solidBlockInitial) {
                        solidBlockEndList.add(new SavedBlock(
                                clockwise ? -block.z() + absX : block.z(),
                                block.y(),
                                clockwise ? block.x() : -block.x() + absZ,
                                block.id(),
                                block.meta()));
                    }
                    if (!nonSolidBlockInitial.isEmpty())
                        for (SavedBlock block : nonSolidBlockInitial) {
                        nonSolidBlockEndList.add(new SavedBlock(
                                clockwise ? -block.z() + absX : block.z(),
                                block.y(),
                                clockwise ? block.x() : -block.x() + absZ,
                                block.id(),
                                block.meta()));
                    }

                    List<SavedBlock> undoNewBlock = new ArrayList<>();
                    List<SavedBlock> undoNewOther = new ArrayList<>();
                    int xa = 9999999;
                    int za = 9999999;
                    int xb = -9999999;
                    int zb = -9999999;

                    if (!solidBlockEndList.isEmpty()) {
                        for (SavedBlock block:solidBlockEndList){
                            int newX = block.x() + Math.min(x1, x2);
                            int newY = block.y() + Math.min(y1, y2);
                            int newZ = block.z() + Math.min(z1, z2);
                            xa = Math.min(newX, xa);
                            za = Math.min(newZ, za);
                            xb = Math.max(newX, xb);
                            zb = Math.max(newZ, zb);

                            rotate(world, flag, block, newX, newY, newZ, undoNewBlock, clockwise);
                        }
                    }
                    if (!nonSolidBlockEndList.isEmpty()) {
                        for (SavedBlock block : nonSolidBlockEndList) {
                            int newX = block.x() + Math.min(x1, x2);
                            int newY = block.y() + Math.min(y1, y2);
                            int newZ = block.z() + Math.min(z1, z2);
                            xa = Math.min(newX, xa);
                            za = Math.min(newZ, za);
                            xb = Math.max(newX, xb);
                            zb = Math.max(newZ, zb);

                            rotate(world, flag, block, newX, newY, newZ, undoNewOther, clockwise);

                        }
                    }
                    try {
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + xa + "," + y1 + "," + za);
                        PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + xb + "," + y2 + "," + zb);

                    } catch (Exception e) {
                        BlockSelectionManager.setServBlock1(xa, y1,za);
                        BlockSelectionManager.setServBlock2(xb, y2,zb);
                    }

                    undoList.addAll(undoNewOther);
                    undoList.addAll(undoNewBlock);
                    undoList.addAll(undoOldBlock);
                    undoList.addAll(undoOldOther);

                    sendMsg(sender, "§d" + numBlock + " block(s) have been rotated",mute);

                    if (!undoList.isEmpty()) {
                        addToUndoListServer(undoList, sender);
                        clearRedoListServer(sender);
                    }
                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // rotate [reverse]",mute);
                }
            }
            case "stack"-> {
                try {

                    int absY = MathHelper.abs_int(y1 - y2);
                    int absZ = MathHelper.abs_int(z1 - z2);
                    int absX = MathHelper.abs_int(x1 - x2);

                    List<SavedBlock> secondPass = new ArrayList<>();
                    List<SavedBlock> firstPass = new ArrayList<>();
                    for (int i = 0; i <= absX; i++) {
                        for (int k = 0; k <= absZ; k++) {
                            for (int j = 0; j <= absY; j++) {
                                int x = Math.min(x1, x2) + i;
                                int y = Math.min(y1, y2) + j;
                                int z = Math.min(z1, z2) + k;
                                int id = world.getBlockId(x, y, z);
                                int meta = world.getBlockMetadata(x, y, z);
                                Block block = Block.blocksList[id];

                                SavedBlock savedBlock = new SavedBlock(x, y, z, id, meta);
                                if (block != null && !block.isOpaqueCube()) {
                                    secondPass.add(savedBlock);
                                } else {
                                    firstPass.add(savedBlock);
                                }

                                numBlock++;
                            }
                        }
                    }

                    List<SavedBlock> undoList = new ArrayList<>();

                    if(!firstPass.isEmpty()) stack(strings, world, flag, y1, y2, x1, x2, z1, z2, firstPass, undoList);
                    if(!secondPass.isEmpty()) stack(strings, world, flag, y1, y2, x1, x2, z1, z2, secondPass, undoListFinal);


                    undoListFinal.addAll(undoList);

                    if (!undoListFinal.isEmpty()) {
                        addToUndoListServer(undoListFinal, sender);
                        clearRedoListServer(sender);
                    }
                    sendMsg(sender, "§d" + numBlock + " block(s) have been placed",mute);

                } catch (Exception e) {
                    sendMsg(sender, "§cWrong usage: // stack [side] [count]",mute);
                }
            }
        }
    }

    private static void rotate(World world, int flag, SavedBlock block, int newX, int newY, int newZ, List<SavedBlock> undoList, boolean clockwise) {

            Block currentBlock = Block.blocksList[block.id()];

            if (currentBlock != null) {
                undoList.add(new SavedBlock(newX, newY, newZ, world.getBlockId(newX, newY, newZ), world.getBlockMetadata(newX, newY, newZ)));

                if (currentBlock instanceof ButtonBlock || currentBlock instanceof BlockLever) {
                    int i = block.meta();
                    int i2 = i & 8;
                    i &= 7;
                    i = clockwise ? (i == 1 ? 3 : i == 3 ? 2 : i == 2 ? 4 : 1) : i == 1 ? 4 : i == 4 ? 2 : i == 2 ? 3 : 1;
                    if (!(currentBlock instanceof ButtonBlock)) i |= i2;

                    world.setBlock(newX, newY, newZ, block.id(), i, flag);

                } else if (currentBlock instanceof BlockSign sign) {
                    int i = block.meta();
                    if (sign.isFreestanding) i = i + (clockwise ? 4 : -4) & 15;
                    else
                        i = clockwise ? (i == 2 ? 5 : i == 5 ? 3 : i == 3 ? 4 : 2) : i == 2 ? 4 : i == 4 ? 3 : i == 3 ? 5 : 2;

                    world.setBlock(newX, newY, newZ, block.id(), i, flag);

                } else {
                    int iMetadata = block.meta();
                    int iNewMetadata = currentBlock.rotateMetadataAroundYAxis(iMetadata, clockwise);

                    world.setBlock(newX, newY, newZ, block.id(), iNewMetadata, flag);

                }
            }

    }

    private static void stack(String[] strings, World world, int flag, int y1, int y2, int x1, int x2, int z1, int z2, List<SavedBlock> blocks, List<SavedBlock> undoList) {
        int count = Integer.parseInt(strings[2]);
        boolean xP = strings[1].equalsIgnoreCase("E");
        boolean xN = strings[1].equalsIgnoreCase("W");
        boolean yP = strings[1].equalsIgnoreCase("U");
        boolean yN = strings[1].equalsIgnoreCase("D");
        boolean zP = strings[1].equalsIgnoreCase("S");
        boolean zN = strings[1].equalsIgnoreCase("N");
        int height = Math.abs(y1 - y2)+1;
        int width = Math.abs(x1 - x2)+1;
        int length = Math.abs(z1 - z2)+1;
        for (int i = 1; i <= count; i++) {
            for (SavedBlock block : blocks) {
                int newX = block.x() + (xP ? width : xN ? -width : 0)*i;
                int newY = block.y() + (yP ? height : yN ? -height : 0)*i;
                int newZ = block.z() + (zP ? length : zN ? -length : 0)*i;
                undoList.add(new SavedBlock(newX,newY,newZ, world.getBlockId(newX,newY,newZ), world.getBlockMetadata(newX,newY,newZ)));
                world.setBlock(newX, newY, newZ, block.id(), block.meta(), flag);
            }
        }
    }

    private static void sendMsg(ICommandSender sender, String msg, boolean mute) {
        if (mute) return;
        notifyAdmins(sender,msg);
    }

    public static void saveShapeTool(ICommandSender sender, String[] strings, ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));

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
        buildingParamsNBT.setString("volume", strings.length>4?strings[4]:"full");

        for (String s : strings) {
            switch (s) {
                case "hollow" -> buildingParamsNBT.setString("volume", "hollow");
                case "open" -> buildingParamsNBT.setString("volume", "open");
            }
        }
        sendMsg(sender, "§dCurrent item got transformed into a building tool with the characteristics:",mute);
        sendMsg(sender, " §f-shape= §a" + buildingParamsNBT.getString("shape"),mute);
        sendMsg(sender, " §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed"),mute);
        sendMsg(sender, " §f-parameters= §a" + buildingParamsNBT.getString("parameters"),mute);
        sendMsg(sender, " §f-volume= §a" + buildingParamsNBT.getString("volume"),mute);
    }

    public static void redo(int num, ICommandSender sender, World world, int flag, boolean mute) {
        if (!getRedoList(sender).isEmpty()) {
            int count = 0;
            /*int xa = 9999999;
            int ya = 9999999;
            int za = 9999999;
            int yb = -9999999;
            int xb = -9999999;
            int zb = -9999999;*/

            for (int j = 0; j < num; j++) {
                if (getRedoList(sender).isEmpty()) break;

                List<SavedBlock> list = new ArrayList<>();

                for (SavedBlock undoBlock : getRedoList(sender).get(getRedoList(sender).size() - 1)) {
                    list.add(new SavedBlock(undoBlock.x(), undoBlock.y(), undoBlock.z(), world.getBlockId(undoBlock.x(), undoBlock.y(), undoBlock.z()), world.getBlockMetadata(undoBlock.x(), undoBlock.y(), undoBlock.z())));
                }

                for (int i = getRedoList(sender).get(getRedoList(sender).size() - 1).size() - 1; i >= 0; i--) {
                    SavedBlock block = getRedoList(sender).get(getRedoList(sender).size() - 1).get(i);
                    world.setBlock(block.x(), block.y(), block.z(), block.id(), block.meta(), flag);
                    /*xa = Math.min(block.x(), xa);
                    ya = Math.min(block.y(), ya);
                    za = Math.min(block.z(), za);
                    xb = Math.max(block.x(), xb);
                    yb = Math.max(block.y(), yb);
                    zb = Math.max(block.z(), zb);*/
                }

                addToUndoListServer(list, sender);
                removeToRedoList(sender);
            }
            /*
            try {
                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + xa + "," + ya + "," + za);
                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + xb + "," + yb + "," + zb);
            } catch (Exception e) {
                BlockSelectionManager.setServBlock1(xa,ya,za);
                BlockSelectionManager.setServBlock2(xb,yb,zb);

            }*/
            sendMsg(sender, "§dThe last " + (count > 1 ? count + " " : "") + "action" + (count > 1 ? "s have been" : " has been") + " redone",mute);
        }else sendMsg(sender, "§dThere is no action to redo",mute);
    }

    public static void undo(int num, ICommandSender sender, World world, int flag, boolean mute) {
        if (!getUndoList(sender).isEmpty()) {
            int count = 0;
           /* int xa = 9999999;
            int ya = 9999999;
            int za = 9999999;
            int yb = -9999999;
            int xb = -9999999;
            int zb = -9999999;*/

            for (int i = 0; i < num; i++) {
                if (getUndoList(sender).isEmpty()) break;

                List<SavedBlock> list = new ArrayList<>();
                for (SavedBlock block : getUndoList(sender).get(getUndoList(sender).size() - 1)) {
                    int x = block.x();
                    int y = block.y();
                    int z = block.z();
                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, block.id(), block.meta(), flag);
                    numBlock++;
                   /* xa = Math.min(x, xa);
                    ya = Math.min(y, ya);
                    za = Math.min(z, za);
                    xb = Math.max(x, xb);
                    yb = Math.max(y, yb);
                    zb = Math.max(z, zb);*/

                }

                addToRedoList(list, sender);
                removeFromList(sender);
                count++;
            }
            /*
            try {
                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos1:" + xa + "," + ya + "," + za);
                PacketSender.sendServerToClientMessage(getPlayer(sender, sender.getCommandSenderName()), "updatePos2:" + xb + "," + yb + "," + zb);
            } catch (Exception ignored) {
                BlockSelectionManager.setServBlock1(xa,ya,za);
                BlockSelectionManager.setServBlock2(xb,yb,zb);

            }*/

            sendMsg(sender, "§dThe last " + (count > 1 ? count + " " : "") + "action" + (count > 1 ? "s have been" : " has been") + " reverted",mute);
        }else sendMsg(sender, "§dThere is no action to undo",mute);
    }

    public static void replaceArea(ICommandSender sender, String[] strings, int y1, int y2, int x1, int x2, int z1, int z2, World world, int flag) {
        numBlock=0;
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));

        List<SavedBlock> list = new ArrayList<>();
        List<BlockInfo> results1 = getBlockInfo(strings[1], sender.getCommandSenderName());
        if (results1==null){
            sendMsg(sender, "§cWrong name or id",mute);
            return;
        }
        List<BlockInfo> results2 = new ArrayList<>();
        if (strings.length > 2) {
            results2 = getBlockInfo(strings[2], sender.getCommandSenderName());
        }
        if (results2==null){
            sendMsg(sender, "§cWrong name or id",mute);
            return;
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
                            sendMsg(sender, "§cIndex " + result1.meta() + " out of bounds for length " + (subtype.size()),mute);
                            return;
                        }
                    }
                    int x = Math.min(x1, x2) + i;
                    int y = Math.min(y1, y2) + j;
                    int z = Math.min(z1, z2) + k;
                    if (strings.length > 2) {
                        for (BlockInfo result2 : results2) {
                            if (result2 != null && world.getBlockId(x, y, z) == result2.id() && world.getBlockMetadata(x, y, z) == result2.meta()) {
                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                world.setBlock(x, y, z, result1.id(), result1.meta(), flag);
                                numBlock++;
                            }
                        }
                    } else if (!world.isAirBlock(x, y, z)) {
                        list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                        world.setBlock(x, y, z, result1.id(), result1.meta(), flag);
                        numBlock++;
                    }
                }
            }
        }
        sendMsg(sender, "§d" + numBlock + " block(s) was replaced",mute);
        if (!list.isEmpty()) {
            addToUndoListServer(list, sender);
            clearRedoListServer(sender);
        }
    }

    public static void setArea(String[] strings, int y1, int y2, int x1, int x2, int z1, int z2, ICommandSender sender, World world, int flag) {
        numBlock=0;
        List<BlockInfo> results = getBlockInfo(strings[1], sender.getCommandSenderName());
        List<SavedBlock> list = new ArrayList<>();
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));
        if (results==null){
            sendMsg(sender, "§cWrong name or id",mute);
            return;
        }
        List<SavedBlock> firstPass = new ArrayList<>();
        List<SavedBlock> secondPass = new ArrayList<>();

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
                            sendMsg(sender, "§cIndex " + result.meta() + " out of bounds for length " + (subtype.size()),mute);
                        }
                    }
                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    Block block = Block.blocksList[result.id()];
                    SavedBlock savedBlock = new SavedBlock(x, y, z, result.id(), result.meta());
                    if (block != null && !block.isOpaqueCube()) {
                        secondPass.add(savedBlock);

                    } else {
                        firstPass.add(savedBlock);

                    }
                    //world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    numBlock++;
                }
            }
        }
        List<SavedBlock> blocks = new ArrayList<>();
        blocks.addAll(firstPass);
        blocks.addAll(secondPass);

        for (SavedBlock block:blocks)world.setBlock(block.x(),block.y(),block.z(),block.id(),block.meta(),flag);

        if (!list.isEmpty()) {
            addToUndoListServer(list, sender);
            clearRedoListServer(sender);
        }
        sendMsg(sender, "§d" + numBlock + " block(s) have been placed",mute);
    }

    public static void paste(ICommandSender sender, String[] strings, boolean ignoreAir, World world, int causeUpdate) {
        numBlock = 0;
        List<SavedBlock> list = new ArrayList<>();
        int x;
        int y;
        int z;
        if (strings.length > 1) {
            try {
                x = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[1]));
                y = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[2]));
                z = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[3]));
            } catch (Exception ignored) {
                x = Integer.parseInt(strings[1]);
                y = Integer.parseInt(strings[2]);
                z = Integer.parseInt(strings[3]);
            }
        } else {
            x = sender.getPlayerCoordinates().posX;
            y = sender.getPlayerCoordinates().posY;
            z = sender.getPlayerCoordinates().posZ;
        }
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));
        if (!getCopyList(sender).isEmpty()) {
            for (SavedBlock block : getCopyList(sender)) {
                if (ignoreAir) {
                    if (block.id() != 0) {
                        list.add(new SavedBlock(block.x() + x, block.y() + y, block.z() + z, world.getBlockId(block.x() + x, block.y() + y, block.z() + z), world.getBlockMetadata(block.x() + x, block.y() + y, block.z() + z)));
                        world.setBlock(block.x() + x, block.y() + y, block.z() + z, block.id(), block.meta(), causeUpdate);
                        numBlock++;
                    }
                } else {
                    list.add(new SavedBlock(block.x() + x, block.y() + y, block.z() + z, world.getBlockId(block.x() + x, block.y() + y, block.z() + z), world.getBlockMetadata(block.x() + x, block.y() + y, block.z() + z)));
                    world.setBlock(block.x() + x, block.y() + y, block.z() + z, block.id(), block.meta(), causeUpdate);
                    numBlock++;
                }
            }
            sendMsg(sender, "§d" + numBlock + " block(s) have been pasted",mute);
            if (!list.isEmpty()) {
                addToUndoListServer(list, sender);
                clearRedoListServer(sender);
            }
        }else sendMsg(sender, "§cYou need to copy an area first",mute);
    }

    public static void copy(ICommandSender sender, boolean isPosValid, int x1, int x2, int y1, int y2, int z1, int z2, boolean finalIgnoreAir, World world, boolean mute) {
        numBlock = 0;
        clearCopyList(sender);
        List<SavedBlock> list = new ArrayList<>();
        if (isPosValid) {
            for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                    for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                        int x = Math.min(x1, x2) + i;
                        int y = Math.min(y1, y2) + j;
                        int z = Math.min(z1, z2) + k;

                        if (finalIgnoreAir) {
                            if (!world.isAirBlock(x, y, z)) {
                                list.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                numBlock++;
                            }
                        } else {
                            list.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                            numBlock++;
                        }
                    }
                }
            }
            saveCopyList(list, sender);
            sendMsg(sender, "§d" + numBlock + " block(s) have been copied",mute);
        } else sendMsg(sender, "§cYou need to select an area",mute);
    }

    private static List<SavedBlock> getCopyList(ICommandSender player) {
        List<SavedBlock> copyList = PacketUtils.playersCopyServer.get(player.getCommandSenderName());
        if (copyList==null){
            return new ArrayList<>();
        }else return copyList;
    }

    private static List<List<SavedBlock>> getUndoList(ICommandSender player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getCommandSenderName());
        if (undoList==null){
            return new ArrayList<>();
        }else return undoList;
    }

    private static List<List<SavedBlock>> getRedoList(ICommandSender player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getCommandSenderName());
        if (redoList==null){
            return new ArrayList<>();
        }else return redoList;
    }

    private static void removeFromList(ICommandSender player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getCommandSenderName());
        undoList.remove(undoList.size()-1);
        PacketUtils.playersUndoListServer.put(player.getCommandSenderName(),undoList);
    }

    private static void removeToRedoList(ICommandSender player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getCommandSenderName());
        redoList.remove(redoList.size()-1);
        PacketUtils.playersRedoListServer.put(player.getCommandSenderName(),redoList);
    }

    public static void saveCopyList(List<SavedBlock> list, ICommandSender player) {
        List<SavedBlock> copyList = PacketUtils.playersCopyServer.get(player.getCommandSenderName());
        if (copyList==null){
            copyList=new ArrayList<>();
        }
        copyList.addAll(list);
        PacketUtils.playersCopyServer.put(player.getCommandSenderName(),copyList);
    }

    private static void addToRedoList(List<SavedBlock> list, ICommandSender player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getCommandSenderName());
        if (redoList==null){
            redoList=new ArrayList<>();
        }
        redoList.add(list);
        PacketUtils.playersRedoListServer.put(player.getCommandSenderName(),redoList);
    }

    public static void addToUndoListServer(List<SavedBlock> list1, ICommandSender player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getCommandSenderName());
        if (undoList==null){
            undoList=new ArrayList<>();
        }
        undoList.add(list1);
        PacketUtils.playersUndoListServer.put(player.getCommandSenderName(),undoList);
    }

    public static void clearRedoListServer(ICommandSender player) {
        PacketUtils.playersRedoListServer.remove(player.getCommandSenderName());
    }

    public static void clearCopyList(ICommandSender player) {
        PacketUtils.playersCopyServer.remove(player.getCommandSenderName());
    }

}
