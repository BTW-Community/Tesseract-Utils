package net.dravigen.tesseractUtils.inventory;// You'll need to create this class yourself in your mod's source folder

// Example using MCP names. Replace with obfuscated names when building.

import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class InventoryEntry {
    private String name;
    private List<String> itemStackNbtBase64;

    public InventoryEntry() {
        this.itemStackNbtBase64 = new ArrayList<>();
    }

    public InventoryEntry(String name, List<ItemStack> items) {
        this.name = name;
        this.itemStackNbtBase64 = new ArrayList<>();
        for (ItemStack stack : items) {
            this.itemStackNbtBase64.add(itemStackToBase64(stack));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (String base64Nbt : itemStackNbtBase64) {
            ItemStack stack = base64ToItemStack(base64Nbt);
            items.add(stack);
        }
        return items;
    }

    private String itemStackToBase64(ItemStack stack) {
        if (stack == null) {
            stack = new ItemStack(Item.minecartHopper);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        stack.writeToNBT(nbt);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressedStreamTools.write(nbt, new DataOutputStream(outputStream));

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private ItemStack base64ToItemStack(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }
        byte[] rawNbt = Base64.getDecoder().decode(base64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawNbt);
        NBTTagCompound nbt = CompressedStreamTools.read(new DataInputStream(inputStream));
        ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbt);
        return itemStack.getItem() == Item.minecartHopper ? null : itemStack;
    }
}