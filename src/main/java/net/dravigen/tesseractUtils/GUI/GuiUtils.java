package net.dravigen.tesseractUtils.GUI;

import static net.dravigen.tesseractUtils.TessUConfig.*;
import static net.dravigen.tesseractUtils.TessUConfig.flySpeed;

public class GuiUtils {


    public static GuiUtils instance;

    public static GuiUtils getInstance() {
        return instance == null ? (new GuiUtils()) : instance;
    }


    // Handles slider value
    public void setSliderConfig(String property, float value) {
        switch (property) {
            case "reach":
                reach = (int)(value*128);
                break;
            case "flightSpeed":
                flySpeed = (int)(value*32)+1;
                break;
        }
        saveConfig();

    }

    // Gets slider display
    public String getSliderDisplay(String property) {
        return switch (property) {
            case "reach" -> "Reach: " + (int) reach;
            case "flightSpeed" -> flySpeed < 32 ? "Flight speed: " + (int) (flySpeed) : "Flight speed: too fast";
            default -> "";
        };
    }
}
