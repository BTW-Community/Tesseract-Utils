package net.dravigen.tesseractUtils.mixin.clientServer;

import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Chunk.class)
public class ChunkMixin {

    @Unique
    private static Entity entity;

    @Redirect(method = "getEntitiesWithinAABBForEntity",at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object b(List instance, int i){
        entity = (Entity)instance.get(i);
        return entity;
    }

    @Redirect(method = "getEntitiesWithinAABBForEntity",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AxisAlignedBB;intersectsWith(Lnet/minecraft/src/AxisAlignedBB;)Z"))
    private boolean b(AxisAlignedBB instance, AxisAlignedBB par1AxisAlignedBB){
        if (entity instanceof EntityPlayer player && PacketUtils.playersGamemodeServer.get(player.getEntityName())==2)return false;

        return instance.intersectsWith(par1AxisAlignedBB);
    }


}
