package net.dravigen.tesseractUtils.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

import static net.dravigen.tesseractUtils.command.UtilsCommand.*;

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
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        if (par2ArrayOfStr.length==1){
            return getListOfStringsMatchingLastWord(par2ArrayOfStr,"player","entity","item","all");
        }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("entity")){
            return UtilsCommand.getInstance().getEntityName(par2ArrayOfStr);
        }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("player")){
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
        }else if (par2ArrayOfStr.length==2 && par2ArrayOfStr[0].equalsIgnoreCase("item")){
            return UtilsCommand.getInstance().getItemNameList(par2ArrayOfStr);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        boolean dropLoot = false;
        for (String string : strings) {
            if (!dropLoot) {
                dropLoot = string.equalsIgnoreCase("dropLoot");
            }
        }
        if (strings.length>=1) {
            if (strings[0].equalsIgnoreCase("all")) {
                int killCount = 0;
                for (int i = 0; i < iCommandSender.getEntityWorld().loadedEntityList.size(); i++) {
                    Entity entity = (Entity) iCommandSender.getEntityWorld().loadedEntityList.get(i);
                    if (!(entity instanceof EntityPlayer)) {
                        killCount++;
                        if (dropLoot) {
                            entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                        } else entity.setDead();
                        iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText(entity.getEntityName()));

                    }
                }
                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " + killCount + " entities"));
            } else if (strings[0].equalsIgnoreCase("entity")) {
                int killCount = 0;
                for (String name : entityShowNameList) {
                    if (strings[1].equalsIgnoreCase(name)) {
                        for (int i = 0; i < iCommandSender.getEntityWorld().loadedEntityList.size(); i++) {
                            Entity entity = (Entity)iCommandSender.getEntityWorld().loadedEntityList.get(i);
                            if (EntityList.getEntityString(entity) != null) {
                                String base = EntityList.getEntityString(entity);
                                String unlocalizedName = "entity." + base + ".name";
                                String translated = StringTranslate.getInstance().translateKey(unlocalizedName);
                                if (translated.equalsIgnoreCase(unlocalizedName)){
                                    translated=base;
                                }
                                String finalName = translated.replace("Entity","").replace("addon","").replace("fc","").replace(StringTranslate.getInstance().translateKey("entity.villager.name"),"").replace("DireWolf","The_Beast").replace("JungleSpider","Jungle_Spider").replace(" ","_");

                                if (finalName.equalsIgnoreCase(name)) {
                                    if (dropLoot) {
                                        entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                                    } else entity.setDead();
                                    killCount++;
                                }
                            }
                        }
                    }
                }
                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " + killCount + " '" + strings[1].replace("_"," ") + "'"));

            } else if (strings[0].equalsIgnoreCase("player")&&strings.length==2) {
                EntityPlayerMP var3;
                var3 = CommandGive.getPlayer(iCommandSender, strings[1]);
                if (dropLoot) {
                    var3.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                } else {
                    var3.setHealth(0);
                }
                var3.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(var3.getEntityName() + " got killed."));

            } else if (strings[0].equalsIgnoreCase("item")) {
                int killCount = 0;
                ItemInfo itemInfo = getItemInfo(strings);
                int id = itemInfo.id();
                for (Object entity: iCommandSender.getEntityWorld().loadedEntityList){
                    if (entity instanceof EntityItem item){
                        if (id==item.getEntityItem().itemID&&itemInfo.meta()==item.getEntityItem().getItemDamage()){
                            killCount++;
                            item.setDead();
                        }
                    }
                }
                iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Killed " +killCount + " '" + itemInfo.itemName().replace("_"," ") + "' item"));
            }
        }
    }

}
