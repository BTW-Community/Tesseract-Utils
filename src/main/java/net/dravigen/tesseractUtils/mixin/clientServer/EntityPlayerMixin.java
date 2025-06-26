package net.dravigen.tesseractUtils.mixin.clientServer;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase{

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Shadow public PlayerCapabilities capabilities;

    @Unique private GameSettings gameSettings=Minecraft.getMinecraft().gameSettings; // Replace 'gameSettings' with the actual obfuscated field name
    @Unique private KeyBinding flyUpKey;
    @Unique private KeyBinding flyDownKey;
    @Unique private KeyBinding sprintKey;

    @ModifyConstant(method = "moveEntityWithHeading",constant = @Constant(floatValue = 2.0F))
    private float modifySprintFlightSpeed(float constant){
        return this.capabilities.isCreativeMode ? (int)FLIGHT_SPEED.getValue() : constant;
    }

    @Redirect(method = "moveEntityWithHeading",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;isSprinting()Z"))
    private boolean isSprinting(EntityPlayer instance){
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying&& (int)FLIGHT_SPEED.getValue()!=2) {
            if (sprintKey == null) {
                sprintKey = gameSettings.keyBindSpecial;
            }
            return sprintKey.pressed;
        }else return instance.isSprinting();
    }

    @Inject(method = "moveEntityWithHeading",at = @At("TAIL"))
    private void handleDisabledMomentum(float par1, float par2, CallbackInfo ci){
        if (this.capabilities.isCreativeMode&&this.moveStrafing==0 && this.moveForward==0&&this.capabilities.isFlying&& (boolean)FLIGHT_MOMENTUM.getValue()) {
            this.motionX *= 0.3;
            this.motionY *= 0.75;
            this.motionZ *= 0.3;
        }
    }

    @Inject(method = "onUpdate",at = @At(value = "HEAD"))
    private void processTUConfigs(CallbackInfo ci){
        if (this.capabilities.isCreativeMode) {
            if (this.boundingBox.minY < -3) {
                if (this.motionY < 0) {
                    this.motionY = 0;
                }
            }
            if ((boolean)NO_CLIP.getValue()) {
                this.onGround = false;
                this.capabilities.isFlying = true;
                this.noClip = true;
            } else this.noClip = false;
        }
        if (this.capabilities.isCreativeMode&&this.capabilities.isFlying) {
            if (flyUpKey == null || flyDownKey == null || sprintKey == null) {
                flyUpKey = gameSettings.keyBindJump;
                flyDownKey = gameSettings.keyBindSneak;
                sprintKey = gameSettings.keyBindSpecial;
            }
            if (sprintKey.pressed && (int)FLIGHT_SPEED.getValue() != 2) {
                if (flyUpKey.pressed) {
                    this.motionY += 0.12D * (int)FLIGHT_SPEED.getValue();
                }
                if (flyDownKey.pressed) {
                    this.motionY -= 0.12D * (int)FLIGHT_SPEED.getValue();
                }
            }
            if ((boolean) FLIGHT_MOMENTUM.getValue()&&!sprintKey.pressed) {
                if (flyUpKey.pressed) {
                    this.motionY += 0.05D;
                }
                if (flyDownKey.pressed) {
                    this.motionY -= 0.05D;
                }
            }
        }
    }

    @Redirect(method = "isEntityInsideOpaqueBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isEntityInsideOpaqueBlock()Z"))
    private boolean disableBlockOcclusion(EntityLivingBase instance){
        if ((boolean) NO_CLIP.getValue()&&this.capabilities.isCreativeMode) {
            return false;
        }else return super.isEntityInsideOpaqueBlock();
    }


}