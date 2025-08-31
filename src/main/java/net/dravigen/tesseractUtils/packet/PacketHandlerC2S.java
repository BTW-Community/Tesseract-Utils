package net.dravigen.tesseractUtils.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.util.*;

import btw.item.items.PlaceAsBlockItem;
import net.dravigen.tesseractUtils.command.CommandWorldEdit;
import net.dravigen.tesseractUtils.enums.EnumShape;
import net.dravigen.tesseractUtils.utils.ShapeGen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.dravigen.tesseractUtils.TesseractUtilsAddon.*;

import static net.dravigen.tesseractUtils.command.CommandWorldEdit.*;
import static net.dravigen.tesseractUtils.utils.ListsUtils.*;
import static net.dravigen.tesseractUtils.utils.PacketUtils.*;


public class PacketHandlerC2S {

    private static int prevMode = 1;
    private static boolean firstLog = true;

    /**
     * Handles incoming custom payload packets from clients.
     * This method is called from NetServerHandlerMixin and MUST run on the Server Thread.
     *
     * @param packet The received custom payload packet.
     * @param player The player who sent the packet.
     */
    public static void handle(Packet250CustomPayload packet, EntityPlayerMP player) {
        if (packet.channel.equals(TUChannels.CLIENT_TO_SERVER_CHANNEL)) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
                DataInputStream dis = new DataInputStream(bis);

                String receivedMessage = dis.readUTF();

                // --- STUFF ON SERVER HERE ---

                MinecraftServer server = MinecraftServer.getServer();
                World world = player.worldObj;
                String[] splitText = receivedMessage.split(":");
                String subChannel = splitText[0];
                String property = "";
                if (splitText.length == 2) {
                    property = splitText[1];
                }
                final String trim = property.replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
                switch (subChannel) {
                    case "isPermanent" -> {
                        int entityID;
                        entityID = Integer.parseInt(property);
                        Entity entity = player.worldObj.getEntityByID(entityID);
                        if (entity instanceof EntityLiving living) {
                            boolean canDespawn;
                            try {
                                Method canDespawnMethod = living.getClass().getDeclaredMethod("canDespawn");
                                canDespawnMethod.setAccessible(true);
                                canDespawn = (boolean) canDespawnMethod.invoke(living);
                            } catch (Exception e) {
                                try {
                                    Method canDespawnMethod = EntityLiving.class.getDeclaredMethod("canDespawn");
                                    canDespawnMethod.setAccessible(true);
                                    canDespawn = (boolean) canDespawnMethod.invoke(entity);
                                } catch (Exception ex) {
                                    canDespawn = true;
                                }
                            }
                            PacketSender.sendServerToClientMessage(player, "isPermanent:" + (living.isNoDespawnRequired() || !canDespawn));
                        }
                    }
                    case "isPlayerOP" -> PacketSender.sendServerToClientMessage(player, "isPlayerOP:" + server.getConfigurationManager().isPlayerOpped(property));
                    case "updateAllPos" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        int[] block1 = {x1, y1, z1};
                        int[] block2 = {x2, y2, z2};
                        if (infos.length == 7) {
                            int idRequested = Integer.parseInt(infos[6]);
                            ClientRequestManager.executeUpdatedPosCallback(player, idRequested, block1, block2);
                        }
                    }
                    case "extrudeShrink", "extrudeExpand" -> {
                        List<SavedBlock> list = new ArrayList<>();

                        String[] infos = property.split(",");
                        int x = Integer.parseInt(infos[0]);
                        int y = Integer.parseInt(infos[1]);
                        int z = Integer.parseInt(infos[2]);
                        int sideHit = Integer.parseInt(infos[3]);
                        boolean fuzzy = Boolean.parseBoolean(infos[4]);
                        int extrudeLimit = Integer.parseInt(infos[5]);

                        int referenceBlockId = world.getBlockId(x, y, z);
                        int referenceBlockMeta = world.getBlockMetadata(x, y, z);

                        List<BlockPos> connectedBlocks = findConnectedBlocksInPlane(world, x, y, z, referenceBlockId, referenceBlockMeta, sideHit, fuzzy, extrudeLimit);

                        if (connectedBlocks.isEmpty()) {
                            player.addChatMessage("§cNo connected blocks of the same type found.");
                            return;
                        }
                        int blocksPlaced = 0;
                        for (BlockPos blockPos : connectedBlocks) {
                            int newBlockX = blockPos.x;
                            int newBlockY = blockPos.y;
                            int newBlockZ = blockPos.z;

                            if (subChannel.equalsIgnoreCase("extrudeExpand")) {
                                switch (sideHit) {
                                    case 0:
                                        newBlockY--;break; // Bottom face: place below
                                    case 1:
                                        newBlockY++;break; // Top face: place above
                                    case 2:
                                        newBlockZ--;break; // North face: place North
                                    case 3:
                                        newBlockZ++;break; // South face: place South
                                    case 4:
                                        newBlockX--;break; // West face: place West
                                    case 5:
                                        newBlockX++;break; // East face: place East
                                }
                            }
                            int targetBlockId = world.getBlockId(newBlockX, newBlockY, newBlockZ);
                            int targetBlockMeta = world.getBlockMetadata(newBlockX, newBlockY, newBlockZ);
                            SavedBlock savedBlock = new SavedBlock(newBlockX, newBlockY, newBlockZ, targetBlockId, targetBlockMeta);
                            if (subChannel.equalsIgnoreCase("extrudeExpand") && targetBlockId == 0) {
                                list.add(savedBlock);
                                world.setBlock(newBlockX, newBlockY, newBlockZ, referenceBlockId, referenceBlockMeta, 2);
                                blocksPlaced++;
                            } else if (subChannel.equalsIgnoreCase("extrudeShrink") && targetBlockId != 0) {
                                list.add(savedBlock);
                                world.setBlockToAir(newBlockX, newBlockY, newBlockZ);
                                blocksPlaced++;
                            }
                        }
                        if (blocksPlaced > 0) {
                            List<List<SavedBlock>> undoList = playersUndoListServer.get(player.getEntityName());
                            if (undoList == null) {
                                undoList = new ArrayList<>();
                            }
                            undoList.add(list);
                            playersUndoListServer.put(player.getEntityName(), undoList);
                            player.addChatMessage("§d" + blocksPlaced + " block(s) have been" + (subChannel.equalsIgnoreCase("extrudeExpand") ? " placed" : " removed"));
                        } else {
                            player.addChatMessage("§cNo blocks could be" + (subChannel.equalsIgnoreCase("extrudeExpand") ? " placed" : " removed"));
                        }
                    }
                    case "deleteBlocks" -> {
                        List<SavedBlock> list = new ArrayList<>();
                        String[] infos = property.split(",");
                        int x = Integer.parseInt(infos[0]);
                        int y = Integer.parseInt(infos[1]);
                        int z = Integer.parseInt(infos[2]);
                        boolean fuzzy = Boolean.parseBoolean(infos[3]);
                        int extrudeLimit = Integer.parseInt(infos[4]);
                        boolean sneaking = Boolean.parseBoolean(infos[5]);
                        int flag = Integer.parseInt(infos[6]);
                        int referenceBlockId = world.getBlockId(x, y, z);
                        int referenceBlockMeta = world.getBlockMetadata(x, y, z);

                        List<BlockPos> connectedBlocks = findConnectedBlocksIn3D(world, x, y, z, referenceBlockId, referenceBlockMeta, fuzzy, extrudeLimit);

                        if (connectedBlocks.isEmpty()) {
                            if (sneaking) player.addChatMessage("§cNo connected blocks of the same type found.");
                            return;
                        }
                        int blocksBroken = 0;
                        for (BlockPos blockPos : connectedBlocks) {
                            int newBlockX = blockPos.x;
                            int newBlockY = blockPos.y;
                            int newBlockZ = blockPos.z;
                            int targetBlockId = world.getBlockId(newBlockX, newBlockY, newBlockZ);
                            int targetBlockMeta = world.getBlockMetadata(newBlockX, newBlockY, newBlockZ);
                            if (targetBlockId != 0) {
                                list.add(new SavedBlock(newBlockX, newBlockY, newBlockZ, targetBlockId, targetBlockMeta));
                                world.setBlock(newBlockX, newBlockY, newBlockZ, 0, 0, flag);
                                blocksBroken++;
                            }
                        }
                        if (sneaking) {
                            if (blocksBroken > 0) {
                                List<List<SavedBlock>> undoList = playersUndoListServer.get(player.getEntityName());
                                if (undoList == null) {
                                    undoList = new ArrayList<>();
                                }
                                undoList.add(list);
                                playersUndoListServer.put(player.getEntityName(), undoList);
                                player.addChatMessage("§d" + blocksBroken + " block(s) have been removed");
                            } else {
                                player.addChatMessage("§cNo blocks could be removed.");
                            }
                        }
                    }
                    case "replace" -> {
                        String[] infos = property.split(",");
                        int x = Integer.parseInt(infos[0]);
                        int y = Integer.parseInt(infos[1]);
                        int z = Integer.parseInt(infos[2]);
                        float clickX = Float.parseFloat(infos[3]);
                        float clickY = Float.parseFloat(infos[4]);
                        float clickZ = Float.parseFloat(infos[5]);
                        int hitSide = Integer.parseInt(infos[6]);
                        int flag = Integer.parseInt(infos[7]);
                        ItemStack stack = player.getHeldItem();
                        int meta = stack.getItemDamage();
                        PlaceAsBlockItem blockItem = (PlaceAsBlockItem) stack.getItem();
                        int iNewBlockID = blockItem.getBlockIDToPlace(meta, hitSide, clickX, clickY, clickZ);
                        Block newBlock = Block.blocksList[iNewBlockID];
                        int iNewMetadata = blockItem.getMetadata(meta);
                        iNewMetadata = newBlock.onBlockPlaced(world, x, y, z, hitSide, clickX, clickY, clickZ, iNewMetadata);
                        if (world.setBlock(x, y, z, iNewBlockID, newBlock.preBlockPlacedBy(world, x, y, z, iNewMetadata, player), flag)) {
                            if (world.getBlockId(x, y, z) == iNewBlockID) {
                                newBlock.onBlockPlacedBy(world, x, y, z, player, stack);
                            }
                        }
                    }
                   /* case "useBuildTool" -> {
                        String[] infos = property.split(",");
                        int x = Integer.parseInt(infos[0]);
                        int y = Integer.parseInt(infos[1]);
                        int z = Integer.parseInt(infos[2]);
                        boolean replace = player.isUsingSpecialKey();

                        String shape = infos[EnumShape.SHAPE.ordinal()];
                        String[] parameters = infos;
                        String hollowOpen = infos[EnumShape.VOLUME.ordinal()];

                        String toolHollowOrOpen = infos[10];
                        List<SavedBlock> list = new ArrayList<>();
                        ItemStack[] hotbarItems = new ItemStack[9];
                        System.arraycopy(player.inventory.mainInventory, 0, hotbarItems, 0, 9);
                        StringBuilder blocks= new StringBuilder();
                        for (ItemStack item:hotbarItems){
                            if (item==null)continue;
                            if (!(item.getItem() instanceof PlaceAsBlockItem blockItem))continue;
                            blocks.append(blockItem.getBlockIDToPlace(item.getItemDamage(),0,0,0,0)).append("/").append(item.getItemDamage()).append(":").append(item.stackSize).append(";");
                        }
                        String blockUsed = String.valueOf(blocks);
                        ItemStack stack = player.getHeldItem();
                        if (!shapeMode&& stack!=null&&stack.getTagCompound() != null && stack.getTagCompound().hasKey( "BuildingParams")) {
                            NBTTagCompound buildingParamsNBT = stack.getTagCompound().getCompoundTag("BuildingParams");
                            shape = buildingParamsNBT.getString("shape");
                            parameters = buildingParamsNBT.getString("parameters").split(":");
                            blockUsed = buildingParamsNBT.getString("blockUsed");
                            toolHollowOrOpen = buildingParamsNBT.getString("volume");
                        }

                        list = useShapeTool(player, shape, parameters, y, toolHollowOrOpen, list, x, z, blockUsed, replace);
                        if (!list.isEmpty()) {
                            playersRedoListServer.remove(player.getEntityName());
                            List<List<SavedBlock>> undoList = playersUndoListServer.get(player.getEntityName());
                            if (undoList==null){
                                undoList=new ArrayList<>();
                            }
                            undoList.add(list);
                            playersUndoListServer.put(player.getEntityName(), undoList);
                        }
                    }*/
                    case "useBuildToolList" -> {
                        List<String> listPos = new ArrayList<>(List.of(property.split(";")));
                        String[] infos = listPos.get(listPos.size() - 1).split(",");
                        listPos.remove(listPos.size() - 1);
                        boolean replace = player.isUsingSpecialKey();
                        boolean shapeMode = Boolean.parseBoolean(infos[infos.length - 1]);
                        int flag = Integer.parseInt(infos[infos.length - 2]);
                        String shape = infos[EnumShape.SHAPE.ordinal()];
                        String[] parameters = infos;
                        String hollowOpen = infos[EnumShape.VOLUME.ordinal()];

                        List<SavedBlock> list = new ArrayList<>();
                        ItemStack[] hotbarItems = new ItemStack[9];
                        System.arraycopy(player.inventory.mainInventory, 0, hotbarItems, 0, 9);
                        StringBuilder blocks = new StringBuilder();
                        for (ItemStack item : hotbarItems) {
                            if (item == null) continue;
                            if (!(item.getItem() instanceof PlaceAsBlockItem blockItem)) continue;
                            blocks.append(blockItem.getBlockIDToPlace(item.getItemDamage(), 0, 0, 0, 0)).append("/").append(item.getItemDamage()).append(":").append(item.stackSize).append(";");
                        }
                        String blockUsed = String.valueOf(blocks);
                        ItemStack stack = player.getHeldItem();
                        if (!shapeMode && stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("BuildingParams")) {
                            NBTTagCompound buildingParamsNBT = stack.getTagCompound().getCompoundTag("BuildingParams");
                            shape = buildingParamsNBT.getString("shape");
                            parameters = buildingParamsNBT.getString("parameters").split(":");
                            blockUsed = buildingParamsNBT.getString("blockUsed");
                            hollowOpen = buildingParamsNBT.getString("volume");
                        }
                        if (blockUsed == null || blockUsed.equalsIgnoreCase("")) {
                            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to have block in your hotbars"));
                            return;
                        }
                        for (String pos : listPos) {
                            String[] coord = pos.split(",");
                            int x = Integer.parseInt(coord[0]);
                            int y = Integer.parseInt(coord[1]);
                            int z = Integer.parseInt(coord[2]);
                            list = useShapeTool(player, shape, parameters, y, hollowOpen, list, x, z, blockUsed, replace);
                            if (!list.isEmpty()) {
                                playersRedoListServer.remove(player.getEntityName());
                                List<List<SavedBlock>> undoList = playersUndoListServer.get(player.getEntityName());
                                if (undoList == null) {
                                    undoList = new ArrayList<>();
                                }
                                undoList.add(list);
                                playersUndoListServer.put(player.getEntityName(), undoList);
                            }
                        }
                    }
                    case "killEntity", "killEntities" -> {
                        String[] infos = property.split(",");
                        if (subChannel.equalsIgnoreCase("killEntity")) {
                            int entityId = Integer.parseInt(infos[0]);
                            Entity entity = world.getEntityByID(entityId);
                            if (entity != null) entity.setDead();
                        } else {
                            double x = Double.parseDouble(infos[0]);
                            double y = Double.parseDouble(infos[1]);
                            double z = Double.parseDouble(infos[2]);
                            List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(x - 2, y - 2, z - 2, x + 2, y + 2, z + 2));
                            for (Entity entity : entityList) entity.setDead();
                        }
                    }
                    case "updatePermanentMob" -> {
                        String[] infos = property.split(",");
                        int entityId = Integer.parseInt(infos[0]);
                        EntityLiving entity = (EntityLiving) world.getEntityByID(entityId);
                        boolean canDespawn;
                        try {
                            Method canDespawnMethod = entity.getClass().getDeclaredMethod("canDespawn");
                            canDespawnMethod.setAccessible(true);
                            canDespawn = (boolean) canDespawnMethod.invoke(entity);
                        } catch (Exception e) {
                            try {
                                Method canDespawnMethod = EntityLiving.class.getDeclaredMethod("canDespawn");
                                canDespawnMethod.setAccessible(true);
                                canDespawn = (boolean) canDespawnMethod.invoke(entity);
                            } catch (Exception ex) {
                                canDespawn = true;
                            }
                        }
                        if (!canDespawn || entity instanceof EntityWither || entity instanceof EntityDragon) {
                            player.sendChatToPlayer(ChatMessageComponent.createFromText("This '" + entity.getTranslatedEntityName() + "' cannot despawn already"));
                        } else {
                            entity.setPersistent(!entity.isNoDespawnRequired());
                            PacketSender.sendServerToClientMessage(player, "isPermanent:" + entity.isNoDespawnRequired());
                            player.sendChatToPlayer(ChatMessageComponent.createFromText(entity.getTranslatedEntityName() + ("This '" + entity.getTranslatedEntityName() + (entity.isNoDespawnRequired() ? "' is now permanent" : "' isn't permanent anymore"))));
                        }
                    }
                    case "mobCapUpdate" -> {
                        int currentHostile = world.countEntitiesThatApplyToSpawnCap(IMob.class);
                        int currentCreature = world.countEntitiesThatApplyToSpawnCap(EntityAnimal.class);
                        int currentAmbient = world.countEntitiesThatApplyToSpawnCap(EntityAmbientCreature.class);
                        int currentWater = world.countEntitiesThatApplyToSpawnCap(EntityWaterMob.class);
                        float constant = world.getActiveChunksCoordsList().size() / 256f;

                        PacketSender.sendServerToClientMessage(player, "mobCapUpdate:" + currentHostile + "," + currentCreature + "," + currentAmbient + "," + currentWater + "," + constant);
                    }
                    case "updatePlayerInfo" -> {
                        String[] infos = property.split(",");
                        List<String> configsList = new ArrayList<>(Arrays.asList(infos).subList(0, infos.length - 2));
                        playersInfoServer.put(player.getEntityName(), configsList);
                        playersBuildModeServer.put(player.getEntityName(), Integer.parseInt(infos[infos.length - 2]));
                        int modeState = Integer.parseInt(infos[infos.length - 1]);
                        playersGamemodeServer.put(player.getEntityName(), modeState);
                        if (firstLog) {
                            prevMode = modeState;
                            firstLog = false;
                        }
                        if (modeState != prevMode) {
                            player.setGameType(modeState == 1 ? EnumGameType.SURVIVAL : EnumGameType.CREATIVE);
                            PacketSender.sendServerToClientMessage(player, "updateMode:" + modeState);
                            prevMode = modeState;
                            if (modeState == 1) {
                                player.fallDistance = 0;
                            }
                        }
                    }

