package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TessUConfig;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow private Minecraft mc;

    @ModifyConstant(method = "getMouseOver",constant = @Constant(doubleValue = 6.0,ordinal = 0))
    private double disableDistanceLimit(double constant){
        if (this.mc.thePlayer.capabilities.isCreativeMode&& TessUConfig.reach>5){
            return 999999;
        }else return constant;
    }
}