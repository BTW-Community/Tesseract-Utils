package net.dravigen.tesseractUtils.mixin.server;

import net.dravigen.tesseractUtils.packet.PacketSender;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.utils.PacketUtils.playersGamemodeServer;

@Mixin(CommandGameMode.class)
public abstract class CommandGameModeMixin{
    @Shadow protected abstract EnumGameType getGameModeFromCommand(ICommandSender iCommandSender, String string);

    @Inject(method = "processCommand",at = @At(value = "RETURN"))
    private void a (ICommandSender sender, String[] strings, CallbackInfo ci) {
        EnumGameType enumGameType = this.getGameModeFromCommand(sender, strings[0]);
        EntityPlayerMP player = CommandBase.getPlayer(sender, sender.getCommandSenderName());
        int modeState = enumGameType == EnumGameType.CREATIVE ? 0 : 1;

        playersGamemodeServer.put(player.getEntityName(), modeState);

        PacketSender.sendServerToClientMessage(player, "updateMode:" + modeState);
    }
}
