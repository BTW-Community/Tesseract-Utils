package net.dravigen.tesseractUtils.mixin.clientServer;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow private EnumGameType currentGameType;
    @Shadow @Final private Minecraft mc;
    @Shadow private int blockHitDelay;

    @ModifyConstant(method = "getBlockReachDistance", constant = @Constant(floatValue = 5.0f))
    private float modifyCreativeReach(float constant) {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if (heldItem != null) {
            int id = heldItem.itemID;
            if (id ==Item.swordWood.itemID|| id ==Item.axeWood.itemID|| id ==Item.shovelWood.itemID||(heldItem.getTagCompound()!=null&& heldItem.getTagCompound().hasKey("BuildingParams")))
                return 128;
        }
        if (this.currentGameType.isCreative()) return (int)REACH.getValue();
        else return constant;
    }

    @Redirect(method = "onPlayerDamageBlock",at = @At(value = "FIELD", target = "Lnet/minecraft/src/PlayerControllerMP;blockHitDelay:I",opcode = Opcodes.GETFIELD))
    private int disableBreakCooldown(PlayerControllerMP instance, int value){
        return this.currentGameType.isCreative()&& BREAKING_COOLDOWN.getBoolValue() ? 0 : this.blockHitDelay;
    }

    @Inject(method = "updateController",at = @At("TAIL"))
    private void getModeState(CallbackInfo ci){
        EnumGameType type = this.currentGameType;
        TesseractUtilsAddon.modeState = type == EnumGameType.CREATIVE ?  (NO_CLIP.getBoolValue() ? 2 : 0) : type == EnumGameType.SURVIVAL ? 1 : 3;
    }
}