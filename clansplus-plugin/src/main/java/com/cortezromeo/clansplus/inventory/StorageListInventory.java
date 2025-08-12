package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.subject.OpenStorage;
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

    public StorageListInventory(Player owner) {
        super(owner);
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
    public boolean handleMenu(InventoryClickEvent event) {
        if (!super.handleMenu(event))
            return false;

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);
        IClanData clanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

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

        if (!PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName().equals(clanData.getName()))
            return false;

        if (itemCustomData.startsWith("storage=")) {
            int storageNumber = Integer.parseInt(itemCustomData.replace("storage=" , ""));
            if (clanData.getMaxStorage() < storageNumber) {
                if (event.getClick().isRightClick()) {
                    new UpgradeMenuInventory(getOwner()).open();
                    return true;
                }
            }
            new OpenStorage(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.OPENSTORAGE), getOwner(), getOwner().getName(), storageNumber).execute();
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

            IClanData clanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

            int itemsStored = 0;
            if (!clanData.getStorageHashMap().isEmpty()) {
                for (int clanStorageNumber : clanData.getStorageHashMap().keySet()) {
                    itemsStored = itemsStored + getUsedSlot(clanStorageNumber, clanData);
                }
            }

            List<String> clanStorageInfoLore = fileConfiguration.getStringList("items.clanStorageInfo.lore");
            int finalItemsStored = itemsStored;
            clanStorageInfoLore.replaceAll(string -> ClansPlus.nms.addColor(string
                    .replace("%clanMaxStorage%", String.valueOf(clanData.getMaxStorage()))
                    .replace("%serverMaxStorage%", String.valueOf(Settings.STORAGE_SETTINGS_MAX_INVENTORY))
                    .replace("%itemsStored%", String.valueOf(finalItemsStored))));

            ItemStack clanStorageInfoItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.clanStorageInfo.type").toUpperCase()),
                    fileConfiguration.getString("items.clanStorageInfo.value"),
                    fileConfiguration.getInt("items.clanStorageInfo.customModelData"),
                    fileConfiguration.getString("items.clanStorageInfo.name"),
                    clanStorageInfoLore, false), "clanStorageInfo");
            int clanStorageInfoItemSlot = fileConfiguration.getInt("items.clanStorageInfo.slot");
            inventory.setItem(clanStorageInfoItemSlot, ItemUtil.getClanItemStack(clanStorageInfoItem, clanData));

            storages.clear();
            for (int maxStorage = 1; maxStorage <= Settings.STORAGE_SETTINGS_MAX_INVENTORY; maxStorage++) {
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
                    if (clanData.getMaxStorage() < storageNumber)
                        storageType = "locked.";
                    else
                        storageType = "unlocked.";

                    List<String> storageItemLore = fileConfiguration.getStringList(storageItemPath + storageType + "lore");
                    storageItemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%usedSlots%", String.valueOf(getUsedSlot(storageNumber, clanData)))));

                    ItemStack storageItem = ItemUtil.getItem(
                            ItemType.valueOf(fileConfiguration.getString(storageItemPath + storageType + "type").toUpperCase()),
                            fileConfiguration.getString(storageItemPath + storageType + "value"),
                            fileConfiguration.getInt(storageItemPath + storageType + "customModelData"),
                            fileConfiguration.getString(storageItemPath + storageType + "name").replace("%storageNumber%", String.valueOf(storageNumber)),
                            storageItemLore, false);
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

    int getUsedSlot(int storageNumber, IClanData clanData) {
        if (!clanData.getStorageHashMap().containsKey(storageNumber))
            return 0;

        int usedSlot = 0;
        for (ItemStack itemStack : clanData.getStorageHashMap().get(storageNumber).getContents()) {
            if (itemStack == null)
                continue;

            if (ClansPlus.nms.getCustomData(itemStack).equals("next") || ClansPlus.nms.getCustomData(itemStack).equals("previous") || ClansPlus.nms.getCustomData(itemStack).equals("noStorage"))
                continue;

            usedSlot = usedSlot + 1;
        }

        return usedSlot;
    }
}
