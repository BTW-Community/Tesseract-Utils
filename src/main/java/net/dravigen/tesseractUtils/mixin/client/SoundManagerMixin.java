package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.enums.EnumConfig;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Shadow private int ticksBeforeMusic;
    @Shadow private SoundSystem sndSystem;
    @Shadow @Final private SoundPool soundPoolMusic;
    @Unique private SoundPoolEntry music;
    @Unique private boolean isStartPressed=false;
    @Unique private boolean isStopPressed=false;


    @Redirect(method = "playRandomMusicIfReady",at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;playing(Ljava/lang/String;)Z",ordinal = 0),remap = false)
    private boolean stopAtStartMusic(SoundSystem instance, String s){
        if (Minecraft.getMinecraft().currentScreen==null&&Keyboard.isKeyDown((Integer) EnumConfig.START_MUSIC_KEY.getValue())) {
            if (!isStartPressed ) {
                this.sndSystem.stop("BgMusic");
            }
        }else isStartPressed=false;
        return this.sndSystem.playing(s);
    }
    @Redirect(method = "playRandomMusicIfReady",at = @At(value = "FIELD", target = "Lnet/minecraft/src/SoundManager;ticksBeforeMusic:I",opcode = Opcodes.GETFIELD))
    private int stopAtStartMusic(SoundManager instance){
        if (Minecraft.getMinecraft().currentScreen==null&&Keyboard.isKeyDown((Integer) EnumConfig.START_MUSIC_KEY.getValue())) {
            if (!isStartPressed) {
                music = this.soundPoolMusic.getRandomSound();
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Started: " + music.getSoundName()));
                return 0;
            }
        }
        return this.ticksBeforeMusic;
    }
    @Redirect(method = "playRandomMusicIfReady",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/SoundPool;getRandomSound()Lnet/minecraft/src/SoundPoolEntry;"))
    private SoundPoolEntry getSoundName(SoundPool instance){
        if (Minecraft.getMinecraft().currentScreen==null&&Keyboard.isKeyDown((Integer) EnumConfig.START_MUSIC_KEY.getValue())) {
            if (!isStartPressed) {
                isStartPressed = true;
                return music;
            }
        }
        return this.soundPoolMusic.getRandomSound();
    }

    @Inject(method = "playRandomMusicIfReady",at = @At("HEAD"))
    private void stopMusic(CallbackInfo ci){
        if (Minecraft.getMinecraft().currentScreen==null&&Keyboard.isKeyDown((Integer) EnumConfig.STOP_MUSIC_KEY.getValue())) {
            if (!isStopPressed ) {
                this.sndSystem.stop("BgMusic");
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Stopped the background music"));
                isStopPressed=true;
            }
        }else isStopPressed=false;
    }
}
