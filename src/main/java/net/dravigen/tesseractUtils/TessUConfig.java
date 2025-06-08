package net.dravigen.tesseractUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TessUConfig {

    private static final File configFile = new File("config/tesseract_utils.properties");
    private static final Properties props = new Properties();

    public static float reach = 5;
    public static float flySpeed = 2;
    public static boolean disablePlaceCooldown = false;
    public static boolean disableBreakCooldown = false;
    public static boolean disableMomentum = false;
    public static boolean enableClickReplace = false;
    public static boolean enableNoClip = false;
    public static boolean enableExtraDebugInfo = false;

    public static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        } catch (IOException e) {
            setDefaultConfig();
            saveConfig();
            return;
        }
        reach = Float.parseFloat(props.getProperty("CreativeReach"));
        flySpeed = Float.parseFloat(props.getProperty("FlySpeed"));
        disablePlaceCooldown = Boolean.parseBoolean(props.getProperty("DisablePlacingCooldown"));
        disableBreakCooldown = Boolean.parseBoolean(props.getProperty("DisableBreakCooldown"));
        disableMomentum = Boolean.parseBoolean(props.getProperty("DisableMomentum"));
        enableClickReplace = Boolean.parseBoolean(props.getProperty("EnableClickReplace"));
        enableNoClip = Boolean.parseBoolean(props.getProperty("EnableNoClip"));
        enableExtraDebugInfo = Boolean.parseBoolean(props.getProperty("EnableExtraDebugInfo"));
    }

    public static void saveConfig() {
        props.setProperty("CreativeReach", String.valueOf(reach));
        props.setProperty("FlySpeed", String.valueOf(flySpeed));
        props.setProperty("DisablePlacingCooldown", String.valueOf(disablePlaceCooldown));
        props.setProperty("DisableBreakCooldown", String.valueOf(disableBreakCooldown));
        props.setProperty("DisableMomentum", String.valueOf(disableMomentum));
        props.setProperty("EnableClickReplace", String.valueOf(enableClickReplace));
        props.setProperty("EnableNoClip", String.valueOf(enableNoClip));
        props.setProperty("EnableExtraDebugInfo", String.valueOf(enableExtraDebugInfo));

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "TesseractUtils Configuration");
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
    }

    public static void setDefaults() {
        setDefaultConfig();
    }
}
