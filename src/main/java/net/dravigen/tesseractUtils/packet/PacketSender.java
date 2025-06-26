package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayerMP; // Server-side player
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload; // The custom packet class

import net.dravigen.tesseractUtils.TesseractUtilsAddon.*;

public class PacketSender {

    /**
     * Sends a custom string message packet from Client to Server.
     * This method MUST be called on the CLIENT side.
     *
     * @param message The string message to send.
     */
    public static void sendClientToServerMessage(String message) {
        // Ensure this is client-side code
        if (!Minecraft.getMinecraft().theWorld.isRemote) {
            System.err.println("Attempted to send C2S packet from server-side!");
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeUTF(message); // Write the string message

            Packet250CustomPayload packet = new Packet250CustomPayload(TUChannels.CLIENT_TO_SERVER_CHANNEL, bos.toByteArray());

            // Add the packet to the client's send queue
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
            System.out.println("CLIENT: Sent C2S message: " + message);

        } catch (IOException e) {
            System.err.println("CLIENT: Error sending C2S message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a custom string message packet from Server to a specific Client.
     * This method MUST be called on the SERVER side.
     *
     * @param player The player to send the message to.
     * @param message The string message to send.
     */
    public static void sendServerToClientMessage(EntityPlayerMP player, boolean message) {
        // Ensure this is server-side code
        if (player.worldObj.isRemote) {
            System.err.println("Attempted to send S2C packet from client-side!");
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeUTF(String.valueOf(message)); // Write the string message

            Packet250CustomPayload packet = new Packet250CustomPayload(TUChannels.SERVER_TO_CLIENT_CHANNEL, bos.toByteArray());
            // Send the packet to the specified player
            player.playerNetServerHandler.sendPacketToPlayer(packet);
            System.out.println("SERVER: Sent S2C message to " + player.getCommandSenderName() + ": " + message);

        } catch (IOException e) {
            System.err.println("SERVER: Error sending S2C message to " + player.getCommandSenderName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
