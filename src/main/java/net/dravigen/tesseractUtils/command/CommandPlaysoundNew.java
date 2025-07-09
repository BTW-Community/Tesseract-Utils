package net.dravigen.tesseractUtils.command;

import com.google.common.collect.Lists;
import net.dravigen.tesseractUtils.mixin.accessor.SoundManagerAccessor;
import net.dravigen.tesseractUtils.mixin.accessor.SoundPoolAccessor;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import paulscode.sound.SoundSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandPlaysoundNew extends CommandBase {
    @Override
    public String getCommandName() {
        return "playsound";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "";
    }

    public static List<String> soundsName = new ArrayList<>();
    public static List<String> soundsChangedName = new ArrayList<>();
    public static List<String> musicsName = new ArrayList<>();

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if(!PacketUtils.clientHaveTU.get(sender.getCommandSenderName())) return false;
        return super.canCommandSenderUseCommand(sender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (!MinecraftServer.getIsServer()) {
            if (strings.length == 1) {
                return getListOfStringsMatchingLastWord(strings, "sound", "music");
            } else if (strings.length == 2) {
                List<String> list = new ArrayList<>();
                SoundManagerAccessor var1 = (SoundManagerAccessor) Minecraft.getMinecraft().sndManager;
                if (strings[0].equalsIgnoreCase("sound")) {
                    SoundPoolAccessor sounds = (SoundPoolAccessor) var1.getSoundPoolSounds();
                    List<String> var2 = Lists.newArrayList(sounds.getNameToSoundPool().keySet());
                    for (String s : var2) {
                        soundsName.add(s);
                        String var3 = s.replace("mob.", "")
                                .replace("btw:", "")
                                .replace("block.", "")
                                .replace("ambient.", "")
                                .replace("misc.", "")
                                .replace("item.", "")
                                .replace("entity.", "");

                        soundsChangedName.add(var3);
                        if (var3.contains(strings[strings.length - 1])) {
                            list.add(var3);
                        }
                    }

                } else if (strings[0].equalsIgnoreCase("music")) {
                    SoundPoolAccessor musics = (SoundPoolAccessor) var1.getSoundPoolMusic();
                    List<String> var2 = Lists.newArrayList(musics.getNameToSoundPool().keySet());
                    for (String s : var2) {
                        musicsName.add(s);
                        if (s.contains(strings[strings.length - 1])) {
                            list.add(s);
                        }
                    }
                }
                Collections.sort(list);
                return list;
            }
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (!MinecraftServer.getIsServer()) {
            if (strings.length == 2) {
                SoundManagerAccessor var1 = (SoundManagerAccessor) Minecraft.getMinecraft().sndManager;
                SoundSystem var2 = var1.getSndSystem();
                String soundName;
                ChunkCoordinates coords = sender.getPlayerCoordinates();
                if (strings[0].equalsIgnoreCase("sound")) {
                    soundName = soundsName.get(soundsChangedName.indexOf(strings[1]));
                    Minecraft.getMinecraft().sndManager.playSound(soundName, coords.posX, coords.posY, coords.posZ, 1, 1);
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("Playing sound: " + strings[1]));
                } else if (strings[0].equalsIgnoreCase("music")) {
                    soundName = musicsName.get(musicsName.indexOf(strings[1]));
                    SoundPoolEntry var3 = var1.getSoundPoolMusic().getRandomSoundFromSoundPool(soundName);
                    var2.stop("BgMusic");
                    var2.backgroundMusic("BgMusic", var3.getSoundUrl(), var3.getSoundName(), false);
                    var2.setVolume("BgMusic", Minecraft.getMinecraft().gameSettings.musicVolume);
                    var2.play("BgMusic");
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("Playing music: " + soundName));

                }
            }
        }
    }
}
