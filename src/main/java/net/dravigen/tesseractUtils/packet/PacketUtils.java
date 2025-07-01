package net.dravigen.tesseractUtils.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class PacketUtils {

    @Environment(EnvType.CLIENT)
    public static boolean isLookedAtEntityPermanentClientSide = false;

    @Environment(EnvType.CLIENT)
    public static boolean isPlayerOP = false;
}
