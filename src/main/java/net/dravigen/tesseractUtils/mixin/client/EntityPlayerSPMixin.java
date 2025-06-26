package net.dravigen.tesseractUtils.mixin.client;

import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Shadow
    protected abstract boolean isBlockTranslucent(int par1, int par2, int par3);

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 0))
    private boolean onPushOutOfBlocks(EntityPlayerSP instance, int par1, int par2, int par3) {
        if ((boolean) NO_CLIP.getValue() && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 1))
    private boolean onPushOutOfBlocks1(EntityPlayerSP instance, int par1, int par2, int par3) {
        if ((boolean) NO_CLIP.getValue() && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Unique
    private static boolean keyPressed = false;

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void swapHotbar(CallbackInfo ci) {
        if (this.mc.currentScreen==null&&!Keyboard.isKeyDown(Keyboard.KEY_F3)&&Keyboard.isKeyDown((int) BAR_SWAP_KEY.getValue())) {
            if (!keyPressed) {
                int windowId = this.inventoryContainer.windowId;
                PlayerControllerMP controllerMP = this.mc.playerController;
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    for (int column = 0; column <= 8; column++) {
                        int hotbarSlot = 9 + column;
                        int row1Slot = 18 + column;
                        int row2Slot = 27 + column;
                        int row3Slot = 36 + column;
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                    }
                }else {
                    int current = this.inventory.currentItem;
                    int hotbarSlot = 9 + current;
                    int row1Slot = 18 + current;
                    int row2Slot = 27 + current;
                    int row3Slot = 36 + current;
                    controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                    controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                    controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                    controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                    controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                }
                keyPressed = true;
            }
        } else keyPressed = false;
    }
}
