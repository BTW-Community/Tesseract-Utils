package net.dravigen.tesseractUtils;

import btw.AddonHandler;
import btw.BTWAddon;
import net.dravigen.tesseractUtils.command.*;
import net.dravigen.tesseractUtils.inventory.InventoryDataManager;
import net.dravigen.tesseractUtils.inventory.ModDirectories;
import net.dravigen.tesseractUtils.inventory.SavedInventoriesList;
import net.dravigen.tesseractUtils.item.DeleteEntityItem;
import net.minecraft.src.*;

import static net.dravigen.tesseractUtils.TessUConfig.*;

public class TesseractUtilsAddon extends BTWAddon {

    public TesseractUtilsAddon() {
        super();
    }

    public static TesseractUtilsAddon instance;
    public static SavedInventoriesList globalSavedInventories;
    public static int modeState;
    public static Language listLanguage;
    public static Item deleteEntityItem;

    public static TesseractUtilsAddon getInstance() {
        return instance == null ? (new TesseractUtilsAddon()) : instance;
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        loadConfig();
        registerNewCommands();
        deleteEntityItem=new DeleteEntityItem(1800-256).setCreativeTab(CreativeTabs.tabTools);
    }

    private void registerNewCommands(){
        registerAddonCommand(new CommandInv());
        registerAddonCommand(new CommandWorldEdit());
        registerAddonCommand(new CommandSummon());
        registerAddonCommand(new CommandNewKill());
        registerAddonCommand(new CommandNewGive());
    }

    @Override
    public void preInitialize() {
        super.preInitialize();
        ModDirectories.init();
        globalSavedInventories = InventoryDataManager.loadInventories();
    }
}