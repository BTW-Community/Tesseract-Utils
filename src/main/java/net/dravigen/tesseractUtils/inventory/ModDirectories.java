package net.dravigen.tesseractUtils.inventory;

import java.io.File;

public class ModDirectories {
    public static File configDir;

    public static void init() {
        configDir= getInventoryConfigDir();
    }

    public static File getInventoryConfigDir() {
        File modConfigDir = new File("tesseract_utils", "inventories");
        if (!modConfigDir.exists()) {
            modConfigDir.mkdirs();
        }
        return modConfigDir;
    }
}