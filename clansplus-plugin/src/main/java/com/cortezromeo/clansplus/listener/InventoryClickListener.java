package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.inventory.ClanPlusInventoryBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClickListener implements Listener {

    public InventoryClickListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ClanPlusInventoryBase)
            ((ClanPlusInventoryBase) holder).handleMenu(event);
    }

}
