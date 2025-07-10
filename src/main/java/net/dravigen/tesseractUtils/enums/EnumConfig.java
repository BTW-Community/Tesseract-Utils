package net.dravigen.tesseractUtils.enums;

import org.lwjgl.input.Keyboard;


public enum EnumConfig {

    PLACING_COOLDOWN("Instant Place", "placeCooldown", 300,0, false,0,false),
    BREAKING_COOLDOWN("Instant Break", "breakCooldown",301,0, false,0, false),
    FLIGHT_MOMENTUM("No Momentum", "momentum",302,0, false,0, false),
    //NO_CLIP("No Clip", "noClip",303, 0,false,0, false),
    EXTRA_DEBUG("Extra Debug Info", "extraDebug",303, 0,false,0, false),
    VANILLA_NIGHTVIS("Vanilla Night Vision", "nightVis",304, 0,false,0, false),
    FUZZY_EXTRUDER("Fuzzy extruder","fuzzy",305, 0,false,0, false),
    NO_UPDATE("No Block Update","blockUpdate",306, 0,true,0, false),
    IGNORE_AIR("Ignore Air","ignoreAir",307, 0,false,0, false),
    KEY_HELP("Build Mode Keybind","keyHelp",308, 0,true,0, false),
    SHAPE_DISPLAY("Visualize Shape","shapeDisplay",309, 0,true,0, false),
    SELECTION_DISPLAY("Visualize Selection","selectionDisplay",310, 0,true,0, false),


    REACH("Reach", "reach",350,1, 4,128, false),
    FLIGHT_SPEED("Flight Speed", "flightSpeed",351,1, 1,32, false),
    EXTRUDE_LIMIT("Extrude Limit","extrudeLimit",352,1, 127,2048, false),

    CONFIG_MENU_KEY("Config Menu","configMenuKey",375,2, Keyboard.KEY_F6,0, true),
    SHAPE_MENU_KEY("Shape Menu","shapeMenuKey",376,2,Keyboard.KEY_F7,0,false),
    //BAR_SWAP_KEY("Hotbar Swap","barSwapKey",377,2, Keyboard.KEY_H,0, true),
    UNDO_KEY("Undo","undo",378,2, Keyboard.KEY_SUBTRACT,0, false),
    REDO_KEY("Redo","redo",379,2, Keyboard.KEY_ADD,0, false),
    START_MUSIC_KEY("Start Music","startMusicKey",380,2, Keyboard.KEY_M,0, true),
    STOP_MUSIC_KEY("Stop Music","stopMusicKey",381,2, Keyboard.KEY_L,0, true);

    private final String name;
    private final String property;
    private final int index;
    private final int type;
    private final Object baseValue;
    private final int maxValue;
    private Object enumValue;
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
    public boolean  getBSFriendly(){return this.survivalFriendly;}
    public void setValue(Object value){
        this.enumValue =value;
    }
    public Object getBaseValue(){return this.baseValue;}
    public int getMaxValue(){return this.maxValue;}
    public int getIntValue(){return this.isKeybind() ? (int) this.enumValue : Math.min((int) this.enumValue+1,this.maxValue);}
    public boolean getBoolValue(){return (boolean) this.enumValue;}

    EnumConfig(String name, String property, int index, int valueType, int value, int max, boolean bFriendly) {
        this.name = name;
        this.property = property;
        this.index = index;
        this.enumValue = value;
        this.type = valueType;
        this.baseValue = value;
        this.maxValue = max;
        this.survivalFriendly = bFriendly;
    }
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

    public boolean isBool() {
        return this.type==0;
    }

    public boolean isInt() {
        return this.type==1;
    }

    public boolean isKeybind(){
        return this.type==2;
    }

    public static EnumConfig getConfigFromIndex(int index){
        for (EnumConfig enumMode : EnumConfig.values()) {
            if (enumMode.index==index){
                return enumMode;
            }
        }
        return null;
    }
}