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
    private String clanName;
    private boolean fromViewClan;

    public AllyListInventory(Player owner, String clanName, boolean fromViewClan) {
        super(owner);
        this.clanName = clanName;
        this.fromViewClan = fromViewClan;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            return;
        }
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        title = title.replace("%search%", getSearch() != null ? fileConfiguration.getString("title-placeholders.search").replace("%search%", getSearch()) : "");
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

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        super.handleMenu(event);
        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("prevPage")) {
            if (getPage() != 0) {
                setPage(getPage() - 1);
                open();
            }
        }
        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= allies.size())) {
                setPage(getPage() + 1);
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());

        if (itemCustomData.equals("back")) {
            if (fromViewClan) {
                new ViewClanInformationInventory(getOwner(), clanName).open();
                return;
            }
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {
                    new AlliesMenuInventory(getOwner()).open();
                    return;
                }
            }
            new ViewClanInformationInventory(getOwner(), clanName).open();
        }

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null)
            return;

        if (!PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName().equals(clanName))
            return;

        if (itemCustomData.contains("ally=")) {
            playClickSound(fileConfiguration, "clan");
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {
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
            if (!PluginDataManager.getClanDatabase(clanName).getAllies().isEmpty()) {
                allies.addAll(PluginDataManager.getClanDatabase(clanName).getAllies());
            }

            if (getSearch() != null) {
                List<String> newAllies = new ArrayList<>();
                for (String ally : allies) {
                    if (ally.toLowerCase().contains(getSearch().toLowerCase())) {
                        newAllies.add(ally);
                    }
                }
                allies.clear();
                allies.addAll(newAllies);
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * getPage() + i;
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
