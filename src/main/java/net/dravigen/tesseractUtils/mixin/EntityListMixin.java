package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.minecraft.src.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Inject(method = "addMapping(Ljava/lang/Class;Ljava/lang/String;I)V",at = @At("TAIL"))
    private static void getList(Class par0Class, String par1Str, int par2, CallbackInfo ci){
        TesseractUtilsAddon.entityNameList.add(par1Str);
    }
}
