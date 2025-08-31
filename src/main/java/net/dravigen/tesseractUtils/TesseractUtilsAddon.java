package net.dravigen.tesseractUtils;

import btw.AddonHandler;
import btw.BTWAddon;
import net.dravigen.tesseractUtils.command.*;
import net.dravigen.tesseractUtils.inventory.InventoryDataManager;
import net.dravigen.tesseractUtils.utils.ModDirectories;
import net.dravigen.tesseractUtils.inventory.SavedInventoriesList;
import net.minecraft.src.*;

import static net.dravigen.tesseractUtils.configs.TessUConfig.*;

public class TesseractUtilsAddon extends BTWAddon {

    public TesseractUtilsAddon() {
        super();
    }

    public static TesseractUtilsAddon instance;
    public static SavedInventoriesList savedInventories;
    public static SavedInventoriesList inventoryPresets;

    public static int modeState;
    public static Language listLanguage;
    public static long mspt;
    public static float tps;
    public static int currentBuildingMode =8;
    public static float partialTick = 1;
    public static boolean checkedOP = false;


    public static TesseractUtilsAddon getInstance() {
        return instance == null ? (new TesseractUtilsAddon()) : instance;
    }
    public static class TUChannels {
        public static final String CLIENT_TO_SERVER_CHANNEL = "T-U:C2S";
        public static final String SERVER_TO_CLIENT_CHANNEL = "T-U:S2C";
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        loadConfig();
        registerNewCommands();

    }


    private void registerNewCommands(){
        registerAddonCommand(new CommandInv());
        registerAddonCommand(new CommandWorldEdit());
        registerAddonCommand(new CommandSummon());
        registerAddonCommand(new CommandNewKill());
        registerAddonCommand(new CommandNewGive());
        registerAddonCommand(new CommandNewEffect());
        registerAddonCommand(new CommandPlaysoundNew());
        registerAddonCommand(new CommandAlias());
        registerAddonCommand(new CommandNewEnchant());
    }

    @Override
    public void preInitialize() {
        super.preInitialize();
        ModDirectories.init();
        savedInventories = InventoryDataManager.loadInventories(false);
        inventoryPresets = InventoryDataManager.loadInventories(true);

    }
}