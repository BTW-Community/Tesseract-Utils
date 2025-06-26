package net.dravigen.tesseractUtils.mixin.server;

import btw.item.items.RedstoneItem;
import net.dravigen.tesseractUtils.TesseractUtilsAddon.TUChannels;
import net.dravigen.tesseractUtils.command.CommandWorldEdit;
import net.dravigen.tesseractUtils.packet.PacketHandlerC2S;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import static net.dravigen.tesseractUtils.command.UtilsCommand.*;
import static net.dravigen.tesseractUtils.configs.EnumConfig.*;


@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin {

    @Shadow public EntityPlayerMP playerEntity;

    @Inject(method = "getCollidingBoundingBoxesIgnoreSpecifiedEntities", at = @At("RETURN"), cancellable = true)
    private void disableCollision(World world, Entity entity, AxisAlignedBB par2AxisAlignedBB, CallbackInfoReturnable<List<AxisAlignedBB>> cir) {
        if ((boolean) NO_CLIP.getValue() && entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
            cir.setReturnValue(new ArrayList<>());
        }
    }

    @Redirect(method = "handleBlockDig", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isBlockProtected(Lnet/minecraft/src/World;IIILnet/minecraft/src/EntityPlayer;)Z"))
    private boolean disableBreak(MinecraftServer instance, World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer) {
        ItemStack heldItem = par5EntityPlayer.inventory.getCurrentItem();
        if (par5EntityPlayer.capabilities.isCreativeMode && heldItem != null) {
            int id = heldItem.itemID;
            if (id==Item.shovelWood.itemID)heldItem.getItem().onBlockDestroyed(heldItem,par1World,0,0,0,0,par5EntityPlayer);
            return id == Item.shovelWood.itemID  || id == Item.axeWood.itemID || id == Item.swordWood.itemID || (this.playerEntity.getHeldItem().getTagCompound()!=null&&this.playerEntity.getHeldItem().getTagCompound().hasKey("BuildingParams"));
        } else return false;
    }

    @ModifyConstant(method = "handleBlockDig", constant = @Constant(doubleValue = 36.0))
    private double disableBreakDistanceLimit(double constant) {
        ItemStack heldItem = this.playerEntity.getHeldItem();
        if (heldItem != null) {
            int id = heldItem.itemID;
            if (id ==Item.swordWood.itemID||id==Item.axeWood.itemID||id==Item.shovelWood.itemID||(heldItem.getTagCompound()!=null&& heldItem.getTagCompound().hasKey("BuildingParams")))
                return 999999;
        }
        if (this.playerEntity.capabilities.isCreativeMode && (int)REACH.getValue() > 5) {
            return 999999;
        } else return constant;
    }

    @Inject(method = "handleUseEntity",at = @At("HEAD"), cancellable = true)
    private void clickEntity(Packet7UseEntity packet7UseEntity, CallbackInfo ci) {
        ItemStack heldItem = this.playerEntity.getHeldItem();
        if (heldItem != null&&this.playerEntity.capabilities.isCreativeMode) {
            Entity entity = this.playerEntity.worldObj.getEntityByID(packet7UseEntity.targetEntity);
            if (entity != null) {
                if (heldItem.itemID == Item.swordWood.itemID) {
                    entity.setDead();
                    ci.cancel();
                } else if (heldItem.itemID == Item.hoeWood.itemID) {
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
                            } catch (Exception ex) {
                                canDespawn=true;
                            }
                        }
                        if (!canDespawn||living instanceof EntityWither||living instanceof EntityDragon) {
                            playerEntity.sendChatToPlayer(ChatMessageComponent.createFromText(living.getTranslatedEntityName() + " already cannot despawn"));
                        }else {
                            living.setPersistent(!living.isNoDespawnRequired());
                            PacketSender.sendServerToClientMessage(this.playerEntity, living.isNoDespawnRequired());
                            playerEntity.sendChatToPlayer(ChatMessageComponent.createFromText(living.getTranslatedEntityName() + (living.isNoDespawnRequired() ? " is now a permanent mob" : " is now able to despawn")));
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "handleUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D"))
    private double disableDistanceLimitUseEntity(EntityPlayerMP instance, Entity entity) {
        if (this.playerEntity.capabilities.isCreativeMode) {
            if (instance.getHeldItem() != null) {
                if (instance.getHeldItem().itemID == Item.swordWood.itemID||instance.getHeldItem().itemID ==Item.hoeWood.itemID) {
                    return 0;
                }
                if ((int) REACH.getValue() > 5) {
                    return 0;
                }
            }
        }
        return this.playerEntity.getDistanceSqToEntity(entity);
    }

    @Redirect(method = "handlePlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;getDistanceSq(DDD)D"))
    private double disableDistanceLimitPlace(EntityPlayerMP instance, double x, double y, double z) {
        if (this.playerEntity.capabilities.isCreativeMode && (int)REACH.getValue() > 5) {
            return 0;
        } else return this.playerEntity.getDistanceSq(x, y, z);
    }

    @Inject(method = "handleBlockDig",at = @At("TAIL"))
    private void worldEditTool(Packet14BlockDig packet, CallbackInfo ci) {
        ItemStack heldItem = this.playerEntity.getHeldItem();
        if (heldItem != null ) {
            NBTTagCompound nbt = heldItem.getTagCompound();
            if (nbt != null && nbt.hasKey("BuildingParams")) {
                NBTTagCompound buildingParamsNBT = nbt.getCompoundTag("BuildingParams");
                boolean replace = buildingParamsNBT.getString("actionType").equalsIgnoreCase("replace");
                String shape = buildingParamsNBT.getString("shape");
                String[] parameters = buildingParamsNBT.getString("parameters").split(":");
                String blockUsed = buildingParamsNBT.getString("blockUsed");
                String toolHollowOrOpen = buildingParamsNBT.getString("volume");
                List<SavedBlock> list = new ArrayList<>();
                switch (shape) {
                    case "sphere" -> {
                        int var1 = Integer.parseInt(parameters[0]);
                        int var2 = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                        if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                            list = CommandWorldEdit.generateHollowSphere(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, replace);
                        else
                            list = CommandWorldEdit.generateSphere(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, replace);
                    }
                    case "cylinder" -> {
                        int var1 = Integer.parseInt(parameters[0]);
                        int var2 = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                        int var3 = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                        if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                            list = CommandWorldEdit.generateHollowCylinder(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, var3, replace);
                        else if (toolHollowOrOpen.equalsIgnoreCase("open"))
                            list = CommandWorldEdit.generateOpenCylinder(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, var3, replace);
                        else
                            list = CommandWorldEdit.generateCylinder(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, replace);
                    }
                    case "cube" -> {
                        int var1 = Integer.parseInt(parameters[0]);
                        int var2 = parameters.length > 1 ? Integer.parseInt(parameters[1]) : 1;
                        int var3 = parameters.length > 2 ? Integer.parseInt(parameters[2]) : 1;
                        int var4 = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                        if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                            list = CommandWorldEdit.generateHollowCube(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, var3, var4, replace);
                        else if (toolHollowOrOpen.equalsIgnoreCase("open"))
                            list = CommandWorldEdit.generateOpenCube(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, var3, var4, replace);
                        else
                            list = CommandWorldEdit.generateCube(playerEntity.worldObj, packet.xPosition, packet.yPosition, packet.zPosition, blockUsed, 2, var1, var2, var3, replace);
                    }
                }
                if (!list.isEmpty()) {
                    redoSaved.clear();
                    undoSaved.add(list);
                }
            }
        }
    }

    @Redirect(method = "handlePlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemInWorldManager;activateBlockOrUseItem(Lnet/minecraft/src/EntityPlayer;Lnet/minecraft/src/World;Lnet/minecraft/src/ItemStack;IIIIFFF)Z"))
    private boolean replaceInsteadOfPlacing(ItemInWorldManager instance, EntityPlayer player, World world, ItemStack itemStack, int x, int y, int z, int side, float offX, float offY, float offZ) {
        if (this.playerEntity.capabilities.isCreativeMode&& itemStack != null) {
            if ((boolean)CLICK_REPLACE.getValue() && !this.playerEntity.isSneaking()  && (itemStack.getItem() instanceof ItemBlock || itemStack.getItem() instanceof RedstoneItem)) {
                world.setBlock(x, y, z, 0, 0, 2);
            }
        }return instance.activateBlockOrUseItem(this.playerEntity, world, itemStack, x, y, z, side, offX, offY, offZ);
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