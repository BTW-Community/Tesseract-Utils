package net.dravigen.tesseractUtils;

import net.minecraft.src.KeyBinding;
import net.minecraft.src.StatCollector;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TessUConfig {

    private static final File configFile = new File("config/tesseract_utils.properties");
    public static final Properties properties = new Properties();

    public static float reach = 5;
    public static float flySpeed = 2;
    public static boolean disablePlaceCooldown = false;
    public static boolean disableBreakCooldown = false;
    public static boolean disableMomentum = false;
    public static boolean enableClickReplace = false;
    public static boolean enableNoClip = false;
    public static boolean enableExtraDebugInfo = false;
    public static KeyBinding configMenu = new KeyBinding(StatCollector.translateToLocal("ConfigMenuKey"), Keyboard.KEY_F6);
    public static KeyBinding hotbarSwap = new KeyBinding(StatCollector.translateToLocal("HotbarSwapKey"), Keyboard.KEY_H);
    public static KeyBinding[] modBinds;

    public static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (IOException e) {
            setDefaultConfig();
            saveConfig();
            return;
        }
        try {
            reach = Float.parseFloat(properties.getProperty("CreativeReach"));
            flySpeed = Float.parseFloat(properties.getProperty("FlySpeed"));
            disablePlaceCooldown = Boolean.parseBoolean(properties.getProperty("DisablePlacingCooldown"));
            disableBreakCooldown = Boolean.parseBoolean(properties.getProperty("DisableBreakCooldown"));
            disableMomentum = Boolean.parseBoolean(properties.getProperty("DisableMomentum"));
            enableClickReplace = Boolean.parseBoolean(properties.getProperty("EnableClickReplace"));
            enableNoClip = Boolean.parseBoolean(properties.getProperty("EnableNoClip"));
            enableExtraDebugInfo = Boolean.parseBoolean(properties.getProperty("EnableExtraDebugInfo"));
            configMenu.keyCode = Integer.parseInt(properties.getProperty("ConfigMenuKey"));
            hotbarSwap.keyCode = Integer.parseInt(properties.getProperty("HotbarSwapKey"));
            modBinds = new KeyBinding[]{configMenu, hotbarSwap};
        } catch (Throwable e) {
            if (configFile.delete()) {
                try {
                    if (configFile.createNewFile()) {
                        saveConfig();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static void saveConfig() {
        properties.setProperty("CreativeReach", String.valueOf(reach));
        properties.setProperty("FlySpeed", String.valueOf(flySpeed));
        properties.setProperty("DisablePlacingCooldown", String.valueOf(disablePlaceCooldown));
        properties.setProperty("DisableBreakCooldown", String.valueOf(disableBreakCooldown));
        properties.setProperty("DisableMomentum", String.valueOf(disableMomentum));
        properties.setProperty("EnableClickReplace", String.valueOf(enableClickReplace));
        properties.setProperty("EnableNoClip", String.valueOf(enableNoClip));
        properties.setProperty("EnableExtraDebugInfo", String.valueOf(enableExtraDebugInfo));
        properties.setProperty("ConfigMenuKey", String.valueOf(configMenu.keyCode));
        properties.setProperty("HotbarSwapKey", String.valueOf(hotbarSwap.keyCode));
        modBinds = new KeyBinding[]{configMenu, hotbarSwap};

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "TesseractUtils Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setDefaultConfig() {
        reach = 5.0F;
        flySpeed = 2;
        disablePlaceCooldown = false;
        disableBreakCooldown = false;
        disableMomentum = false;
        enableClickReplace = false;
        enableNoClip = false;
        enableExtraDebugInfo = false;
        configMenu.keyCode = Keyboard.KEY_F6;
        hotbarSwap.keyCode = Keyboard.KEY_H;
    }

    public static void setDefaults() {
        setDefaultConfig();
    }
}
