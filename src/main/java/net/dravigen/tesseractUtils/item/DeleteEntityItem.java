package net.dravigen.tesseractUtils.item;

import net.minecraft.src.*;


public class DeleteEntityItem extends Item {
    public DeleteEntityItem(int par1) {
        super(par1);
        this.setTextureName("tesseract_utils:deleteEntityItem");
        this.setUnlocalizedName("deleteEntityItem");
    }



    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase defendEntity, EntityLivingBase attackEntity) {
        if (defendEntity!=null) {
            defendEntity.setDead();
        }
        return false;
    }

}
