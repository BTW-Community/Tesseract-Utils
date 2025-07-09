package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import java.util.List;

public class CommandNewEffect extends CommandBase {
    @Override
    public String getCommandName() {
        return "effect";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.effect.usage";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (strings.length==1){
            return CommandGive.getListOfStringsMatchingLastWord(strings, this.getPlayers());
        }else if (strings.length==2){
            return CommandGive.getListOfStringsMatchingLastWord(strings, "clear", "set");
        }else if (strings.length==3&&strings[1].equalsIgnoreCase("set")){
            return getListOfStringsFromIterableMatchingLastWord(strings, PacketUtils.playersPotionsNameListServer.get(sender.getCommandSenderName()).keySet());
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length < 2) throw new WrongUsageException("commands.effect.usage");
        EntityPlayerMP entityPlayerMP = CommandEffect.getPlayer(sender, strings[0]);
        if (strings[1].equals("clear")) {
            if (entityPlayerMP.getActivePotionEffects().isEmpty()) {
                throw new CommandException("commands.effect.failure.notActive.all", entityPlayerMP.getEntityName());
            }
            entityPlayerMP.clearActivePotions();
            CommandEffect.notifyAdmins(sender, "commands.effect.success.removed.all", entityPlayerMP.getEntityName());
        }else if (strings[1].equalsIgnoreCase("set")&&strings.length>=3){
            try {
            int id;
            try {
                id = Integer.parseInt(strings[2]);
            }catch (Exception ignored){
                try {
                    id = PacketUtils.playersPotionsNameListServer.get(sender.getCommandSenderName()).get(strings[2]);
                } catch (Exception e) {
                    throw new NumberInvalidException("commands.effect.notFound", strings[2]);
                }
            }
            Potion potion = Potion.potionTypes[id];
            int duration = 600;
            int n3 = 30;
            int amplifier = 0;
            if (strings.length >= 4) {
                n3 = CommandEffect.parseIntBounded(sender, strings[3], 0, 1000000);
                duration = potion.isInstant() ? n3 : n3 * 20;
            } else if (potion.isInstant()) {
                duration = 1;
            }
            if (strings.length >= 5) {
                amplifier = CommandEffect.parseIntBounded(sender, strings[4], 0, 255);
            }

            PotionEffect currentPotion = entityPlayerMP.getActivePotionEffect(potion);
            if (currentPotion!=null){
                entityPlayerMP.removePotionEffect(potion.id);
            }

            PotionEffect potionEffect = new PotionEffect(potion.id, duration, amplifier);
            entityPlayerMP.addPotionEffect(potionEffect);
            CommandEffect.notifyAdmins(sender, "commands.effect.success", ChatMessageComponent.createFromTranslationKey(potionEffect.getEffectName()), potion.id, amplifier, entityPlayerMP.getEntityName(), n3);
            } catch (Exception e) {
                throw new WrongUsageException("commands.effect.usage");
            }
        }
    }

    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
