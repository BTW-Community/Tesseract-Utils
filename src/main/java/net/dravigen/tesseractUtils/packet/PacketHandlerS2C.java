package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload;

import net.dravigen.tesseractUtils.TesseractUtilsAddon.*;

import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.*;
import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

public class PacketHandlerS2C {

    /**
     * Handles incoming custom payload packets from the server.
     * This method is called from NetClientHandlerMixin.
     *
     * @param packet The received custom payload packet.
     */
    public static void handle(Packet250CustomPayload packet) {
        if (packet.channel.equals(TUChannels.SERVER_TO_CLIENT_CHANNEL)) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
                DataInputStream dis = new DataInputStream(bis);

                String receivedMessage = dis.readUTF();

                // --- DO STUFF ON CLIENT HERE ---
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayer player = mc.thePlayer;
                String[] splitText = receivedMessage.split(":");
                String subChannel = splitText[0];
                String property="";
                if (splitText.length==2){
                    property = splitText[1];
                }
                switch (subChannel) {
                    case "isPermanent" -> PacketUtils.isLookedAtEntityPermanentClient = Boolean.parseBoolean(property);
                    case "isPlayerOP" -> PacketUtils.isPlayerOPClient = Boolean.parseBoolean(property);
                    case "updatePos1" -> {
                        String[] coords = property.split(",");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        int z = Integer.parseInt(coords[2]);
                        setBlock1(x, y, z);
                    }
                    case "updatePos2" -> {
                        String[] coords = property.split(",");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        int z = Integer.parseInt(coords[2]);
                        setBlock2(x, y, z);
                    }
                    case "clearPos" -> clear();
                    case "mobCapUpdate" -> PacketUtils.mobCapInfosClient = property.split(",");
                    case "updatePlayerInfo" -> PacketSender.sendClientToServerMessage("updatePlayerInfo:" + PacketUtils.playerInfoClient(Integer.parseInt(property)));
                    case "updateMode" -> {
                        int mode = Integer.parseInt(property);
                        if (mode==1&&TesseractUtilsAddon.currentBuildingMode!=8){
                            TesseractUtilsAddon.currentBuildingMode=8;
                        }
                        TesseractUtilsAddon.modeState=mode;
                        if (mode == 2) {
                            player.onGround = false;
                            player.capabilities.isFlying = true;
                            player.noClip = true;
                        } else player.noClip = false;
                    }
                    case "updateAllPos"->{
                        int x1 = (int)block1.xCoord;
                        int y1 = (int)block1.yCoord;
                        int z1 = (int)block1.zCoord;
                        int x2 = (int)block2.xCoord;
                        int y2 = (int)block2.yCoord;
                        int z2 = (int)block2.zCoord;
                        PacketSender.sendClientToServerMessage("updateAllPos:" + x1 + "," + y1 + "," + z1+","+x2 + "," + y2 + "," + z2 + "," + property);
                    }
                    case "sendNamesLists"->{
                        initAllClientList();

                        PacketSender.sendClientToServerMessage("getBlocksNameList:"+ blocksMap);
                        PacketSender.sendClientToServerMessage("getItemsNameList:"+ itemsMap);

                        //PacketSender.sendClientToServerMessage("getEntitiesNameList:"+ entitiesMap);
                        //PacketSender.sendClientToServerMessage("getPotionsNameList:"+ potionsMap);
                        //PacketSender.sendClientToServerMessage("getEnchantNameList:" + enchantMap);
                    }
                    case "haveTU"-> {
                        PacketUtils.serverHaveTU=true;
                        PacketSender.sendClientToServerMessage("haveTU");
                    }
                }

            } catch (Exception e) {
                System.err.println("CLIENT: Error handling S2C message packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
