package net.dravigen.tesseractUtils;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import java.util.*;

import static net.dravigen.tesseractUtils.TessUConfig.*;

public class TesseractUtilsAddon extends BTWAddon {

    public TesseractUtilsAddon() {
        super();
    }

    public static TesseractUtilsAddon instance;

    public static int x1=9999999;
    public static int y1=9999999;
    public static int z1=9999999;
    public static int x2=9999999;
    public static int y2=9999999;
    public static int z2=9999999;
    public static int modeState;
    public static List<String> blocksNameList = new ArrayList<>();
    public static List<String> invSavedNameList = new ArrayList<>();
    public static List<List<SavedBlock>> undoSaved = new ArrayList<>();
    public static List<List<SavedBlock>> redoSaved = new ArrayList<>();
    public static List<SavedBlock> copySaved = new ArrayList<>();
    public static List<List<ItemStack>> invSavedList = new ArrayList<>();
    // CONFIG
    public static Map<String, String> properties;

    public static TesseractUtilsAddon getInstance() {
        return instance == null ? (new TesseractUtilsAddon()) : instance;
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        createNewCommand();
        TessUConfig.loadConfig();
    }


    private void createNewCommand() {
        registerAddonCommand(new CommandBase() {
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
                if (blocksNameList.isEmpty()) {
                    initBlocksNameList();
                }
                if (strings.length==1) {
                    return getListOfStringsMatchingLastWord(strings, new String[]{"set", "setblock", "replace", "move", "undo", "redo", "copy", "paste", "pos1", "pos2"/*, "reach", "flySpeed", "disablePlaceCooldown", "disableBreakCooldown", "disableMomentum", "enableClickReplace", "enableNoClip", "enableExtraDebugInfo"*/});
                }
                /*String[] boolString = new String[]{"disablePlaceCooldown","disableBreakCooldown", "disableMomentum", "enableClickReplace", "enableNoClip", "enableExtraDebugInfo"};
                for (String string : boolString) {
                    if (strings[0].equalsIgnoreCase(string)) return getListOfStringsMatchingLastWord(strings, new String[]{"true", "false"});
                }*/
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
                }else if (strings[0].equalsIgnoreCase("setblock")) {
                    if (strings.length == 5) {
                        return getBlockList(strings);
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
                        return getBlockList(strings);
                    } else if (strings.length == 3) {
                        return getListOfStringsMatchingLastWord(strings, new String[]{"hollow", "wall"});
                    }
                } else if (strings[0].equalsIgnoreCase("replace")) {
                    if (strings.length == 2 || strings.length == 3) {
                        return getBlockList(strings);
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
                        return getListOfStringsMatchingLastWord(strings, new String[]{"to", "add"});
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

            private MovingObjectPosition getBlockPlayerIsLooking(ICommandSender par1ICommandSender) {
                EntityPlayer player = getPlayer(par1ICommandSender, par1ICommandSender.getCommandSenderName());
                Vec3 var3 = player.getPosition(1);
                Vec3 var4 = var3.addVector(0, player.getEyeHeight(), 0);
                Vec3 var5 = player.getLook(1);
                Vec3 var6 = var4.addVector(var5.xCoord * reach, var5.yCoord * reach, var5.zCoord * reach);
                return player.worldObj.clip(var4, var6);
            }

            private @NotNull List<String> getBlockList(String[] strings) {
                List<String> finalList = new ArrayList<>();
                String var1 = strings[strings.length - 1];
                String var2 = ";";
                boolean afterSemiColon = var2.regionMatches(true, 0, var1, var1.length()-1, 1);

                for (String string : blocksNameList) {
                    List<String> split = new ArrayList<>(List.of(strings[strings.length - 1].split(";")));
                    String lastString = split.get(split.size()-1);
                    if (!afterSemiColon) split.remove(split.size()-1);
                    String firstString = afterSemiColon ? var1 : split.isEmpty() ? "" : String.join(";",split)+";";

                    if (afterSemiColon){
                        lastString = "";
                    }
                    if (string.toLowerCase().contains(lastString.toLowerCase())){
                        finalList.add(firstString+string);
                    }
                }
                return finalList;
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
                                BlockInfoFromCommand result = getBlockInfo(strings, 4);
                                list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                world.setBlock(x, y, z, result.id, result.meta, flag);
                                undoSaved.add(list);
                                redoSaved.clear();
                                getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("Placed " + result.blockName() + " at: " + x + ", " + y + ", " + z + "."));
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
                                                        BlockInfoFromCommand result = getBlockInfo(strings, 1);
                                                        int x = minX + i;
                                                        int y = wall ? Math.min(y1, y2)+j : minY + j;
                                                        int z = minZ + k;
                                                        if (x == maxX || z == maxZ || x == minX || z == minZ) {
                                                            list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                            world.setBlock(x, y, z, result.id, result.meta, flag);
                                                            numBlock++;
                                                        } else if ((y == maxY || y == minY) && hollow) {
                                                            list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                            world.setBlock(x, y, z, result.id, result.meta, flag);
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
                                                    BlockInfoFromCommand result = getBlockInfo(strings, 1);

                                                    int x = Math.min(x1, x2) + i;
                                                    int y = Math.min(y1, y2) + j;
                                                    int z = Math.min(z1, z2) + k;
                                                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                    world.setBlock(x, y, z, result.id, result.meta, flag);
                                                    numBlock++;
                                                }
                                            }
                                        }
                                    }
                                    if (!list.isEmpty()) {
                                        undoSaved.add(list);
                                        redoSaved.clear();
                                    }
                                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(numBlock + " block(s) have been placed."));
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
                                                BlockInfoFromCommand result1 = getBlockInfo(strings, 1);
                                                BlockInfoFromCommand result2 = null;
                                                if (strings.length > 2) {
                                                    result2 = getBlockInfo(strings, 2);
                                                }
                                                int x = Math.min(x1, x2) + i;
                                                int y = Math.min(y1, y2) + j;
                                                int z = Math.min(z1, z2) + k;
                                                if (strings.length > 2) {
                                                    if (result2 != null && world.getBlockId(x, y, z) == result2.id) {
                                                        String[] idMeta = strings[2].split("/");
                                                        if (idMeta.length == 1 || (idMeta.length == 2 && world.getBlockMetadata(x, y, z) == result2.meta)) {
                                                            list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                            world.setBlock(x, y, z, result1.id, result1.meta, flag);
                                                            numBlock++;
                                                        }
                                                    }
                                                } else if (!world.isAirBlock(x, y, z)) {
                                                    list.add(new SavedBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                                    world.setBlock(x, y, z, result1.id, result1.meta, flag);
                                                    numBlock++;
                                                }
                                            }
                                        }
                                    }
                                    getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText(numBlock + " block(s) was replaced."));

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
                                            int finalX = xPos + block.x;
                                            int finalY = yPos + block.y;
                                            int finalZ = zPos + block.z;
                                            if (strings[1].equalsIgnoreCase("to")) {
                                                finalX -= Math.max(x1, x2);
                                                finalY -= Math.min(y1, y2);
                                                finalZ -= Math.max(z1, z2);
                                            }
                                            list1.add(new SavedBlock(finalX, finalY, finalZ, world.getBlockId(finalX, finalY, finalZ), world.getBlockMetadata(finalX, finalY, finalZ)));
                                            world.setBlock(finalX, finalY, finalZ, block.id, block.meta,flag);
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
                                        getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText(numBlock + " block(s) have been moved."));
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
                            getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText(numBlock + " block(s) have been copied."));

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
                                        if (block.id!=0){
                                            list.add(new SavedBlock(block.x+x, block.y+y, block.z+z,world.getBlockId(block.x+x, block.y+y, block.z+z), world.getBlockMetadata(block.x+x, block.y+y, block.z+z)));
                                            world.setBlock(block.x+x, block.y+y, block.z+z, block.id, block.meta,flag);
                                            numBlock++;
                                        }
                                    }else {
                                        list.add(new SavedBlock(block.x+x, block.y+y, block.z+z,world.getBlockId(block.x+x, block.y+y, block.z+z), world.getBlockMetadata(block.x+x, block.y+y, block.z+z)));
                                        world.setBlock(block.x+x, block.y+y, block.z+z, block.id, block.meta,flag);
                                        numBlock++;
                                    }
                                }
                            }
                            if (!list.isEmpty()){
                                undoSaved.add(list);
                                redoSaved.clear();
                            }
                            getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText(numBlock + " block(s) have been pasted."));

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
                                        list.add(new SavedBlock(block.x, block.y, block.z, world.getBlockId(block.x, block.y, block.z), world.getBlockMetadata(block.x, block.y, block.z)));
                                        world.setBlock(block.x, block.y, block.z, block.id, block.meta, flag);
                                        xa = Math.min(block.x, xa);
                                        ya = Math.min(block.y, ya);
                                        za = Math.min(block.z, za);
                                        xb = Math.max(block.x, xb);
                                        yb = Math.max(block.y, yb);
                                        zb = Math.max(block.z, zb);
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
                                        getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("Previous action(s) were reverted."));
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
                                        list.add(new SavedBlock(undoBlock.x, undoBlock.y, undoBlock.z, world.getBlockId(undoBlock.x, undoBlock.y, undoBlock.z), world.getBlockMetadata(undoBlock.x, undoBlock.y, undoBlock.z)));
                                    }
                                    for (int i = redoSaved.get(redoSaved.size() - 1).size() - 1; i >= 0; i--) {
                                        SavedBlock block = redoSaved.get(redoSaved.size() - 1).get(i);
                                        world.setBlock(block.x, block.y, block.z, block.id, block.meta, flag);
                                        xa = Math.min(block.x, xa);
                                        ya = Math.min(block.y, ya);
                                        za = Math.min(block.z, za);
                                        xb = Math.max(block.x, xb);
                                        yb = Math.max(block.y, yb);
                                        zb = Math.max(block.z, zb);
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
                                        getPlayer(iCommandSender, iCommandSender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("Previous action(s) were redone."));
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
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("First position set to (" + x1 + ", " + y1 + ", " + z1 + ")."));
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
                            iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Second position set to (" + x2 + ", " + y2 + ", " + z2 + ")."));
                        }
                    /*    case "reach" -> {
                            if (strings.length==2){
                                reach = Float.parseFloat(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Your reach has been set to: " + reach + "."));
                            }
                        }
                        case "flySpeed"-> {
                            if (strings.length > 1) {
                                flySpeed = Float.parseFloat(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Your flying speed has been set to: " + flySpeed + "."));
                            }
                        }
                        case "disablePlaceCooldown"->{
                            if (strings.length>1) {
                                disablePlaceCooldown = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The placing cooldown has been " + (!disablePlaceCooldown ? "enabled" : "disabled") +"."));
                            }
                        }
                        case "disableBreakCooldown"->{
                            if (strings.length>1) {
                                disableBreakCooldown = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("The breaking cooldown has been " + (!disableBreakCooldown ? "enabled" : "disabled") +"."));
                            }
                        }
                        case "disableMomentum"->{
                            if (strings.length>1) {
                                disableMomentum = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Your flying momentum has been " + (!disableMomentum ? "enabled" : "disabled") +"."));
                            }
                        }
                        case "enableClickReplace"->{
                            if (strings.length>1) {
                                enableClickReplace = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("ClickReplace has been "+(enableClickReplace ? "enabled" : "disabled")+"."));
                            }
                        }
                        case "enableNoClip"->{
                            if (strings.length>1) {
                                enableNoClip = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("NoClip has been " + (enableNoClip ? "enabled" : "disabled") +"."));
                            }
                        }
                        case "enableExtraDebugInfo"->{
                            if (strings.length>1) {
                                enableExtraDebugInfo = Boolean.parseBoolean(strings[1]);
                                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Extra debug infos have been " + (enableExtraDebugInfo ? "enabled" : "disabled") +"."));
                            }

                        }
                        */
                    }
                } catch (NumberFormatException e) {
                    throw new WrongUsageException("Invalid command.");
                }
            }

            private static @NotNull BlockInfoFromCommand getBlockInfo(String[] strings, int idPos) {
                Block block;
                String blockName = "Air";
                int id = 99999;
                int meta = 0;
                List<String> differentBlock = Arrays.stream(strings[idPos].split(";")).toList();
                List<Integer> blockOdd = new ArrayList<>();
                List<String[]> idMetaList = new ArrayList<>();
                for (String s : differentBlock){
                    String[] s1 = s.split(":");
                    blockOdd.add(s1.length>1 ? Integer.parseInt(s1[1]) : 1 );
                    idMetaList.add(s1[0].split("/"));
                }
                String[] idMeta = getRandomBlockFromOdds(blockOdd, idMetaList);
                if (idMeta.length == 2) {
                    meta = Integer.parseInt(idMeta[1]);
                }
                try {
                    id = Integer.parseInt(idMeta[0]);
                } catch (NumberFormatException ignored) {
                }
                if (id == 99999) {
                    if (idMeta[0].equalsIgnoreCase("Air")) {
                        id = 0;
                    } else {
                        for (int i = 0; i < Block.blocksList.length; i++) {
                            block = Block.blocksList[i];
                            if (block != null && block.getLocalizedName().replace(" ", "").equalsIgnoreCase(idMeta[0])) {
                                blockName = block.getLocalizedName().replace(" ", "");
                                id = block.blockID;
                                break;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < Block.blocksList.length; i++) {
                        block = Block.blocksList[i];
                        if (block != null && block.blockID == id) {
                            blockName = Block.blocksList[i].getLocalizedName().replace(" ", "");
                        }
                    }
                }
                return new BlockInfoFromCommand(blockName, id, meta);
            }

            private static String[] getRandomBlockFromOdds(List<Integer> blockOdd, List<String[]> idMetaList) {
                Random rand = new Random();
                int totalOdd=0;
                for (int odd: blockOdd){
                    totalOdd+=odd;
                }

                int randNum = rand.nextInt(totalOdd)+1;
                int blockPosInList=0;
                for (int i = 0; i< blockOdd.size(); i++){
                    if (blockOdd.get(i)<randNum){
                        randNum-=blockOdd.get(i);
                    }else {
                        blockPosInList = i;
                        break;
                    }
                }

                return idMetaList.get(blockPosInList);
            }

            private record BlockInfoFromCommand(String blockName, int id, int meta) {
            }
            private static void initBlocksNameList() {
                blocksNameList.add("Air");
                for (int i = 0; i < Block.blocksList.length; i++) {
                    Block block = Block.blocksList[i];
                    boolean sameName = false;
                    if (block != null) {
                        String blockName = block.getLocalizedName().replace(" ", "").replace("tile.","").replace("fc","").replace(".name","");
                        for (String name : blocksNameList) {
                            if (name.equalsIgnoreCase(blockName)) {
                                sameName = true;
                                break;
                            }
                        }
                        if (!sameName) {
                            blocksNameList.add(blockName);
                        }
                    }
                }
            }
        });
        registerAddonCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "inv";
            }

            @Override
            public String getCommandUsage(ICommandSender iCommandSender) {
                return "/inv <save> <name> // OR /inv <load> <name> OR //inv <preset> <presetName>";
            }
            @Override
            public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] strings) {
                if (strings.length==1) {
                    return getListOfStringsMatchingLastWord(strings, new String[]{"save", "load", "preset"});
                }else if (strings.length==2 && strings[0].equalsIgnoreCase("load")){
                    if (!invSavedNameList.isEmpty()) return getListOfStringsFromIterableMatchingLastWord(strings,invSavedNameList);
                }
                return null;
            }
            @Override
            public void processCommand(ICommandSender iCommandSender, String[] strings) {
                EntityPlayerMP player = getPlayer(iCommandSender,iCommandSender.getCommandSenderName());
                switch (strings[0]){

                    case "save" -> {
                        if (strings.length==2){
                            boolean sameName=false;
                            for (String name : invSavedNameList){
                                sameName = name.equalsIgnoreCase(strings[1]);
                            }
                            int index=invSavedNameList.size();
                            if (sameName){
                                index = invSavedNameList.indexOf(strings[1]);
                            }
                            invSavedNameList.add(index,strings[1]);
                            List<ItemStack> list = new ArrayList<>();
                            for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
                                ItemStack var2 = ((Slot)player.inventoryContainer.inventorySlots.get(i)).getStack();
                                list.add(var2);
                            }
                            invSavedList.add(list);
                            player.sendChatToPlayer(ChatMessageComponent.createFromText("Current inventory named \""+strings[1]+ "\" got saved."));
                        }
                    }
                    case "load" -> {
                        if (strings.length==2){
                            for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
                                ItemStack stack = invSavedList.get(invSavedNameList.indexOf(strings[1])).get(i);
                                ((Slot) player.inventoryContainer.inventorySlots.get(i)).putStack(stack);
                                player.sendChatToPlayer(ChatMessageComponent.createFromText("The inventory named \""+strings[1]+ "\" got loaded."));

                            }
                        }
                    }
                }
            }
        });
    }
    private record SavedBlock(int x, int y, int z, int id, int meta) {
    }

    // Handles slider value
    public void setSliderConfig(String property, float value) {
        switch (property) {
            case "reach":
                reach = (int)(value*128);
                break;
            case "flightSpeed":
                flySpeed = (int)(value*32)+1;
                break;
        }
        saveConfig();

    }

    // Gets slider display
    public String getSliderDisplay(String property) {
        return switch (property) {
            case "reach" -> "Reach: " + (int) reach;
            case "flightSpeed" -> flySpeed < 32 ? "Flight speed: " + (int) (flySpeed) : "Flight speed: too fast";
            default -> "";
        };
    }
}