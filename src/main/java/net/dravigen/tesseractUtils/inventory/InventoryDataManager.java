package net.dravigen.tesseractUtils.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InventoryDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "saved_inventories.json";

    private static File getSaveFile() {
        if (ModDirectories.configDir == null) {
            ModDirectories.configDir = ModDirectories.getGlobalConfigDir();
        }
        return new File(ModDirectories.configDir, FILE_NAME);
    }

    // Load inventories from the global file
    public static SavedInventoriesList loadInventories() {
        File saveFile = getSaveFile();
        if (!saveFile.exists()) {
            System.out.println("Global saved inventory file not found. Creating new list.");
            return new SavedInventoriesList();
        }

        try (FileReader reader = new FileReader(saveFile)) {
            SavedInventoriesList loadedList = GSON.fromJson(reader, SavedInventoriesList.class);
            if (loadedList == null) {
                System.err.println("Loaded global JSON was empty or malformed, returning new list.");
                return new SavedInventoriesList();
            }
            System.out.println("Loaded " + loadedList.getInventories().size() + " global saved inventories.");
            return loadedList;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading global saved inventories from JSON: " + e.getMessage());
            e.printStackTrace();
            return new SavedInventoriesList();
        }
    }

    // Save inventories to the global file
    public static void saveInventories(SavedInventoriesList inventoriesList) {
        File saveFile = getSaveFile();
        try (FileWriter writer = new FileWriter(saveFile)) {
            GSON.toJson(inventoriesList, writer);
            System.out.println("Saved " + inventoriesList.getInventories().size() + " global inventories to " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving global inventories to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}