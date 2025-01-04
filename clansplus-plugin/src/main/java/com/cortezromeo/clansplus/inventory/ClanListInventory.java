package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.ClanListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.HashMapUtil;
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

import java.util.ArrayList;
import java.util.List;

public class ClanListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = ClanListInventoryFile.get();
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
        String title = fileConfiguration.getString("title");
        title = title.replace("%totalClans%", String.valueOf(PluginDataManager.getClanDatabase().size()));
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
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
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
            addPaginatedMenuItems(fileConfiguration);
            if (PluginDataManager.getPlayerDatabase(getOwner().getName()).getClan() != null) {
                ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                        fileConfiguration.getString("items.back.value"),
                        fileConfiguration.getInt("items.back.customModelData"),
                        fileConfiguration.getString("items.back.name"),
                        fileConfiguration.getStringList("items.back.lore")), "back");
                int backItemSlot = fileConfiguration.getInt("items.back.slot");
                if (backItemSlot < 0)
                    backItemSlot = 0;
                if (backItemSlot > 8)
                    backItemSlot = 8;
                backItemSlot = (getSlots() - 9) + backItemSlot;
                inventory.setItem(backItemSlot, backItem);
            }

            ItemStack clanListInfoItem = ClansPlus.nms.addCustomData(
                    getClanInfoItemStack(ItemUtil.getItem(fileConfiguration.getString("items.clanListInfo.type"),
                            fileConfiguration.getString("items.clanListInfo.value"),
                            fileConfiguration.getInt("items.clanListInfo.customModelData"),
                            fileConfiguration.getString("items.clanListInfo.name"),
                            fileConfiguration.getStringList("items.clanListInfo.lore")
                            )), "clanListInfoItem");
            int clanListInfoSlot = fileConfiguration.getInt("items.clanListInfo.slot");
            if (clanListInfoSlot < 0)
                clanListInfoSlot = 0;
            if (clanListInfoSlot > 8)
                clanListInfoSlot = 8;
            clanListInfoSlot = (getSlots() - 9) + clanListInfoSlot;

            inventory.setItem(clanListInfoSlot, clanListInfoItem);

            ItemStack shortItemsItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.shortItems.type"),
                    fileConfiguration.getString("items.shortItems.value"),
                    fileConfiguration.getInt("items.shortItems.customModelData"),
                    fileConfiguration.getString("items.shortItems.name"),
                    fileConfiguration.getStringList("items.shortItems.lore." + shortItemsType.toString())), "shortItemsItem");
            int shortItemsItemSlot = fileConfiguration.getInt("items.shortItems.slot");
            if (shortItemsItemSlot < 0)
                shortItemsItemSlot = 0;
            if (shortItemsItemSlot > 8)
                shortItemsItemSlot = 8;
            shortItemsItemSlot = (getSlots() - 9) + shortItemsItemSlot;
            inventory.setItem(shortItemsItemSlot, shortItemsItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            List<String> clans = new ArrayList<>();

            if (shortItemsType == ShortItemsType.HIGHESTSCORE)
                clans = HashMapUtil.shortFromGreatestToLowestI(ClanManager.getClansScoreHashMap());
            if (shortItemsType == ShortItemsType.HIGHESTWARPOINT)
                clans = HashMapUtil.shortFromGreatestToLowestI(ClanManager.getClansWarpointHashMap());
            if (shortItemsType == ShortItemsType.HIGHESTPLAYERSIZE)
                clans = HashMapUtil.shortFromGreatestToLowestI(ClanManager.getClansPlayerSize());
            if (shortItemsType == ShortItemsType.OLDEST)
                clans = HashMapUtil.shortFromLowestToGreatestL(ClanManager.getClansCreatedDate());

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= clans.size())
                    break;
                if (clans.get(index) != null) {
                    String clanName = clans.get(index);
                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);
                    ItemStack clanItem = ItemUtil.getItem(
                            clanData.getIconType().toString(),
                            clanData.getIconValue(),
                            0,
                            fileConfiguration.getString("items.clan.name"),
                            fileConfiguration.getStringList("items.clan.lore"));
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

    private @NotNull ItemStack getClanInfoItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();

        String NAString = "N/A";
        String bestScoreClanName;
        int bestScoreClanValue;
        String bestWarPointClanName;
        int bestWarPointClanValue;
        String oldestClanName;
        String oldestClanValue;
        if (!PluginDataManager.getClanDatabase().isEmpty()) {
            IClanData bestScoreClan = PluginDataManager.getClanDatabase(HashMapUtil.shortFromGreatestToLowestI(ClanManager.getClansScoreHashMap()).get(0));
            IClanData bestWarPointClan = PluginDataManager.getClanDatabase(HashMapUtil.shortFromGreatestToLowestI(ClanManager.getClansWarpointHashMap()).get(0));
            IClanData oldestClan = PluginDataManager.getClanDatabase(HashMapUtil.shortFromLowestToGreatestL(ClanManager.getClansCreatedDate()).get(0));
            bestScoreClanName = ClanManager.getFormatClanName(bestScoreClan);
            bestWarPointClanName = ClanManager.getFormatClanName(bestWarPointClan);
            oldestClanName = ClanManager.getFormatClanName(oldestClan);
            bestScoreClanValue = bestScoreClan.getScore();
            bestWarPointClanValue = bestWarPointClan.getWarPoint();
            oldestClanValue = StringUtil.dateTimeToDateFormat(oldestClan.getCreatedDate());
        } else {
            bestWarPointClanValue = 0;
            bestScoreClanValue = 0;
            bestScoreClanName = NAString;
            bestWarPointClanName = NAString;
            oldestClanName = NAString;
            oldestClanValue = NAString;
        }

        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%totalClans%",
                        String.valueOf(PluginDataManager.getClanDatabase().size()))
                .replace("%totalPlayers%", String.valueOf(PluginDataManager.getPlayerDatabase().size()))
                .replace("%bestScoreClan%", bestScoreClanName)
                .replace("%bestScoreClanValue%", String.valueOf(bestScoreClanValue))
                .replace("%bestWarPointClan%", bestWarPointClanName)
                .replace("%bestWarPointClanValue%", String.valueOf(bestWarPointClanValue))
                .replace("%oldestClan%", oldestClanName)
                .replace("%oldestClanValue%", oldestClanValue)));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public enum ShortItemsType {
        HIGHESTSCORE, HIGHESTWARPOINT, HIGHESTPLAYERSIZE, OLDEST
    }

}
