package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;

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
            return getListOfStringsMatchingLastWord(strings,"set", "setblock", "replace", "move", "undo", "redo", "copy", "paste", "pos1", "pos2");
        }
        MovingObjectPosition blockCoord = UtilsCommand.getInstance().getBlockPlayerIsLooking(par1ICommandSender);
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
        }else if (strings[0].equalsIgnoreCase("setblock")) {
            if (strings.length == 5) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }
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
        } else if (strings[0].equalsIgnoreCase("set")) {
            if (strings.length == 2) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            } else if (strings.length == 3) {
                return getListOfStringsMatchingLastWord(strings, "hollow", "wall");
            }
        } else if (strings[0].equalsIgnoreCase("replace")) {
            if (strings.length == 2 || strings.length == 3) {
                return UtilsCommand.getInstance().getBlockNameList(strings);
            }
        } else if (strings[0].equalsIgnoreCase("copy")) return getListOfStringsMatchingLastWord(strings, "ignoreAir");
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
        } else if (strings[0].equalsIgnoreCase("move")) {
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
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        try {
            World world = iCommandSender.getEntityWorld();
            int numBlock = 0;
            boolean ignoreAir = false;
            int flag = 2;
            for (String string : strings) {
                if (!ignoreAir) {
                    ignoreAir = string.equalsIgnoreCase("ignoreAir");
                }
                if (flag==2) {
                    flag = string.equalsIgnoreCase("causeUpdate") ? 3 : flag;
                }
            }
            switch (strings[0]) {

                case "setblock" -> {
                    try {
                        List<SavedBlock> list = new ArrayList<>();
                        int x;
                        int y;
                        int z;
                        try {
                            x = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[1]));
                            y = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[2]));
                            z = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[3]));
                        } catch (Exception ignored) {
                            x = Integer.parseInt(strings[1]);
                            y = Integer.parseInt(strings[2]);
                            z = Integer.parseInt(strings[3]);
                        }
                        BlockInfo result = getBlockInfo(strings, 4);
                        if (result.id()!=0) {
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
                        getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§dPlaced " + result.blockName().replace("_", " ") + " at: " + x + ", " + y + ", " + z));
                    } catch (NumberFormatException e) {
                        throw new WrongUsageException("Wrong format: /edit setblock x y z id/metadata).");
                    }
                }
                case "set" -> {
                    try {
                        boolean hollow = strings.length > 2 && strings[2].equalsIgnoreCase("hollow");
                        boolean wall = strings.length > 2 && strings[2].equalsIgnoreCase("wall");
                        if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999) {
                            List<SavedBlock> list = new ArrayList<>();
                            if (hollow || wall) {
                                int thickness=1;
                                thickness = strings.length > 3 ? strings[3].equalsIgnoreCase("ignoreAir")||strings[3].equalsIgnoreCase("causeUpdate") ? thickness : Integer.parseInt(strings[3]) : thickness;
                                for (int l = 0; l < thickness; l++) {
                                    int maxX = Math.max(x1, x2)-l;
                                    int minX = Math.min(x1, x2)+l;
                                    int maxY = Math.max(y1, y2)-l;
                                    int minY = Math.min(y1, y2)+l;
                                    int maxZ = Math.max(z1, z2)-l;
                                    int minZ = Math.min(z1, z2)+l;
                                    int absX = MathHelper.abs_int(x1 - x2)-l*2;
                                    int absY = wall ? MathHelper.abs_int(y1 - y2) : MathHelper.abs_int(y1 - y2)-l*2;
                                    int absZ = MathHelper.abs_int(z1 - z2)-l*2;
                                    for (int j = 0; j <= absY; j++) {
                                        for (int i = 0; i <= absX; i++) {
                                            for (int k = 0; k <= absZ; k++) {
                                                BlockInfo result = getBlockInfo(strings, 1);
                                                int x = minX + i;
                                                int y = wall ? Math.min(y1, y2) + j : minY + j;
                                                int z = minZ + k;
                                                if (result.id()!=0) {
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
                                for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                    for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                                        for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                            BlockInfo result = getBlockInfo(strings, 1);
                                            int x = Math.min(x1, x2) + i;
                                            int y = Math.min(y1, y2) + j;
                                            int z = Math.min(z1, z2) + k;
                                            if (result.id()!=0) {
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
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§d"+numBlock + " block(s) have been placed"));
                        }
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "replace" -> {
                    try {
                        if (strings.length>1) {
                            List<SavedBlock> list = new ArrayList<>();
                            for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                                    for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                        BlockInfo result1 = getBlockInfo(strings, 1);
                                        if (result1.id()!=0) {
                                            ItemStack itemStack = new ItemStack(result1.id(), 0, 0);
                                            List<ItemStack> subtype = new ArrayList<>();
                                            itemStack.getItem().getSubItems(itemStack.itemID, null, subtype);
                                            if (result1.meta() > subtype.size() - 1) {
                                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cIndex " + result1.meta() + " out of bounds for length " + (subtype.size())));
                                                return;
                                            }
                                        }
                                        BlockInfo result2 = null;
                                        if (strings.length > 2) {
                                            result2 = getBlockInfo(strings, 2);
                                        }
                                        int x = Math.min(x1, x2) + i;
                                        int y = Math.min(y1, y2) + j;
                                        int z = Math.min(z1, z2) + k;
                                        if (strings.length > 2) {
                                            if (result2 != null && world.getBlockId(x, y, z) == result2.id()) {
                                                String[] idMeta = strings[2].split("/");
                                                idMeta[0] = idMeta[0].split("\\|")[0];
                                                if (idMeta.length == 1 || (idMeta.length == 2 && world.getBlockMetadata(x, y, z) == result2.meta())) {
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
                            getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§d"+numBlock + " block(s) was replaced"));

                            if (!list.isEmpty()) {
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                case "move" -> {
                    if (strings.length>1) {
                        List<SavedBlock> list = new ArrayList<>();
                        List<SavedBlock> list1 = new ArrayList<>();
                        int xPos=0;
                        int yPos=0;
                        int zPos=0;
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
                        }else if (strings[1].equalsIgnoreCase("add")){
                            if (strings.length > 4) {
                                xPos = Integer.parseInt(strings[2]);
                                yPos = Integer.parseInt(strings[3]);
                                zPos = Integer.parseInt(strings[4]);
                            }
                        }
                        if (x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999 && (strings[1].equalsIgnoreCase("to")||strings[1].equalsIgnoreCase("add"))) {
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
                                int xa=9999999;
                                int ya=9999999;
                                int za=9999999;
                                int yb=-9999999;
                                int xb=-9999999;
                                int zb=-9999999;
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
                                    world.setBlock(finalX, finalY, finalZ, block.id(), block.meta(),flag);
                                    xa = Math.min(finalX, xa);
                                    ya = Math.min(finalY, ya);
                                    za = Math.min(finalZ, za);
                                    xb = Math.max(finalX, xb);
                                    yb = Math.max(finalY, yb);
                                    zb = Math.max(finalZ, zb);
                                }
                                x1= xa;
                                y1 =ya;
                                z1 =za;
                                x2 =xb;
                                y2 =yb;
                                z2 =zb;
                            }
                            list1.addAll(list);
                            if (!list1.isEmpty()) {
                                undoSaved.add(list1);
                                redoSaved.clear();
                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§d"+numBlock + " block(s) have been moved"));
                            }
                        }

                    }
                }
                case "copy" ->{
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
                                    }else {
                                        copySaved.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                        numBlock++;
                                    }
                                }
                            }
                        }
                    }
                    getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§d"+numBlock + " block(s) have been copied"));

                }
                case "paste" ->{
                    List<SavedBlock> list = new ArrayList<>();
                    int x;
                    int y;
                    int z;
                    if (strings.length>1) {
                        try {
                            x = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[1]));
                            y = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[2]));
                            z = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[3]));
                        } catch (Exception ignored) {
                            x = Integer.parseInt(strings[1]);
                            y = Integer.parseInt(strings[2]);
                            z = Integer.parseInt(strings[3]);
                        }
                    }else {
                        x=iCommandSender.getPlayerCoordinates().posX;
                        y=iCommandSender.getPlayerCoordinates().posY;
                        z=iCommandSender.getPlayerCoordinates().posZ;
                    }
                    if (!copySaved.isEmpty()){
                        for (SavedBlock block : copySaved){
                            if (ignoreAir){
                                if (block.id()!=0){
                                    list.add(new SavedBlock(block.x()+x, block.y()+y, block.z()+z,world.getBlockId(block.x()+x, block.y()+y, block.z()+z), world.getBlockMetadata(block.x()+x, block.y()+y, block.z()+z)));
                                    world.setBlock(block.x()+x, block.y()+y, block.z()+z, block.id(), block.meta(),flag);
                                    numBlock++;
                                }
                            }else {
                                list.add(new SavedBlock(block.x()+x, block.y()+y, block.z()+z,world.getBlockId(block.x()+x, block.y()+y, block.z()+z), world.getBlockMetadata(block.x()+x, block.y()+y, block.z()+z)));
                                world.setBlock(block.x()+x, block.y()+y, block.z()+z, block.id(), block.meta(),flag);
                                numBlock++;
                            }
                        }
                    }
                    if (!list.isEmpty()){
                        undoSaved.add(list);
                        redoSaved.clear();
                    }
                    getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§d"+numBlock + " block(s) have been pasted"));

                }
                case "undo"->{
                    boolean messageSent = false;
                    for (int i = 0; i < (strings.length > 1 ? Integer.parseInt(strings[1]) : 1); i++) {
                        if (!undoSaved.isEmpty()) {
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
                            if (!messageSent) {
                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§dPrevious action(s) were reverted"));
                                messageSent=true;
                            }
                        }
                    }
                }
                case "redo" -> {
                    boolean messageSent=false;
                    for (int j = 0; j < (strings.length > 1 ? Integer.parseInt(strings[1]) : 1); j++) {
                        if (!redoSaved.isEmpty()) {
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
                            if (!messageSent) {
                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§dPrevious action(s) were redone"));
                                messageSent=true;
                            }
                        }
                    }
                }
                case "pos1" -> {
                    if (strings.length == 4) {
                        try {
                            x1 = Integer.parseInt(strings[1]);
                            y1 = Integer.parseInt(strings[2]);
                            z1 = Integer.parseInt(strings[3]);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        x1 = iCommandSender.getPlayerCoordinates().posX;
                        y1 = iCommandSender.getPlayerCoordinates().posY;
                        z1 = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dFirst position set to (" + x1 + ", " + y1 + ", " + z1 + ")."));
                }
                case "pos2" -> {
                    if (strings.length == 4) {
                        try {
                            x2 = Integer.parseInt(strings[1]);
                            y2 = Integer.parseInt(strings[2]);
                            z2 = Integer.parseInt(strings[3]);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        x2 = iCommandSender.getPlayerCoordinates().posX;
                        y2 = iCommandSender.getPlayerCoordinates().posY;
                        z2 = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("§dSecond position set to (" + x2 + ", " + y2 + ", " + z2 + ")."));
                }
            }
        } catch (NumberFormatException e) {
            throw new WrongUsageException("Invalid command.");
        }
    }

}
