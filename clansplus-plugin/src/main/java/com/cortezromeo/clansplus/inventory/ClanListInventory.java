package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.ClanListInventoryFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ClanListInventory extends PaginatedInventory {

    private ShortItemsType shortItemsType;
    private BukkitTask bukkitRunnable;

    public ClanListInventory(Player owner) {
        super(owner);
        shortItemsType = ShortItemsType.HIGHESTSCORE;
    }

    @Override
    public void open() {
        if (getInventory() == null || !getInventory().getViewers().contains(getOwner()))
            super.open();
        else {
            //setMenuItems();
            //getOwner().updateInventory();
            super.open();
        }

/*        if (bukkitRunnable == null) {
            bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getInventory().getViewers().isEmpty() || !getOwner().getOpenInventory().getTopInventory().equals(getInventory())) {
                        cancel();
                        return;
                    }
                    open();
                }
            }.runTaskTimerAsynchronously(ClansPlus.nms.getPlugin(), 20, 20);
        }*/
    }

    @Override
    public String getMenuName() {
        String title = ClanListInventoryFile.get().getString("title");
        title = title.replace("%totalClans%", String.valueOf(PluginDataManager.getClanDatabase().size()));
        return ClansPlus.nms.addColor(title);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("prevPage")) {
            if (page != 0) {
                page = page - 1;
                open();
            }
        }
        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= PluginDataManager.getClanDatabase().size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.devMessage(getOwner(), "You're already on the last page.");
            }
        }
        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("shortItemsItem")) {
            if (shortItemsType == ShortItemsType.HIGHESTSCORE)
                shortItemsType = ShortItemsType.HIGHESTWARPOINT;
            else if (shortItemsType == ShortItemsType.HIGHESTWARPOINT)
                shortItemsType = ShortItemsType.HIGHESTPLAYERSIZE;
            else if (shortItemsType == ShortItemsType.HIGHESTPLAYERSIZE)
                shortItemsType = ShortItemsType.OLDEST;
            else if (shortItemsType == ShortItemsType.OLDEST)
                shortItemsType = ShortItemsType.HIGHESTSCORE;
            super.open();
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
            addPaginatedMenuItems();
            FileConfiguration invFileConfig = ClanListInventoryFile.get();

            ItemStack clanListInfoItem = ClansPlus.nms.addCustomData(
                    getSessionInfoItemStack(ItemUtil.getItem(invFileConfig.getString("items.clanListInfo.type"),
                            invFileConfig.getString("items.clanListInfo.value"),
                            invFileConfig.getInt("items.clanListInfo.customModelData"),
                            invFileConfig.getString("items.clanListInfo.name"),
                            invFileConfig.getStringList("items.clanListInfo.lore")
                            )), "clanListInfoItem");
            int sessionInfoItemSlot = invFileConfig.getInt("items.clanListInfo.slot");
            if (sessionInfoItemSlot < 0)
                sessionInfoItemSlot = 0;
            if (sessionInfoItemSlot > 8)
                sessionInfoItemSlot = 8;
            sessionInfoItemSlot = 45 + sessionInfoItemSlot;

            inventory.setItem(sessionInfoItemSlot, clanListInfoItem);

            ItemStack shortItemsItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(invFileConfig.getString("items.shortItems.type"),
                    invFileConfig.getString("items.shortItems.value"),
                    invFileConfig.getInt("items.shortItems.customModelData"),
                    invFileConfig.getString("items.shortItems.name"),
                    invFileConfig.getStringList("items.shortItems.lore." + shortItemsType.toString())), "shortItemsItem");
            int shortItemsItemSlot = invFileConfig.getInt("items.shortItems.slot");
            if (shortItemsItemSlot < 0)
                shortItemsItemSlot = 0;
            if (shortItemsItemSlot > 8)
                shortItemsItemSlot = 8;
            shortItemsItemSlot = 45 + shortItemsItemSlot;
            inventory.setItem(shortItemsItemSlot, shortItemsItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            List<String> clans = new ArrayList<>();

            if (shortItemsType == ShortItemsType.HIGHESTSCORE) {
                HashMap<String, Integer> clansScoreHashMap = ClanManager.getClansScoreHashMap();
                clansScoreHashMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> clans.add(entry.getKey()));
            }

            if (shortItemsType == ShortItemsType.HIGHESTWARPOINT) {
                HashMap<String, Integer> clansScoreHashMap = ClanManager.getClansWarpointHashMap();
                clansScoreHashMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> clans.add(entry.getKey()));
            }

            if (shortItemsType == ShortItemsType.HIGHESTPLAYERSIZE) {
                HashMap<String, Integer> clansScoreHashMap = ClanManager.getClansPlayerSize();
                clansScoreHashMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> clans.add(entry.getKey()));
            }

            if (shortItemsType == ShortItemsType.OLDEST) {
                HashMap<String, Long> clansScoreHashMap = ClanManager.getClansCreatedDate();
                clansScoreHashMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())  // Sort by value
                        .forEach(entry -> clans.add(entry.getKey()));
            }

            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= clans.size())
                    break;
                if (clans.get(index) != null) {
                    String clanName = clans.get(index);
                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);
                    ItemStack clanItem = ItemUtil.getItem(clanData.getIconType().toString(),
                            clanData.getIconValue(),
                            0,
                            invFileConfig.getString("items.clan.name"),
                            invFileConfig.getStringList("items.clan.lore"));
                    ItemStack itemStack = ClansPlus.nms.addCustomData(getClanItemStack(clanItem, clanData), "inventoryItem");
                    inventory.addItem(itemStack);
                }
            }
        });
    }

    private @NotNull ItemStack getClanItemStack(ItemStack itemStack, IClanData clanData) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = itemName.replace("%formatClanName%", ClanManager.getFormatClanName(clanData));
        itemName = itemName.replace("%clanName%", clanData.getName());
        itemName = itemName.replace("%clanCustomName%", ClanManager.getFormatCustomName(clanData));
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%score%", String.valueOf(clanData.getScore()))
                .replace("%warPoint%", String.valueOf(clanData.getWarPoint()))
                .replace("%formatClanName%", ClanManager.getFormatClanName(clanData))
                .replace("%clanName%", String.valueOf(clanData.getName()))
                .replace("%clanCustomName%", ClanManager.getFormatCustomName(clanData)))
                .replace("%owner%", String.valueOf(clanData.getOwner()))
                .replace("%memberSize%", String.valueOf(clanData.getMembers().size()))
                .replace("%maxMember%", String.valueOf(clanData.getMaxMember()))
                .replace("%allySize%", String.valueOf(clanData.getAllies().size()))
                .replace("%message%", ClanManager.getFormatMessage(clanData))
                .replace("%createdDate%", StringUtil.dateTimeToDateFormat(clanData.getCreatedDate()))
                .replace("%warning%", String.valueOf(clanData.getWarning())));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    private @NotNull ItemStack getSessionInfoItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%totalClans%",
                        String.valueOf(PluginDataManager.getClanDatabase().size()))
                .replace("%totalPlayers%", String.valueOf(PluginDataManager.getPlayerDatabase().size()))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public enum ShortItemsType {
        HIGHESTSCORE, HIGHESTWARPOINT, HIGHESTPLAYERSIZE, OLDEST
    }

}
