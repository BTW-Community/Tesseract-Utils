package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.advanced_edit.BlockSelectionManager;
import net.dravigen.tesseractUtils.advanced_edit.EnumBuildMode;
import net.dravigen.tesseractUtils.command.UtilsCommand;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.packet.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Shadow
    protected abstract boolean isBlockTranslucent(int par1, int par2, int par3);

    @Shadow
    public abstract ItemStack getHeldItem();

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 0))
    private boolean onPushOutOfBlocks(EntityPlayerSP instance, int par1, int par2, int par3) {
        if ( NO_CLIP.getBoolValue() && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 1))
    private boolean onPushOutOfBlocks1(EntityPlayerSP instance, int par1, int par2, int par3) {
        if (NO_CLIP.getBoolValue() && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Unique
    private static boolean keyPressed = false;
    @Unique
    private static boolean rightClickPressed = false;
    @Unique
    private static boolean leftClickPressed = false;
    @Unique
    private static boolean checkedOP = false;

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void update(CallbackInfo ci) {
        if (!checkedOP){
            PacketSender.sendClientToServerMessage("isPlayerOP:"+this.getEntityName());
            checkedOP=true;
        }
        Vec3 var3 = this.getPosition(1);
        Vec3 var4 = this.getLook(1);
        Vec3 var5 = var3.addVector(var4.xCoord * 256, var4.yCoord * 256, var4.zCoord * 256);
        MovingObjectPosition block = this.worldObj.clip(var3, var5);
        if (this.capabilities.isCreativeMode && PacketUtils.isPlayerOP) {
            if (((this.getHeldItem() != null && this.getHeldItem().itemID == Item.axeWood.itemID) || EnumBuildMode.getEnumFromIndex(TesseractUtilsAddon.currentBuildingMode).getCanSelect())) {
                if (Mouse.isButtonDown(1)) {
                    if (!rightClickPressed) {
                        if (block != null) {
                            int x = block.blockX;
                            int y = block.blockY;
                            int z = block.blockZ;
                            UtilsCommand.setCoord2(x, y, z);
                            if (this.worldObj.isRemote) {
                                this.addChatMessage("§dSecond position set to (" + x + ", " + y + ", " + z + ")");
                            }
                        } else if (this.isSneaking()) {
                            BlockSelectionManager.clear();
                            if (this.worldObj.isRemote) {
                                this.addChatMessage("§dSelections cleared");
                            }
                        }
                        rightClickPressed = true;
                    }
                } else rightClickPressed = false;
                if (Mouse.isButtonDown(0)) {
                    if (!leftClickPressed) {
                        if (block != null) {
                            int x = block.blockX;
                            int y = block.blockY;
                            int z = block.blockZ;
                            UtilsCommand.setCoord1(x, y, z);
                            if (this.mc.theWorld.isRemote) {
                                this.mc.thePlayer.addChatMessage("§dFirst position set to (" + x + ", " + y + ", " + z + ")");
                            }
                        }
                        leftClickPressed = true;
                    }
                } else leftClickPressed = false;
            }
            if (this.mc.currentScreen == null && !Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown((int) BAR_SWAP_KEY.getValue())) {
                if (!keyPressed) {
                    int windowId = this.inventoryContainer.windowId;
                    PlayerControllerMP controllerMP = this.mc.playerController;
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        for (int column = 0; column <= 8; column++) {
                            int hotbarSlot = 9 + column;
                            int row1Slot = 18 + column;
                            int row2Slot = 27 + column;
                            int row3Slot = 36 + column;
                            controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                            controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                        }
                    } else {
                        int current = this.inventory.currentItem;
                        int hotbarSlot = 9 + current;
                        int row1Slot = 18 + current;
                        int row2Slot = 27 + current;
                        int row3Slot = 36 + current;
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                    }
                    keyPressed = true;
                }
            } else keyPressed = false;
        }
    }
}
