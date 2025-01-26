package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.file.inventory.EventsMenuInventoryFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = EventsMenuInventoryFile.get();
    private BukkitTask bukkitRunnable;

    public EventsMenuInventory(Player owner) {
        super(owner);
    }

    @Override
    public void open() {
        if (!fileConfiguration.getBoolean("auto-update.enabled")) {
            super.open();
            return;
        }

        if (getInventory() == null || !getInventory().getViewers().contains(getOwner())) {
            super.open();
        } else {
            setMenuItems();
            getOwner().updateInventory();
        }

        if (bukkitRunnable == null) {
            bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getInventory().getViewers().isEmpty() || !getOwner().getOpenInventory().getTopInventory().equals(getInventory())) {
                        cancel();
                        return;
                    }
                    open();
                }
            }.runTaskTimerAsynchronously(ClansPlus.nms.getPlugin(), 20, fileConfiguration.getInt("auto-update.seconds") * 20L);
        }
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        return ClansPlus.nms.addColor(title);
    }

    @Override
    public int getSlots() {
        int rows = fileConfiguration.getInt("rows") * 9;
        if (rows < 27 || rows > 54)
            return 54;
        return rows;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("warEvent")) {
            EventManager.getWarEvent().sendEventStatusMessage(getOwner(), false);
            getOwner().closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
            int closeItemSlot = fileConfiguration.getInt("items.close.slot");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            int warEventItemSlot = fileConfiguration.getInt("items.warEvent.slot");
            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"), false);
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++) {
                    if (itemSlot == closeItemSlot || itemSlot == backItemSlot || itemSlot == warEventItemSlot)
                        continue;
                    inventory.setItem(itemSlot, borderItem);
                }
            }

            if (PluginDataManager.getPlayerDatabase(getOwner().getName()).getClan() != null) {
                ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                        fileConfiguration.getString("items.back.value"),
                        fileConfiguration.getInt("items.back.customModelData"),
                        fileConfiguration.getString("items.back.name"),
                        fileConfiguration.getStringList("items.back.lore"), false), "back");
                inventory.setItem(backItemSlot, backItem);
            }

            ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                    fileConfiguration.getString("items.close.value"),
                    fileConfiguration.getInt("items.close.customModelData"),
                    fileConfiguration.getString("items.close.name"),
                    fileConfiguration.getStringList("items.close.lore"), false), "close");
            inventory.setItem(closeItemSlot, closeItem);

            List<String> warEventItemLore = new ArrayList<>();
            for (String lore : EventManager.getWarEvent().isStarting() ? fileConfiguration.getStringList("items.warEvent.lore.starting") : fileConfiguration.getStringList("items.warEvent.lore.not-starting")) {
                lore = lore.replace("%closestTimeFrame%", new SimpleDateFormat("HH:mm:ss").format(new Date(EventManager.getWarEvent().getClosestTimeFrameMillis())));
                lore = lore.replace("%closestTimeFrameTimeLeft%", String.valueOf(StringUtil.getTimeFormat(EventManager.getWarEvent().getClosestTimeFrameTimeLeft())));
                lore = lore.replace("%minimumPlayerOnline%", String.valueOf(EventManager.getWarEvent().MINIMUM_PLAYER_ONLINE));
                lore = lore.replace("%eventTimeLeft%", StringUtil.getTimeFormat(EventManager.getWarEvent().getTimeLeft()));
                if (lore.contains("%eventTimeFrame%")) {
                    for (String warEventTimeFrame : EventManager.getWarEvent().EVENT_TIME_FRAME) {
                        warEventItemLore.add(fileConfiguration.getString("items.warEvent.lore-placeholders.eventTimeFrame").replace("%eventTimeFrame%", warEventTimeFrame));
                    }
                    continue;
                }
                if (lore.contains("%requiredWorlds%")) {
                    for (String warEventRequiredWorld : EventManager.getWarEvent().WORLD_REQUIREMENT_WORLDS) {
                        warEventItemLore.add(fileConfiguration.getString("items.warEvent.lore-placeholders.requiredWorlds").replace("%requiredWorld%", warEventRequiredWorld));
                    }
                    continue;
                }
                warEventItemLore.add(lore);
            }
            ItemStack warEventItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.warEvent.type"),
                    fileConfiguration.getString("items.warEvent.value"),
                    fileConfiguration.getInt("items.warEvent.customModelData"),
                    fileConfiguration.getString("items.warEvent.name"),
                    warEventItemLore, false), "warEvent");
            inventory.setItem(warEventItemSlot, warEventItem);
        });
    }

}
