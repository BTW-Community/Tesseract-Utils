package net.dravigen.tesseractUtils.mixin.accessor;

import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("soundPoolSounds")
    SoundPool getSoundPoolSounds();

    @Accessor("soundPoolMusic")
    SoundPool getSoundPoolMusic();

    @Accessor("sndSystem")
    SoundSystem getSndSystem();


}
