package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TessUConfig;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public PlayerControllerMP playerController;
    @Shadow private int rightClickDelayTimer;

    @Inject(method = "runTick",at = @At("HEAD"))
    private void disableRightClickCooldown(CallbackInfo ci){
        if (this.playerController!=null&&this.playerController.isInCreativeMode()){
            this.rightClickDelayTimer = TessUConfig.disablePlaceCooldown ? 0 : this.rightClickDelayTimer;
        }

    }

}