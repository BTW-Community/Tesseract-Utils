package net.dravigen.tesseractUtils.mixin.clientServer;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.enums.EnumBuildMode;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow private EnumGameType currentGameType;
    @Shadow @Final private Minecraft mc;
    @Shadow private int blockHitDelay;

    @Inject(method = "onPlayerRightClick", at = @At(value = "HEAD"), cancellable = true)
    private void disableRightClickIfReplaceMode(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, Vec3 par8Vec3, CallbackInfoReturnable<Boolean> cir){
        if (this.currentGameType.isCreative()&& PacketUtils.isPlayerOPClient&&TesseractUtilsAddon.currentBuildingMode==EnumBuildMode.REPLACE_MODE.getIndex()&&!this.mc.thePlayer.isSneaking()&&par3ItemStack!=null) {
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "getBlockReachDistance", constant = @Constant(floatValue = 5.0f))
    private float modifyCreativeReach(float constant) {
        if (this.currentGameType.isCreative()&& PacketUtils.isPlayerOPClient) {
            ItemStack heldItem = this.mc.thePlayer.getHeldItem();
            if (heldItem != null) {
                int id = heldItem.itemID;
                if (id == Item.swordWood.itemID ||id == Item.axeWood.itemID ||id == Item.shovelWood.itemID || (heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams")&&!this.mc.thePlayer.isSneaking()))
                    return 0;
            }
            if (TesseractUtilsAddon.currentBuildingMode!=8&&TesseractUtilsAddon.currentBuildingMode!= EnumBuildMode.REPLACE_MODE.getIndex())return 0;
            return REACH.getIntValue();
        }
        return constant;
    }


    @Redirect(method = "onPlayerDamageBlock",at = @At(value = "FIELD", target = "Lnet/minecraft/src/PlayerControllerMP;blockHitDelay:I",opcode = Opcodes.GETFIELD))
    private int disableBreakCooldown(PlayerControllerMP instance, int value){
        return this.currentGameType.isCreative()&& BREAKING_COOLDOWN.getBoolValue() ? 0 : this.blockHitDelay;
    }
}