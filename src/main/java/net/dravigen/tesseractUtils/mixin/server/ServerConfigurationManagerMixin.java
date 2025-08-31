package net.dravigen.tesseractUtils.mixin.server;

import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Mixin(ServerConfigurationManager.class)
public abstract class ServerConfigurationManagerMixin {


    @Shadow public abstract EntityPlayerMP getPlayerEntity(String par1Str);

    @Unique
    boolean recievedMap = false;

    @Inject(method = "playerLoggedIn",at = @At("HEAD"))
    private void queryPlayerInfo(EntityPlayerMP player, CallbackInfo ci){
        PacketUtils.clientHaveTU.put(player.getEntityName(),false);
        String[] s = {"false","false","false","false","false","false","true","false","true","true","4","1","127","64","65","35","74","78","50","38"};

        List<String> list = new ArrayList<>(Arrays.asList(s));
        PacketUtils.playersInfoServer.put(player.getEntityName(),list);
        PacketUtils.playersGamemodeServer.put(player.getEntityName(),(player.capabilities.isCreativeMode ? 0 : 1));
        PacketUtils.playersBuildModeServer.put(player.getEntityName(),8);
        PacketUtils.playersInfoServer.put("@",list);

        /*
        PacketUtils.playersBlocksMapServer.put(player.getEntityName(),sortMapByStringValues(blocksMap));
        PacketUtils.playersEntitiesNameMapServer.put(player.getEntityName(),entitiesMap);
        PacketUtils.playersPotionsNameListServer.put(player.getEntityName(),sortMapShort(potionsMap));
        PacketUtils.playersItemsNameMapServer.put(player.getEntityName(),sortMapByStringValues(itemsMap));
        PacketUtils.playersEnchantNameListServer.put(player.getEntityName(),sortMapShort(enchantMap));

        if (PacketUtils.playersEnchantNameListServer.get("@")==null){
            PacketUtils.playersBlocksMapServer.put("@",sortMapByStringValues(blocksMap));
            PacketUtils.playersEntitiesNameMapServer.put("@",entitiesMap);
            PacketUtils.playersPotionsNameListServer.put("@",sortMapShort(potionsMap));
            PacketUtils.playersItemsNameMapServer.put("@",sortMapByStringValues(itemsMap));
            PacketUtils.playersEnchantNameListServer.put("@",sortMapShort(enchantMap));
        }
*/

        if (!recievedMap){
            PacketSender.sendServerToClientMessage(player, "sendNamesLists");
            recievedMap=true;
        }

        PacketSender.sendServerToClientMessage(player, "updatePlayerInfo:" + (player.capabilities.isCreativeMode ? 0 : 1));
        PacketSender.sendServerToClientMessage(player, "haveTU");
    }

    @Inject(method = "playerLoggedOut",at = @At("HEAD"))
    private void writePlayerInfo(EntityPlayerMP player, CallbackInfo ci){
        if (PacketUtils.playersGamemodeServer.get(player.getEntityName())==2){
            PacketSender.sendServerToClientMessage(player,"updatePlayerInfo:"+0);
        }
    }
    @Inject(method = "addOp",at = @At("TAIL"))
    private void updateOpOnAdd(String username, CallbackInfo ci){
        PacketSender.sendServerToClientMessage(getPlayerEntity(username),"isPlayerOP:"+true);
        PacketSender.sendServerToClientMessage(getPlayerEntity(username),"updatePlayerInfo:"+(getPlayerEntity(username).capabilities.isCreativeMode ? 0 : 1));
    }
    @Inject(method = "removeOp",at = @At("TAIL"))
    private void updateOpOnRemove(String username, CallbackInfo ci){
        PacketSender.sendServerToClientMessage(getPlayerEntity(username),"isPlayerOP:"+false);
        PacketSender.sendServerToClientMessage(getPlayerEntity(username),"updatePlayerInfo:"+(getPlayerEntity(username).capabilities.isCreativeMode ? 0 : 1));
    }
}