                    case "getBlocksNameList" -> {
                        List<String> blocks = List.of(trim.split(","));
                        for (String identity : blocks) {
                            String[] split = identity.split("=");
                            blocksMap.putIfAbsent(split[0], split[1]);
                        }
                        blocksMap = sortMapStringFloat(blocksMap);
                        System.out.println(blocksMap);
                    }
                    case "getItemsNameList" -> {
                        List<String> items = List.of(trim.split(","));
                        for (String identity : items) {
                            String[] split = identity.split("=");
                            itemsMap.putIfAbsent(split[0], split[1]);
                        }
                        itemsMap=sortMapByStringValues(itemsMap);
                    }
                    /*
                    case "getEntitiesNameList" -> {
                        playersEntitiesNameMapServer.remove(player.getEntityName());
                        List<String> entities = List.of(trim.split(","));
                        Map<String, Integer> map = new TreeMap<>();
                        for (String identity : entities) {
                            String[] split = identity.split("=");
                            String displayName = split[0];
                            int idName = Integer.parseInt(split[1]);
                            map.put(displayName, idName);
                        }
                        playersEntitiesNameMapServer.put(player.getEntityName(), map);
                    }
                    case "getPotionsNameList" -> {
                        playersPotionsNameListServer.remove(player.getEntityName());
                        List<String> potions = List.of(trim.split(","));
                        Map<String, Short> map = new HashMap<>();
                        for (String identity : potions) {
                            String[] split = identity.split("=");
                            String name = split[0];
                            short id = Short.parseShort(split[1]);
                            map.put(name, id);
                        }
                        playersPotionsNameListServer.put(player.getEntityName(), sortMapShort(map));

                    }
                    case "getEnchantNameList" -> {
                        playersEnchantNameListServer.remove(player.getEntityName());
                        List<String> enchant = List.of(trim.split(","));
                        Map<String, Short> map = new HashMap<>();
                        for (String identity : enchant) {
                            String[] split = identity.split("=");
                            String name = split[0];
                            short id = Short.parseShort(split[1]);
                            map.put(name, id);
                        }
                        playersEnchantNameListServer.put(player.getEntityName(), sortMapShort(map));

                    }
                   */
                    case "copy" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        boolean ignoreAir = Boolean.parseBoolean(infos[6]);
                        copy(player, true, x1, x2, y1, y2, z1, z2, ignoreAir, player.worldObj,false);
                    }
                    case "paste" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        boolean ignoreAir = Boolean.parseBoolean(infos[6]);
                        int flag = Integer.parseInt(infos[7]);
                        int x = Math.max(x1, x2);
                        int y = Math.min(y1, y2);
                        int z = Math.max(z1, z2);
                        String[] strings = {"", "" + x, "" + y, "" + z};
                        paste(player, strings, ignoreAir, player.worldObj, flag);
                    }
                    case "cut" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        boolean ignoreAir = Boolean.parseBoolean(infos[6]);
                        int flag = Integer.parseInt(infos[7]);
                        int numBlock = 0;
                        clearCopyList(player);
                        List<SavedBlock> undoList = new ArrayList<>();
                        List<SavedBlock> copyList = new ArrayList<>();

                        for (int i = 0; i <= MathHelper.abs_int(x1 - x2); i++) {
                            for (int j = 0; j <= MathHelper.abs_int(y1 - y2); j++) {
                                for (int k = 0; k <= MathHelper.abs_int(z1 - z2); k++) {
                                    int x = Math.min(x1, x2) + i;
                                    int y = Math.min(y1, y2) + j;
                                    int z = Math.min(z1, z2) + k;
                                    if (ignoreAir) {
                                        if (!world.isAirBlock(x, y, z)) {
                                            undoList.add(new SavedBlock(x,y,z,world.getBlockId(x,y,z),world.getBlockMetadata(x,y,z)));
                                            copyList.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                            world.setBlock(x, y, z, 0, 0, flag);
                                            numBlock++;
                                        }
                                    } else {
                                        undoList.add(new SavedBlock(x,y,z,world.getBlockId(x,y,z),world.getBlockMetadata(x,y,z)));
                                        copyList.add(new SavedBlock(x - Math.max(x1, x2), y - Math.min(y1, y2), z - Math.max(z1, z2), world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z)));
                                        world.setBlock(x, y, z, 0, 0, flag);
                                        numBlock++;
                                    }
                                }
                            }
                        }
                        saveCopyList(copyList, player);
                        addToUndoListServer(undoList, player);
                        clearRedoListServer(player);
                        player.sendChatToPlayer(ChatMessageComponent.createFromText("§d" + numBlock + " block(s) have been cut"));
                    }
                    case "set" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        int flag = Integer.parseInt(infos[6]);
                        List<SavedBlock> undoList = new ArrayList<>();
                        ItemStack[] hotbarItems = new ItemStack[9];
                        System.arraycopy(player.inventory.mainInventory, 0, hotbarItems, 0, 9);
                        StringBuilder blocks = new StringBuilder();
                        for (ItemStack stack : hotbarItems) {
                            if (stack == null) continue;
                            if (!(stack.getItem() instanceof PlaceAsBlockItem blockItem)) continue;
                            blocks.append(blockItem.getBlockIDToPlace(stack.getItemDamage(), 0, 0, 0, 0)).append("/").append(stack.getItemDamage()).append(":").append(stack.stackSize).append(";");
                        }
                        String[] finalString = {"", String.valueOf(blocks)};
                        setArea(finalString, y1, y2, x1, x2, z1, z2, player, world, flag);
                    }
                    case "replaceArea" -> {
                        String[] infos = property.split(",");
                        int x1 = Integer.parseInt(infos[0]);
                        int y1 = Integer.parseInt(infos[1]);
                        int z1 = Integer.parseInt(infos[2]);
                        int x2 = Integer.parseInt(infos[3]);
                        int y2 = Integer.parseInt(infos[4]);
                        int z2 = Integer.parseInt(infos[5]);
                        int flag = Integer.parseInt(infos[6]);
                        ItemStack[] hotbarItems = new ItemStack[9];
                        System.arraycopy(player.inventory.mainInventory, 0, hotbarItems, 0, 9);
                        StringBuilder blocks = new StringBuilder();
                        String replacedBlock;
                        ItemStack heldItem = player.getHeldItem();
                        for (ItemStack stack : hotbarItems) {
                            if (stack == null) continue;
                            if (!(stack.getItem() instanceof PlaceAsBlockItem blockItem)) continue;
                            if (stack == heldItem) continue;
                            blocks.append(blockItem.getBlockIDToPlace(stack.getItemDamage(), 0, 0, 0, 0)).append("/").append(stack.getItemDamage()).append(":").append(stack.stackSize).append(";");
                        }
                        String[] finalString;
                        if (heldItem != null && heldItem.getItem() instanceof PlaceAsBlockItem blockItem) {
                            replacedBlock = blockItem.getBlockIDToPlace(heldItem.getItemDamage(), 0, 0, 0, 0) + "/" + heldItem.getItemDamage();
                            finalString = new String[]{"", String.valueOf(blocks), replacedBlock};
                        } else finalString = new String[]{"", String.valueOf(blocks)};

                        replaceArea(player, finalString, y1, y2, x1, x2, z1, z2, world, flag);
                    }
                    case "undo" -> undo(1, player, world, Integer.parseInt(property),false);
                    case "redo" -> redo(1, player, world, Integer.parseInt(property),false);
                    case "haveTU" -> clientHaveTU.put(player.getEntityName(), true);
                    case "saveShapeTool" ->{
                        String info = property.replace("!",":");
                        String[] infos = info.split(",");
                        CommandWorldEdit.saveShapeTool(player,infos,player.getHeldItem());
                    }
                }
            } catch (Exception e) {
                System.err.println("SERVER: Error handling C2S message packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private static List<SavedBlock> useShapeTool(EntityPlayerMP player, String shape, String[] parameters, int y, String toolHollowOrOpen, List<SavedBlock> list, int x, int z, String blockUsed, boolean replace) {
        switch (shape.toLowerCase()) {
            case "sphere" -> {
                int radius;
                int thickness=1;
                if(parameters.length>=EnumShape.values().length){
                    radius = Integer.parseInt(parameters[EnumShape.RADIUS.ordinal()]);
                    thickness = parameters.length > 1 ? Integer.parseInt(parameters[EnumShape.THICKNESS.ordinal()]) : 1;
                }else {
                    radius = Integer.parseInt(parameters[0]);
                    thickness = parameters.length>1 ? Integer.parseInt(parameters[1]) : thickness;
                }
                if (player.isSneaking()) y = y +radius;
                if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                    list = ShapeGen.generateHollowSphere(player.worldObj, x, y, z, blockUsed, 2, radius, thickness, replace, player);
                else
                    list = ShapeGen.generateSphere(player.worldObj, x, y, z, blockUsed, 2, radius, replace, player);
            }
            case "cylinder" -> {
                int radius;
                int height;
                int thickness=1;
                if(parameters.length>=EnumShape.values().length){
                    radius = Integer.parseInt(parameters[EnumShape.RADIUS.ordinal()]);
                    height = parameters.length > 1 ? Integer.parseInt(parameters[EnumShape.HEIGHT.ordinal()]) : 1;
                    thickness = parameters.length > 2 ? Integer.parseInt(parameters[EnumShape.THICKNESS.ordinal()]) : 1;
                }else {
                    radius = Integer.parseInt(parameters[0]);
                    height = Integer.parseInt(parameters[1]);
                    thickness = parameters.length>1 ? Integer.parseInt(parameters[2]) : thickness;
                }
                y = height < 0 ? y + height : y;
                height = height < 0 ? height * (-1) : height;
                if (player.isSneaking()) y = y +height/2;
                if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                    list = ShapeGen.generateHollowCylinder(player.worldObj, x, y, z, blockUsed, 2, radius, height, thickness, replace, player);
                else if (toolHollowOrOpen.equalsIgnoreCase("open"))
                    list = ShapeGen.generateOpenCylinder(player.worldObj, x, y, z, blockUsed, 2, radius, height, thickness, replace, player);
                else
                    list = ShapeGen.generateCylinder(player.worldObj, x, y, z, blockUsed, 2, radius, height, replace, player);
            }
            case "cube" -> {
                int sizeX;
                int sizeY;
                int sizeZ;
                int thickness;
                if (parameters.length>=EnumShape.values().length) {
                    sizeX = Integer.parseInt(parameters[EnumShape.SIZE_X.ordinal()]);
                    sizeY = Integer.parseInt(parameters[EnumShape.SIZE_Y.ordinal()]);
                    sizeZ = Integer.parseInt(parameters[EnumShape.SIZE_Z.ordinal()]);
                    thickness = Integer.parseInt(parameters[EnumShape.THICKNESS.ordinal()]);
                } else {
                    sizeX = Integer.parseInt(parameters[0]);
                    sizeY = Integer.parseInt(parameters[1]);
                    sizeZ = Integer.parseInt(parameters[2]);
                    thickness = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                }
                y = sizeY < 0 ? y + sizeY : y;
                sizeY = sizeY < 0 ? sizeY * (-1) : sizeY;
                if (player.isSneaking()) y = y +sizeY/2;
                if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                    list = ShapeGen.generateHollowCube(player.worldObj, x, y, z, blockUsed, 2, sizeX, sizeY, sizeZ, thickness, replace, player);
                else if (toolHollowOrOpen.equalsIgnoreCase("open"))
                    list = ShapeGen.generateOpenCube(player.worldObj, x, y, z, blockUsed, 2, sizeX, sizeY, sizeZ, thickness, replace, player);
                else
                    list = ShapeGen.generateCube(player.worldObj, x, y, z, blockUsed, 2, sizeX, sizeY, sizeZ, replace, player);
            }
            case "pyramid" -> {
                int sizeX;
                int sizeY;
                int sizeZ;
                int thickness;
                if (parameters.length>=EnumShape.values().length) {
                    sizeX = Integer.parseInt(parameters[EnumShape.SIZE_X.ordinal()]);
                    sizeY = Integer.parseInt(parameters[EnumShape.SIZE_Y.ordinal()]);
                    sizeZ = Integer.parseInt(parameters[EnumShape.SIZE_Z.ordinal()]);
                    thickness = Integer.parseInt(parameters[EnumShape.THICKNESS.ordinal()]);
                } else {
                    sizeX = Integer.parseInt(parameters[0]);
                    sizeY = Integer.parseInt(parameters[1]);
                    sizeZ = Integer.parseInt(parameters[2]);
                    thickness = parameters.length > 3 ? Integer.parseInt(parameters[3]) : 1;
                }
                if (toolHollowOrOpen.equalsIgnoreCase("hollow"))
                    list = ShapeGen.generateHollowPyramid(player.worldObj, x, y, z, blockUsed, 2, sizeX, sizeY, sizeZ, thickness, replace, player);
                else
                    list = ShapeGen.generatePyramid(player.worldObj, x, y, z, blockUsed, 2, sizeX, sizeY, sizeZ, replace, player);
            }
            case "plane"->{
                int sizeX = Integer.parseInt(parameters[EnumShape.SIZE_X.ordinal()]);
                int sizeY = Integer.parseInt(parameters[EnumShape.SIZE_Y.ordinal()]);
                int sizeZ = Integer.parseInt(parameters[EnumShape.SIZE_Z.ordinal()]);
                int thickness = Integer.parseInt(parameters[EnumShape.THICKNESS.ordinal()]);
                int side = Integer.parseInt(parameters[EnumShape.FACING_SIDE.ordinal()]);
                list = ShapeGen.buildPlane(player.worldObj, blockUsed, x, y, z, sizeX, sizeY, sizeZ, thickness, side, replace, 2, player);
            }
        }
        return list;
    }
}
