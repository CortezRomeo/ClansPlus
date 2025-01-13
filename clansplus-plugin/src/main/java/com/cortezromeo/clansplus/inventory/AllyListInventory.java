package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.file.inventory.AllyListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AllyListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = AllyListInventoryFile.get();
    private List<String> allies = new ArrayList<>();
    private IClanData clanData;

    public AllyListInventory(Player owner, IClanData clanData) {
        super(owner);
        this.clanData = clanData;
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        title = title.replace("%totalMembers%", String.valueOf(PluginDataManager.getClanDatabase().size()));
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
            if (!((index + 1) >= allies.size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());

        if (itemCustomData.equals("back")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanData.getName())) {
                    new AlliesMenuInventory(getOwner()).open();
                    return;
                }
            }
            new ViewClanInventory(getOwner(), clanData.getName()).open();
        }
        if (itemCustomData.contains("ally=")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanData.getName())) {
                    itemCustomData = itemCustomData.replace("ally=", "");
                    new ManageAllyInventory(getOwner(), itemCustomData).open();
                    return;
                }
            }
            MessageUtil.sendMessage(getOwner(), Messages.TARGET_CLAN_ALLY_MEMBERSHIP_ERROR.replace("%clan%", itemCustomData.replace("ally=", "")));
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

            allies.clear();
            if (!clanData.getAllies().isEmpty()) {
                allies.addAll(clanData.getAllies());
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= allies.size())
                    break;
                if (allies.get(index) != null) {
                    String clanName = allies.get(index);
                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);
                    ItemStack clanItem = ItemUtil.getItem(
                            clanData.getIconType().toString(),
                            clanData.getIconValue(),
                            0,
                            fileConfiguration.getString("items.clan.name"),
                            fileConfiguration.getStringList("items.clan.lore"), false);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(ItemUtil.getClanItemStack(clanItem, clanData), "ally=" + clanName);
                    inventory.addItem(itemStack);
                }
            }
        });
    }

}
