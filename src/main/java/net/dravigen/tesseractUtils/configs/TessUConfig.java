package net.dravigen.tesseractUtils.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TessUConfig {

    private static final File configFile = new File("config/tesseract_utils.properties");
    public static final Properties properties = new Properties();
    public static final EnumConfig[] enumConfigs;

    static {
        enumConfigs = new EnumConfig[]{EnumConfig.PLACING_COOLDOWN, EnumConfig.BREAKING_COOLDOWN, EnumConfig.FLIGHT_MOMENTUM, EnumConfig.CLICK_REPLACE, EnumConfig.NO_CLIP, EnumConfig.EXTRA_DEBUG, EnumConfig.VANILLA_NIGHTVIS, EnumConfig.FUZZY_EXTRUDER,
                EnumConfig.REACH, EnumConfig.FLIGHT_SPEED, EnumConfig.EXTRUDE_LIMIT,
                EnumConfig.CONFIG_MENU_KEY, EnumConfig.BAR_SWAP_KEY, EnumConfig.START_MUSIC_KEY,EnumConfig.STOP_MUSIC_KEY};
    }

    public static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (IOException e) {
            setDefaultConfig();
            saveConfig();
            return;
        }
        try {
            for (EnumConfig config: enumConfigs){
                if (config.isBool()) {
                    config.setValue(Boolean.parseBoolean(properties.getProperty(config.getProperty())));
                }else config.setValue(Integer.parseInt(properties.getProperty(config.getProperty())));
            }
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

        for (EnumConfig config: enumConfigs){
            properties.setProperty(config.getProperty(), String.valueOf(config.getValue()));
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "TesseractUtils Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setDefaultConfig() {
        for (EnumConfig config: enumConfigs){
            config.setValue(config.getBaseValue());
        }
    }
}
