package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.TesseractUtilsAddon.TUChannels;
import net.dravigen.tesseractUtils.packet.PacketHandlerS2C;
import net.minecraft.src.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.listLanguage;
import static net.dravigen.tesseractUtils.utils.ListsUtils.*;
import static net.dravigen.tesseractUtils.utils.ListsUtils.initBlocksNameList;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void tu_onCustomPayloadS2C(Packet250CustomPayload packet, CallbackInfo ci) {
        if (packet.channel.equals(TUChannels.SERVER_TO_CLIENT_CHANNEL)) {
            PacketHandlerS2C.handle(packet);
        }
    }

    @Inject(method = "handleLogin",at = @At("HEAD"))
    private void initLists(Packet1Login par1Packet1Login, CallbackInfo ci){
        if (listLanguage!= Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
            itemsMap.clear();
            entitiesMap.clear();
            blocksMap.clear();
            potionsMap.clear();
        }

        if (itemsMap.isEmpty()||entitiesMap.isEmpty()|| blocksMap.isEmpty()||potionsMap.isEmpty()) {
            initItemsNameList();
            initEntityList();
            initPotionList();
            initBlocksNameList();
            listLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        }
    }
}
