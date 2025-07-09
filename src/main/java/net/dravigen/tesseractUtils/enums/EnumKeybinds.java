package net.dravigen.tesseractUtils.enums;

import org.lwjgl.input.Keyboard;

public enum EnumKeybinds {

    CONFIG_MENU_KEY("Config Menu","configMenuKey",EnumConfig.CONFIG_MENU_KEY.getIndex(), Keyboard.KEY_F6, true),
    SHAPE_MENU_KEY("Shape Menu","shapeMenuKey",EnumConfig.SHAPE_MENU_KEY.getIndex(), Keyboard.KEY_F7,false),
    BAR_SWAP_KEY("Hotbar Swap","barSwapKey",EnumConfig.BAR_SWAP_KEY.getIndex(), Keyboard.KEY_H, true),
    UNDO_KEY("Undo","undo",EnumConfig.UNDO_KEY.getIndex(), Keyboard.KEY_SUBTRACT, false),
    REDO_KEY("Redo","redo",EnumConfig.REDO_KEY.getIndex(), Keyboard.KEY_ADD, false),
    START_MUSIC_KEY("Start Music","startMusicKey",EnumConfig.START_MUSIC_KEY.getIndex(), Keyboard.KEY_M,true),
    STOP_MUSIC_KEY("Stop Music","stopMusicKey",EnumConfig.STOP_MUSIC_KEY.getIndex(), Keyboard.KEY_L, true);

    private final String name;
    private final String property;
    private final int index;
    private final Object baseValue;
    private Object enumValue;
    private final boolean survivalFriendly;

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
    public int getIntValue(){return EnumConfig.getConfigFromIndex(this.index).getIntValue();}
    public boolean getBoolValue(){return (boolean) this.enumValue;}
    public int getIndex() {
        return this.index;
    }


    EnumKeybinds(String name, String property, int index, int value, boolean bFriendly) {
        this.name = name;
        this.property = property;
        this.index = index;
        this.enumValue = value;
        this.baseValue = value;
        this.survivalFriendly = bFriendly;
    }
}
