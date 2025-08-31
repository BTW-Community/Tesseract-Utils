package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.utils.ListsUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

public class CommandNewKill extends CommandBase{
    @Override
    public String getCommandName() {
        return "kill";
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
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (strings.length==1){
            return getListOfStringsMatchingLastWord(strings,"player","entity","item","all");
        }else if (strings.length==2 && strings[0].equalsIgnoreCase("entity")){
            return ListsUtils.getInstance().getEntityName(strings);
        }else if (strings.length==2 && strings[0].equalsIgnoreCase("player")){
            return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
        }else if (strings.length==2 && strings[0].equalsIgnoreCase("item")){
            return ListsUtils.getInstance().getItemNameList(strings);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        boolean dropLoot = false;
        for (String string : strings) {
            if (!dropLoot) {
                dropLoot = string.equalsIgnoreCase("dropLoot");
            }
        }
        if (strings.length>=1) {
            if (strings[0].equalsIgnoreCase("all")) {
                int killCount = 0;
                for (int i = 0; i < sender.getEntityWorld().loadedEntityList.size(); i++) {
                    Entity entity = (Entity) sender.getEntityWorld().loadedEntityList.get(i);
                    if (!(entity instanceof EntityPlayer)) {
                        killCount++;
                        if (dropLoot) {
                            entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                        } else entity.setDead();
                    }
                }
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " + killCount + " entities"));
            } else if (strings[0].equalsIgnoreCase("entity")) {
                int killCount = 0;
                int id = entitiesMap.get(strings[1]);
                for (Object object:sender.getEntityWorld().loadedEntityList){
                    Entity entity = (Entity) object;
                    if (!(entity instanceof EntityPlayer)&&EntityList.getEntityIDFromClass(entity.getClass())==id){
                        if (dropLoot){
                            entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                        } else entity.setDead();
                        killCount++;
                    }
                }
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " + killCount + " '" + strings[1].replace("_"," ") + "'"));

            }
            else if (strings[0].equalsIgnoreCase("player")&&strings.length==2) {
                EntityPlayerMP var3;
                var3 = CommandGive.getPlayer(sender, strings[1]);
                if (dropLoot) {
                    var3.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                } else {
                    var3.setHealth(0);
                }
                var3.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(var3.getEntityName() + " got killed."));

            }
            else if (strings[0].equalsIgnoreCase("item")) {
                int killCount = 0;
                ItemInfo itemInfo = getItemInfo(strings);
                if (itemInfo==null){
                    getPlayer(sender,sender.getCommandSenderName()).sendChatToPlayer(ChatMessageComponent.createFromText("§cWrong name or id"));
                    return;
                }
                int id = itemInfo.id();
                for (Object entity: sender.getEntityWorld().loadedEntityList){
                    if (entity instanceof EntityItem item){
                        if (id==item.getEntityItem().itemID&&itemInfo.meta()==item.getEntityItem().getItemDamage()){
                            killCount++;
                            item.setDead();
                        }
                    }
                }
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " +killCount + " '" + itemInfo.itemName().replace("_"," ") + "' item"));
            }
        }else throw new WrongUsageException("/kill <player|entity|item|all> <name>");
    }

}
