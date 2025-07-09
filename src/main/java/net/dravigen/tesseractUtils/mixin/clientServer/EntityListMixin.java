package net.dravigen.tesseractUtils.mixin.clientServer;

import btw.entity.CanvasEntity;
import btw.entity.DynamiteEntity;
import btw.entity.InfiniteArrowEntity;
import btw.entity.UrnEntity;
import btw.entity.item.BloodWoodSaplingItemEntity;
import btw.entity.item.FloatingItemEntity;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import net.dravigen.tesseractUtils.utils.ListsUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Shadow private static Map<Class<?>, String> classToStringMapping;

    @Inject(method = "addMapping(Ljava/lang/Class;  Ljava/lang/String;I)V",at = @At("TAIL"))
    private static void getMap(CallbackInfo ci){
        ListsUtils.CLASS_TO_STRING_MAPPING = classToStringMapping;
        List<Class<?>> blacklistClass = new ArrayList<>();
        blacklistClass.add(EntityPainting.class);
        blacklistClass.add(UrnEntity.class);
        blacklistClass.add(CanvasEntity.class);
        blacklistClass.add(EntityFallingSand.class);
        blacklistClass.add(BlockLiftedByPlatformEntity.class);
        blacklistClass.add(EntityItem.class);
        blacklistClass.add(BloodWoodSaplingItemEntity.class);
        blacklistClass.add(FloatingItemEntity.class);
        blacklistClass.add(EntityItemFrame.class);
        blacklistClass.add(EntityLeashKnot.class);
        blacklistClass.add(InfiniteArrowEntity.class);
        blacklistClass.add(DynamiteEntity.class);

        for (Class<?> entry : blacklistClass) {
            ListsUtils.CLASS_TO_STRING_MAPPING.remove(entry);
        }
    }
}
