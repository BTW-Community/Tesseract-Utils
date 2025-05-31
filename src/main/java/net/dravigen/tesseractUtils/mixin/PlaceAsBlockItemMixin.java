package net.dravigen.tesseractUtils.mixin;

import btw.item.items.PlaceAsBlockItem;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlaceAsBlockItem.class)
public class PlaceAsBlockItemMixin {

    @Inject(method = "canPlaceItemBlockOnSide",at = @At("RETURN"), cancellable = true)
    private void allow(World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (player.capabilities.isCreativeMode&& (TesseractUtilsAddon.enableNoClip||TesseractUtilsAddon.enableClickReplace)){
            cir.setReturnValue(true);
        }
    }
}