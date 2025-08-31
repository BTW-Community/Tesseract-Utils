package net.dravigen.tesseractUtils.utils;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.configs.TessUConfig;

import java.util.*;

public class PacketUtils {

    public static boolean isLookedAtEntityPermanentClient = false;

    public static boolean serverHaveTU = false;

    public static Map<String,Boolean> clientHaveTU = new HashMap<>();

    public static boolean isPlayerOPClient = false;

    public static String[] mobCapInfosClient = null;

    public static String playerInfoClient(int modeState){
        StringBuilder configsString = new StringBuilder();
        for (EnumConfig config : TessUConfig.enumConfigs) {
            configsString.append(config.getValue()).append(",");
        }
        modeState= modeState==-1 ? TesseractUtilsAddon.modeState : modeState;
        return configsString.toString() + TesseractUtilsAddon.currentBuildingMode + "," + modeState;
    }

    public static Map<String,List<List<ListsUtils.SavedBlock>>> playersUndoListServer = new HashMap<>();

    public static Map<String,List<List<ListsUtils.SavedBlock>>> playersRedoListServer = new HashMap<>();

    public static Map<String,List<ListsUtils.SavedBlock>> playersCopyServer = new HashMap<>();

    public static Map<String,List<String>> playersInfoServer = new HashMap<>();

    public static Map<String,Integer> playersBuildModeServer = new HashMap<>();

    public static Map<String,Integer> playersGamemodeServer = new HashMap<>();


    /*
    public static Map<String,Map<String,String>> playersBlocksMapServer = new HashMap<>();

    public static Map<String,Map<String,String>> playersItemsNameMapServer = new HashMap<>();

    public static Map<String,Map<String,Integer>> playersEntitiesNameMapServer = new TreeMap<>();

    public static Map<String,Map<String,Short>> playersPotionsNameListServer = new HashMap<>();

    public static Map<String,Map<String,Short>> playersEnchantNameListServer = new HashMap<>();
*/

}
