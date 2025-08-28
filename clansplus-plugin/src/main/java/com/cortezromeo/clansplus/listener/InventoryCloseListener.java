package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.inventory.ClanPlusStorageInventoryBase;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryCloseListener implements Listener {

    public InventoryCloseListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof ClanPlusStorageInventoryBase inventoryBase) {
/*            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack == null)
                    continue;
                if (ClansPlus.nms.getCustomData(itemStack).equals("next") || ClansPlus.nms.getCustomData(itemStack).equals("previous") || ClansPlus.nms.getCustomData(itemStack).equals("noStorage"))
                    inventory.remove(itemStack);
                MessageUtil.devMessage(itemStack.getType().toString());
            }
            IClanData clanData = PluginDataManager.getClanDatabase(inventoryBase.getClanName());
            clanData.getStorageHashMap().put(inventoryBase.getStorageNumber(), inventory);*/
            PluginDataManager.saveClanDatabaseToStorage(inventoryBase.getClanName());
        }
    }
}
