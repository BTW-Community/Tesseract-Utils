package net.dravigen.tesseractUtils.configs;

import net.minecraft.src.Vec3;

public class BlockSelectionManager {


    public static Vec3 block1 = Vec3.createVectorHelper(9999999,9999999,9999999);
    public static Vec3 block2 = Vec3.createVectorHelper(9999999,9999999,9999999);

    public static int[] servBlock1 = new int[]{9999999,9999999,9999999};
    public static int[] servBlock2 = new int[]{9999999,9999999,9999999};

    public static boolean isBlock1Selected=false;
    public static boolean isBlock2Selected=false;

    public enum SelectionState {
        NONE,
        FIRST_SELECTED,
        TWO_SELECTED
    }
    public static SelectionState currentSelectionState = SelectionState.NONE;

    public static void clear(){
        block1 = Vec3.createVectorHelper(9999999,9999999,9999999);
        block2 = Vec3.createVectorHelper(9999999,9999999,9999999);
        currentSelectionState = SelectionState.NONE;
        isBlock1Selected=false;
        isBlock2Selected=false;
    }

    public static void setBlock1(int x, int y, int z) {
        if (!isBlock1Selected) {
            switch (currentSelectionState) {
                case NONE -> currentSelectionState = SelectionState.FIRST_SELECTED;
                case FIRST_SELECTED -> currentSelectionState = SelectionState.TWO_SELECTED;
            }
        }
        block1.xCoord = x;
        block1.yCoord = y;
        block1.zCoord = z;
        isBlock1Selected = true;
    }

    public static void setBlock2(int x, int y, int z) {
        if (!isBlock2Selected) {
            switch (currentSelectionState) {
                case NONE -> currentSelectionState = SelectionState.FIRST_SELECTED;
                case FIRST_SELECTED -> currentSelectionState = SelectionState.TWO_SELECTED;
            }
        }
        block2.xCoord=x;
        block2.yCoord=y;
        block2.zCoord=z;
        isBlock2Selected=true;
    }

    public static void setServBlock1(int x, int y, int z){
        servBlock1 = new int[]{x, y, z};
    }
    public static void setServBlock2(int x, int y, int z){
        servBlock2 = new int[]{x, y, z};
    }
}
