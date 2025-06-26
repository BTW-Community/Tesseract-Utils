package net.dravigen.tesseractUtils.configs;

import org.lwjgl.input.Keyboard;

public enum EnumConfig {

    PLACING_COOLDOWN("Instant Place", "placeCooldown", 300,0, false,0,false),
    BREAKING_COOLDOWN("Instant Break", "breakCooldown",301,0, false,0, false),
    FLIGHT_MOMENTUM("No Momentum", "momentum",302,0, false,0, false),
    CLICK_REPLACE("Replacing Click", "replace",303, 0,false,0, false),
    NO_CLIP("No Clip", "noClip",304, 0,false,0, false),
    EXTRA_DEBUG("Extra Debug Info", "extraDebug",305, 0,false,0, false),
    VANILLA_NIGHTVIS("Vanilla Night Vision", "nightVis",306, 0,false,0, false),
    FUZZY_EXTRUDER("Fuzzy extruder","fuzzy",307, 0,false,0, false),

    REACH("Reach", "reach",350,1, 5,128, false),
    FLIGHT_SPEED("Flight Speed", "flightSpeed",351,1, 2,32, false),
    EXTRUDE_LIMIT("Extrude Limit","extrudeLimit",352,1, 128,2048, false),

    CONFIG_MENU_KEY("Config Menu","configMenuKey",375,2, Keyboard.KEY_F6,0, true),
    BAR_SWAP_KEY("Hotbar Swap","barSwapKey",376,2, Keyboard.KEY_H,0, true),
    START_MUSIC_KEY("Start Music","startMusicKey",377,2, Keyboard.KEY_M,0, true),
    STOP_MUSIC_KEY("Stop Music","stopMusicKey",378,2, Keyboard.KEY_L,0, true);

    private final String name;
    private final String property;
    private final int index;
    private final int type;
    private Object enumValue;
    private final Object baseValue;
    private final int maxValue;
    private int intValue;
    private final boolean survivalFriendly;

    public int getIndex() {
        return this.index;
    }
    public String getName() {
        return this.name;
    }
    public String getProperty() {
        return this.property;
    }
    public Object getValue(){return this.enumValue;}
    public boolean getBSFriendly(){return this.survivalFriendly;}
    public void setValue(Object value){
        if (this.isInt()){
            this.intValue= (int) value;
        }
        this.enumValue =value;
    }
    public Object getBaseValue(){return this.baseValue;}
    public int getMaxValue(){return this.maxValue;}
    public int getIntValue(){return this.intValue;}

    EnumConfig(String name, String property, int index, int valueType, boolean value, int max, boolean bFriendly) {
        this.name = name;
        this.property = property;
        this.index = index;
        this.enumValue = value;
        this.type = valueType;
        this.baseValue = value;
        this.maxValue = max;
        this.survivalFriendly = bFriendly;
    }
    EnumConfig(String name, String property, int index, int valueType, int value, int max, boolean bFriendly) {
        this.name = name;
        this.property = property;
        this.index = index;
        this.enumValue = value;
        this.intValue = value;
        this.type = valueType;
        this.baseValue = value;
        this.maxValue = max;
        this.survivalFriendly = bFriendly;

    }
    public boolean isBool() {
        return this.type==0;
    }

    public boolean isInt() {
        return this.type==1;
    }

    public boolean isKeybind(){
        return this.type==2;
    }

}