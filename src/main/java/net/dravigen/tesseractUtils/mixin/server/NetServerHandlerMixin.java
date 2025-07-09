package net.dravigen.tesseractUtils.mixin.server;

import net.dravigen.tesseractUtils.TesseractUtilsAddon.TUChannels;
import net.dravigen.tesseractUtils.enums.EnumBuildMode;
import net.dravigen.tesseractUtils.packet.PacketHandlerC2S;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.enums.EnumConfig.*;


@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin {

    @Shadow public EntityPlayerMP playerEntity;

    @Shadow @Final public MinecraftServer mcServer;

    @Inject(method = "getCollidingBoundingBoxesIgnoreSpecifiedEntities", at = @At("RETURN"), cancellable = true)
    private void disableCollision(World world, Entity entity, AxisAlignedBB par2AxisAlignedBB, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        if (entity instanceof EntityPlayer && isPlayerOpped() && PacketUtils.playersGamemodeServer.get(playerEntity.getEntityName())==2) {
            cir.setReturnValue(new ArrayList<>());
        }
    }

    @Unique
    private boolean isPlayerOpped() {
        return this.mcServer.getConfigurationManager().isPlayerOpped(this.playerEntity.getEntityName());
    }

    @Redirect(method = "handleBlockDig", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isBlockProtected(Lnet/minecraft/src/World;IIILnet/minecraft/src/EntityPlayer;)Z"))
    private boolean disableBreak(MinecraftServer instance, World par1World, int x, int y, int z, EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (player.capabilities.isCreativeMode&& isPlayerOpped() && heldItem != null) {
            int id = heldItem.itemID;
            if (id==Item.shovelWood.itemID||id==Item.axeWood.itemID)heldItem.getItem().onBlockDestroyed(heldItem,par1World,par1World.getBlockId(x,y,z),x,y,z,player);
            return id == Item.shovelWood.itemID  || id == Item.axeWood.itemID || id == Item.swordWood.itemID || (!playerEntity.isSneaking()&&heldItem.getTagCompound()!=null&&heldItem.getTagCompound().hasKey("BuildingParams")&&!this.playerEntity.isSneaking());
        } else {
            int mode = PacketUtils.playersBuildModeServer.get(playerEntity.getEntityName());
            return mode!= 8 && mode!=EnumBuildMode.REPLACE_MODE.getIndex();
        }
    }

    @ModifyConstant(method = "handleBlockDig", constant = @Constant(doubleValue = 36.0))
    private double disableBreakDistanceLimit(double constant) {
        if (this.playerEntity.capabilities.isCreativeMode&& isPlayerOpped()) {
            ItemStack heldItem = this.playerEntity.getHeldItem();
            if (heldItem != null) {
                int id = heldItem.itemID;
                if (id == Item.swordWood.itemID || id == Item.hoeWood.itemID || id == Item.axeWood.itemID || id == Item.shovelWood.itemID || (!playerEntity.isSneaking()&&heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams")))
                    return 0;
            }
            List<String> playerConfigs = PacketUtils.playersInfoServer.get(playerEntity.getEntityName());
            int buildMode = PacketUtils.playersBuildModeServer.get(playerEntity.getEntityName());
            if (buildMode!=8&&buildMode!=EnumBuildMode.REPLACE_MODE.getIndex())return 0;
            if (!playerConfigs.isEmpty()&&Integer.parseInt(playerConfigs.get(REACH.ordinal())) > 5) {
                return 999999;
            }
        }
        return constant;
    }

    @Redirect(method = "handleUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D"))
    private double disableDistanceLimitUseEntity(EntityPlayerMP instance, Entity entity) {
        if (this.playerEntity.capabilities.isCreativeMode&& isPlayerOpped()) {
            if (instance.getHeldItem() != null) {
                ItemStack heldItem = instance.getHeldItem();
                int id = heldItem.itemID;
                if (id == Item.swordWood.itemID || id == Item.hoeWood.itemID || id == Item.axeWood.itemID || id == Item.shovelWood.itemID || (heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams"))) {
                    return 999999;
                }
            }
            int buildMode = PacketUtils.playersBuildModeServer.get(playerEntity.getEntityName());
            if (buildMode!=8&&buildMode!=EnumBuildMode.REPLACE_MODE.getIndex())return 999999;
        }
        return this.playerEntity.getDistanceSqToEntity(entity);
    }

    @Redirect(method = "handlePlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSq(DDD)D"))
    private double disableDistanceLimitPlace(EntityPlayerMP instance, double x, double y, double z) {
        if (this.playerEntity.capabilities.isCreativeMode&& isPlayerOpped()) {
            if (instance.getHeldItem() != null) {
                ItemStack heldItem = instance.getHeldItem();
                int id = heldItem.itemID;
                if (id == Item.swordWood.itemID || id == Item.hoeWood.itemID || id == Item.axeWood.itemID || id == Item.shovelWood.itemID || (!playerEntity.isSneaking()&&heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey("BuildingParams"))) {
                    return 999999;
                }
            }
            List<String> playerConfigs = PacketUtils.playersInfoServer.get(playerEntity.getEntityName());
            int buildMode = PacketUtils.playersBuildModeServer.get(playerEntity.getEntityName());
            if (buildMode!=8&&buildMode!=EnumBuildMode.REPLACE_MODE.getIndex())return 999999;
            if (!playerConfigs.isEmpty()&&Integer.parseInt(playerConfigs.get(REACH.ordinal())) > 5) {
                return 0;
            }
        }
        return this.playerEntity.getDistanceSq(x, y, z);
    }

    @ModifyConstant(method = "handleChat",constant = @Constant(intValue = 100))
    private int increaseChatLimit(int constant){
        return 512;
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void tu_onCustomPayloadC2S(Packet250CustomPayload packet, CallbackInfo ci) {
        if (packet.channel.equals(TUChannels.CLIENT_TO_SERVER_CHANNEL)) {
            PacketHandlerC2S.handle(packet, this.playerEntity);
            ci.cancel();
        }
    }

}