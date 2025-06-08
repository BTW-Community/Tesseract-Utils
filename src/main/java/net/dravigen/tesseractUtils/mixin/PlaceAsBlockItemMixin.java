package net.dravigen.tesseractUtils.mixin;

import btw.item.items.PlaceAsBlockItem;
import net.dravigen.tesseractUtils.TessUConfig;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlaceAsBlockItem.class)
public class PlaceAsBlockItemMixin {

    @Inject(method = "canPlaceItemBlockOnSide",at = @At("RETURN"), cancellable = true)
    private void allow(World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (player.capabilities.isCreativeMode&& (TessUConfig.enableNoClip||TessUConfig.enableClickReplace)){
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "onItemUse",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"))
    private boolean disableUpdateIfReplaceClick(World instance, int i, int j, int k, int iBlockID, int iMetadata){
        if (TessUConfig.enableClickReplace){
            instance.setBlock(i,j,k,iBlockID,iMetadata,2);
            return false;
        }else return instance.setBlockAndMetadataWithNotify(i,j,k,iBlockID,iMetadata);
    }
}