package net.dravigen.tesseractUtils.GUI;

public enum EnumConfig {

    PLACING_COOLDOWN("tesseract_utils.options.placing_cooldown", "placingCooldown",true,new int[]{0,0},false),
    BREAKING_COOLDOWN("tesseract_utils.options.breaking_cooldown", "breakingCooldown",true,new int[]{0,0},false),
    FLIGHT_MOMENTUM("tesseract_utils.options.flight_momentum", "flightMomentum",true,new int[]{0,0},false),
    CLICK_REPLACE("tesseract_utils.options.click_replace", "clickReplace",true,new int[]{0,0},false),
    NO_CLIP("tesseract_utils.options.no_clip", "noClip",true,new int[]{0,0},false),
    EXTRA_DEBUG("tesseract_utils.options.extra_debug_info", "extraDebugInfo",true,new int[]{0,0},false),
    REACH("tesseract_utils.options.reach", "reach", false, new int[]{0, 0}, true),
    FLIGHT_SPEED("tesseract_utils.options.flight_speed", "flightSpeed", false, new int[]{0, 0}, true);

    private final String name;
    private final String property;
    private final boolean isBool;
    private final int[] isFloat;
    private final boolean isInt;

    public int returnEnumOrdinal() {
        return this.ordinal();
    }

    EnumConfig(String name, String property, boolean isBool, int[] isFloat, boolean isInt) {
        this.name = name;
        this.property = property;
        this.isBool = isBool;
        this.isFloat = isFloat;
        this.isInt = isInt;
    }

    public String getEnumName() { return this.name; }

    public String getEnumProperty() { return this.property; }

    public boolean getEnumFloat() {
        return this.isFloat[0] != 0;
    }

    public int getEnumFloatNotches() {
        return this.isFloat[1];
    }

    public boolean getEnumBoolean() {
        return this.isBool;
    }

    public boolean getEnumInt() { return this.isInt; }
}