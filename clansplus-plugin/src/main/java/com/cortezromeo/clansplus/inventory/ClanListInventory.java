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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = ClanListInventoryFile.get();
    private SortItemType sortItemType;

    public ClanListInventory(Player owner) {
        super(owner);
        sortItemType = SortItemType.HIGHESTSCORE;
    }

    @Override
    public void open() {
        super.open();
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
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("sortItem")) {
            if (sortItemType == SortItemType.HIGHESTSCORE)
                sortItemType = SortItemType.HIGHESTWARPOINT;
            else if (sortItemType == SortItemType.HIGHESTWARPOINT)
                sortItemType = SortItemType.HIGHESTPLAYERSIZE;
            else if (sortItemType == SortItemType.HIGHESTPLAYERSIZE)
                sortItemType = SortItemType.OLDEST;
            else if (sortItemType == SortItemType.OLDEST)
                sortItemType = SortItemType.HIGHESTSCORE;
            page = 0;
            super.open();
        }
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.contains("clan=")) {
            itemCustomData = itemCustomData.replace("clan=", "");
            new ViewClanInventory(getOwner(), itemCustomData).open();
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
                        fileConfiguration.getStringList("items.back.lore"), false), "back");
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
                            , false)), "clanListInfoItem");
            int clanListInfoSlot = fileConfiguration.getInt("items.clanListInfo.slot");
            if (clanListInfoSlot < 0)
                clanListInfoSlot = 0;
            if (clanListInfoSlot > 8)
                clanListInfoSlot = 8;
            clanListInfoSlot = (getSlots() - 9) + clanListInfoSlot;

            inventory.setItem(clanListInfoSlot, clanListInfoItem);

            ItemStack sortItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.sortItem.type"),
                    fileConfiguration.getString("items.sortItem.value"),
                    fileConfiguration.getInt("items.sortItem.customModelData"),
                    fileConfiguration.getString("items.sortItem.name"),
                    fileConfiguration.getStringList("items.sortItem.lore." + sortItemType.toString()), false), "sortItem");
            int sortItemSlot = fileConfiguration.getInt("items.sortItem.slot");
            if (sortItemSlot < 0)
                sortItemSlot = 0;
            if (sortItemSlot > 8)
                sortItemSlot = 8;
            sortItemSlot = (getSlots() - 9) + sortItemSlot;
            inventory.setItem(sortItemSlot, sortItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            List<String> clans = new ArrayList<>();

            if (sortItemType == SortItemType.HIGHESTSCORE)
                clans = HashMapUtil.sortFromGreatestToLowestI(ClanManager.getClansScoreHashMap());
            if (sortItemType == SortItemType.HIGHESTWARPOINT)
                clans = HashMapUtil.sortFromGreatestToLowestL(ClanManager.getClansWarpointHashMap());
            if (sortItemType == SortItemType.HIGHESTPLAYERSIZE)
                clans = HashMapUtil.sortFromGreatestToLowestI(ClanManager.getClansPlayerSize());
            if (sortItemType == SortItemType.OLDEST)
                clans = HashMapUtil.sortFromLowestToGreatestL(ClanManager.getClansCreatedDate());

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
                            fileConfiguration.getStringList("items.clan.lore"), false);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(ItemUtil.getClanItemStack(clanItem, clanData), "clan=" + clanName);
                    inventory.addItem(itemStack);
                }
            }
        });
    }

    private @NotNull ItemStack getClanInfoItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();

        String NAString = "N/A";
        String bestScoreClanName;
        int bestScoreClanValue;
        String bestWarPointClanName;
        long bestWarPointClanValue;
        String oldestClanName;
        String oldestClanValue;
        if (!PluginDataManager.getClanDatabase().isEmpty()) {
            IClanData bestScoreClan = PluginDataManager.getClanDatabase(HashMapUtil.sortFromGreatestToLowestI(ClanManager.getClansScoreHashMap()).get(0));
            IClanData bestWarPointClan = PluginDataManager.getClanDatabase(HashMapUtil.sortFromGreatestToLowestL(ClanManager.getClansWarpointHashMap()).get(0));
            IClanData oldestClan = PluginDataManager.getClanDatabase(HashMapUtil.sortFromLowestToGreatestL(ClanManager.getClansCreatedDate()).get(0));
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

    public enum SortItemType {
        HIGHESTSCORE, HIGHESTWARPOINT, HIGHESTPLAYERSIZE, OLDEST
    }

}
