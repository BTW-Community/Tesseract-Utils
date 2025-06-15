package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TessUConfig;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "runTick",at = @At("HEAD"))
    private void disableRightClickCooldown(CallbackInfo ci){
        if (this.playerController!=null&&this.playerController.isInCreativeMode()){
            this.rightClickDelayTimer = TessUConfig.disablePlaceCooldown ? 0 : this.rightClickDelayTimer;
        }
        if (listLanguage!= Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()){
            itemNameList.clear();
            entityShowNameList.clear();
            blocksNameList.clear();
        }
        if (itemNameList.isEmpty()||entityShowNameList.isEmpty()||blocksNameList.isEmpty()) {
            initItemsNameList();
            initEntityList();
            initBlocksNameList();
            listLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        }
    }
}