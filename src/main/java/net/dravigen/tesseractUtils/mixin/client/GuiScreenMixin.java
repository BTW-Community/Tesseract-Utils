package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.GUI.GuiConfigSettingsScreen;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @ModifyArg(method = "drawBackground",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation customBackground(ResourceLocation par1ResourceLocation){
        if ((Object)this instanceof GuiConfigSettingsScreen){
            return new ResourceLocation("tesseract_utils:textures/gui/custom background.png");
        }
        return par1ResourceLocation;
    }
}
