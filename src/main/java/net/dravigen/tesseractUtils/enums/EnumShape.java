package net.dravigen.tesseractUtils.enums;

public enum EnumShape {

    SHAPE("Shape", "shape", 500, 3, 0, 4),
    VOLUME("Volume Type","volume",501,3,0,2),
    RADIUS("Radius", "radius", 502, 1, 4, 64),
    THICKNESS("Thickness", "thickness", 503, 1, 0, 64),
    HEIGHT("Height", "height", 504, 1, 4, 128),
    SIZE_X("X Size", "sizeX", 505, 1, 9, 128),
    SIZE_Y("Y Size", "sizeY", 506, 1, 9, 128),
    SIZE_Z("Z Size ", "sizeZ", 507, 1, 9, 128),
    FACING_SIDE("Facing Side","facingSide",508,3,0,1);


    private final String name;
    private final String property;
    private final int index;
    private final int type;
    private final Object baseValue;
    private final int maxValue;
    private Object enumValue;

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public String getProperty() {
        return this.property;
    }

    public String getShape(){
        return switch (this.getIntValue()) {
            case 1 -> "Cylinder";
            case 2 -> "Cube";
            case 3 -> "Pyramid";
            case 4 -> "plane";
            default -> "Sphere";
        };
    }

    public String getVolume(){return this.getIntValue()==0 ? "full" : this.getIntValue()==1 ? "hollow" : "open";}

    public Object getValue() {
        if (this.index== SHAPE.index)return this.getShape();
        if (this.index== VOLUME.index)return this.getVolume();
        if (this.isIntSlider()) return Math.min((int) this.enumValue+1,this.getMaxValue());
        return this.enumValue;
    }

    public void setValue(Object value) {
        this.enumValue = value;
    }

    public Object getBaseValue() {
        return this.baseValue;
    }

    public boolean getHasOpen(){
        String shape = SHAPE.getShape();
        return switch (shape.toLowerCase()) {
            case "sphere", "pyramid", "plane" -> false;
            default -> true;
        };
    }

    public boolean getHasHollow(){
        String shape = SHAPE.getShape();
        return switch (shape.toLowerCase()) {
            case "plane" -> false;
            default -> true;
        };
    }

    public int getMaxValue() {
        if (this == THICKNESS){
            String shape = SHAPE.getShape();
            switch (shape.toLowerCase()){
                case "sphere":return RADIUS.getIntValue();
                case "cylinder":return Math.min(RADIUS.getIntValue(), HEIGHT.getIntValue()/2);
                case "cube","pyramid","plane":return Math.min(SIZE_X.getIntValue()/2,Math.min(SIZE_Y.getIntValue()/2,SIZE_Z.getIntValue()/2));
            }
        }
        return this.maxValue;
    }

    public int getIntValue() {
        if (this.isIntSlider()) return Math.min((int) this.enumValue+1,this.getMaxValue());
        return (int) this.enumValue;
    }

    public boolean getBoolValue() {
        return (boolean) this.enumValue;
    }

    EnumShape(String name, String property, int index, int valueType, int value, int max) {
        this.name = name;
        this.property = property;
        this.index = index;
        this.enumValue = value;
        this.type = valueType;
        this.baseValue = value;
        this.maxValue = max;
    }

    public boolean isBool() {
        return this.type == 0;
    }

    public boolean isIntSlider() {
        return this.type == 1;
    }

    public boolean isInt() {
        return this.type == 3;
    }

    public boolean isKeybind() {
        return this.type == 2;
    }

    public static String getParameters(){
        StringBuilder stringBuilder = new StringBuilder();
        for (EnumShape value : EnumShape.values()) {
            stringBuilder.append(value.getValue()).append(",");
        }
        return String.valueOf(stringBuilder);
    }
}
