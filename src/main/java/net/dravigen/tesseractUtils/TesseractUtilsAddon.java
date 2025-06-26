package net.dravigen.tesseractUtils;

import btw.AddonHandler;
import btw.BTWAddon;
import net.dravigen.tesseractUtils.command.*;
import net.dravigen.tesseractUtils.inventory.InventoryDataManager;
import net.dravigen.tesseractUtils.inventory.ModDirectories;
import net.dravigen.tesseractUtils.inventory.SavedInventoriesList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import static net.dravigen.tesseractUtils.configs.TessUConfig.*;

public class TesseractUtilsAddon extends BTWAddon {

    public TesseractUtilsAddon() {
        super();
    }

    public static TesseractUtilsAddon instance;
    public static SavedInventoriesList globalSavedInventories;
    public static int modeState;
    public static Language listLanguage;
    public static long mspt;
    public static float tps;

    @Environment(EnvType.CLIENT)
    public static boolean isLookedAtEntityPermanentClientSide = false;

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
    }

    @Override
    public void preInitialize() {
        super.preInitialize();
        ModDirectories.init();
        globalSavedInventories = InventoryDataManager.loadInventories();
    }
}