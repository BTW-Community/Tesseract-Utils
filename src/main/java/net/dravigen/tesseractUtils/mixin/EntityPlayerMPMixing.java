package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TessUConfig;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixing {


    @Shadow public abstract void setGameType(EnumGameType par1EnumGameType);

    @Unique
    private static boolean logic = false;

    @Unique
    private static boolean F4 = false;
    @Unique
    private static int chosenMode;
    @Unique
    private static boolean F3= false;
    @Unique
    private static int previousMode;
    @Unique
    private static boolean previousCalled=false;
    @Unique
    private static boolean F4Foolpressed = false;
    @Unique
    private static boolean F4pressed = false;

    @Inject(method = "onUpdate",at = @At("HEAD"))
    private void update(CallbackInfo ci){
        if(!Keyboard.isKeyDown(61) && Keyboard.isKeyDown(62)){
            F4Foolpressed =true;
        }
        if (Keyboard.isKeyDown(61)&&!F4Foolpressed) {
            F3=true;
            if (Keyboard.isKeyDown(62)) {
                F4pressed=true;
            }
            if (F4pressed) {
                if (!Keyboard.isKeyDown(62)) {
                    F4 = false;
                }
                if (!F4) {
                    logic = Keyboard.isKeyDown(62);
                }
                if (logic) {
                    if (previousCalled) {
                        chosenMode++;
                        chosenMode = chosenMode > 2 ? 0 : chosenMode;
                    } else {
                        chosenMode = previousMode;
                        previousCalled = true;
                    }
                    logic = false;
                    F4 = true;
                }
            }
        }else if (F3&&!Keyboard.isKeyDown(61)){
            F3=false;
            previousCalled=false;
            F4pressed=false;
            if (TesseractUtilsAddon.modeState != chosenMode){
                previousMode=TesseractUtilsAddon.modeState;
                this.setGameType(chosenMode == 1 ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
                TessUConfig.enableNoClip = chosenMode == 2;
            }
            chosenMode =TesseractUtilsAddon.modeState;
        }else {
            chosenMode=TesseractUtilsAddon.modeState;
        }
        if (!Keyboard.isKeyDown(61)&&!Keyboard.isKeyDown(62)){
            F4Foolpressed =false;
        }
    }
}
