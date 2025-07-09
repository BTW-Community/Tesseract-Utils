package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

import java.util.Arrays;
@Mixin(value = EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow private Minecraft mc;

    @Redirect(method = "updateFogColor",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isPotionActive(Lnet/minecraft/src/Potion;)Z"))
    private boolean disableRedNightVis(EntityLivingBase instance, Potion par1Potion){
        if (this.mc.thePlayer.capabilities.isCreativeMode&&(boolean) VANILLA_NIGHTVIS.getValue()&& PacketUtils.isPlayerOPClient){
            return false;
        }else return instance.isPotionActive(par1Potion);
    }
    @ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] manageNightvisionColor(int[] par1ArrayOfInteger) {
        if (this.mc.thePlayer.capabilities.isCreativeMode&& PacketUtils.isPlayerOPClient &&this.mc.thePlayer.isPotionActive(Potion.nightVision)&&(boolean) VANILLA_NIGHTVIS.getValue()) {
            int[] numbers = new int[256];
            Arrays.fill(numbers, -1);
            return numbers;
        }
        return par1ArrayOfInteger;
    }
}