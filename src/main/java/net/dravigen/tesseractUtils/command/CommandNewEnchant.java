package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.utils.ListsUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

public class CommandNewEnchant extends CommandBase {
    @Override
    public String getCommandName() {
        return "enchant";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "";
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length >= 2) {
            NBTTagList nBTTagList;
            EntityPlayerMP entityPlayerMP = CommandEnchant.getPlayer(sender, strings[0]);
            int id;

            try {
                id = CommandEnchant.parseIntBounded(sender, strings[1], 0, Enchantment.enchantmentsList.length - 1);

            } catch (Exception ignored) {
                try {
                    id = ListsUtils.enchantMap.get(strings[1]);

                } catch (Exception e) {
                    throw new NumberInvalidException("commands.enchant.notFound", strings[1]);

                }
            }

            int level = 1;
            ItemStack itemStack = entityPlayerMP.getCurrentEquippedItem();

            if (itemStack == null) {
                throw new CommandException("commands.enchant.noItem");
            }

            Enchantment enchantment = Enchantment.enchantmentsList[id];

            if (enchantment == null) {
                throw new NumberInvalidException("commands.enchant.notFound", id);
            }
/*
            if (!enchantment.canApply(itemStack)) {
                throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
            }*/
            if (strings.length >= 3) {
                level = CommandEnchant.parseIntBounded(sender, strings[2], enchantment.getMinLevel(), Byte.MAX_VALUE);
            }
            if (itemStack.hasTagCompound() && (nBTTagList = itemStack.getEnchantmentTagList()) != null) {
                for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                   // Enchantment enchantment2;
                    short s = ((NBTTagCompound) nBTTagList.tagAt(i)).getShort("id");
                    if (s == enchantment.effectId || Enchantment.enchantmentsList[s] == null /*|| (enchantment2 = Enchantment.enchantmentsList[s]).canApplyTogether(enchantment)*/){
                        if (s == enchantment.effectId) {
                            nBTTagList.removeTag(i);
                        }
                    }
                   // throw new CommandException("commands.enchant.cantCombine", enchantment.getTranslatedName(level), enchantment2.getTranslatedName(((NBTTagCompound) nBTTagList.tagAt(i)).getShort("lvl")));
                }
            }
            itemStack.addEnchantment(enchantment, level);
            CommandEnchant.notifyAdmins(sender, "commands.enchant.success");
            return;
        }
        throw new WrongUsageException("commands.enchant.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (strings.length == 1) {
            return CommandEnchant.getListOfStringsMatchingLastWord(strings, this.getListOfPlayers());
        } else if (strings.length == 2) {
            return getListOfStringsFromIterableMatchingLastWord(strings, ListsUtils.enchantMap.keySet());
        }
        return null;
    }

    protected String[] getListOfPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

}
