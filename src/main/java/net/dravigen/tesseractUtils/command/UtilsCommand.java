package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.dravigen.tesseractUtils.TessUConfig.extrudeLimit;
import static net.minecraft.src.CommandBase.getPlayer;

public class UtilsCommand {

    public static List<String> entityShowNameList = new ArrayList<>();
    public static List<String> entityTrueNameList = new ArrayList<>();
    public static List<String> potionNameList = new ArrayList<>();

    public static Map<Class<?>, String> CLASS_TO_STRING_MAPPING;

    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> blocksNameList = new ArrayList<>();
    public static List<List<SavedBlock>> undoSaved = new ArrayList<>();
    public static List<List<SavedBlock>> redoSaved = new ArrayList<>();
    public static List<SavedBlock> copySaved = new ArrayList<>();
    public record SavedBlock(int x, int y, int z, int id, int meta) {
    }
    public static int x1=9999999;
    public static int y1=9999999;
    public static int z1=9999999;
    public static int x2=9999999;
    public static int y2=9999999;
    public static int z2=9999999;

    public static UtilsCommand instance;

    public static UtilsCommand getInstance() {
        return instance == null ? (new UtilsCommand()) : instance;
    }

    public static MovingObjectPosition getBlockPlayerIsLooking(ICommandSender par1ICommandSender) {
        EntityPlayer player = getPlayer(par1ICommandSender, par1ICommandSender.getCommandSenderName());
        Vec3 var3 = player.getPosition(1);
        Vec3 var4 = var3.addVector(0, player.getEyeHeight(), 0);
        Vec3 var5 = player.getLook(1);
        Vec3 var6 = var4.addVector(var5.xCoord * 128, var5.yCoord * 128, var5.zCoord * 128);
        return player.worldObj.clip(var4, var6);
    }

    public static BlockInfo getRandomBlockFromOdds(List<BlockInfo> blocks) {
        Random rand = new Random();
        int totalOdd=0;
        for (BlockInfo block: blocks){
            totalOdd+=block.odd;
        }
        int randNum = rand.nextInt(totalOdd)+1;
        int blockPosInList=0;
        for (int i = 0; i< blocks.size(); i++){
            if (blocks.get(i).odd<randNum){
                randNum-=blocks.get(i).odd;
            }else {
                blockPosInList = i;
                break;
            }
        }

        return blocks.get(blockPosInList);
    }

    public static void initBlocksNameList() {
        blocksNameList.clear();
        blocksNameList.add("Air");
        for (int i = 0; i < Block.blocksList.length; i++) {
            Block block = Block.blocksList[i];
            if (block == null||block.blockID==74) continue;
            List<ItemStack> subBlocks = new ArrayList<>();
            try {
                block.getSubBlocks(block.blockID, null, subBlocks);
                if (subBlocks.isEmpty()) {
                    subBlocks.add(new ItemStack(block));
                }

            } catch (Throwable e) {
                System.err.println("Error getting sub-blocks for Block ID " + i + " (" + block.getClass().getName() + "): " + e.getMessage());
                subBlocks.clear();
                subBlocks.add(new ItemStack(block));
            }
            for (ItemStack stack : subBlocks) {
                if (stack != null) {
                    String entry;
                    String previousString="";
                    if (!blocksNameList.isEmpty()) {
                        previousString = blocksNameList.get(blocksNameList.size()-1).split("\\|")[0].split("/")[0];
                    }
                    if (previousString.equalsIgnoreCase(stack.getDisplayName().replace(" ", "_"))) {
                        if (stack.getItemDamage() == 0) {
                            entry = stack.getDisplayName().replace(" ", "_") + "|" + stack.itemID;
                            blocksNameList.set(blocksNameList.size()-1,Item.itemsList[i-1].getItemDisplayName(new ItemStack(Item.itemsList[i-1])).replace(" ", "_") + "|" + new ItemStack(Item.itemsList[i-1]).itemID);
                        } else {
                            entry = stack.getDisplayName().replace(" ", "_") + "/" + stack.getItemDamage();
                            blocksNameList.set(blocksNameList.size()-1,stack.getItem().getItemDisplayName(new ItemStack(stack.getItem().itemID,1,subBlocks.get(subBlocks.indexOf(stack)-1).getItemDamage())).replace(" ", "_") + "/" + (subBlocks.get(subBlocks.indexOf(stack)-1).getItemDamage()));
                        }
                    } else entry = stack.getDisplayName().replace(" ", "_");
                    blocksNameList.add(entry.replace(".name", "").replace("name.", "").replace("tile.", "").replace("fc", ""));
                }
            }
        }
    }

