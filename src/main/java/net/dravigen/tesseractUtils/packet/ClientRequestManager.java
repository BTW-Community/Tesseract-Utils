package net.dravigen.tesseractUtils.packet;

import net.dravigen.tesseractUtils.utils.interfaces.IClientStatusCallback;
import net.minecraft.src.EntityPlayerMP; // This is the server-side player object

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientRequestManager {

    private static final Map<UUID, Map<Integer, IClientStatusCallback>> pendingCallbacks = new HashMap<>();

    private static final Map<UUID, Integer> nextRequestIds = new HashMap<>();

    /**
     * Generates a unique request ID (ticket number) for a player.
     * @param playerUUID The player's unique ID.
     * @return A new, unique request ID.
     */
    private static int generateRequestId(UUID playerUUID) {
        Integer currentId = nextRequestIds.getOrDefault(playerUUID, 0);
        currentId++;
        nextRequestIds.put(playerUUID, currentId);
        return currentId;
    }

    /**
     * 1. This method is called by your server code when you send a packet to the client
     * and expect a reply.
     * 2. It generates a unique request ID.
     * 3. It stores your "what to do next" code (the callback) linked to that ID.
     *
     * @param player The player to whom you're sending the request.
     * @param callback The code you want to run when the client responds.
     * @return The unique request ID you MUST send to the client.
     */
    public static int registerStatusCallback(EntityPlayerMP player, IClientStatusCallback callback) {
        int requestId = generateRequestId(player.getUniqueID());

        Map<Integer, IClientStatusCallback> playerCallbacks =
                pendingCallbacks.computeIfAbsent(player.getUniqueID(), k -> new HashMap<>());

        playerCallbacks.put(requestId, callback);

        return requestId;
    }

    /**
     * This method is called by your **server-side packet handler** when a client's
     * response packet arrives.
     *
     * @param player    The player who sent the response.
     * @param requestId The request ID (ticket number) from the client's packet.
     */
    public static void executeUpdatedPosCallback(EntityPlayerMP player, int requestId, int[] block1, int[] block2) {
        Map<Integer, IClientStatusCallback> playerCallbacks = pendingCallbacks.get(player.getUniqueID());
        if (playerCallbacks != null) {
            IClientStatusCallback callback = playerCallbacks.remove(requestId);

            if (callback != null) {
                callback.onClientUpdateReceived(block1,block2);
                if (playerCallbacks.isEmpty()) {
                    pendingCallbacks.remove(player.getUniqueID());
                }
            }
        }
    }

    /**
     * Cleans up any leftover "notes" for a player (called when they log out).
     * This prevents memory leaks.
     * @param playerUUID The UUID of the player.
     */
    public static void cleanupPlayerRequests(UUID playerUUID) {
        pendingCallbacks.remove(playerUUID);
        nextRequestIds.remove(playerUUID);
    }
}