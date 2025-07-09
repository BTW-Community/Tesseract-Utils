package net.dravigen.tesseractUtils.utils;

import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.enums.EnumShape;

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
                config.setValue((int)(value*config.getMaxValue()));
                break;
            }
        }
        for (EnumShape customShape: EnumShape.values()){
            if (customShape.getProperty().equalsIgnoreCase(property)){
                customShape.setValue((int)(value*customShape.getMaxValue()));
                break;
            }
        }
        saveConfig();
    }

    // Gets slider display
    public String getSliderDisplay(String property) {
        for (EnumConfig config:enumConfigs){
            if (config.getProperty().equalsIgnoreCase(property)){
                return config.getName()+": "+config.getIntValue();
            }
        }
        for (EnumShape customShape: EnumShape.values()){
            if (customShape.getProperty().equalsIgnoreCase(property)){
                return customShape.getName()+": "+customShape.getIntValue();
            }
        }return "";
    }
}
