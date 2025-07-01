package net.dravigen.tesseractUtils.advanced_edit;

public enum EnumBuildMode {

    NORMAL_MODE("Normal Mode", 0, "Your average creative experience",false),
    SELECTION_MODE("Selection Mode", 1, "Select an area or modify it",true),
    MOVE_MODE("Move Mode", 2, "Move your selection",true),
    EXTRUDE_MODE("Extrude Mode", 3, "Expand or shrink the surface you're looking at",false),
    COPY_MODE("Copy Mode", 4, "Copy and paste a selection",true),
    DELETE_MODE("Delete Mode", 5, "Delete a selection",false),
    SHAPE_MODE("Shape Mode", 6, "Spawn custom shape in your world",false),
    REPLACE_MODE("Replace Mode", 7, "Replace blocks you're looking with other blocks",false);

    private final String name;
    private final int index;
    private final String description;
    private final boolean canSelect;

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

    EnumBuildMode(String enumName, int enumIndex, String enumDescription, boolean enumCanSelect){
        this.name=enumName;
        this.index=enumIndex;
        this.description=enumDescription;
        this.canSelect=enumCanSelect;
    }


    public static EnumBuildMode getEnumFromIndex(int index){
        for (EnumBuildMode enumMode : EnumBuildMode.values()) {
            if (enumMode.index==index){
                return enumMode;
            }
        }
        return EnumBuildMode.NORMAL_MODE;
    }
}
