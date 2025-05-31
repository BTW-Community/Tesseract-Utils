package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase{
    @Shadow public PlayerCapabilities capabilities;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }
    @Unique
    private GameSettings gameSettings=Minecraft.getMinecraft().gameSettings; // Replace 'gameSettings' with the actual obfuscated field name
    @Unique
    private KeyBinding flyUpKey;
    @Unique
    private KeyBinding flyDownKey;
    @Unique
    private KeyBinding sprintKey;

    @ModifyConstant(method = "moveEntityWithHeading",constant = @Constant(floatValue = 2.0F))
    private float modifySprintFlightSpeed(float constant){
        return this.capabilities.isCreativeMode ? TesseractUtilsAddon.flySpeed : constant;
    }

    @Redirect(method = "moveEntityWithHeading",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;isSprinting()Z"))
    private boolean isSprinting(EntityPlayer instance){
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying&& TesseractUtilsAddon.flySpeed!=1) {
            if (sprintKey == null) {
                sprintKey = gameSettings.keyBindSpecial;
            }
            return sprintKey.pressed;
        }else return instance.isSprinting();
    }

    @Inject(method = "moveEntityWithHeading",at = @At("TAIL"))
    private void a(float par1, float par2, CallbackInfo ci){
        if (this.capabilities.isCreativeMode&&this.moveStrafing==0 && this.moveForward==0&&this.capabilities.isFlying&& TesseractUtilsAddon.disableMomentum) {
            this.motionX *= 0.3;
            this.motionY *= 0.75;
            this.motionZ *= 0.3;
        }
    }
    @Inject(method = "onUpdate",at = @At(value = "HEAD"))
    private void b(CallbackInfo ci){
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying) {
            if (flyUpKey == null || flyDownKey == null || sprintKey == null) {
                flyUpKey = gameSettings.keyBindJump;
                flyDownKey = gameSettings.keyBindSneak;
                sprintKey = gameSettings.keyBindSpecial;
            }
            if (sprintKey.pressed && TesseractUtilsAddon.flySpeed != 1) {
                if (flyUpKey.pressed) {
                    this.motionY += 0.12D * TesseractUtilsAddon.flySpeed;
                }
                if (flyDownKey.pressed) {
                    this.motionY -= 0.12D * TesseractUtilsAddon.flySpeed;
                }
            }
            if (TesseractUtilsAddon.disableMomentum&&!sprintKey.pressed) {
                if (flyUpKey.pressed) {
                    this.motionY += 0.05D;
                }
                if (flyDownKey.pressed) {
                    this.motionY -= 0.05D;
                }
            }
        }
    }


    public ItemStack getHeldItem() {
        return null;
    }

    public ItemStack getCurrentItemOrArmor(int var1) {
        return null;
    }

    public void setCurrentItemOrArmor(int var1, ItemStack var2) {

    }

    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }
}
