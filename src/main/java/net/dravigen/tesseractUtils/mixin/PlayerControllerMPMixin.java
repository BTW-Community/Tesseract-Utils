package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow private EnumGameType currentGameType;
    @Shadow @Final private Minecraft mc;
    @Shadow private int blockHitDelay;

    @ModifyConstant(method = "getBlockReachDistance", constant = @Constant(floatValue = 5.0f))
    private float modifyCreativeReach(float constant) {
        if (this.currentGameType.isCreative()) return TesseractUtilsAddon.reach;
        else return constant;
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"))
    private void selectionFirst(int x, int y, int z, int par4, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentGameType.isCreative()) {
            if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().itemID == 271) {
                TesseractUtilsAddon.x1 = x;
                TesseractUtilsAddon.y1 = y;
                TesseractUtilsAddon.z1 = z;
                if (this.mc.theWorld.isRemote) {
                    this.mc.thePlayer.addChatMessage("First position set to (" + x + ", " + y + ", " + z + ").");
                }
            }
        }
    }

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"))
    private void selectionSecond(EntityPlayer entity, World world, ItemStack itemStack, int x, int y, int z, int par7, Vec3 par8Vec3, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentGameType.isCreative()) {
            if (itemStack != null && itemStack.itemID == 271) {
                TesseractUtilsAddon.x2 = x;
                TesseractUtilsAddon.y2 = y;
                TesseractUtilsAddon.z2 = z;
                if (world.isRemote) {
                    entity.addChatMessage("Second position set to (" + x + ", " + y + ", " + z + ").");
                }
            }
        }
    }

    @Redirect(method = "onPlayerDamageBlock",at = @At(value = "FIELD", target = "Lnet/minecraft/src/PlayerControllerMP;blockHitDelay:I",opcode = Opcodes.GETFIELD))
    private int disableBreakCooldown(PlayerControllerMP instance, int value){
        return this.currentGameType.isCreative()&& TesseractUtilsAddon.disableBreakCooldown ? 0 : this.blockHitDelay;
    }
}