package net.dravigen.tesseractUtils.inventory;

import java.util.ArrayList;
import java.util.List;

public class SavedInventoriesList {
    private List<InventoryEntry> inventories;

    public SavedInventoriesList() {
        this.inventories = new ArrayList<>();
    }

    public List<InventoryEntry> getInventories() {
        return inventories;
    }

    public void setInventories(List<InventoryEntry> inventories) {
        this.inventories = inventories;
    }

    public void addInventory(InventoryEntry entry) {
        this.inventories.add(entry);
    }

    public InventoryEntry getInventoryByName(String name) {
        for (InventoryEntry entry : inventories) {
            if (entry.getName().equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }

    public void removeInventoryByName(String name) {
        inventories.removeIf(entry -> entry.getName().equalsIgnoreCase(name));
    }
}