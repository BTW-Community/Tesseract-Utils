package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.command.UtilsCommand;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.tesseractUtils.configs.EnumConfig.*;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow private EnumGameType currentGameType;
    @Shadow @Final private Minecraft mc;
    @Shadow private int blockHitDelay;

    @ModifyConstant(method = "getBlockReachDistance", constant = @Constant(floatValue = 5.0f))
    private float modifyCreativeReach(float constant) {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if (heldItem != null) {
            int id = heldItem.itemID;
            if (id ==Item.swordWood.itemID|| id ==Item.axeWood.itemID|| id ==Item.shovelWood.itemID||(heldItem.getTagCompound()!=null&& heldItem.getTagCompound().hasKey("BuildingParams")))
                return 128;
        }
        if (this.currentGameType.isCreative()) return (int)REACH.getValue();
        else return constant;
    }

    @Unique
    private long prevTime = System.currentTimeMillis()-1050;

    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"))
    private void selectionFirst(int x, int y, int z, int par4, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentGameType.isCreative()) {
            ItemStack heldItem = this.mc.thePlayer.getHeldItem();
            if (heldItem != null) {
                int id = heldItem.itemID;
                if (id == Item.axeWood.itemID) {
                    if (prevTime + 1000 < System.currentTimeMillis()) {
                        UtilsCommand.x1 = x;
                        UtilsCommand.y1 = y;
                        UtilsCommand.z1 = z;
                        if (this.mc.theWorld.isRemote) {
                            this.mc.thePlayer.addChatMessage("§dFirst position set to (" + x + ", " + y + ", " + z + ")");
                        }
                        prevTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"))
    private void selectionSecond(EntityPlayer entity, World world, ItemStack itemStack, int x, int y, int z, int par7, Vec3 par8Vec3, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentGameType.isCreative()) {
            if (itemStack != null && itemStack.itemID == Item.axeWood.itemID) {
                if (prevTime + 1000 > System.currentTimeMillis()) return;
                UtilsCommand.x2 = x;
                UtilsCommand.y2 = y;
                UtilsCommand.z2 = z;
                if (world.isRemote) {
                    entity.addChatMessage("§dSecond position set to (" + x + ", " + y + ", " + z + ")");
                }
                prevTime = System.currentTimeMillis();
            }
        }
    }

    @Redirect(method = "onPlayerDamageBlock",at = @At(value = "FIELD", target = "Lnet/minecraft/src/PlayerControllerMP;blockHitDelay:I",opcode = Opcodes.GETFIELD))
    private int disableBreakCooldown(PlayerControllerMP instance, int value){
        return this.currentGameType.isCreative()&& (boolean)BREAKING_COOLDOWN.getValue() ? 0 : this.blockHitDelay;
    }

    @Inject(method = "setGameType",at = @At("TAIL"))
     private void setGameType(EnumGameType par1EnumGameType, CallbackInfo ci){
         if (par1EnumGameType == EnumGameType.SURVIVAL && (boolean) NO_CLIP.getValue()){
             NO_CLIP.setValue(false);
             this.mc.thePlayer.noClip=false;
         }
     }

    @Inject(method = "updateController",at = @At("TAIL"))
    private void getModeState(CallbackInfo ci){
        EnumGameType type = this.currentGameType;
        TesseractUtilsAddon.modeState = type == EnumGameType.CREATIVE ?  ((boolean) NO_CLIP.getValue() ? 2 : 0) : type == EnumGameType.SURVIVAL ? 1 : 3;
    }

    /*
    @Inject(method = "func_78768_b",at = @At("HEAD"), cancellable = true)
    private void clickOnEntity(EntityPlayer playerEntity, Entity entity, CallbackInfoReturnable<Boolean> cir){
        ItemStack heldItem = playerEntity.getHeldItem();
        if (heldItem != null&&playerEntity.capabilities.isCreativeMode) {
            if (entity != null) {
                if (heldItem.itemID == Item.swordWood.itemID) {
                    entity.setDead();
                    cir.cancel();
                }
            }
        }
    }*/
}