package net.dravigen.tesseractUtils.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import static net.dravigen.tesseractUtils.command.UtilsCommand.*;

public class CommandNewGive extends CommandBase {
    @Override
    public String getCommandName() {
        return "give";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.give.usage";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        if (par2ArrayOfStr.length==1){
            return CommandGive.getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers());
        }else if (par2ArrayOfStr.length==2){
            return UtilsCommand.getInstance().getItemNameList(par2ArrayOfStr);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender par1ICommandSender, String[] strings) {
        int meta;
        int count;
        int id;
        String name;
        EntityPlayerMP player;
        if (strings.length >= 2) {
            player = CommandGive.getPlayer(par1ICommandSender, strings[0]);
            ItemInfo itemInfo = getItemInfo(strings);
            id = itemInfo.id();
            count = 1;
            meta = itemInfo.meta();
            name = itemInfo.itemName();
            if (Item.itemsList[id] == null) {
                throw new NumberInvalidException("commands.give.notFound", id);
            }
            if (strings.length >= 3) {
                count = CommandGive.parseIntBounded(par1ICommandSender, strings[2], 1, 64);
            }
            ItemStack var7 = new ItemStack(id, count, meta);
            var7.getItem().initializeStackOnGiveCommand(player.worldObj.rand, var7);
            EntityItem var8 = player.dropPlayerItem(var7);
            var8.delayBeforeCanPickup = 0;
            CommandGive.notifyAdmins(par1ICommandSender, "commands.give.success", name.replace("_"," "), id+(meta!=0?"/"+meta:""), count, player.getEntityName());
        } else {
            throw new WrongUsageException("commands.give.usage");
        }
    }

    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
