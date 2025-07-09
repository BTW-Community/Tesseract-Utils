package net.dravigen.tesseractUtils.mixin.clientServer;

import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlaceAsBlockItem.class)
public class PlaceAsBlockItemMixin {

    @Unique
    EntityPlayer entityPlayer;
/*
    @Inject(method = "onItemUse",at = @At("HEAD"))
    private void getPlayer(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, CallbackInfoReturnable<Boolean> cir){
        entityPlayer=player;
    }

    @Redirect(method = "onItemUse",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"))
    private boolean disableUpdateIfReplaceClick(World instance, int i, int j, int k, int iBlockID, int iMetadata){
        if (TesseractUtilsAddon.currentBuildingMode== EnumBuildMode.REPLACE_MODE.getIndex()&&!entityPlayer.isSneaking()){
            return false;
        }else return instance.setBlockAndMetadataWithNotify(i,j,k,iBlockID,iMetadata);
    }*/
}