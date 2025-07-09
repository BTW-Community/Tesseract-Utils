package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.configs.BlockSelectionManager;
import net.dravigen.tesseractUtils.enums.EnumBuildMode;
import net.dravigen.tesseractUtils.utils.RayTracingUtils;
import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.checkedOP;
import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.block1;
import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.block2;
import static net.dravigen.tesseractUtils.utils.RayTracingUtils.intersectAABB;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public PlayerControllerMP playerController;
    @Shadow private int rightClickDelayTimer;
    @Shadow public EntityClientPlayerMP thePlayer;
    @Shadow public GuiScreen currentScreen;

    @Shadow protected abstract void clickMiddleMouseButton();

    @Unique long prevTime;
    @Unique int delay;

    @Inject(method = "runTick",at = @At("HEAD"))
    private void tickHead(CallbackInfo ci) {

        if (delay==0){
            TesseractUtilsAddon.tps = System.currentTimeMillis() - prevTime;
        }
        prevTime = System.currentTimeMillis();

    }

    @Inject(method = "runTick",at = @At("TAIL"))
    private void tickTail(CallbackInfo ci) {
        if (delay==0){
            TesseractUtilsAddon.mspt = System.currentTimeMillis() - prevTime;
            delay=10;
        }
        prevTime = System.currentTimeMillis();
        delay--;

    }

    @Inject(method = "runTick",at = @At("HEAD"))
    private void disableRightClickCooldown(CallbackInfo ci){
        if (this.playerController!=null&&this.playerController.isInCreativeMode()){
            this.rightClickDelayTimer = EnumConfig.PLACING_COOLDOWN.getBoolValue() ? 0 : this.rightClickDelayTimer;
            if (this.rightClickDelayTimer!=0&&this.thePlayer!=null){
                if (this.thePlayer.getHeldItem()!=null){
                    this.rightClickDelayTimer = this.thePlayer.getHeldItem().itemID==Item.swordWood.itemID ? 0 : this.rightClickDelayTimer;
                }
            }
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/src/WorldClient;)V",at = @At("TAIL"))
    private void clear(WorldClient world, CallbackInfo ci){
        if (world==null) {
            BlockSelectionManager.clear();
            TesseractUtilsAddon.currentBuildingMode = 8;
            checkedOP=false;
        }
    }

    @Redirect(method = "runTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Minecraft;clickMiddleMouseButton()V"))
    private void disableMiddleClickActionIfSelectionMode(Minecraft instance) {
        if (this.currentScreen == null && thePlayer != null && PacketUtils.isPlayerOPClient && thePlayer.capabilities.isCreativeMode && EnumBuildMode.getEnumFromIndex(TesseractUtilsAddon.currentBuildingMode).getCanSelect()) {
            return;
        }
        clickMiddleMouseButton();
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/InventoryPlayer;changeCurrentItem(I)V") )
    private void preHandleMouseInput(InventoryPlayer instance, int par1) {
        if (this.currentScreen == null && Mouse.getEventDWheel() != 0 && thePlayer != null && PacketUtils.isPlayerOPClient && thePlayer.capabilities.isCreativeMode&& BlockSelectionManager.currentSelectionState== BlockSelectionManager.SelectionState.TWO_SELECTED&& EnumBuildMode.getEnumFromIndex(TesseractUtilsAddon.currentBuildingMode).getCanSelect()) {
            RayTracingUtils.HitResult hitResult;
            double minX = Math.min(block1.xCoord, block2.xCoord) - 0.01;
            double minY = Math.min(block1.yCoord, block2.yCoord) - 0.01;
            double minZ = Math.min(block1.zCoord, block2.zCoord) - 0.01;

            double maxX = Math.max(block1.xCoord, block2.xCoord) + 1.01;
            double maxY = Math.max(block1.yCoord, block2.yCoord) + 1.01;
            double maxZ = Math.max(block1.zCoord, block2.zCoord) + 1.01;
            EntityPlayer player = thePlayer;
            Vec3 rayOriginWorld = Vec3.createVectorHelper(
                    player.posX,
                    player.posY + player.getEyeHeight(),
                    player.posZ
            );
            Vec3 rayDirection = player.getLook(TesseractUtilsAddon.partialTick);
            if (player.isUsingSpecialKey()) {
                rayOriginWorld = Vec3.createVectorHelper(0, 0, 0);
                rayDirection.xCoord *= -1;
                rayDirection.yCoord *= -1;
                rayDirection.zCoord *= -1;
                hitResult = intersectAABB(rayOriginWorld, rayDirection, -1, -1, -1, 1, 1, 1);
            }else {
                if (rayOriginWorld.xCoord <= maxX && rayOriginWorld.xCoord >= minX && rayOriginWorld.yCoord <= maxY && rayOriginWorld.yCoord >= minY && rayOriginWorld.zCoord <= maxZ && rayOriginWorld.zCoord >= minZ) {
                    rayDirection.xCoord *= -1;
                    rayDirection.yCoord *= -1;
                    rayDirection.zCoord *= -1;
                }
                hitResult = intersectAABB(rayOriginWorld, rayDirection, minX, minY, minZ, maxX, maxY, maxZ);
            }
            if (hitResult.hit) {
                return;
            }
        }
        instance.changeCurrentItem(par1);
    }
}