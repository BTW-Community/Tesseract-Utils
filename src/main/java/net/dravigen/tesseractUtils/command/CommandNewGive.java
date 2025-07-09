package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.utils.ListsUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

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
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (strings.length==1){
            return CommandGive.getListOfStringsMatchingLastWord(strings, this.getPlayers());
        }else if (strings.length==2){
            return ListsUtils.getInstance().getItemNameList(strings, sender.getCommandSenderName());
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        int meta;
        int count;
        int id;
        String name;
        EntityPlayerMP player;
        if (strings.length >= 2) {
            player = CommandGive.getPlayer(sender, strings[0]);
            ItemInfo itemInfo;
            if (strings.length==4) {
                itemInfo = getItemInfo(new String[]{"",strings[1]+"/"+strings[3]},sender.getCommandSenderName());
            }else {
                itemInfo = getItemInfo(strings,sender.getCommandSenderName());
            }
            if (itemInfo==null){
                player.sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
                return;
            }
            id = itemInfo.id();
            count = 1;
            if (strings.length >= 3) {
                count = CommandGive.parseIntBounded(sender, strings[2], 1, 64);
            }
            meta = itemInfo.meta();
            name = itemInfo.itemName();
            if (Item.itemsList[id] == null) {
                throw new NumberInvalidException("commands.give.notFound", id);
            }
            ItemStack stack = new ItemStack(id, count, meta);
            stack.getItem().initializeStackOnGiveCommand(player.worldObj.rand, stack);
            EntityItem item = player.dropPlayerItem(stack);
            item.delayBeforeCanPickup = 0;
            CommandGive.notifyAdmins(sender, "commands.give.success", name.replace("_"," "), id+(meta!=0?"/"+meta:""), count, player.getEntityName());
        } else {
            throw new WrongUsageException("commands.give.usage");
        }
    }

    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
