package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ClanPlusInventoryBase implements InventoryHolder {

    protected Inventory inventory;
    protected Player owner;

    public ClanPlusInventoryBase(Player owner) {
        this.owner = owner;
    }

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void addBasicButton(FileConfiguration fileConfiguration, boolean backButton) {
        if (fileConfiguration.getBoolean("items.border.enabled")) {
            ItemStack borderItem = ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.border.type").toUpperCase()),
                    fileConfiguration.getString("items.border.value"),
                    fileConfiguration.getInt("items.border.customModelData"),
                    fileConfiguration.getString("items.border.name"),
                    fileConfiguration.getStringList("items.border.lore"), false);
            for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++) {
                List<String> leaveBlankSlots = fileConfiguration.getStringList("items.border.leave-blank");
                if (leaveBlankSlots != null) {
                    if (leaveBlankSlots.contains(String.valueOf(itemSlot)))
                        continue;
                }
                inventory.setItem(itemSlot, ClansPlus.nms.addCustomData(borderItem, "border"));
            }
        }

        ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.close.type").toUpperCase()),
                fileConfiguration.getString("items.close.value"),
                fileConfiguration.getInt("items.close.customModelData"),
                fileConfiguration.getString("items.close.name"),
                fileConfiguration.getStringList("items.close.lore"), false), "close");
        int closeItemSlot = fileConfiguration.getInt("items.close.slot");
        inventory.setItem(closeItemSlot, closeItem);

        if (backButton) {
            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.back.type").toUpperCase()),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            inventory.setItem(backItemSlot, backItem);
        }
    }

    public void playClickSound(FileConfiguration fileConfiguration, String itemName) {
        String itemPath = "items." + itemName + ".click-sound.";
        String soundName = fileConfiguration.getString(itemPath + "name");

        if (!fileConfiguration.getBoolean(itemPath + "enabled") || fileConfiguration.getString(itemPath + "name") == null) {
            return;
        }

        getOwner().playSound(getOwner().getLocation(), ClansPlus.nms.createSound(soundName), fileConfiguration.getInt(itemPath + "volume"), fileConfiguration.getInt(itemPath + "pitch"));
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public boolean handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        return event.getCurrentItem() != null;
    }

    public abstract void setMenuItems();

    public Player getOwner() {
        return owner;
    }

}
