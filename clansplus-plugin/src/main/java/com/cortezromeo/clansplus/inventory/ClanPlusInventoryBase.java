package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

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

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setMenuItems();

    public Player getOwner() {
        return owner;
    }

}
