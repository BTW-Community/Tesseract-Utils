package net.dravigen.tesseractUtils.utils;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import java.util.*;

import static net.dravigen.tesseractUtils.utils.PacketUtils.*;
import static net.minecraft.src.CommandBase.getPlayer;

public class ListsUtils {

    public static Map<Class<?>, String> CLASS_TO_STRING_MAPPING;

    public static Map<String,String> blocksMapServ = new HashMap<>();
    public static Map<String,String> itemsMapServ = new HashMap<>();
    public static Map<String,String> itemsMapClient = new HashMap<>();
    public static Map<String,String> blocksMapClient = new HashMap<>();

    public static Map<String,Short> potionsMap = new HashMap<>();
    public static Map<String,Integer> entitiesMap = new HashMap<>();
    public static Map<String,Short> enchantMap = new HashMap<>();


    public record SavedBlock(int x, int y, int z, int id, int meta) {}

    public static ListsUtils instance;

    public static Map<String,String> englishLanguage = new HashMap<>();

    public static ListsUtils getInstance() {
        return instance == null ? (new ListsUtils()) : instance;
    }

    public static void initAllServList(){
        initEnchantNameList();
        initEntityList();
        initPotionList();
        initBlocksNameListServ();
        initItemsNameListServ();
    }

    public static void initAllClientList(){
        initBlocksNameListClientSub();
        initItemsNameListClientSub();
    }

