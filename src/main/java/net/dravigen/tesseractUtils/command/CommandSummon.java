package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.dravigen.tesseractUtils.command.UtilsCommand.entityShowNameList;

public class CommandSummon extends CommandBase {
    @Override
    public String getCommandName() {
        return "summon";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/summon <entity> [count]";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] strings) {
        if (strings.length==1){
            return UtilsCommand.getInstance().getEntityName(strings);
        }
        MovingObjectPosition blockCoord = UtilsCommand.getInstance().getBlockPlayerIsLooking(par1ICommandSender);
        if (strings.length>=3) {
            if (blockCoord != null) {
                int x = blockCoord.blockX;
                int y = blockCoord.blockY;
                int z = blockCoord.blockZ;
                if (strings.length == 3) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(x));
                } else if (strings.length == 4) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(y));
                } else if (strings.length == 5) {
                    return getListOfStringsMatchingLastWord(strings, String.valueOf(z));
                }
            } else return null;
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length < 1) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cUsage: " + getCommandUsage(sender)));
            return;
        }
        int entityNum = 1;
        if (strings.length >= 2) {
            entityNum = Integer.parseInt(strings[1]);
        }
        int x;
        int y;
        int z;
        if (strings.length >= 4) {
            try {
                x = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[1]));
                y = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[2]));
                z = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[3]));
            } catch (Exception ignored) {
                x = Integer.parseInt(strings[2]);
                y = Integer.parseInt(strings[3]);
                z = Integer.parseInt(strings[4]);
            }
        } else {
            x = sender.getPlayerCoordinates().posX;
            y = sender.getPlayerCoordinates().posY;
            z = sender.getPlayerCoordinates().posZ;
        }
        int count=0;
        for (int i = 0; i < entityNum; i++) {
            List<String> entitiesName = Arrays.stream(strings[0].split(":")).toList();
            List<Entity> entities = new ArrayList<>();

            for (String entity1 :entitiesName) {
                for (String name : entityShowNameList){
                    if (name.equalsIgnoreCase(entity1)){
                        entities.add(EntityList.createEntityByName(UtilsCommand.entityTrueNameList.get(entityShowNameList.indexOf(name)), sender.getEntityWorld()));
                        break;
                    }
                }
            }
            for (int j=0; j<entities.size(); j++){
                Entity entity2 = entities.get(j);
                entity2.setPosition(x,y,z);
                sender.getEntityWorld().spawnEntityInWorld(entity2);
                count++;
                if (j>0) {
                    entity2.mountEntity(entities.get(j - 1));
                }
            }
        }
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Summoned " + count + " new '" + Arrays.stream(strings[0].split(":")).toList().get(0).replace("_"," ") + "'"));
    }
}
