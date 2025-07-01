package net.dravigen.tesseractUtils.advanced_edit;

import net.dravigen.tesseractUtils.command.UtilsCommand;
import net.minecraft.src.Vec3;

public class BlockSelectionManager {


    public static Vec3 block1 = Vec3.createVectorHelper(UtilsCommand.x1,UtilsCommand.y1,UtilsCommand.z1);
    public static Vec3 block2 = Vec3.createVectorHelper(UtilsCommand.x2,UtilsCommand.y2,UtilsCommand.z2);

    public static boolean isBlock1Selected=false;
    public static boolean isBlock2Selected=false;

    // Enum to manage the selection state
    public enum SelectionState {
        NONE,            // No blocks selected
        FIRST_SELECTED,  // One block selected
        TWO_SELECTED     // Two blocks selected, ready for box rendering
    }
    public static SelectionState currentSelectionState = SelectionState.NONE;

    public static void clear(){
        block1 = Vec3.createVectorHelper(UtilsCommand.x1,UtilsCommand.y1,UtilsCommand.z1);
        block2 = Vec3.createVectorHelper(UtilsCommand.x2,UtilsCommand.y2,UtilsCommand.z2);
        UtilsCommand.setCoord1(9999999,9999999,9999999);
        UtilsCommand.setCoord2(9999999,9999999,9999999);
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


}
