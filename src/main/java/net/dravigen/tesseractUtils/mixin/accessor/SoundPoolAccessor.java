package net.dravigen.tesseractUtils.mixin.accessor;

import net.minecraft.src.SoundPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundPool.class)
public interface SoundPoolAccessor {
    @Accessor("nameToSoundPoolEntriesMapping")
    Map getNameToSoundPool();

}
