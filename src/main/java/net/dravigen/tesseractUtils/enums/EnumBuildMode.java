package net.dravigen.tesseractUtils.enums;

import net.minecraft.src.GameSettings;
import net.minecraft.src.Minecraft;

public enum EnumBuildMode {

    NORMAL_MODE("Normal Mode", 0, "§cYour average creative experience",false, new String[]{}),
    SELECTION_MODE("Selection Mode", 1, "§cSelect an area and modify it",true, new String[]{"§aLeft-Click§f = 1st pos","§aRight-Click§f = 2nd pos","§aMiddle-Click§f = selection to cursor", "§aScroll-Wheel§f = extrude face",  "§a"+Constants.sneak+"§f+§aScroll-Wheel§f = move selection"}),
    COPY_MODE("Copy Mode", 2, "§cCopy/cut and paste a selection",true, new String[]{"§aLeft-Click§f = copy","§a"+Constants.sneak+"§f+§aLeft-Click§f = cut","§aRight-Click§f = paste","§aMiddle-Click§f = selection to cursor", "§a"+Constants.shift+ "§f+§aLeft-Click§f = 1st pos", "§a"+Constants.shift+ "§f+§aRight-Click§f = 2nd pos","§aScroll-Wheel§f = move selection"}),
    BLOCK_SET_MODE("Block Set Mode", 3,"§cReplace a selection with blocks",true, new String[]{"§aLeft-Click§f = replace everything","§aRight-Click§f = replace only blocks","§aMiddle-Click§f = selection to cursor", "§a"+Constants.shift+ "§f+§aLeft-Click§f = 1st pos", "§a"+Constants.shift+ "§f+§aRight-Click§f = 2nd pos","§aScroll-Wheel§f = move selection"}),
    EXTRUDE_MODE("Extrude Mode", 4, "§cExpand or shrink looked surface",false, new String[]{"§aLeft-Click§f = shrink","§aRight-Click§f = expand"}),
    DELETE_MODE("Delete Mode", 5, "§cDelete blocks or entities",false, new String[]{"§aLeft-Click§f = delete blocks","§aRight-Click§f = delete entity", "§a"+Constants.shift+ "§f+§aRight-Click§f = delete nearby entities"}),
    SHAPE_MODE("Shape Mode", 6, "§cSpawn custom shape",false, new String[]{"§aLeft-Click§f = place shape (continuous)","§aRight-Click§f = place shape (once)","§a"+Constants.shift+ "§f+§aAny§f = replace blocks", "§a"+Constants.sneak+ "§f+§aAny§f = place from bottom"}),
    REPLACE_MODE("Replace Mode", 7, "§cReplace blocks with a block held",false, new String[]{"§aRight-Click§f = replace block (once)","§a"+Constants.shift+ "§f+§aRight-Click§f = replace block (continuous)"});

    private final String name;
    private final int index;
    private final String description;
    private final boolean canSelect;
    private final String[] actionsHelp;

    public int getIndex() {
        return this.index;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public boolean getCanSelect(){return this.canSelect;}
    public String[] getActionsHelp(){return this.actionsHelp;}

    EnumBuildMode(String enumName, int enumIndex, String enumDescription, boolean enumCanSelect, String[] actionsHelp){
        this.name=enumName;
        this.index=enumIndex;
        this.description=enumDescription;
        this.canSelect=enumCanSelect;
        this.actionsHelp = actionsHelp;
    }


    public static EnumBuildMode getEnumFromIndex(int index){
        for (EnumBuildMode enumMode : EnumBuildMode.values()) {
            if (enumMode.index==index){
                return enumMode;
            }
        }
        return EnumBuildMode.NORMAL_MODE;
    }

    private static class Constants {
        private static final GameSettings settings = Minecraft.getMinecraft().gameSettings;
        public static final String shift = GameSettings.getKeyDisplayString(settings.keyBindSpecial.keyCode);
        public static final String sneak = GameSettings.getKeyDisplayString(settings.keyBindSneak.keyCode);

    }
}
