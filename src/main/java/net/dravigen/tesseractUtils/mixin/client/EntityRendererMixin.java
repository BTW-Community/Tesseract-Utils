package net.dravigen.tesseractUtils.mixin.client;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

import java.util.Arrays;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow private Minecraft mc;

    @ModifyConstant(method = "getMouseOver",constant = @Constant(doubleValue = 6.0,ordinal = 0))
    private double disableDistanceLimit(double constant){
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if(heldItem != null) {
            int id = heldItem.itemID;
            if (id ==1800||(heldItem.getTagCompound()!=null&& heldItem.getTagCompound().hasKey("BuildingParams"))) return 999999;
        }
        if (this.mc.thePlayer.capabilities.isCreativeMode&& (int)REACH.getValue()>5){
            return 999999;
        }else return constant;
    }

    @Redirect(method = "updateFogColor",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isPotionActive(Lnet/minecraft/src/Potion;)Z"))
    private boolean disableRedNightVis(EntityLivingBase instance, Potion par1Potion){
        if ((boolean) VANILLA_NIGHTVIS.getValue()){
            return false;
        }else return instance.isPotionActive(par1Potion);
    }
    @ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] manageNightvisionColor(int[] par1ArrayOfInteger) {
        if (this.mc.thePlayer.isPotionActive(Potion.nightVision)&&(boolean) VANILLA_NIGHTVIS.getValue()) {
            int[] numbers = new int[256];
            Arrays.fill(numbers, -1);
            return numbers;
        }
        return par1ArrayOfInteger;
    }
}