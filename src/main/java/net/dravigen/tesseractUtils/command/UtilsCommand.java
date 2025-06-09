package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

import static net.dravigen.tesseractUtils.TessUConfig.reach;
import static net.minecraft.src.CommandBase.getPlayer;

public class UtilsCommand {

    public static List<String> entityShowNameList = new ArrayList<>();
    public static List<String> entityTrueNameList = new ArrayList<>();

    private static Map<Class<?>, String> CLASS_TO_STRING_MAPPING;

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

    public MovingObjectPosition getBlockPlayerIsLooking(ICommandSender par1ICommandSender) {
        EntityPlayer player = getPlayer(par1ICommandSender, par1ICommandSender.getCommandSenderName());
        Vec3 var3 = player.getPosition(1);
        Vec3 var4 = var3.addVector(0, player.getEyeHeight(), 0);
        Vec3 var5 = player.getLook(1);
        Vec3 var6 = var4.addVector(var5.xCoord * reach, var5.yCoord * reach, var5.zCoord * reach);
        return player.worldObj.clip(var4, var6);
    }

    public static String[] getRandomBlockFromOdds(List<Integer> blockOdd, List<String[]> idMetaList) {
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

    public static void initBlocksNameList() {
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

    public static @NotNull BlockInfo getBlockInfo(String[] strings, int idPos) {
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
        return new BlockInfo(blockName, id, meta);
    }

    public static void initItemsNameList() {
        for (int i = 0; i < Item.itemsList.length; i++) {
            Item item = Item.itemsList[i];
            if (item == null) {
                continue;
            }
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
                    stack.getDisplayName();
                    String entry;
                    String previousString="";
                    if (!itemNameList.isEmpty()) {
                        previousString = itemNameList.get(itemNameList.size()-1);
                    }
                    if (previousString.equalsIgnoreCase(stack.getDisplayName().replace(" ", "_"))) {
                        if (stack.getItemDamage() == 0) {
                            entry = stack.getDisplayName().replace(" ", "_") + "|" + stack.itemID;
                        } else entry = stack.getDisplayName().replace(" ", "_") + "/" + stack.getItemDamage();
                        itemNameList.set(itemNameList.size()-1,Item.itemsList[i-1].getItemDisplayName(new ItemStack(Item.itemsList[i-1])).replace(" ", "_") + "|" + new ItemStack(Item.itemsList[i-1]).itemID);
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
                if (item == null) {
                    continue;
                }
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
                            itemName= string;
                        }
                    }
                }
            }
        }else {
            itemName= new ItemStack(id,1,meta).getDisplayName();
        }
        return new ItemInfo(id,meta,itemName);
    }

    public static void initEntityList(){
        try {

            String obfuscatedFieldName = "classToStringMapping";
            Field mapField = EntityList.class.getDeclaredField(obfuscatedFieldName);
            mapField.setAccessible(true);
            CLASS_TO_STRING_MAPPING = (Map<Class<?>, String>) mapField.get(null);

        }catch (Exception ignored){
        }
        for (Map.Entry<Class<?>, String> entry : CLASS_TO_STRING_MAPPING.entrySet()) {
            Class<?> entityClass = entry.getKey();
            String base = CLASS_TO_STRING_MAPPING.get(entityClass);
            String unlocalizedName = "entity." + base + ".name";
            String translated = StringTranslate.getInstance().translateKey(unlocalizedName);
            if (translated.equalsIgnoreCase(unlocalizedName)){
                translated=base;
            }
            String finalName = translated.replace("Entity","").replace("addon","").replace("fc","").replace(StringTranslate.getInstance().translateKey("entity.villager.name"),"").replace("DireWolf","The_Beast").replace("JungleSpider","Jungle_Spider").replace(" ","_");
            entityTrueNameList.add(base);
            entityShowNameList.add(finalName);
        }
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

    public record BlockInfo(String blockName, int id, int meta) { }

    public record ItemInfo (int id, int meta, String itemName){ }

}
