package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin {
    @Shadow protected Minecraft mc;

    @Shadow protected abstract boolean isBlockTranslucent(int par1, int par2, int par3);

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z",ordinal = 0))
    private boolean onPushOutOfBlocks(EntityPlayerSP instance, int par1, int par2, int par3) {
        if (TesseractUtilsAddon.enableNoClip&&this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1,par2,par3);
    }
    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z",ordinal = 1))
    private boolean onPushOutOfBlocks1(EntityPlayerSP instance, int par1, int par2, int par3) {
        if (TesseractUtilsAddon.enableNoClip&&this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1,par2,par3);
    }
}
