package net.dravigen.tesseractUtils.command;

import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.dravigen.tesseractUtils.utils.ListsUtils.*;

public class CommandSummon extends CommandBase {
    @Override
    public String getCommandName() {
        return "summon";
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/summon <entity> [count] [x] [y] [z]";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings) {
        if (strings.length==1){
            return getInstance().getEntityName(strings);
        }
        MovingObjectPosition blockCoord = getBlockPlayerIsLooking(sender);
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
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String[] var1 = strings[0].split("=");
        int entityNum = 1;
        if (var1.length==2)entityNum = Integer.parseInt(var1[1]);
        double x;
        double y;
        double z;
        if (strings.length == 4) {
            try {
                x = CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, strings[1])+0.5;
                y = CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, strings[2]);
                z = CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, strings[3])+0.5;
            } catch (Exception ignored) {
                x = Integer.parseInt(strings[1])+0.5;
                y = Integer.parseInt(strings[2]);
                z = Integer.parseInt(strings[3])+0.5;
            }
        } else {
            x = sender.getPlayerCoordinates().posX+0.5;
            y = sender.getPlayerCoordinates().posY;
            z = sender.getPlayerCoordinates().posZ+0.5;
        }
        int count=0;
        for (int i = 0; i < entityNum; i++) {
            List<String> entitiesName = Arrays.stream(var1[0].split(":")).toList();
            List<Entity> entities = new ArrayList<>();

            for (String entityName :entitiesName) {
                int id = entitiesMap.get(entityName);
                entities.add(EntityList.createEntityByID(id, sender.getEntityWorld()));
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
        String msg = "Summoned " + count + " new '" + Arrays.stream(strings[0].split(":")).toList().get(0).replace("_"," ") + "'";
        boolean mute = Arrays.stream(strings).anyMatch(s -> s.equalsIgnoreCase("mute"));

        if (mute)return;
        notifyAdmins(sender,msg);

        /*
        // 1. Validate the number of arguments.
        // The command requires at least one argument (the entity's name).
        if (strings.length < 1) {
            throw new WrongUsageException("commands.summon.usage", new Object[0]);
        }

        // 2. Get the entity name.
        String entityName = strings[0];

        // 3. Determine the spawn coordinates.
        double x = sender.getPlayerCoordinates().posX;
        double y = sender.getPlayerCoordinates().posY;
        double z = sender.getPlayerCoordinates().posZ;

        // The code would handle relative coordinates (~) and absolute coordinates.
        // If args.length >= 4, it would parse the coordinates.
        if (strings.length >= 4) {
            x = CommandBase.func_110666_a(sender, x, strings[1]);
            y = CommandBase.func_110666_a(sender, y, strings[2]);
            z = CommandBase.func_110666_a(sender, z, strings[3]);
        }

        // 4. Create the entity's NBT data.
        // The command would check for an optional NBT tag string.
        NBTTagCompound nbtData = new NBTTagCompound();
        if (strings.length >= 5) {
            String nbtString = buildString(strings, 4);
            try {
                // A helper method would parse the JSON-like NBT string.

                nbtData = JsonToNBT.func_150315_a(nbtString);
            } catch (NBTException e) {
                throw new CommandException("commands.summon.tagError", new Object[] {e.getMessage()});
            }
        }

        // 5. Spawn the entity.
        Entity entity = EntityList.createEntityByName(entityName, sender.getEntityWorld());
        if (entity != null) {
            // Apply position and NBT data.
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
           // entity.readFromNBT(nbtData);

            // Add the entity to the world.
            sender.getEntityWorld().spawnEntityInWorld(entity);

            // Send confirmation message to the user.
            //sender.sendChatToPlayer(sender, "commands.summon.success", new Object[0]);
        } else {
            throw new CommandException("commands.summon.failed", new Object[0]);
        }*/

    }
}
