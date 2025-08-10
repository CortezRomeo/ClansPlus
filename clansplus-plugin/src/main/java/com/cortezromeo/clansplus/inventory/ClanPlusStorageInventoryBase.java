package com.cortezromeo.clansplus.inventory;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class ClanPlusStorageInventoryBase implements InventoryHolder {

    protected Inventory inventory;
    protected int inventoryNumber;
    protected String clanName;

    public ClanPlusStorageInventoryBase(int inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null)
            inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        setMenuItems();
        return inventory;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public int getInventoryNumber() {
        return inventoryNumber;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setMenuItems();

}
