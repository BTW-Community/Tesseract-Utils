package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TessUConfig;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import static net.dravigen.tesseractUtils.TessUConfig.hotbarSwap;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixing extends EntityPlayer {


    public EntityPlayerMPMixing(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Shadow public abstract void setGameType(EnumGameType par1EnumGameType);

    @Shadow public MinecraftServer mcServer;
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
    private void gameSwapLogic(CallbackInfo ci) {
        if (this.mcServer.getConfigurationManager().isPlayerOpped(this.username)) {
            if (!Keyboard.isKeyDown(61) && Keyboard.isKeyDown(62)) {
                F4Foolpressed = true;
            }
            if (Keyboard.isKeyDown(61) && !F4Foolpressed) {
                F3 = true;
                if (Keyboard.isKeyDown(62)) {
                    F4pressed = true;
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
            } else if (F3 && !Keyboard.isKeyDown(61)) {
                F3 = false;
                previousCalled = false;
                F4pressed = false;
                if (TesseractUtilsAddon.modeState != chosenMode) {
                    previousMode = TesseractUtilsAddon.modeState;
                    this.setGameType(chosenMode == 1 ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
                    TessUConfig.enableNoClip = chosenMode == 2;
                }
                chosenMode = TesseractUtilsAddon.modeState;
            } else {
                chosenMode = TesseractUtilsAddon.modeState;
            }
            if (!Keyboard.isKeyDown(61) && !Keyboard.isKeyDown(62)) {
                F4Foolpressed = false;
            }
        }
    }
    @Unique
    private static boolean keyPressed = false;

    @Inject(method = "onUpdate",at = @At("TAIL"))
    private void swapHotbar(CallbackInfo ci){

        if (hotbarSwap.isPressed()){
            if (!keyPressed) {
                List<ItemStack> bar1 = new ArrayList<>();
                List<ItemStack> bar2 = new ArrayList<>();
                List<ItemStack> bar3 = new ArrayList<>();
                List<ItemStack> bar4 = new ArrayList<>();
                for (int i = 0; i < this.inventory.mainInventory.length; i++) {
                    if (i >= 27) bar2.add(this.inventory.mainInventory[i]);
                    else if (i >= 18) bar3.add(this.inventory.mainInventory[i]);
                    else if (i >= 9) bar4.add(this.inventory.mainInventory[i]);
                    else bar1.add(this.inventory.mainInventory[i]);
                }
                for (int i = 0; i < this.inventory.mainInventory.length; i++) {
                    if (i >= 27)  this.inventory.mainInventory[i]=(bar3.get(i - 27));
                    else if (i >= 18) this.inventory.mainInventory[i]=(bar4.get(i - 18));
                    else if (i >= 9) this.inventory.mainInventory[i]=(bar1.get(i - 9));
                    else this.inventory.mainInventory[i]=(bar2.get(i));
                    this.inventory.onInventoryChanged();
                    this.inventoryContainer.detectAndSendChanges();
                }
                keyPressed = true;
            }
            /*
            if(!Keyboard.isKeyDown(17) && Keyboard.isKeyDown(82)){
                RFoolpressed =true;
            }
            if (Keyboard.isKeyDown(17)&&!RFoolpressed) {
                control=true;
                if (Keyboard.isKeyDown(82)) {
                    RPressed =true;
                }
                if (RPressed) {
                    if (!Keyboard.isKeyDown(82)) {
                        R = false;
                    }
                    if (!R) {
                        logic = Keyboard.isKeyDown(82);
                    }
                    if (logic) {
                        chosenMode++;
                        chosenMode = chosenMode > 2 ? 0 : chosenMode;
                        logic = false;
                        R = true;
                    }
                }
            }else if (control &&!Keyboard.isKeyDown(17)){
                control =false;
                RPressed =false;
                chosenMode =TesseractUtilsAddon.modeState;
            }else {
                chosenMode=TesseractUtilsAddon.modeState;
            }
            if (!Keyboard.isKeyDown(17)&&!Keyboard.isKeyDown(82)){
                RFoolpressed =false;
            }
        }*/
        }else keyPressed =false;
    }
}

