package net.dravigen.tesseractUtils.inventory;

import net.minecraft.src.Minecraft;

import java.io.File;

public class ModDirectories {
    public static File configDir;

    public static void init() {
        configDir=getGlobalConfigDir();
        System.out.println("Mod config directory: " + configDir.getAbsolutePath());
    }

    public static File getGlobalConfigDir() {
        File mcDir = Minecraft.getMinecraft().mcDataDir;
        File configBase = new File(mcDir, "config");
        File modConfigDir = new File(configBase, "tesseract_utils");
        if (!modConfigDir.exists()) {
            modConfigDir.mkdirs();
        }
        return modConfigDir;
    }
}