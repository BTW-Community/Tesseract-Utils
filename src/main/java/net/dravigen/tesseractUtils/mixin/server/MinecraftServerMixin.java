package net.dravigen.tesseractUtils.mixin.server;

import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "stopServer",at = @At("TAIL"))
    private void stop(CallbackInfo ci){
        PacketUtils.playersRedoListServer.clear();
        PacketUtils.playersCopyServer.clear();
        PacketUtils.playersUndoListServer.clear();
    }
}
