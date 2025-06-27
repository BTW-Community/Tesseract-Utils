package net.dravigen.tesseractUtils.mixin.clientServer;

import btw.item.items.SwordItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public class DeleteItem extends ItemSword {
    public DeleteItem(int par1, EnumToolMaterial par2EnumToolMaterial) {
        super(par1, par2EnumToolMaterial);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer par3EntityPlayer) {
        if (par3EntityPlayer.capabilities.isCreativeMode&&stack.itemID==Item.swordWood.itemID) {
            return stack;
        }else return super.onItemRightClick(stack,par2World,par3EntityPlayer);
    }
}
