package net.dravigen.tesseractUtils.mixin.clientServer;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase{

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Shadow public PlayerCapabilities capabilities;

    @Shadow public abstract boolean isUsingSpecialKey();

    @Redirect(method = "moveEntityWithHeading",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PlayerCapabilities;getFlySpeed()F"))
    private float modifySprintFlightSpeed(PlayerCapabilities instance){
        return this.capabilities.isCreativeMode&&FLIGHT_SPEED.getIntValue()!=2&&this.isUsingSpecialKey() ? FLIGHT_MOMENTUM.getBoolValue() ? FLIGHT_SPEED.getIntValue()*0.35f : FLIGHT_SPEED.getIntValue()*0.1f: instance.getFlySpeed();
    }

    @Redirect(method = "moveEntityWithHeading",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;isSprinting()Z"))
    private boolean isSprinting(EntityPlayer instance){
        if(FLIGHT_SPEED.getIntValue()!=2){
            return false;
        }
        return instance.isSprinting();
    }

    @Inject(method = "moveEntityWithHeading",at = @At("HEAD"))
    private void handleDisabledMomentum(float par1, float par2, CallbackInfo ci){
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying&& (boolean)FLIGHT_MOMENTUM.getValue()) {
            if (!this.isUsingSpecialKey()){
                this.motionY = this.motionY > FLIGHT_SPEED.getIntValue() ? 0.37 : this.motionY < -FLIGHT_SPEED.getIntValue() ? -0.37 : this.motionY;
            }
            if ((FLIGHT_SPEED.getIntValue()!=2&&this.isUsingSpecialKey())||(this.moveStrafing == 0 && this.moveForward == 0 && !this.isJumping&&!this.isSneaking())) {
                this.motionX *= 0.3;
                this.motionY *= 0.3;
                this.motionZ *= 0.3;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "onUpdate",at = @At(value = "HEAD"))
    private void processTUConfigs(CallbackInfo ci){
        if (this.capabilities.isCreativeMode) {
            if (this.boundingBox.minY < -3) {
                this.motionY = 0.2;
            } else if (this.boundingBox.minY >= -3 && this.boundingBox.minY <=-2.8) {
                this.motionY = 0;
            }
            if (TesseractUtilsAddon.modeState==2) {
                this.onGround = false;
                this.capabilities.isFlying = true;
                this.noClip = true;
            } else this.noClip = false;
        }
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying) {
            if (this.isUsingSpecialKey() && FLIGHT_SPEED.getIntValue() != 2) {
                if (this.isJumping&&!this.isSneaking()) {
                    if (FLIGHT_MOMENTUM.getBoolValue()) {
                        this.motionY = FLIGHT_SPEED.getIntValue();
                    }else this.motionY = FLIGHT_SPEED.getIntValue()*0.3;
                }
                if (this.isSneaking()&&!this.isJumping) {
                    if (FLIGHT_MOMENTUM.getBoolValue()) {
                        this.motionY = -FLIGHT_SPEED.getIntValue();
                    }else this.motionY = -FLIGHT_SPEED.getIntValue()*0.3;
                }
                /*
                if (gameSettings.keyBindForward.pressed){
                    Vec3 direction = this.getLook(TesseractUtilsAddon.partialTick);
                    float add = (int)FLIGHT_SPEED.getValue();
                    this.moveEntity(direction.xCoord*add,0,direction.zCoord*add);
                }*/
            }
        }
    }

    @Redirect(method = "isEntityInsideOpaqueBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isEntityInsideOpaqueBlock()Z"))
    private boolean disableBlockOcclusion(EntityLivingBase instance){
        if (this.capabilities.isCreativeMode&&TesseractUtilsAddon.modeState==2) {
            return false;
        }else return super.isEntityInsideOpaqueBlock();
    }
}