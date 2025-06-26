package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.configs.EnumConfig;
import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.dravigen.tesseractUtils.TesseractUtilsAddon.listLanguage;
import static net.dravigen.tesseractUtils.command.UtilsCommand.*;
import static net.dravigen.tesseractUtils.command.UtilsCommand.initEntityList;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public PlayerControllerMP playerController;
    @Shadow private int rightClickDelayTimer;
    @Unique long prevTime;
    @Unique int delay;

    @Inject(method = "runTick",at = @At("HEAD"))
    private void tick(CallbackInfo ci) {

        if (delay==0){
            TesseractUtilsAddon.tps = System.currentTimeMillis() - prevTime;
        }
        prevTime = System.currentTimeMillis();

    }
    @Inject(method = "runTick",at = @At("TAIL"))
    private void tick1(CallbackInfo ci) {
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
            this.rightClickDelayTimer = (boolean) EnumConfig.PLACING_COOLDOWN.getValue() ? 0 : this.rightClickDelayTimer;
        }
        if (listLanguage!= Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
            itemNameList.clear();
            entityShowNameList.clear();
            blocksNameList.clear();
            potionNameList.clear();
        }
        if (itemNameList.isEmpty()||entityShowNameList.isEmpty()||blocksNameList.isEmpty()||potionNameList.isEmpty()) {
            initItemsNameList();
            initEntityList();
            initPotionList();
            initBlocksNameList();
            listLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        }
    }
}