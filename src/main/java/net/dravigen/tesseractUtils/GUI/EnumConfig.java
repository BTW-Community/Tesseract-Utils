package net.dravigen.tesseractUtils.GUI;

public enum EnumConfig {

    PLACING_COOLDOWN("Placing Cooldown", "placingCooldown", true, new int[]{0, 0}, false, false,300),
    BREAKING_COOLDOWN("Breaking Cooldown", "breakingCooldown", true, new int[]{0, 0}, false, false,301),
    FLIGHT_MOMENTUM("Flight Momentum", "flightMomentum", true, new int[]{0, 0}, false, false,302),
    CLICK_REPLACE("Replacing Click", "clickReplace", true, new int[]{0, 0}, false, false,303),
    NO_CLIP("No Clip", "noClip", true, new int[]{0, 0}, false, false,304),
    EXTRA_DEBUG("Extra Debug Info", "extraDebugInfo", true, new int[]{0, 0}, false, false,305),
    VANILLA_NIGHTVIS("Vanilla Night Vision", "vanillaNightvis", true, new int[]{0, 0}, false, false,306),
    FUZZY_EXTRUDER("Fuzzy extruder","fuzzyExtruder", true, new int[]{0, 0}, false, false,307),

    REACH("", "reach", false, new int[]{0, 0}, true, false,350),
    FLIGHT_SPEED("", "flightSpeed", false, new int[]{0, 0}, true, false,351),
    EXTRUDE_LIMIT("","extrudeLimit",false,new int[]{0, 0}, true, false,352),

    CONFIG_MENU_KEY("Config Menu","configMenuKey",false,new int[]{0,0},false,true,375),
    BAR_SWAP_KEY("Hotbar Swap","barSwapKey",false,new int[]{0,0},false,true,376);


    private final String name;
    private final String property;
    private final boolean isBool;
    private final int[] isFloat;
    private final boolean isInt;
    private final int index;
    private final boolean isKeybind;

    public int returnEnumOrdinal() {
        return this.index;
    }
    public String getEnumName() {
        return this.name;
    }

    public String getEnumProperty() {
        return this.property;
    }
    EnumConfig(String name, String property, boolean isBool, int[] isFloat, boolean isInt, boolean isKeybind, int index) {
        this.name = name;
        this.property = property;
        this.isBool = isBool;
        this.isFloat = isFloat;
        this.isInt = isInt;
        this.index = index;
        this.isKeybind = isKeybind;
    }


    public boolean getKeyBind(){
        return this.isKeybind;
    }

    public boolean getEnumFloat() {
        return this.isFloat[0] != 0;
    }

    public int getEnumFloatNotches() {
        return this.isFloat[1];
    }

    public boolean getEnumBoolean() {
        return this.isBool;
    }

    public boolean getEnumInt() {
        return this.isInt;
    }
}