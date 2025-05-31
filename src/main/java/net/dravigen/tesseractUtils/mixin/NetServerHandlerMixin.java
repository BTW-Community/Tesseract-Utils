package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin {

    @Shadow public EntityPlayerMP playerEntity;

    @Inject(method = "getCollidingBoundingBoxesIgnoreSpecifiedEntities", at = @At("RETURN"), cancellable = true)
    private void disableCollision(World world, Entity entity, AxisAlignedBB par2AxisAlignedBB, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        if (TesseractUtilsAddon.enableNoClip && entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
            cir.setReturnValue(new ArrayList<>());
        }
    }

    @Redirect(method = "handleBlockDig", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isBlockProtected(Lnet/minecraft/src/World;IIILnet/minecraft/src/EntityPlayer;)Z"))
    private boolean disableBreak(MinecraftServer instance, World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
        if (par5EntityPlayer.capabilities.isCreativeMode && par5EntityPlayer.inventory.getCurrentItem() != null) {
            return par5EntityPlayer.inventory.getCurrentItem().itemID == 271;
        } else return false;
    }

    @ModifyConstant(method = "handleBlockDig", constant = @Constant(doubleValue = 36.0))
    private double disableBreakDistanceLimit(double constant) {
        if (this.playerEntity.capabilities.isCreativeMode && TesseractUtilsAddon.reach > 5) {
            return 999999;
        } else return constant;
    }

    @Redirect(method = "handleUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D"))
    private double disableDistanceLimitUseEntity(EntityPlayerMP instance, Entity entity) {
        if (this.playerEntity.capabilities.isCreativeMode && TesseractUtilsAddon.reach > 5) {
            return 0;
        } else return this.playerEntity.getDistanceSqToEntity(entity);
    }

    @Redirect(method = "handlePlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSq(DDD)D"))
    private double disableDistanceLimitUseEntity(EntityPlayerMP instance, double x, double y, double z) {
        if (this.playerEntity.capabilities.isCreativeMode && TesseractUtilsAddon.reach > 5) {
            return 0;
        } else return this.playerEntity.getDistanceSq(x, y, z);
    }

    @Redirect(method = "handlePlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemInWorldManager;activateBlockOrUseItem(Lnet/minecraft/src/EntityPlayer;Lnet/minecraft/src/World;Lnet/minecraft/src/ItemStack;IIIIFFF)Z"))
    private boolean replaceInsteadOfPlacing(ItemInWorldManager instance, EntityPlayer player, World world, ItemStack itemStack, int x, int y, int z, int side, float offX, float offY, float offZ) {
        if (this.playerEntity.capabilities.isCreativeMode && TesseractUtilsAddon.enableClickReplace && !this.playerEntity.isSneaking()) {
            world.setBlockToAir(x, y, z);
        }return instance.activateBlockOrUseItem(this.playerEntity, world, itemStack, x, y, z, side, offX, offY, offZ);
    }
}