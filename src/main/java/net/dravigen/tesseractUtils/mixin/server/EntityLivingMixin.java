package net.dravigen.tesseractUtils.mixin.server;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase {
    @Shadow private boolean persistenceRequired;

    @Shadow protected abstract void despawnEntity();

    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "entityLivingUpdateAITasks",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;despawnEntity()V"))
    private void preventDespawn1(EntityLiving instance){
        if (!this.persistenceRequired){
            this.despawnEntity();
        }
    }
    @Redirect(method = "updateEntityActionState",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;despawnEntity()V"))
    private void preventDespawn2(EntityLiving instance){
        if (!this.persistenceRequired){
            this.despawnEntity();
        }
    }
}
