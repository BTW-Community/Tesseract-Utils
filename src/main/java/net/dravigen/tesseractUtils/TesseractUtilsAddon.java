package net.dravigen.tesseractUtils;

import btw.AddonHandler;
import btw.BTWAddon;
import net.dravigen.tesseractUtils.item.DeleteEntityItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.dravigen.tesseractUtils.TessUConfig.*;
import static net.minecraft.src.CommandBase.getPlayer;

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
    public static List<String> entityNameList = new ArrayList<>();
    public static List<String> itemNameList = new ArrayList<>();
    private static Language listLanguage;

    public static List<String> invSavedNameList = new ArrayList<>();
    public static List<List<SavedBlock>> undoSaved = new ArrayList<>();
    public static List<List<SavedBlock>> redoSaved = new ArrayList<>();
    public static List<SavedBlock> copySaved = new ArrayList<>();
    public static List<List<ItemStack>> invSavedList = new ArrayList<>();
    
    public static Item deleteEntityItem;
    

    public static TesseractUtilsAddon getInstance() {
        return instance == null ? (new TesseractUtilsAddon()) : instance;
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        createNewCommand();
        loadConfig();
        
        deleteEntityItem=new DeleteEntityItem(1800-256).setCreativeTab(CreativeTabs.tabTools);
    }


    private void createNewCommand() {
        /// worldEdit command
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
                if (listLanguage!=Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
                    blocksNameList.clear();
                }
                if (blocksNameList.isEmpty()) {
                    initBlocksNameList();
                }
                if (strings.length==1) {
                    return getListOfStringsMatchingLastWord(strings,"set", "setblock", "replace", "move", "undo", "redo", "copy", "paste", "pos1", "pos2");
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
                        return getListOfStringsMatchingLastWord(strings, "hollow", "wall");
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
                    if (string.toLowerCase().startsWith(lastString.toLowerCase())){
                        finalList.add(firstString+string);
                    }
                }
                for (String string : blocksNameList) {
                    List<String> split = new ArrayList<>(List.of(strings[strings.length - 1].split(";")));
                    String lastString = split.get(split.size()-1);
                    if (!afterSemiColon) split.remove(split.size()-1);
                    String firstString = afterSemiColon ? var1 : split.isEmpty() ? "" : String.join(";",split)+";";

                    if (afterSemiColon){
                        lastString = "";
                    }
                    if (!string.toLowerCase().startsWith(lastString.toLowerCase())&&string.toLowerCase().contains(lastString.toLowerCase())){
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
                            if (block != null) {
                                String name = StringTranslate.getInstance().translateKey(block.getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
                                if (name.equalsIgnoreCase(idMeta[0])) {
                                    blockName = name;
                                    id = block.blockID;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < Block.blocksList.length; i++) {
                        block = Block.blocksList[i];
                        if (block != null && block.blockID == id) {
                            blockName = StringTranslate.getInstance().translateKey(Block.blocksList[i].getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
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
                        String blockName = StringTranslate.getInstance().translateKey(block.getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
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
        /// inventory command
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
                    return getListOfStringsMatchingLastWord(strings, "save", "load", "preset");
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
        /// summon command
        registerAddonCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "summon";
            }

            @Override
            public String getCommandUsage(ICommandSender iCommandSender) {
                return "/summon <entity> [count]";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] strings) {
                if (strings.length==1){
                    return getEntityName(strings);
                }
                MovingObjectPosition blockCoord = getBlockPlayerIsLooking(par1ICommandSender);
                if (strings.length>=3) {
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
                    } else return null;
                }
                return null;
            }

            @Override
            public void processCommand(ICommandSender iCommandSender, String[] strings) {
                if (strings.length>=1) {
                    int entityNum = 1;
                    if (strings.length >= 2) {
                        entityNum = Integer.parseInt(strings[1]);
                    }
                    int x;
                    int y;
                    int z;
                    if (strings.length >= 4) {
                        try {
                            x = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posX, strings[1]));
                            y = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posY, strings[2]));
                            z = MathHelper.floor_double(CommandBase.func_110666_a(iCommandSender, iCommandSender.getPlayerCoordinates().posZ, strings[3]));
                        } catch (Exception ignored) {
                            x = Integer.parseInt(strings[2]);
                            y = Integer.parseInt(strings[3]);
                            z = Integer.parseInt(strings[4]);
                        }
                    } else {
                        x = iCommandSender.getPlayerCoordinates().posX;
                        y = iCommandSender.getPlayerCoordinates().posY;
                        z = iCommandSender.getPlayerCoordinates().posZ;
                    }
                    for (int i = 0; i < entityNum; i++) {
                        List<String> entitiesName = Arrays.stream(strings[0].split(":")).toList();
                        List<Entity> entities = new ArrayList<>();

                        for (String entity1 :entitiesName) {
                            for (String name :entityNameList){
                                if (name.equalsIgnoreCase(entity1)){
                                    entities.add(EntityList.createEntityByName(entityNameList.get(entityNameList.indexOf(name)), iCommandSender.getEntityWorld()));
                                    break;
                                }
                            }
                        }
                        for (int j=0; j<entities.size(); j++){
                            Entity entity2 = entities.get(j);
                            entity2.setPosition(x,y,z);
                            iCommandSender.getEntityWorld().spawnEntityInWorld(entity2);
                            if (j>0) {
                                entity2.mountEntity(entities.get(j - 1));
                            }
                        }
                    }
                }
            }

        });
        /// kill command
        registerAddonCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "kill";
            }

            @Override
            public String getCommandUsage(ICommandSender iCommandSender) {
                return "";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
                if (listLanguage!=Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
                    itemNameList.clear();
                }
                if (itemNameList.isEmpty()) {
                    initItemsNameList();
                    listLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
                }
                if (par2ArrayOfStr.length==1){
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr,"player","mob","item","all");
                }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("mob")){
                    return getEntityName(par2ArrayOfStr);
                }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("player")){
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
                }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("item")){
                    return getItemNameList(par2ArrayOfStr);
                }
                return null;
            }

            @Override
            public void processCommand(ICommandSender iCommandSender, String[] strings) {
                boolean dropLoot = false;
                for (String string : strings) {
                    if (!dropLoot) {
                        dropLoot = string.equalsIgnoreCase("dropLoot");
                    }
                }
                if (strings.length>=1) {
                    if (strings[0].equalsIgnoreCase("all")) {
                        int killCount = 0;
                        for (int i = 0; i < iCommandSender.getEntityWorld().loadedEntityList.size(); i++) {
                            Entity entity = (Entity) iCommandSender.getEntityWorld().loadedEntityList.get(i);
                            if (!(entity instanceof EntityPlayer)) {
                                killCount++;
                                if (dropLoot) {
                                    entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                                } else entity.setDead();
                            }
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(killCount + " entities got killed."));
                    } else if (strings[0].equalsIgnoreCase("mob")) {
                        int killCount = 0;
                        for (String name : entityNameList) {
                            if (strings[1].equalsIgnoreCase(name)) {
                                for (int i = 0; i < iCommandSender.getEntityWorld().loadedEntityList.size(); i++) {
                                    Entity entity = (Entity) iCommandSender.getEntityWorld().loadedEntityList.get(i);
                                    if (entity.getEntityName().equalsIgnoreCase(name)) {
                                        if (dropLoot) {
                                            entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                                        } else entity.setDead();
                                        killCount++;
                                    }
                                }
                            }
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(killCount + " " + strings[1] + " got killed."));

                    } else if (strings[0].equalsIgnoreCase("player")&&strings.length==2) {
                        EntityPlayerMP var3;
                        var3 = CommandGive.getPlayer(iCommandSender, strings[1]);
                        if (dropLoot) {
                            var3.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                        } else {
                            var3.setHealth(0);
                        }
                        var3.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(var3.getEntityName() + " got killed."));

                    } else if (strings[0].equalsIgnoreCase("item")) {
                        int killCount = 0;
                        ItemInfo itemInfo = getItemInfo(strings);
                        int id = itemInfo.id();
                        for (Object entity: iCommandSender.getEntityWorld().loadedEntityList){
                            if (entity instanceof EntityItem item){
                                if (id==item.getEntityItem().itemID){
                                    killCount++;
                                    item.setDead();
                                }
                            }
                        }
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(killCount + " " + itemInfo.itemName + " item(s) got killed."));
                    }
                }
            }


        });
        /// overwrite give command
        registerAddonCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "give";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 2;
            }

            @Override
            public String getCommandUsage(ICommandSender par1ICommandSender) {
                return "commands.give.usage";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
                if (listLanguage!=Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
                    itemNameList.clear();
                }
                if (itemNameList.isEmpty()) {
                    initItemsNameList();
                    listLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
                }
                if (par2ArrayOfStr.length==1){
                    return CommandGive.getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers());
                }else if (par2ArrayOfStr.length==2){
                    return getItemNameList(par2ArrayOfStr);
                }
                return null;
            }

            @Override
            public void processCommand(ICommandSender par1ICommandSender, String[] strings) {
                int var6;
                int var5;
                int var4;
                EntityPlayerMP var3;
                if (strings.length >= 2) {
                    var3 = CommandGive.getPlayer(par1ICommandSender, strings[0]);
                    ItemInfo itemInfo = getItemInfo(strings);
                    var4 = itemInfo.id;
                    var5 = 1;
                    var6 = 0;
                    if (Item.itemsList[var4] == null) {
                        throw new NumberInvalidException("commands.give.notFound", var4);
                    }
                    if (strings.length >= 3) {
                        var5 = CommandGive.parseIntBounded(par1ICommandSender, strings[2], 1, 64);
                    }
                    if (strings.length >= 4) {
                        var6 = CommandGive.parseInt(par1ICommandSender, strings[3]);
                    }
                } else {
                    throw new WrongUsageException("commands.give.usage");
                }
                ItemStack var7 = new ItemStack(var4, var5, var6);
                var7.getItem().initializeStackOnGiveCommand(var3.worldObj.rand, var7);
                EntityItem var8 = var3.dropPlayerItem(var7);
                var8.delayBeforeCanPickup = 0;
                CommandGive.notifyAdmins(par1ICommandSender, "commands.give.success", Item.itemsList[var4].getItemStackDisplayName(var7), var4, var5, var3.getEntityName());
            }

            private String[] getPlayers() {
                return MinecraftServer.getServer().getAllUsernames();
            }

        });


    }
    private static @NotNull ItemInfo getItemInfo(String[] strings) {
        Item item;
        int id = 99999;
        String itemName="";
        try {
            id = Integer.parseInt(strings[1]);
        } catch (NumberFormatException ignored) {
        }
        if (id == 99999) {
            for (int i = 0; i < Item.itemsList.length; i++) {
                item = Item.itemsList[i];
                if (item != null) {
                    itemName = StringTranslate.getInstance().translateKey(item.getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
                    if (itemName.equalsIgnoreCase(strings[1])) {
                        id = item.itemID;
                        break;
                    }
                }
            }
        }else {
            itemName = StringTranslate.getInstance().translateKey(Item.itemsList[id].getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
        }
        return new ItemInfo(id,itemName);
    }
    private record ItemInfo (int id,String itemName){
    }
    private static void initItemsNameList() {
        for (int i = 0; i < Item.itemsList.length; i++) {
            Item item = Item.itemsList[i];
            boolean sameName = false;
            if (item != null) {
                String itemName = StringTranslate.getInstance().translateKey(item.getUnlocalizedName()+ ".name").replace(" ", "_").replace(".name","").replace("name.","").replace("tile.","").replace("fc","");
                for (String name : itemNameList) {
                    if (name.equalsIgnoreCase(itemName)) {
                        sameName = true;
                        break;
                    }
                }
                if (!sameName) {
                    itemNameList.add(itemName);
                }
            }
        }
    }
    private @NotNull List<String> getItemNameList(String[] par2ArrayOfStr) {
        List<String > finalList = new ArrayList<>();
        for (String string: itemNameList){
            if (string.toLowerCase().startsWith(par2ArrayOfStr[1].toLowerCase())){
                finalList.add(string);
            }
        }
        for (String string: itemNameList){
            if (string.toLowerCase().contains(par2ArrayOfStr[1].toLowerCase())&&!string.toLowerCase().startsWith(par2ArrayOfStr[1].toLowerCase())){
                finalList.add(string);
            }
        }
        return finalList;
    }

    private record SavedBlock(int x, int y, int z, int id, int meta) {
    }
    private @NotNull List<String> getEntityName(String[] strings) {
        List<String> finalList = new ArrayList<>();
        String var1 = strings[strings.length - 1];
        String var2 = ":";
        boolean afterColon = var2.regionMatches(true, 0, var1, var1.length()-1, 1);

        for (String string : entityNameList) {
            List<String> split = new ArrayList<>(List.of(strings[strings.length - 1].split(":")));
            String lastString = split.get(split.size()-1);
            if (!afterColon) split.remove(split.size()-1);
            String firstString = afterColon ? var1 : split.isEmpty() ? "" : String.join(":",split)+":";

            if (afterColon){
                lastString = "";
            }
            if (string.toLowerCase().startsWith(lastString.toLowerCase())){
                finalList.add(firstString+string);
            }
        }
        return finalList;
    }
    private MovingObjectPosition getBlockPlayerIsLooking(ICommandSender par1ICommandSender) {
        EntityPlayer player = getPlayer(par1ICommandSender, par1ICommandSender.getCommandSenderName());
        Vec3 var3 = player.getPosition(1);
        Vec3 var4 = var3.addVector(0, player.getEyeHeight(), 0);
        Vec3 var5 = player.getLook(1);
        Vec3 var6 = var4.addVector(var5.xCoord * reach, var5.yCoord * reach, var5.zCoord * reach);
        return player.worldObj.clip(var4, var6);
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