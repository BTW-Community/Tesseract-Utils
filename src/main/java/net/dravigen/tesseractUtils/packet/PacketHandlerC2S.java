package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.dravigen.tesseractUtils.TesseractUtilsAddon.*;


public class PacketHandlerC2S {

    /**
     * Handles incoming custom payload packets from clients.
     * This method is called from NetServerHandlerMixin and MUST run on the Server Thread.
     *
     * @param packet The received custom payload packet.
     * @param player The player who sent the packet.
     */
    public static void handle(Packet250CustomPayload packet, EntityPlayerMP player) {
        if (packet.channel.equals(TUChannels.CLIENT_TO_SERVER_CHANNEL)) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
                DataInputStream dis = new DataInputStream(bis);

                String receivedMessage = dis.readUTF();

                // --- STUFF ON SERVER HERE ---

                MinecraftServer server = MinecraftServer.getServer();

                String[] splitText = receivedMessage.split(":");

                String subChannel = splitText[0];
                final String property = splitText[1];
                switch (subChannel){
                    case "isEntityPermanent" -> {
                        int entityID;
                        try {
                            entityID = Integer.parseInt(property);
                        } catch (Exception ignored) {
                            return;
                        }
                        Entity entity = player.worldObj.getEntityByID(entityID);
                        if (entity instanceof EntityLiving living) {
                            boolean canDespawn;
                            try {
                                Method canDespawnMethod = living.getClass().getDeclaredMethod("canDespawn");
                                canDespawnMethod.setAccessible(true);
                                canDespawn = (boolean) canDespawnMethod.invoke(living);
                            } catch (Exception e) {
                                try {
                                    Method canDespawnMethod = EntityLiving.class.getDeclaredMethod("canDespawn");
                                    canDespawnMethod.setAccessible(true);
                                    canDespawn = (boolean) canDespawnMethod.invoke(entity);
                                } catch (Exception ex) {
                                    canDespawn = true;
                                }
                            }
                            PacketSender.sendServerToClientMessage(player, "isPermanent:" + (living.isNoDespawnRequired() || !canDespawn));
                        }
                    }
                    case "isPlayerOP" -> PacketSender.sendServerToClientMessage(player,"isPlayerOP:"+server.getConfigurationManager().isPlayerOpped(property));
                    case "updateModeState" -> {
                        int modeState = Integer.parseInt(property);
                        player.setGameType(modeState == 1 ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
                        if (modeState==2)player.capabilities.isFlying=true;
                        if (modeState == 1){
                            player.fallDistance=0;
                        }

                    }

                }
            } catch (IOException e) {
                System.err.println("SERVER: Error handling C2S message packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
