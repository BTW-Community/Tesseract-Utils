package net.dravigen.tesseractUtils.GUI;

import net.dravigen.tesseractUtils.configs.EnumConfig;

import static net.dravigen.tesseractUtils.configs.TessUConfig.*;

public class GuiUtils {


    public static GuiUtils instance;

    public static GuiUtils getInstance() {
        return instance == null ? (new GuiUtils()) : instance;
    }


    // Handles slider value
    public void setSliderConfig(String property, float value) {
        for (EnumConfig config:enumConfigs){
            if (config.getProperty().equalsIgnoreCase(property)){
                config.setValue((int)(value*(config.getMaxValue()-1)+1));
                break;
            }
        }
        saveConfig();
    }

    // Gets slider display
    public String getSliderDisplay(String property) {
        for (EnumConfig config:enumConfigs){
            if (config.getProperty().equalsIgnoreCase(property)){
                return config.getName()+": "+config.getValue();
            }
        }
        return "";
    }
}
