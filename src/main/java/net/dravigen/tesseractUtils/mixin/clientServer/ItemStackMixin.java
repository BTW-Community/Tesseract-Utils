package net.dravigen.tesseractUtils.mixin.clientServer;

import net.dravigen.tesseractUtils.configs.EnumConfig;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Redirect(method = "getTooltip",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Item;addInformation(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/EntityPlayer;Ljava/util/List;Z)V"))
    private void addDura(Item instance, ItemStack stack, EntityPlayer player, List par3List, boolean par4){
        if (player.worldObj.isRemote) {
            if ((boolean)EnumConfig.EXTRA_DEBUG.getValue() &&Minecraft.getMinecraft().gameSettings.advancedItemTooltips&&stack.getMaxDamage()>0) {
                par3List.add("Durability: " + (stack.getMaxDamage() - stack.getItemDamage()) + " / " + stack.getMaxDamage());
            }
        }
        instance.addInformation(stack,player,par3List,par4);
    }
}