    public @NotNull List<String> getBlockNameList(String[] strings) {
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

    public static @NotNull List<BlockInfo> getBlockInfo(String string) {
        List<String> differentBlock = Arrays.stream(string.split(";")).toList();
        List<BlockInfo> blocksInfos = new ArrayList<>();
        for (String diff:differentBlock){
            Block block;
            String blockName = "Air";
            int id = 99999;
            int meta = 0;
            int odd;
            String[] idMeta;
            String[] blockNodd = diff.split(":");
            odd = blockNodd.length>1 ? Integer.parseInt(blockNodd[1]) : 1;
            if (blockNodd[0].split("\\|").length>1){
                idMeta=new String[]{blockNodd[0].split("\\|")[1]};
            }else idMeta=blockNodd[0].split("/");
            try {
                id = Integer.parseInt(idMeta[0]);
            } catch (NumberFormatException ignored) {
            }
            if (id == 99999) {
                if (idMeta[0].equalsIgnoreCase("Air")) {
                    id = 0;
                } else {
                    boolean found = false;
                    for (int i = 0; i < Block.blocksList.length; i++) {
                        block = Block.blocksList[i];
                        if (block == null||block.blockID==74) continue;
                        List<ItemStack> subBlocks = new ArrayList<>();
                        try {
                            block.getSubBlocks(block.blockID, null, subBlocks);
                            if (subBlocks.isEmpty()) {
                                subBlocks.add(new ItemStack(block));
                            }

                        } catch (Throwable e) {
                            System.err.println("Error getting sub-blocks for Block ID " + i + " (" + block.getClass().getName() + "): " + e.getMessage());
                            subBlocks.clear();
                            subBlocks.add(new ItemStack(block));
                        }
                        for (ItemStack subBlock : subBlocks) {
                            String name = StringTranslate.getInstance().translateKey(subBlock.getUnlocalizedName() + ".name").replace(" ", "_").replace(".name", "").replace("name.", "").replace("tile.", "").replace("fc", "");
                            if (name.equalsIgnoreCase(idMeta[0])) {
                                blockName = name;
                                id = subBlock.itemID;
                                meta = subBlock.getItemDamage();
                                if (idMeta.length>1){
                                    meta = Integer.parseInt(idMeta[1]);
                                }
                                found=true;
                                break;
                            }
                        }
                        if (found)break;
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
            blocksInfos.add(new BlockInfo(blockName,id,meta,odd));
        }
        return blocksInfos;
    }

    public static void initItemsNameList() {
        itemNameList.clear();
        List<Item> itemList = new ArrayList<>();
        for (Item item:Item.itemsList){
           if (item!=null)itemList.add(item);
        }
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (item == null||item.itemID==74) continue;
            List<ItemStack> subItems = new ArrayList<>();
            try {
                item.getSubItems(item.itemID, null, subItems);
                if (subItems.isEmpty() && !item.getHasSubtypes()) {
                    subItems.add(new ItemStack(item, 1, 0));
                }

            } catch (Throwable e) {
                System.err.println("Error getting sub-items for Item ID " + i + " (" + item.getClass().getName() + "): " + e.getMessage());
                subItems.clear();
                subItems.add(new ItemStack(item, 1, 0));
            }
            for (ItemStack stack : subItems) {
                if (stack != null) {
                    String entry;
                    String previousString="";
                    if (!itemNameList.isEmpty()) {
                        previousString = itemNameList.get(itemNameList.size()-1).split("\\|")[0].split("/")[0];
                    }
                    if (previousString.equalsIgnoreCase(stack.getDisplayName().replace(" ", "_"))) {
                        if (stack.getItemDamage() == 0) {
                            entry = stack.getDisplayName().replace(" ", "_") + "|" + stack.itemID;
                            itemNameList.set(itemNameList.size()-1,itemList.get(i-1).getItemDisplayName(new ItemStack(itemList.get(i-1))).replace(" ", "_") + "|" + new ItemStack(itemList.get(i-1)).itemID);
                        } else {
                            entry = stack.getDisplayName().replace(" ", "_") + "/" + stack.getItemDamage();
                            itemNameList.set(itemNameList.size()-1,stack.getItem().getItemDisplayName(new ItemStack(stack.getItem().itemID,1,subItems.get(subItems.indexOf(stack)-1).getItemDamage())).replace(" ", "_") + "/" + (subItems.get(subItems.indexOf(stack)-1).getItemDamage()));
                        }
                    } else entry = stack.getDisplayName().replace(" ", "_");
                    itemNameList.add(entry.replace("tile.", "").replace("fc", "").replace(".name", ""));
                }
            }
        }
    }

    public @NotNull List<String> getItemNameList(String[] par2ArrayOfStr) {
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

    public static @NotNull ItemInfo getItemInfo(String[] strings) {
        Item item;
        int id = 99999;
        int meta = 0;
        String[] idMeta = strings[1].split("/");
        if (idMeta.length==2){
            meta= Integer.parseInt(idMeta[1]);
        }
        String itemName="";
        try {
            id = Integer.parseInt(idMeta[0]);
        } catch (NumberFormatException ignored) {
        }
        if (id == 99999) {
            for (int i = 0; i < Item.itemsList.length; i++) {
                item = Item.itemsList[i];
                if (item == null||item.itemID==74) continue;
                List<ItemStack> subItems = new ArrayList<>();
                try {
                    item.getSubItems(item.itemID, null, subItems);
                    if (subItems.isEmpty() && !item.getHasSubtypes()) {
                        subItems.add(new ItemStack(item, 1, 0));
                    }
                } catch (Throwable e) {
                    System.err.println("Error getting sub-items for Item ID " + i + " (" + item.getClass().getName() + "): " + e.getMessage());
                    subItems.clear();
                    subItems.add(new ItemStack(item, 1, 0));
                }
                for (ItemStack stack : subItems) {
                    if (stack != null) {
                        boolean sameItem;
                        String string = strings[1].split("/")[0].split("\\|")[0];
                        if (strings[1].split("/")[0].split("\\|").length>1){
                            sameItem= stack.itemID == Integer.parseInt(strings[1].split("/")[0].split("\\|")[1]);
                        }else sameItem = stack.getDisplayName().replace(" ", "_").equalsIgnoreCase(string);
                        if (sameItem){
                            id = stack.itemID;
                            meta = stack.getItemDamage();
                            if (strings[1].split("/").length>1){
                                meta = Integer.parseInt(strings[1].split("/")[1]);
                            }
                            itemName= string;
                            break;
                        }
                    }
                }
            }
        }else {
            itemName= new ItemStack(id,1,meta).getDisplayName();
        }
        return new ItemInfo(id,meta,itemName);
    }

    private static class ClassSimpleNameComparator implements Comparator<Class<?>> {
        @Override
        public int compare(Class<?> class1, Class<?> class2) {
            if (class1 == null && class2 == null) return 0;
            if (class1 == null) return -1;
            if (class2 == null) return 1;
            processEntityName result1 = getProcessEntityName(CLASS_TO_STRING_MAPPING, class1);
            processEntityName result2 = getProcessEntityName(CLASS_TO_STRING_MAPPING, class2);
            return result1.finalName.compareTo(result2.finalName);
        }
    }

    public static void initEntityList(){

        entityShowNameList.clear();
        entityTrueNameList.clear();
        if (CLASS_TO_STRING_MAPPING!=null) {
            Map<Class<?>, String> sortedMap = new TreeMap<>(new ClassSimpleNameComparator());
            sortedMap.putAll(CLASS_TO_STRING_MAPPING);
            for (Map.Entry<Class<?>, String> entry : sortedMap.entrySet()) {
                Class<?> entityClass = entry.getKey();
                processEntityName result = getProcessEntityName(sortedMap, entityClass);
                entityTrueNameList.add(result.base());
                entityShowNameList.add(result.finalName());
            }
        }
    }

    private static @NotNull processEntityName getProcessEntityName(Map<Class<?>, String> sortedMap, Class<?> entityClass) {
        String base = sortedMap.get(entityClass);
        String unlocalizedName = "entity." + base + ".name";
        String translated = StringTranslate.getInstance().translateKey(unlocalizedName);
        if (translated.equalsIgnoreCase(unlocalizedName)) {
            translated = base;
        }
        String finalName = translated.replace("Entity", "").replace("addon", "").replace("fc", "").replace(StringTranslate.getInstance().translateKey("entity.villager.name"), "").replace("DireWolf", "The_Beast").replace("JungleSpider", "Jungle_Spider").replace("arrow", "Arrow").replace(" ", "_");
        return new processEntityName(base, finalName);
    }

    private record processEntityName(String base, String finalName) {
    }

    public @NotNull List<String> getEntityName(String[] strings) {
        List<String> finalList = new ArrayList<>();
        String var1 = strings[strings.length - 1];
        String var2 = ":";
        boolean afterColon = var2.regionMatches(true, 0, var1, var1.length()-1, 1);

        for (String string : entityShowNameList) {
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

    public static void initPotionList(){
        for (Potion potion:Potion.potionTypes){
            if (potion==null)continue;
            String name = StringTranslate.getInstance().translateKey(potion.getName());
            potionNameList.add(name.replace(" ","_"));
        }
    }

    public record BlockInfo(String blockName, int id, int meta, int odd) { }

    public record ItemInfo (int id, int meta, String itemName){ }

    public static class BlockPos {
        public int x, y, z;
        public BlockPos(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockPos blockPos = (BlockPos) o;
            return x == blockPos.x && y == blockPos.y && z == blockPos.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    public static List<BlockPos> findConnectedBlocksInPlane(
            World world,
            int startX, int startY, int startZ,
            int referenceBlockId,
            int referenceBlockMeta,
            int clickedFace,
            boolean fuzzy) {

        List<BlockPos> foundBlocks = new ArrayList<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        BlockPos startPos = new BlockPos(startX, startY, startZ);

        queue.add(startPos);
        visited.add(startPos);

        int[][] planeOffsets;
        if (fuzzy){
            switch (clickedFace) {
                case 0,1: //tranversal
                    planeOffsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1}};
                    break;
                case 2,3: //frontal
                    planeOffsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0},{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}};
                    break;
                case 4,5: //sagittal
                    planeOffsets = new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1},{0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
                    break;
                default:
                    return foundBlocks;
            }
        }else switch (clickedFace) {
            case 0: // Bottom face clicked (+Y direction of extrusion) -> plane is XZ (Y is constant)
            case 1: // Top face clicked (-Y direction of extrusion) -> plane is XZ (Y is constant)
                planeOffsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}}; // Check X,Z neighbors
                break;
            case 2: // North face clicked (+Z direction of extrusion) -> plane is XY (Z is constant)
            case 3: // South face clicked (-Z direction of extrusion) -> plane is XY (Z is constant)
                planeOffsets = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}}; // Check X,Y neighbors
                break;
            case 4: // West face clicked (+X direction of extrusion) -> plane is YZ (X is constant)
            case 5: // East face clicked (-X direction of extrusion) -> plane is YZ (X is constant)
                planeOffsets = new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}; // Check Y,Z neighbors
                break;
            default:
                return foundBlocks;
        }
        while (!queue.isEmpty() && foundBlocks.size() < extrudeLimit) {
            BlockPos current = queue.poll();
            foundBlocks.add(current);
            for (int[] offset : planeOffsets) {
                int nextX = current.x + offset[0];
                int nextY = current.y + offset[1];
                int nextZ = current.z + offset[2];
                BlockPos neighbor = new BlockPos(nextX, nextY, nextZ);
                if (nextY < 0 || nextY >= world.getHeight()) {
                    continue;
                }

                int newBlockX = nextX;
                int newBlockY = nextY;
                int newBlockZ = nextZ;

                switch (clickedFace) {
                    case 0: newBlockY--; break; // Bottom face: place below
                    case 1: newBlockY++; break; // Top face: place above
                    case 2: newBlockZ--; break; // North face: place North
                    case 3: newBlockZ++; break; // South face: place South
                    case 4: newBlockX--; break; // West face: place West
                    case 5: newBlockX++; break; // East face: place East
                }
                int targetBlockId = world.getBlockId(newBlockX, newBlockY, newBlockZ);
                if (!visited.contains(neighbor)&&targetBlockId==0) {
                    int neighborId = world.getBlockId(nextX, nextY, nextZ);
                    int neighborMeta = world.getBlockMetadata(nextX, nextY, nextZ);
                    if (neighborId == referenceBlockId&& neighborMeta == referenceBlockMeta) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
        return foundBlocks;
    }


}
