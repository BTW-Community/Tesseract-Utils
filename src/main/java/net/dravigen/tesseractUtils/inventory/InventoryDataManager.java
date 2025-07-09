package net.dravigen.tesseractUtils.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.dravigen.tesseractUtils.utils.ModDirectories;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InventoryDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String SAVED_INVENTORIES_FILE_NAME = "saved_inventories.json";
    private static final String INVENTORY_PRESETS_FILE_NAME = "inventory_presets.json";


    private static File getSaveFile(boolean isPreset) {
        if (ModDirectories.configDir == null) {
            ModDirectories.configDir = ModDirectories.getInventoryConfigDir();
        }

        return new File(ModDirectories.configDir, isPreset ? INVENTORY_PRESETS_FILE_NAME : SAVED_INVENTORIES_FILE_NAME);
    }

    public static SavedInventoriesList loadInventories(boolean isPreset) {
        File saveFile = getSaveFile(isPreset);
        if (!saveFile.exists()) {
            if (isPreset) {
                System.out.println("Inventory presets file not found. Loading default presets.");
                try (FileWriter writer = new FileWriter(saveFile)) {
                    writer.write(InventoryPresetsDefault.getPresets());
                    System.out.println("Successfully wrote default presets to: " + saveFile.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Failed to write default presets to file: " + e.getMessage());
                    e.printStackTrace();
                }
            }else {
                System.out.println("Saved inventory file not found. Creating new list.");
                return new SavedInventoriesList();
            }
        }

        try (FileReader reader = new FileReader(saveFile)) {
            SavedInventoriesList loadedList = GSON.fromJson(reader, SavedInventoriesList.class);
            if (loadedList == null) {
                System.err.println("Loaded JSON was empty or malformed, returning new list.");
                return new SavedInventoriesList();
            }
            System.out.println("Loaded " + loadedList.getInventories().size() + (isPreset ? " inventory presets." : " saved inventories."));
            return loadedList;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading saved inventories from JSON: " + e.getMessage());
            e.printStackTrace();
            return new SavedInventoriesList();
        }
    }

    public static void saveInventories(SavedInventoriesList inventoriesList, boolean isPreset) {
        File saveFile = getSaveFile(isPreset);
        try (FileWriter writer = new FileWriter(saveFile)) {
            GSON.toJson(inventoriesList, writer);
            System.out.println("Saved " + inventoriesList.getInventories().size() + " inventories to " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving inventories to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}