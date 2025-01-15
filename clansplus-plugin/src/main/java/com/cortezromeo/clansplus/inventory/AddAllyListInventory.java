package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.RequestAlly;
import com.cortezromeo.clansplus.file.inventory.AddAllyListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AddAllyListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = AddAllyListInventoryFile.get();
    private SortItemType sortItemType;
    private List<String> clans = new ArrayList<>();

    public AddAllyListInventory(Player owner) {
        super(owner);
        sortItemType = SortItemType.ALL;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }
        super.open();
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

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
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
            if (!((index + 1) >= clans.size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new AlliesMenuInventory(getOwner()).open();
        if (itemCustomData.equals("sortItem")) {
            if (sortItemType == SortItemType.ALL)
                sortItemType = SortItemType.REQUESTING;
            else if (sortItemType == SortItemType.REQUESTING)
                sortItemType = SortItemType.ALL;
            page = 0;
            super.open();
        }
        if (itemCustomData.contains("request=")) {
            itemCustomData = itemCustomData.replace("request=", "");
            if (new RequestAlly(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.MANAGEALLY), getOwner(), getOwner().getName(), itemCustomData).execute())
                super.open();
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
            addPaginatedMenuItems(fileConfiguration);
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

            clans.clear();

            if (sortItemType == SortItemType.ALL) {
                if (!PluginDataManager.getClanDatabase().isEmpty())
                    clans.addAll(PluginDataManager.getClanDatabase().keySet());
            }
            if (sortItemType == SortItemType.REQUESTING) {
                if (!PluginDataManager.getClanDatabase().isEmpty())
                    for (String clan : PluginDataManager.getClanDatabase().keySet()) {
                        IClanData clanData = PluginDataManager.getClanDatabase(clan);
                        if (clanData.getAllyInvitation().contains(PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName())) {
                            clans.add(clan);
                        }
                    }
            }

            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());
            Rank requiredRank = playerClanData.getSubjectPermission().get(Subject.MANAGEALLY);
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= clans.size())
                    break;
                if (clans.get(index) != null) {
                    String clanName = clans.get(index);
                    if (clanName.equals(playerClanData.getName()))
                        continue;
                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);
                    ArrayList<String> clanItemLore = new ArrayList<>();
                    ItemStack clanItem = ItemUtil.getItem(
                            clanData.getIconType().toString(),
                            clanData.getIconValue(),
                            0,
                            fileConfiguration.getString("items.clan.name"),
                            fileConfiguration.getStringList("items.clan.lore"), false);
                    ItemMeta clanItemItemMeta = clanItem.getItemMeta();
                    for (String lore : clanItemItemMeta.getLore()) {
                        if (clanData.getAllyInvitation().contains(playerClanData.getName()))
                            lore = lore.replace("%checkRelation%", fileConfiguration.getString("items.clan.placeholders.checkRelation.requesting"));
                        else
                            lore = lore.replace("%checkRelation%", playerClanData.getAllies().contains(clanName) ? fileConfiguration.getString("items.clan.placeholders.checkRelation.true") : fileConfiguration.getString("items.clan.placeholders.checkRelation.false"));
                        lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), requiredRank) ? fileConfiguration.getString("items.clan.placeholders.checkPermission.true")
                                : fileConfiguration.getString("items.clan.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(requiredRank)));
                        clanItemLore.add(lore);
                    }
                    clanItemItemMeta.setLore(clanItemLore);
                    clanItem.setItemMeta(clanItemItemMeta);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(ItemUtil.getClanItemStack(clanItem, clanData), "request=" + clanName);
                    inventory.addItem(itemStack);
                }
            }
        });
    }

    public enum SortItemType {
        ALL, REQUESTING
    }

}
