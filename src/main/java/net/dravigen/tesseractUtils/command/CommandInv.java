package net.dravigen.tesseractUtils.command;

import net.dravigen.tesseractUtils.TesseractUtilsAddon;
import net.dravigen.tesseractUtils.inventory.InventoryDataManager;
import net.dravigen.tesseractUtils.inventory.InventoryEntry;
import net.minecraft.src.*;

import java.util.List;
import java.util.ArrayList;

public class CommandInv extends CommandBase {

    @Override
    public String getCommandName() {
        return "inv";
    }
    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] strings) {
        if (strings.length==1) {
            return getListOfStringsMatchingLastWord(strings, "save", "load", "remove", "preset");
        }
        if (strings.length==2) {
            for (String s:new String[]{"load", "remove"}){
                if (strings[0].equalsIgnoreCase(s)){
                    List<String> invList = new ArrayList<>();
                    for (InventoryEntry inv: TesseractUtilsAddon.globalSavedInventories.getInventories()){
                        invList.add(inv.getName());
                    }
                    if (!invList.isEmpty()) return getListOfStringsFromIterableMatchingLastWord(strings,invList);
                }
            }
        }
        return null;
    }
    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/inv <save> <name> OR /inv <load> <name> OR /inv <remove> <name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP player)) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("This command can only be used by a player."));
            return;
        }

        if (args.length < 2) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cUsage: " + getCommandUsage(sender))); // §c for red text
            return;
        }

        String subCommand = args[0].toLowerCase();
        String inventoryName = args[1];

        switch (subCommand) {
            case "save":
                savePlayerInventory(player, inventoryName);
                break;
            case "load":
                loadPlayerInventory(player, inventoryName);
                break;
            case "remove":
                removeSavedInventoryWithName(player,inventoryName);
                break;
            default:
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cUnknown subcommand: " + subCommand));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("§cUsage: " + getCommandUsage(sender)));
                break;
        }
    }


    private void savePlayerInventory(EntityPlayerMP player, String name) {
        List<ItemStack> currentInventory = new ArrayList<>();
        for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
            ItemStack var2 = ((Slot)player.inventoryContainer.inventorySlots.get(i)).getStack();
            currentInventory.add(var2);
        }

        InventoryEntry newEntry = new InventoryEntry(name, currentInventory);

        TesseractUtilsAddon.globalSavedInventories.removeInventoryByName(name);
        TesseractUtilsAddon.globalSavedInventories.addInventory(newEntry);

        InventoryDataManager.saveInventories(TesseractUtilsAddon.globalSavedInventories);

        player.sendChatToPlayer(ChatMessageComponent.createFromText("§aInventory '" + name + "' saved successfully!")); // §a for green text
    }

    private void loadPlayerInventory(EntityPlayerMP player, String name) {
        InventoryEntry entryToLoad = TesseractUtilsAddon.globalSavedInventories.getInventoryByName(name);

        if (entryToLoad == null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cInventory '" + name + "' not found!"));
            return;
        }
        List<ItemStack> items = entryToLoad.getItems();
        for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
            ((Slot) player.inventoryContainer.inventorySlots.get(i)).putStack(items.get(i));
        }

        player.inventory.onInventoryChanged();

        player.sendChatToPlayer(ChatMessageComponent.createFromText("§aInventory '" + name + "' loaded successfully!"));
    }

    private void removeSavedInventoryWithName(EntityPlayerMP player, String name){
        InventoryEntry entryToLoad = TesseractUtilsAddon.globalSavedInventories.getInventoryByName(name);
        if (entryToLoad == null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§cInventory '" + name + "' not found!"));
        }else {
            TesseractUtilsAddon.globalSavedInventories.removeInventoryByName(name);
            InventoryDataManager.saveInventories(TesseractUtilsAddon.globalSavedInventories);
            player.sendChatToPlayer(ChatMessageComponent.createFromText("§aInventory '" + name + "' removed successfully!")); // §a for green text
        }
    }
}