package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.AllyInvitationInventoryFile;
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

public class AllyInvitationListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = AllyInvitationInventoryFile.get();
    private List<String> clans = new ArrayList<>();

    public AllyInvitationListInventory(Player owner) {
        super(owner);
    }

    @Override
    public void open() {
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
        if (itemCustomData.contains("manage=")) {
            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());
            if (ClanManager.isPlayerRankSatisfied(getOwner().getName(), playerClanData.getSubjectPermission().get(Subject.MANAGEALLY))) {
                itemCustomData = itemCustomData.replace("manage=", "");
                new AllyInvitationConfirmInventory(getOwner(), playerClanData, itemCustomData).open();
            }
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

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            clans.clear();
            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

            if (!playerClanData.getAllyInvitation().isEmpty())
                clans.addAll(playerClanData.getAllyInvitation());

            Rank requiredRank = playerClanData.getSubjectPermission().get(Subject.MANAGEALLY);
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= clans.size())
                    break;
                if (clans.get(index) != null) {
                    String clanName = clans.get(index);
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
                        lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), requiredRank) ? fileConfiguration.getString("items.clan.placeholders.checkPermission.true")
                                : fileConfiguration.getString("items.clan.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(requiredRank)));
                        clanItemLore.add(lore);
                    }
                    clanItemItemMeta.setLore(clanItemLore);
                    clanItem.setItemMeta(clanItemItemMeta);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(ItemUtil.getClanItemStack(clanItem, clanData), "manage=" + clanName);
                    inventory.addItem(itemStack);
                }
            }
        });
    }

}
