package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.Packet250CustomPayload;

import net.dravigen.tesseractUtils.TesseractUtilsAddon.*;

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
                // Read the string message

                // --- DO STUFF ON CLIENT HERE ---

                TesseractUtilsAddon.isLookedAtEntityPermanentClientSide= Boolean.parseBoolean(dis.readUTF());

            } catch (IOException e) {
                System.err.println("CLIENT: Error handling S2C message packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
