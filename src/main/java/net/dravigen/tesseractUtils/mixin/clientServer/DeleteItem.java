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
    public boolean hitEntity(ItemStack stack, EntityLivingBase defendEntity, EntityLivingBase attackEntity) {
        if (defendEntity!=null&&stack.itemID==Item.swordWood.itemID&&attackEntity instanceof EntityPlayer player&&player.capabilities.isCreativeMode) {
            defendEntity.setDead();
            return false;
        }else return super.hitEntity(stack,defendEntity,attackEntity);
    }
}