    public static MovingObjectPosition getBlockPlayerIsLooking(ICommandSender sender) {
        EntityPlayer player = getPlayer(sender, sender.getCommandSenderName());
        Vec3 var3 = player.getPosition(TesseractUtilsAddon.partialTick);
        var3.yCoord += player.getEyeHeight();
        Vec3 var4 = player.getLook(TesseractUtilsAddon.partialTick);
        int reach = Integer.parseInt(playersInfoServer.get(player.getEntityName()).get(EnumConfig.REACH.ordinal()));
        Vec3 var5 = var3.addVector(var4.xCoord * reach, var4.yCoord * reach, var4.zCoord * reach);
        return player.worldObj.clip(var3, var5);
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

    public static void initEnchantNameList(){
        enchantMap.clear();
        for (Enchantment enchant:Enchantment.enchantmentsList){
            if (enchant==null)continue;
            String name = StringTranslate.getInstance().translateKey(enchant.getName());
            enchantMap.put(name.replace(" ","_"), (short) enchant.effectId);
        }
        enchantMap=sortMapShort(enchantMap);
    }

    public static void initBlocksNameListServ() {
        blocksMapServ.clear();
        List<ItemStack> subBlocks = new ArrayList<>();
        List<ItemStack> subBlocks2 = new ArrayList<>();

        Map<String,String> map = new HashMap<>();
        map.put("Air", "0");
        for (int i = 0; i < Block.blocksList.length; i++) {
            Block block = Block.blocksList[i];
            if (block == null||block.blockID==74) continue;

            try {
                subBlocks.add(new ItemStack(block));

                block.getSubBlocks(block.blockID, null, subBlocks2);
            } catch (Throwable e) {
                System.err.println("Error getting sub-blocks for Block ID " + i + " (" + block.getClass().getName() + "): " + e.getMessage());
                subBlocks.add(new ItemStack(block));
            }
        }
        for (ItemStack stack : subBlocks) {
            if (stack != null&&stack.getItem() != null) {
                //String entry = stack.getDisplayName().replace(" ", "_");
                String entry = translate(stack);

                if (entry.contains("Old")||entry.contains("BlockCandle")||entry.contains("null")) continue;
                for (ItemStack stack2 : subBlocks2) {
                    if (stack2==null||stack2.getItem()==null||subBlocks.indexOf(stack)==subBlocks2.indexOf(stack2))continue;
                    //String name = stack2.getDisplayName().replace(" ", "_");
                    String name = translate(stack2);

                    if (name.equalsIgnoreCase(entry)) {
                        if (stack.itemID == stack2.itemID) {
                            entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
                        } else {
                            entry += "|" + stack.itemID;
                        }
                        break;
                    }
                }
                String finalItemName = entry.replace("tile.", "").replace("fc", "").replace("name.", "").replace(".name", "").replace("item.", "").replace("btw:", "").replace(".siding", "").replace(".corner", "").replace(".moulding", "");
                map.put(finalItemName, stack.itemID + "/" + stack.getItemDamage());
            }
        }
        blocksMapServ = sortMapStringFloat(map);
    }

    public static void initBlocksNameListClientSub(){
        blocksMapClient.clear();
        List<ItemStack> subBlocks = new ArrayList<>();
        List<ItemStack> subBlocks2 = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        for (Block block:Block.blocksList) {
            if (block==null)continue;
            List<ItemStack> sub = new ArrayList<>();
            Item item = new ItemStack(block).getItem();
            block.getSubBlocks(block.blockID, null, sub);
            if (block.blockID==74) continue;
            subBlocks2.addAll(sub);
            if (!(item.getHasSubtypes())||sub.size()==1) continue;
            for (int j = 1; j < sub.size(); j++) {
                subBlocks.add(sub.get(j));
            }
            /*
            try {
                block.getSubBlocks(block.blockID, null, subBlocks);
            } catch (Throwable e) {
                System.err.println("Error getting sub-blocks for Block ID " + i + " (" + block.getClass().getName() + "): " + e.getMessage());
                subBlocks.add(new ItemStack(block));
            }*/
        }
        for (ItemStack stack : subBlocks) {
            if (stack != null&&stack.getItem() != null) {
                //String entry = stack.getDisplayName().replace(" ", "_");
                String entry = translate(stack);

                if (entry.contains("Old")||entry.contains("BlockCandle")||entry.contains("null")) continue;
                for (ItemStack stack2 : subBlocks2) {
                    if (stack2==null||stack2.getItem()==null||subBlocks.indexOf(stack)==subBlocks2.indexOf(stack2))continue;
                    //String name = stack2.getDisplayName().replace(" ", "_");
                    String name = translate(stack2);

                    if (name.equalsIgnoreCase(entry)) {
                        if (stack.itemID == stack2.itemID) {
                            entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
                        } else {
                            entry += "|" + stack.itemID;
                        }
                        break;
                    }
                }
                String finalItemName = entry.replace("tile.", "").replace("fc", "").replace("name.", "").replace(".name", "").replace("item.", "").replace("btw:", "").replace(".siding", "").replace(".corner", "").replace(".moulding", "");
                map.put(finalItemName, stack.itemID + "/" + stack.getItemDamage());
            }
        }
        blocksMapClient = sortMapStringFloat(map);
    }

    private static @NotNull String translate(ItemStack stack) {
        String par1Str = (stack.getUnlocalizedName() + ".name").trim();
        String var2 = englishLanguage.get(par1Str);
        return (var2 == null ? par1Str : var2).replace(" ", "_");
    }

    public static @NotNull List<String> getBlockNameList(String[] strings) {
        List<String> finalList = new ArrayList<>();
        String var1 = strings[strings.length - 1];
        String var2 = ";";
        boolean afterSemiColon = var2.regionMatches(true, 0, var1, var1.length()-1, 1);

        for (String string : blocksMapServ.keySet()) {
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

        for (String string : blocksMapServ.keySet()) {
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

    public static List<BlockInfo> getBlockInfo(String string, String username) {
        List<String> differentBlock = Arrays.stream(string.split(";")).toList();
        List<BlockInfo> blocksInfos = new ArrayList<>();
        for (String diff:differentBlock){
            String blockName;
            int id;
            int meta=0;
            String[] blockNodd = diff.split(":");
            int odd = blockNodd.length==2 ? Integer.parseInt(blockNodd[1]) : 1;
            String[] idMeta = blockNodd[0].split("/");
            try {
                id = Integer.parseInt(idMeta[0]);
                if (idMeta.length==2){
                    meta= Integer.parseInt(idMeta[1]);
                }
            } catch (NumberFormatException ignored) {
                try {
                    String[] idMetaF = blocksMapServ.get(idMeta[0]).split("/");
                    id = Integer.parseInt(idMetaF[0]);
                    meta = Integer.parseInt(idMetaF[1]);
                    if (id!=0&&new ItemStack(id, 0, 0).getHasSubtypes() && idMeta.length == 2) {
                        meta = Integer.parseInt(idMeta[1]);
                    }
                } catch (Exception ignored1) {
                    return null;
                }
            }
            if (id!=0) {
                blockName = new ItemStack(id, 1, meta).getDisplayName().replace("tile.", "").replace("fc", "").replace(".name", "").replace("item.", "").replace("btw:", "").replace(".siding", "").replace(".corner", "").replace(".moulding", "");
            }else {
                blockName = "Air";
            }
            blocksInfos.add(new BlockInfo(blockName,id,meta,odd));
        }
        return blocksInfos;
    }

    public static void initItemsNameListServ() {
        itemsMapServ.clear();
        List<Item> itemList = new ArrayList<>();
        List<ItemStack> subItems = new ArrayList<>();
        List<ItemStack> subItems2 = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        for (Item item : Item.itemsList) {
            if (item != null) itemList.add(item);
        }
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (item == null || item.itemID == 74) continue;
            try {
                subItems.add(new ItemStack(item, 1, 0));
                item.getSubItems(item.itemID, null, subItems2);
               /*
                if (subItems.isEmpty() && !item.getHasSubtypes()) {
                }*/
            } catch (Throwable e) {
                System.err.println("Error getting sub-items for Item ID " + i + " (" + item.getClass().getName() + "): " + e.getMessage());
                subItems.add(new ItemStack(item, 1, 0));
            }
        }
        for (ItemStack stack : subItems) {
            if (stack == null || stack.getItem() == null) continue;

            //String entry = stack.getDisplayName().replace(" ", "_");
            String entry = translate(stack);

            if (entry.contains("Old") || entry.contains("BlockCandle")) continue;

            for (ItemStack stack2 : subItems2) {
                if (stack2 == null || stack2.getItem() == null || subItems.indexOf(stack) == subItems2.indexOf(stack2))
                    continue;
                //String name = stack2.getDisplayName().replace(" ", "_");
                String name = translate(stack2);
                if (name.equalsIgnoreCase(entry)) {
                    if (stack.itemID == stack2.itemID) {
                        entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
                    } else {
                        entry += "|" + stack.itemID;
                    }
                    break;
                }
            }
            String finalItemName = entry.replace("tile.", "").replace("fc", "").replace(".name", "").replace("item.", "").replace("btw:", "").replace(".siding", "").replace(".corner", "").replace(".moulding", "");
            map.put(finalItemName, stack.itemID + "/" + stack.getItemDamage());

        }
        itemsMapServ =sortMapStringFloat(map);

    }

    public static void initItemsNameListClientSub() {
        itemsMapClient.clear();
        List<ItemStack> subItems = new ArrayList<>();
        List<ItemStack> subItems2 = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        for (Item item : Item.itemsList) {
            if (item == null) continue;
            List<ItemStack> sub = new ArrayList<>();
            item.getSubItems(item.itemID, null, sub);
            if (item.itemID == 74) continue;
            subItems2.addAll(sub);
            if (!item.getHasSubtypes() || sub.size() == 1) continue;
            for (int j = 1; j < sub.size(); j++) {
                subItems.add(sub.get(j));
            }
        }
        for (ItemStack stack : subItems) {
            if (stack == null || stack.getItem() == null) continue;

            //String entry = stack.getDisplayName().replace(" ", "_");
            String entry = translate(stack);

            if (entry.contains("Old") || entry.contains("BlockCandle")) continue;

            for (ItemStack stack2 : subItems2) {
                if (stack2 == null || stack2.getItem() == null || subItems.indexOf(stack) == subItems2.indexOf(stack2)) continue;

                //String name = stack2.getDisplayName().replace(" ", "_");
                String name = translate(stack2);

                if (name.equalsIgnoreCase(entry)) {
                    if (stack.itemID == stack2.itemID) {
                        entry += (stack.getHasSubtypes() ? "/" + stack.getItemDamage() : "");
                    } else {
                        entry += "|" + stack.itemID;
                    }
                    break;
                }
            }
            String finalItemName = entry.replace("tile.", "").replace("fc", "").replace(".name", "").replace("item.", "").replace("btw:", "").replace(".siding", "").replace(".corner", "").replace(".moulding", "");
            map.put(finalItemName, stack.itemID + "/" + stack.getItemDamage());

        }
        itemsMapClient =sortMapStringFloat(map);

    }

    public @NotNull List<String> getItemNameList(String[] strings) {
        List<String > finalList = new ArrayList<>();
        for (String string: itemsMapServ.keySet()){
            if (string.toLowerCase().startsWith(strings[1].toLowerCase())){
                finalList.add(string);
            }
        }
        for (String string: itemsMapServ.keySet()){
            if (string.toLowerCase().contains(strings[1].toLowerCase())&&!string.toLowerCase().startsWith(strings[1].toLowerCase())){
                finalList.add(string);
            }
        }
        return finalList;
    }

    public static ItemInfo getItemInfo(String[] strings) {
        int id;
        int meta = 0;
        String[] idMeta = strings[1].split("/");
        try {
            id = Integer.parseInt(idMeta[0]);
            if (idMeta.length==2){
                meta= Integer.parseInt(idMeta[1]);
            }
        } catch (NumberFormatException ignored) {
            try {
                String[] idMetaF = itemsMapServ.get(idMeta[0]).split("/");
                id = Integer.parseInt(idMetaF[0]);
                meta = Integer.parseInt(idMetaF[1]);
                if (new ItemStack(id, 0, 0).getHasSubtypes() && idMeta.length == 2) {
                    meta = Integer.parseInt(idMeta[1]);
                }
            } catch (Exception ignored1) {
                return null;
            }
        }
        return new ItemInfo(id,meta,strings[1]);
    }

    public static void initEntityList(){
        entitiesMap.clear();
        if (CLASS_TO_STRING_MAPPING!=null) {
            for (Map.Entry<Class<?>, String> entry : CLASS_TO_STRING_MAPPING.entrySet()) {
                Class<?> entityClass = entry.getKey();
                String base = CLASS_TO_STRING_MAPPING.get(entityClass);
                String unlocalizedName = "entity." + base + ".name";
                String translated = StringTranslate.getInstance().translateKey(unlocalizedName);
                if (translated.equalsIgnoreCase(unlocalizedName)) {
                    translated = base;
                }
                String finalName = translated.replace("Entity", "").replace("addon", "").replace("fc", "").replace(StringTranslate.getInstance().translateKey("entity.villager.name"), "").replace("DireWolf", "The_Beast").replace("JungleSpider", "Jungle_Spider").replace("arrow", "Arrow").replace(" ", "_");
                entitiesMap.put(finalName, EntityList.getEntityIDFromClass(entityClass));
            }
        }
    }

    public @NotNull List<String> getEntityName(String[] strings) {
        List<String> finalList = new ArrayList<>();
        String var1 = strings[strings.length - 1];
        String var2 = ":";
        boolean afterColon = var2.regionMatches(true, 0, var1, var1.length()-1, 1);

        for (String string : entitiesMap.keySet()) {
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
        Collections.sort(finalList);
        return finalList;
    }

    public static void initPotionList(){
        for (Potion potion:Potion.potionTypes){
            if (potion==null)continue;
            String name = StringTranslate.getInstance().translateKey(potion.getName());
            potionsMap.put(name.replace(" ","_"), (short) potion.id);
        }
        potionsMap = sortMapShort(potionsMap);
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

    public static List<BlockPos> findConnectedBlocksInPlane(World world, int startX, int startY, int startZ, int referenceBlockId, int referenceBlockMeta, int clickedFace, boolean fuzzy, int extrudeLimit) {
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
                if (nextY < 0 || nextY >= world.getHeight()) continue;
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

    private static final int[][] CARDINAL_OFFSETS = {
            {1, 0, 0}, {-1, 0, 0},
            {0, 1, 0}, {0, -1, 0},
            {0, 0, 1}, {0, 0, -1}
    };

    private static final int[][] FUZZY_3D_OFFSETS;
    static {
        List<int[]> offsetsList = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    offsetsList.add(new int[]{dx, dy, dz});
                }
            }
        }
        FUZZY_3D_OFFSETS = offsetsList.toArray(new int[0][]);
    }

    public static List<BlockPos> findConnectedBlocksIn3D(World world, int startX, int startY, int startZ, int referenceBlockId, int referenceBlockMeta, boolean fuzzy, int extrudeLimit) {

        List<BlockPos> foundBlocks = new ArrayList<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        BlockPos startPos = new BlockPos(startX, startY, startZ);

        queue.add(startPos);
        visited.add(startPos);

        int[][] neighborsOffsets = fuzzy ? FUZZY_3D_OFFSETS : CARDINAL_OFFSETS;
        while (!queue.isEmpty() && foundBlocks.size() < extrudeLimit) {
            BlockPos current = queue.poll();
            foundBlocks.add(current);
            for (int[] offset : neighborsOffsets) {
                int nextX = current.x + offset[0];
                int nextY = current.y + offset[1];
                int nextZ = current.z + offset[2];
                BlockPos neighbor = new BlockPos(nextX, nextY, nextZ);
                if (nextY < 0 || nextY >= world.getHeight()) continue;
                if (!visited.contains(neighbor)) {
                    int neighborId = world.getBlockId(nextX, nextY, nextZ);
                    int neighborMeta = world.getBlockMetadata(nextX, nextY, nextZ);
                    if (neighborId == referenceBlockId && neighborMeta == referenceBlockMeta) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
        return foundBlocks;
    }

    public static Map<String, Integer> sortMapInt(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<>(unsortedMap.entrySet());
        listOfEntries.sort((entry1, entry2) -> {
            int valueComparison = Integer.compare(entry1.getValue(), entry2.getValue());
            if (valueComparison == 0) return entry1.getKey().compareTo(entry2.getKey());
            return valueComparison;
        });
        Map<String, Integer> sortedMapByValue = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : listOfEntries) {
            Integer value = entry.getValue();
            sortedMapByValue.put(entry.getKey(), value);
        }
        return sortedMapByValue;
    }

    public static Map<String, Short> sortMapShort(Map<String, Short> unsortedMap) {
        List<Map.Entry<String, Short>> listOfEntries = new ArrayList<>(unsortedMap.entrySet());
        listOfEntries.sort((entry1, entry2) -> {
            int valueComparison = Short.compare(entry1.getValue(), entry2.getValue());
            if (valueComparison == 0) return entry1.getKey().compareTo(entry2.getKey());
            return valueComparison;
        });
        Map<String, Short> sortedMapByValue = new LinkedHashMap<>();
        for (Map.Entry<String, Short> entry : listOfEntries) {
            Short value = entry.getValue();
            sortedMapByValue.put(entry.getKey(), value);
        }
        return sortedMapByValue;
    }

    public static Map<String, Float> sortMapFloat(Map<String, Float> unsortedMap) {
        List<Map.Entry<String, Float>> listOfEntries = new ArrayList<>(unsortedMap.entrySet());
        listOfEntries.sort((entry1, entry2) -> {
            int valueComparison = Float.compare(entry1.getValue(), entry2.getValue());
            if (valueComparison == 0) return entry1.getKey().compareTo(entry2.getKey());
            return valueComparison;
        });
        Map<String, Float> sortedMapByValue = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : listOfEntries) {
            sortedMapByValue.put(entry.getKey(), entry.getValue());
        }
        return sortedMapByValue;
    }

    public static Map<String, String> sortMapStringFloat(Map<String, String> unsortedMap) {
        Map<String, Float> var2 = new HashMap<>();
        for (Map.Entry<String, String> s:unsortedMap.entrySet()){
            String var1 = s.getValue().replace("/",".");
            float f = Float.parseFloat(var1);
            var2.put(s.getKey(),f);
        }
        List<Map.Entry<String, Float>> listOfEntries = new ArrayList<>(var2.entrySet());
        listOfEntries.sort((entry1, entry2) -> {
            int valueComparison = Float.compare(entry1.getValue(), entry2.getValue());
            if (valueComparison == 0) return entry1.getKey().compareTo(entry2.getKey());
            return valueComparison;
        });
        Map<String, String> sortedMapByValue = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : listOfEntries) {
            sortedMapByValue.put(entry.getKey(), unsortedMap.get(entry.getKey()));
        }
        return sortedMapByValue;
    }

    public static <K> Map<K, String> sortMapByStringValues(Map<K, String> map) {
        return sortMapByStringValues(map, null);
    }
    public static <K> Map<K, String> sortMapByStringValues(Map<K, String> map, Comparator<String> valueComparator) {
        if (map == null || map.isEmpty()) {
            return new LinkedHashMap<>();
        }

        List<Map.Entry<K, String>> entryList = new ArrayList<>(map.entrySet());

        Comparator<Map.Entry<K, String>> entryComparator;
        if (valueComparator == null) {
            entryComparator = Map.Entry.comparingByValue();
        } else {
            entryComparator = Map.Entry.comparingByValue(valueComparator);
        }

        entryList.sort(entryComparator);

        Map<K, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}

