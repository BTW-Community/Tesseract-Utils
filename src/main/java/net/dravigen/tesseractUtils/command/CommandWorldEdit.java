package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.utils.ShapeGen;
import net.dravigen.tesseractUtils.utils.interfaces.IClientStatusCallback;
import net.dravigen.tesseractUtils.packet.ClientRequestManager;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

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
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        String username = sender.getCommandSenderName();
        if (strings.length==1) {
            return getListOfStringsMatchingLastWord(strings,"set", "setblock", "shape", "replace", "line", "plane", "move", "undo", "redo", "copy", "paste", "pos1", "pos2","tool");
        }
        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(sender);
        switch (strings[0].toLowerCase()){
            case "pos1","pos2"->{
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
            case "setblock"->{
                if (strings.length == 2) {
                    return getBlockNameList(strings, username);
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
            case "shape"->{
                if (strings.length == 2) {
                    return getBlockNameList(strings,username);
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
                    return getListOfStringsMatchingLastWord(strings, "sphere","cylinder", "cube", "pyramid");
                }
            }
            case "set"->{
                if (strings.length == 2) {
                    return getBlockNameList(strings,username);
                } else if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, "hollow", "wall");
                }
            }
            case "replace"->{
                if (strings.length == 2 || strings.length == 3) {
                    return getBlockNameList(strings,username);
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
                    if (strings.length == 2) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                    } else if (strings.length == 3) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                    } else if (strings.length == 4) {
                        return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                    }
                } else return null;
            }
            case "move"->{
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
            case "tool"->{
                if (strings.length==2){
                    return getListOfStringsMatchingLastWord(strings, "sphere", "cylinder","cube", "pyramid", "plane");
                } else if (strings.length==3){
                    return getBlockNameList(strings,username);
                }else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, "1:1:1:1");
                } else if (strings.length == 5) {
                    return getListOfStringsMatchingLastWord(strings, "hollow", "open");
                }
            }
            case "line"->{
                if (strings.length == 2) {
                    return getBlockNameList(strings,username);
                }
            }
            case "plane"->{
                if (strings.length == 2) {
                    return getBlockNameList(strings,username);
                } else if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, "x", "z");
                }
            }
        }

        return null;
    }

    public static int numBlock=0;

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        EntityPlayerMP player = getPlayer(sender, sender.getCommandSenderName());
        World world = sender.getEntityWorld();
        numBlock = 0;
        if (strings.length == 0) player.sendChatToPlayer(ChatMessageComponent.createFromText("§cInvalid command"));
        boolean ignoreAir = Boolean.parseBoolean(PacketUtils.playersInfoServer.get(player.getEntityName()).get(EnumConfig.IGNORE_AIR.ordinal()));
        int flag = Boolean.parseBoolean(PacketUtils.playersInfoServer.get(player.getEntityName()).get(EnumConfig.NO_UPDATE.ordinal())) ? 2 : 3;
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

                        getPlayer(sender,sender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
                        return;
                    }
                    BlockInfo result = results.get(0);
                    if (result.id() != 0) {

                        ItemStack itemStack = new ItemStack(result.id(), 0, 0);
                        List<ItemStack> subtype = new ArrayList<>();
                        itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                        if (result.meta() > subtype.size() - 1) {
                            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                            return;
                        }
                    }

                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    addToUndoListServer(list, player);
                    clearRedoListServer(player);
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("§dPlaced " + result.blockName().replace("_", " ") + " at: " + x + ", " + y + ", " + z));
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // setblock <id/metadata> <x> <y> <z>"));
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
                                list = ShapeGen.generateHollowSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace, player);
                            } else
                                list = ShapeGen.generateSphere(world, centerX, centerY, centerZ, strings[1], flag, var1, replace, player);
                        }
                        case "cylinder" -> {
                            centerY = replaceYIfNegative;
                            var2 = var2 < 0 ? var2 * (-1) : var2;
                            if (hollow)
                                list = ShapeGen.generateHollowCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace, player);
                            else if (open)
                                list = ShapeGen.generateOpenCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, var3, replace, player);
                            else
                                list = ShapeGen.generateCylinder(world, centerX, centerY, centerZ, strings[1], flag, var1, var2, replace, player);
                        }
                        case "cube" -> {
                            centerY = replaceYIfNegative;
                            var2 = var2 < 0 ? var2 * (-1) : var2;
                            if (hollow)
                                list = ShapeGen.generateHollowCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, player);
                            else if (open)
                                list = ShapeGen.generateOpenCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, player);
                            else
                                list = ShapeGen.generateCube(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, replace, player);
                        }
                        case "pyramid" -> {
                            if (hollow)
                                list = ShapeGen.generateHollowPyramid(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, var4, replace, player);
                            else
                                list = ShapeGen.generatePyramid(world, centerX, centerY, centerZ, strings[1], flag, (int) var1, var2, var3, replace, player);
                        }
                    }
                    if (!list.isEmpty()) {
                        addToUndoListServer(list, player);
                        clearRedoListServer(player);
                        sender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                    }
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// shape <type> <id> <parameters> <x> <y> <z> [hollow|open] [replace]"));
                }
            }
            case "paste" -> {
                try {
                    paste(sender, strings, player, ignoreAir, world, flag);
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// paste [x] [y] [z]"));
                }
            }
            case "undo" -> {
                try {
                    int num = strings.length > 1 ? Integer.parseInt(strings[1]) : 1;
                    undo(num, player, world, flag);
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// undo [count]"));
                }
            }
            case "redo" -> {
                try {
                    int num = strings.length > 1 ? Integer.parseInt(strings[1]) : 1;
                    redo(num, player, world, flag);
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// redo [count]"));
                }
            }
            case "pos1" -> {
                try {
                    int x;
                    int y;
                    int z;
                    if (strings.length == 4) {
                        x = Integer.parseInt(strings[1]);
                        y = Integer.parseInt(strings[2]);
                        z = Integer.parseInt(strings[3]);
                    } else {
                        ChunkCoordinates coords = sender.getPlayerCoordinates();
                        x =coords.posX;
                        y =coords.posY;
                        z =coords.posZ;
                    }
                    PacketSender.sendServerToClientMessage(player,"updatePos1:"+x+","+y+","+z);
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("§dFirst position set to (" + x + ", " + y + ", " + z + ")."));
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// pos1 [x] [y] [z]"));
                }
            }
            case "pos2" -> {
                try {
                    int x;
                    int y;
                    int z;
                    if (strings.length == 4) {
                        x = Integer.parseInt(strings[1]);
                        y = Integer.parseInt(strings[2]);
                        z = Integer.parseInt(strings[3]);
                    } else {
                        ChunkCoordinates coords = sender.getPlayerCoordinates();
                        x =coords.posX;
                        y =coords.posY;
                        z =coords.posZ;
                    }
                    PacketSender.sendServerToClientMessage(player,"updatePos2:"+x+","+y+","+z);
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("§dSecond position set to (" + x + ", " + y + ", " + z + ")."));
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// pos2 [x] [y] [z]"));
                }
            }
            case "tool" -> {
                try {
                    ItemStack itemStack = player.getHeldItem();
                    if (itemStack == null) return;
                    if (strings.length == 1 && itemStack.hasTagCompound()) {
                        NBTTagCompound nbt = itemStack.getTagCompound();
                        NBTTagCompound buildingParamsNBT;
                        if (nbt.hasKey("BuildingParams")) {
                            buildingParamsNBT = nbt.getCompoundTag("BuildingParams");
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§dCurrent item is a building tool with the characteristics:"));
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-shape= §a" + buildingParamsNBT.getString("shape")));
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed")));
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-parameters= §a" + buildingParamsNBT.getString("parameters")));
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-volume= §a" + buildingParamsNBT.getString("volume")));
                        }
                    } else if (strings.length >= 4) {
                        saveShapeTool(sender, strings, itemStack);
                    } else player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// tool <shape> <id> <parameters> [hollow|open]"));
                } catch (Exception e) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// tool <shape> <id> <parameters> [hollow|open]"));
                }
            }

            /// need pos update
            case "set", "move", "replace", "copy", "line", "plane" -> {
                IClientStatusCallback continuationCode = (int[] block1, int[] block2) -> {
                    final int x1 = block1[0];
                    final int y1 = block1[1];
                    final int z1 = block1[2];
                    final int x2 = block2[0];
                    final int y2 = block2[1];
                    final int z2 = block2[2];
                    final boolean isPosValid = x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999;
                    List<SavedBlock> list = new ArrayList<>();
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
                                            List<BlockInfo> results = getBlockInfo(strings[1],player.getCommandSenderName());
                                            if (results==null){
                                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
                                                return;
                                            }for (int j = 0; j <= absY; j++) {
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
                                                            if (result.meta() > subtype.size() - 1) {
                                                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
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
                                        if (!list.isEmpty()) {
                                            addToUndoListServer(list, player);
                                            clearRedoListServer(player);
                                        }
                                        player.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                                    } else setArea(strings, y1, y2, x1, x2, z1, z2, player, world, flag);
                                } else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                            } catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // set <id/metadata:odds"));
                            }
                        }
                        case "move" -> {
                            try {
                                if (strings.length > 1) {
                                    List<SavedBlock> list1 = new ArrayList<>();
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
                                    if (isPosValid && (strings[1].equalsIgnoreCase("to") || strings[1].equalsIgnoreCase("add"))) {
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
                                            PacketSender.sendServerToClientMessage(player,"updatePos1:"+xa+","+ya+","+za);
                                            PacketSender.sendServerToClientMessage(player,"updatePos2:"+xb+","+yb+","+zb);
                                        }
                                        list1.addAll(list);
                                        if (!list1.isEmpty()) {
                                            addToUndoListServer(list1, player);
                                            clearRedoListServer(player);
                                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been moved"));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§c// move <to|add> <x> <y> <z>"));
                            }
                        }
                        case "replace" -> {
                            try {
                                if (strings.length > 1) {
                                    if (isPosValid) replaceArea(sender, strings, y1, y2, x1, x2, z1, z2, world, flag, player);
                                    else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                                } else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // replace <id> [id replaced]"));
                            } catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // replace <id> [id replaced]"));
                            }
                        }
                        case "copy" -> {
                            try {
                                copy(sender, player, isPosValid, x1, x2, y1, y2, z1, z2, ignoreAir, world);
                            } catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // copy"));
                            }
                        }
                        case "line" -> {
                            try {
                                if (strings.length==1)throw new Exception();
                                if (isPosValid) {
                                    list = ShapeGen.buildLine(world, strings, x1, y1, z1, x2, y2, z2, strings.length > 2 ? Integer.parseInt(strings[2]) : 1, flag, player);
                                    if (!list.isEmpty()) {
                                        addToUndoListServer(list, player);
                                        clearRedoListServer(player);
                                    }
                                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));

                                }else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                            }catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // line <id> [thickness]"));
                            }
                        }
                        case "plane" -> {
                            try {
                                if (strings.length==1)throw new Exception();
                                if (isPosValid) {
                                    int side = strings.length>2 ? strings[2].equalsIgnoreCase("s") ? 1 : 0 : 0;
                                    int thickness = strings.length>3 ? Integer.parseInt(strings[3]) : 1;

                                    list = ShapeGen.buildPlane(world, strings[1], x1, y1, z1, x2, y2, z2, thickness, side,replace, flag, player);

                                    if (!list.isEmpty()) {
                                        addToUndoListServer(list, player);
                                        clearRedoListServer(player);
                                    }
                                    player.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
                                }else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                            }catch (Exception e) {
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong usage: // plane <id> [x:z] [thickness]"));
                            }
                        }
                    }
                };
                int requestId = ClientRequestManager.registerStatusCallback(player, continuationCode);
                PacketSender.sendServerToClientMessage(player, "updateAllPos:" + requestId);
            }
        }
    }

    public static void saveShapeTool(ICommandSender sender, String[] strings, ItemStack itemStack) {
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
        buildingParamsNBT.setString("volume", strings.length>4?strings[4]:"full");

        for (String s : strings) {
            switch (s) {
                case "hollow" -> buildingParamsNBT.setString("volume", "hollow");
                case "open" -> buildingParamsNBT.setString("volume", "open");
            }
        }
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("§dCurrent item got transformed into a building tool with the characteristics:"));
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-shape= §a" + buildingParamsNBT.getString("shape")));
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-block(s)= §a" + buildingParamsNBT.getString("blockUsed")));
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-parameters= §a" + buildingParamsNBT.getString("parameters")));
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(" §f-volume= §a" + buildingParamsNBT.getString("volume")));
    }

    public static void redo(int num, EntityPlayerMP player, World world, int flag) {
        if (!getRedoList(player).isEmpty()) {
            int count = 0;
            for (int j = 0; j < num; j++) {
                if (getRedoList(player).isEmpty()) break;
                int xa = 9999999;
                int ya = 9999999;
                int za = 9999999;
                int yb = -9999999;
                int xb = -9999999;
                int zb = -9999999;
                List<SavedBlock> list = new ArrayList<>();
                for (SavedBlock undoBlock : getRedoList(player).get(getRedoList(player).size() - 1)) {
                    list.add(new SavedBlock(undoBlock.x(), undoBlock.y(), undoBlock.z(), world.getBlockId(undoBlock.x(), undoBlock.y(), undoBlock.z()), world.getBlockMetadata(undoBlock.x(), undoBlock.y(), undoBlock.z())));
                }
                for (int i = getRedoList(player).get(getRedoList(player).size() - 1).size() - 1; i >= 0; i--) {
                    SavedBlock block = getRedoList(player).get(getRedoList(player).size() - 1).get(i);
                    world.setBlock(block.x(), block.y(), block.z(), block.id(), block.meta(), flag);
                }
                addToUndoListServer(list, player);
                removeToRedoList(player);
            }
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§dThe last " + (count > 1 ? count + " " : "") + "action" + (count > 1 ? "s have been" : " has been") + " redone"));
        }else player.sendChatToPlayer(ChatMessageComponent.createFromText("§dThere is no action to redo"));
    }

    public static void undo(int num, EntityPlayerMP player, World world, int flag) {
        if (!getUndoList(player).isEmpty()) {
            int count = 0;
            for (int i = 0; i < num; i++) {
                if (getUndoList(player).isEmpty()) break;
                int xa = 9999999;
                int ya = 9999999;
                int za = 9999999;
                int yb = -9999999;
                int xb = -9999999;
                int zb = -9999999;
                List<SavedBlock> list = new ArrayList<>();
                for (SavedBlock block : getUndoList(player).get(getUndoList(player).size() - 1)) {
                    list.add(new SavedBlock(block.x(), block.y(), block.z(), world.getBlockId(block.x(), block.y(), block.z()), world.getBlockMetadata(block.x(), block.y(), block.z())));
                    world.setBlock(block.x(), block.y(), block.z(), block.id(), block.meta(), flag);
                    numBlock++;
                }
                addToRedoList(list, player);
                removeFromList(player);
                count++;
            }
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§dThe last " + (count > 1 ? count + " " : "") + "action" + (count > 1 ? "s have been" : " has been") + " reverted"));
        }else player.sendChatToPlayer(ChatMessageComponent.createFromText("§dThere is no action to undo"));
    }

    public static void replaceArea(ICommandSender sender, String[] strings, int y1, int y2, int x1, int x2, int z1, int z2, World world, int flag, EntityPlayerMP player) {
        numBlock=0;
        List<SavedBlock> list = new ArrayList<>();
        List<BlockInfo> results1 = getBlockInfo(strings[1], sender.getCommandSenderName());
        if (results1==null){
            getPlayer(sender, sender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return;
        }
        List<BlockInfo> results2 = new ArrayList<>();
        if (strings.length > 2) {
            results2 = getBlockInfo(strings[2], sender.getCommandSenderName());
        }
        if (results2==null){
            getPlayer(sender, sender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
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
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result1.meta() + " out of bounds for length " + (subtype.size())));
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
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) was replaced"));
        if (!list.isEmpty()) {
            addToUndoListServer(list, player);
            clearRedoListServer(player);
        }
    }

    public static void setArea(String[] strings, int y1, int y2, int x1, int x2, int z1, int z2, EntityPlayerMP player, World world, int flag) {
        numBlock=0;
        List<BlockInfo> results = getBlockInfo(strings[1], player.getCommandSenderName());
        List<SavedBlock> list = new ArrayList<>();
        if (results==null){
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
            return;
        }
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
                            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result.meta() + " out of bounds for length " + (subtype.size())));
                        }
                    }
                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                    world.setBlock(x, y, z, result.id(), result.meta(), flag);
                    numBlock++;
                }
            }
        }
        if (!list.isEmpty()) {
            addToUndoListServer(list, player);
            clearRedoListServer(player);
        }player.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been placed"));
    }

    public static void paste(ICommandSender sender, String[] strings, EntityPlayerMP player, boolean ignoreAir, World world, int causeUpdate) {
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
        if (!getCopyList(player).isEmpty()) {
            for (SavedBlock block : getCopyList(player)) {
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
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been pasted"));
            if (!list.isEmpty()) {
                addToUndoListServer(list, player);
                clearRedoListServer(player);
            }
        }else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to copy an area first"));
    }

    public static void copy(ICommandSender sender, EntityPlayerMP player, boolean isPosValid, int x1, int x2, int y1, int y2, int z1, int z2, boolean finalIgnoreAir, World world) {
        numBlock = 0;
        clearCopyList(player);
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
            saveCopyList(list, player);
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been copied"));
        } else player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
    }

    private static List<SavedBlock> getCopyList(EntityPlayerMP player) {
        List<SavedBlock> copyList = PacketUtils.playersCopyServer.get(player.getEntityName());
        if (copyList==null){
            return new ArrayList<>();
        }else return copyList;
    }

    private static List<List<SavedBlock>> getUndoList(EntityPlayerMP player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getEntityName());
        if (undoList==null){
            return new ArrayList<>();
        }else return undoList;
    }

    private static List<List<SavedBlock>> getRedoList(EntityPlayerMP player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getEntityName());
        if (redoList==null){
            return new ArrayList<>();
        }else return redoList;
    }

    private static void removeFromList(EntityPlayerMP player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getEntityName());
        undoList.remove(undoList.size()-1);
        PacketUtils.playersUndoListServer.put(player.getEntityName(),undoList);
    }

    private static void removeToRedoList(EntityPlayerMP player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getEntityName());
        redoList.remove(redoList.size()-1);
        PacketUtils.playersRedoListServer.put(player.getEntityName(),redoList);
    }

    public static void saveCopyList(List<SavedBlock> list, EntityPlayerMP player) {
        List<SavedBlock> copyList = PacketUtils.playersCopyServer.get(player.getEntityName());
        if (copyList==null){
            copyList=new ArrayList<>();
        }
        copyList.addAll(list);
        PacketUtils.playersCopyServer.put(player.getEntityName(),copyList);
    }

    private static void addToRedoList(List<SavedBlock> list, EntityPlayerMP player) {
        List<List<SavedBlock>> redoList = PacketUtils.playersRedoListServer.get(player.getEntityName());
        if (redoList==null){
            redoList=new ArrayList<>();
        }
        redoList.add(list);
        PacketUtils.playersRedoListServer.put(player.getEntityName(),redoList);
    }

    public static void addToUndoListServer(List<SavedBlock> list1, EntityPlayerMP player) {
        List<List<SavedBlock>> undoList = PacketUtils.playersUndoListServer.get(player.getEntityName());
        if (undoList==null){
            undoList=new ArrayList<>();
        }
        undoList.add(list1);
        PacketUtils.playersUndoListServer.put(player.getEntityName(),undoList);
    }

    public static void clearRedoListServer(EntityPlayerMP player) {
        PacketUtils.playersRedoListServer.remove(player.getEntityName());
    }

    public static void clearCopyList(EntityPlayerMP player) {
        PacketUtils.playersCopyServer.remove(player.getEntityName());
    }

}
