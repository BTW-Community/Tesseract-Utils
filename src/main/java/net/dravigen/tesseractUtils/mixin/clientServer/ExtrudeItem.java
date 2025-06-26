package net.dravigen.tesseractUtils.mixin.clientServer;

import btw.item.items.ShovelItem;
import btw.item.items.ToolItem;
import net.dravigen.tesseractUtils.command.UtilsCommand;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.command.UtilsCommand.*;
import static net.dravigen.tesseractUtils.command.UtilsCommand.findConnectedBlocksInPlane;
import static net.dravigen.tesseractUtils.command.UtilsCommand.undoSaved;
import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(ShovelItem.class)
public abstract class ExtrudeItem extends ToolItem {

    protected ExtrudeItem(int iITemID, int iBaseEntityDamage, EnumToolMaterial par3EnumToolMaterial) {
        super(iITemID, iBaseEntityDamage, par3EnumToolMaterial);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ) {
        if (stack.itemID==Item.shovelWood.itemID&&player.capabilities.isCreativeMode) {
            return false;
        }else return super.onItemUse(stack,player,world,i,j,k,iFacing,fClickX,fClickY,fClickZ);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World world, int iBlockID, int i, int j, int k, EntityLivingBase entity) {
        if (itemStack.itemID==Item.shovelWood.itemID) {
            if (!(entity instanceof EntityPlayer player))return super.onBlockDestroyed(itemStack,world,iBlockID,i,j,k,entity);
            if (!player.capabilities.isCreativeMode)return super.onBlockDestroyed(itemStack,world,iBlockID,i,j,k,entity);
            List<SavedBlock> list = new ArrayList<>();
            MovingObjectPosition mop = getBlockPlayerIsLooking(player);
            if (mop == null || mop.typeOfHit != EnumMovingObjectType.TILE) {
                player.addChatMessage("§cYou must look at a block!");
                return false;
            }
            int clickedX = mop.blockX;
            int clickedY = mop.blockY;
            int clickedZ = mop.blockZ;
            int clickedFace = mop.sideHit;
            int referenceBlockId = world.getBlockId(clickedX, clickedY, clickedZ);
            int referenceBlockMeta = world.getBlockMetadata(clickedX, clickedY, clickedZ);

            List<BlockPos> connectedBlocks = findConnectedBlocksInPlane(world, clickedX, clickedY, clickedZ, referenceBlockId, referenceBlockMeta, clickedFace,(boolean)FUZZY_EXTRUDER.getValue());

            if (connectedBlocks.isEmpty()) {
                player.addChatMessage("§cNo connected blocks of the same type found.");
                return false;
            }
            int blocksPlaced = 0;
            for (BlockPos blockPos : connectedBlocks) {
                int newBlockX = blockPos.x;
                int newBlockY = blockPos.y;
                int newBlockZ = blockPos.z;
                int targetBlockId = world.getBlockId(newBlockX, newBlockY, newBlockZ);
                int targetBlockMeta = world.getBlockMetadata(newBlockX, newBlockY, newBlockZ);
                if (targetBlockId != 0) {
                    list.add(new UtilsCommand.SavedBlock(newBlockX, newBlockY, newBlockZ, targetBlockId, targetBlockMeta));
                    world.setBlockToAir(newBlockX, newBlockY, newBlockZ);
                    blocksPlaced++;
                }
            }
            if (blocksPlaced > 0) {
                undoSaved.add(list);
                player.addChatMessage("§d" + blocksPlaced + " block(s) have been placed");
            } else {
                player.addChatMessage("§cNo blocks could be placed.");
            }
        }return super.onBlockDestroyed(itemStack,world,iBlockID,i,j,k,entity);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (itemStack.itemID==Item.shovelWood.itemID&&player.capabilities.isCreativeMode) {
            if (world.isRemote) {
                return itemStack;
            }
            List<SavedBlock> list = new ArrayList<>();
            MovingObjectPosition mop = getBlockPlayerIsLooking(player);
            if (mop == null || mop.typeOfHit != EnumMovingObjectType.TILE) {
                player.addChatMessage("§cYou must look at a block!");
                return itemStack;
            }
            int blockIdToPlace = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
            int blockMetaToPlace = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
            int clickedX = mop.blockX;
            int clickedY = mop.blockY;
            int clickedZ = mop.blockZ;
            int clickedFace = mop.sideHit;
            int referenceBlockId = world.getBlockId(clickedX, clickedY, clickedZ);
            int referenceBlockMeta = world.getBlockMetadata(clickedX, clickedY, clickedZ);

            if (referenceBlockId == 0) {
                player.addChatMessage("§cCannot extend from air!");
                return itemStack;
            }
            List<BlockPos> connectedBlocks = findConnectedBlocksInPlane(world, clickedX, clickedY, clickedZ, referenceBlockId, referenceBlockMeta, clickedFace,(boolean)FUZZY_EXTRUDER.getValue());

            if (connectedBlocks.isEmpty()) {
                player.addChatMessage("§cNo connected blocks of the same type found.");
                return itemStack;
            }
            int blocksPlaced = 0;
            for (BlockPos blockPos : connectedBlocks) {
                int newBlockX = blockPos.x;
                int newBlockY = blockPos.y;
                int newBlockZ = blockPos.z;
                switch (clickedFace) {
                    case 0:
                        newBlockY--;
                        break; // Bottom face: place below
                    case 1:
                        newBlockY++;
                        break; // Top face: place above
                    case 2:
                        newBlockZ--;
                        break; // North face: place North
                    case 3:
                        newBlockZ++;
                        break; // South face: place South
                    case 4:
                        newBlockX--;
                        break; // West face: place West
                    case 5:
                        newBlockX++;
                        break; // East face: place East
                }
                int targetBlockId = world.getBlockId(newBlockX, newBlockY, newBlockZ);
                if (targetBlockId == 0) {
                    list.add(new UtilsCommand.SavedBlock(newBlockX, newBlockY, newBlockZ, targetBlockId, 0));
                    world.setBlock(newBlockX, newBlockY, newBlockZ, blockIdToPlace, blockMetaToPlace, 2);
                    blocksPlaced++;
                }
            }
            if (blocksPlaced > 0) {
                undoSaved.add(list);
                player.addChatMessage("§d" + blocksPlaced + " block(s) have been placed");
            } else {
                player.addChatMessage("§cNo blocks could be placed.");
            }
        }
        return itemStack;
    }
}
