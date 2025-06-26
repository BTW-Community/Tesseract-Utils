package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet250CustomPayload;

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

                String receivedMessage = dis.readUTF(); // Read the string message
                // --- STUFF ON SERVER HERE ---
                String[] splitText = receivedMessage.split(":");
                int entityID;
                try {
                    entityID = Integer.parseInt(splitText[1]);
                } catch (Exception ignored) {
                    return;
                }
                if (splitText[0].equalsIgnoreCase("isEntityPermanent")) {
                   Entity entity = player.worldObj.getEntityByID(entityID);
                   if (entity instanceof EntityLiving living){
                       boolean canDespawn;
                       try {
                           Method canDespawnMethod = living.getClass().getDeclaredMethod("canDespawn");
                           canDespawnMethod.setAccessible(true);
                           canDespawn = (boolean)canDespawnMethod.invoke(living);
                       } catch (Exception e) {
                           try {
                               Method canDespawnMethod = EntityLiving.class.getDeclaredMethod("canDespawn");
                               canDespawnMethod.setAccessible(true);
                               canDespawn = (boolean) canDespawnMethod.invoke(entity);
                           }catch (Exception ex) {
                               canDespawn=true;
                           }
                       }
                       PacketSender.sendServerToClientMessage(player, living.isNoDespawnRequired()||!canDespawn);
                   }
                }

            } catch (IOException e) {
                System.err.println("SERVER: Error handling C2S message packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
