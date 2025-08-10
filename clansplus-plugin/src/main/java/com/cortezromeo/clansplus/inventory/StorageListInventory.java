package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.StorageListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.google.common.primitives.Ints;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StorageListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = StorageListInventoryFile.get();
    private List<Integer> storages = new ArrayList<>();
    private String clanName;

    public StorageListInventory(Player owner, String clanName) {
        super(owner);
        this.clanName = clanName;
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
    public boolean handleMenu(InventoryClickEvent event) {
        if (!super.handleMenu(event))
            return false;

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            getOwner().closeInventory();
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("prevPage")) {
            if (getPage() != 0) {
                setPage(getPage() - 1);
                open();
            }
        }

        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= storages.size())) {
                setPage(getPage() + 1);
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();

        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null)
            return false;

        if (!PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName().equals(clanName))
            return false;

        if (itemCustomData.startsWith("storage=")) {
            int storageNumber = Integer.parseInt(itemCustomData.replace("storage=" , ""));
            ClanManager.openInventory(getOwner(), clanName, storageNumber);
        }

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            ItemStack prevItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.prevPage.type").toUpperCase()),
                    fileConfiguration.getString("items.prevPage.value"),
                    fileConfiguration.getInt("items.prevPage.customModelData"),
                    fileConfiguration.getString("items.prevPage.name"),
                    fileConfiguration.getStringList("items.prevPage.lore"), false), "prevPage");
            int prevPageItemSlot = fileConfiguration.getInt("items.prevPage.slot");

            ItemStack nextItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.nextPage.type").toUpperCase()),
                    fileConfiguration.getString("items.nextPage.value"),
                    fileConfiguration.getInt("items.nextPage.customModelData"),
                    fileConfiguration.getString("items.nextPage.name"),
                    fileConfiguration.getStringList("items.nextPage.lore"), false), "nextPage");
            int nextPageItemSlot = fileConfiguration.getInt("items.nextPage.slot");

            if (page > 0)
                inventory.setItem(prevPageItemSlot, getPageItemStack(prevItem));
            inventory.setItem(nextPageItemSlot, getPageItemStack(nextItem));

            storages.clear();
            for (int maxStorage = 1; maxStorage <= Settings.INVENTORY_SETTINGS_MAX_INVENTORY; maxStorage++) {
                storages.add(maxStorage);
            }

            String storageItemPath = "items.storage.";

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * getPage() + i;
                if (index >= storages.size())
                    break;
                if (storages.get(index) != null) {
                    int storageNumber = storages.get(index);

                    String storageType;
                    if (PluginDataManager.getClanDatabase(clanName).getMaxInventory() < storageNumber)
                        storageType = "locked.";
                    else
                        storageType = "unlocked.";

                    ItemStack storageItem = ItemUtil.getItem(
                            ItemType.valueOf(fileConfiguration.getString(storageItemPath + storageType + "type").toUpperCase()),
                            fileConfiguration.getString(storageItemPath + storageType + "value"),
                            fileConfiguration.getInt(storageItemPath + storageType + "customModelData"),
                            fileConfiguration.getString(storageItemPath + storageType + "name").replace("%storageNumber%", String.valueOf(storageNumber)),
                            fileConfiguration.getStringList(storageItemPath + storageType + "lore"), false);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(storageItem, "storage=" + storageNumber);
                    inventory.setItem(transferSlot(i), itemStack);
                }
            }
        });
    }

    public int transferSlot(int number) {
        int index = number % getStorageTrack().length;
        return getStorageTrack()[index];
    }

    public int[] getStorageTrack() {
        List<Integer> skillTrackList = fileConfiguration.getIntegerList("storage-track");
        return Ints.toArray(skillTrackList);
    }

    @Override
    public int getMaxItemsPerPage() {
        return getStorageTrack().length;
    }
}
