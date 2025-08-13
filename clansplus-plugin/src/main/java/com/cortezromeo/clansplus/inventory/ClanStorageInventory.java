package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.ClanStorageInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClanStorageInventory extends ClanPlusStorageInventoryBase {

    FileConfiguration fileConfiguration = ClanStorageInventoryFile.get();

    public ClanStorageInventory(int storageNumber) {
        super(storageNumber);
    }

    @Override
    public String getMenuName() {
        return ClansPlus.nms.addColor(fileConfiguration.getString("title").replace("%storageNumber%", String.valueOf(storageNumber)));
    }

    @Override
    public int getSlots() {
        return Settings.STORAGE_SETTINGS_SLOTS;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null)
            return;

        if (!player.isOp() || !player.hasPermission("clanplus.admin")) {
            if (!ClanManager.isPlayerInClan(player)) {
                player.closeInventory();
                MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
                return;
            }

            if (!PluginDataManager.getPlayerDatabase(player.getName()).getClan().equals(clanName)) {
                player.closeInventory();
                return;
            }

            if (PluginDataManager.getClanDatabase(clanName) == null) {
                player.closeInventory();
                MessageUtil.sendMessage(player, Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
                return;
            }
        }

        if (ClansPlus.nms.getCustomData(itemStack).equals("noStorage")) {
            event.setCancelled(true);
            if (buttonClickExecutive(event, player, true))
                return;
        }

        if (ClansPlus.nms.getCustomData(itemStack).equals("next")) {
            event.setCancelled(true);

            if (buttonClickExecutive(event, player, false))
                return;

            ClanManager.openClanStorage(player, PluginDataManager.getPlayerDatabase(player.getName()).getClan(), storageNumber + 1);
        }

        if (ClansPlus.nms.getCustomData(itemStack).equals("previous")) {
            event.setCancelled(true);

            if (getStorageNumber() <= 1)
                return;

            if (buttonClickExecutive(event, player, false))
                return;

            ClanManager.openClanStorage(player, PluginDataManager.getPlayerDatabase(player.getName()).getClan(), storageNumber - 1);
        }
    }

    public boolean buttonClickExecutive(InventoryClickEvent event, Player player, boolean noStorages) {
        if (event.getClick().isRightClick()) {
            if (event.getClick().isShiftClick() && noStorages) {
                new UpgradeMenuInventory(player).open();
            } else
                new StorageListInventory(player).open();
            return true;
        }
        return false;
    }

    @Override
    public void setMenuItems() {
        if (getClanName() == null || PluginDataManager.getClanDatabase(getClanName()) == null) {
            return;
        }

        ItemStack noStorageItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.noStorage.type").toUpperCase()),
                fileConfiguration.getString("items.noStorage.value"),
                fileConfiguration.getInt("items.noStorage.customModelData"),
                fileConfiguration.getString("items.noStorage.name"),
                fileConfiguration.getStringList("items.noStorage.lore"), false), "noStorage");

        ItemStack nextPageItem = ClansPlus.nms.addCustomData(getStorageItemStack(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.next.type").toUpperCase()),
                fileConfiguration.getString("items.next.value"),
                fileConfiguration.getInt("items.next.customModelData"),
                fileConfiguration.getString("items.next.name"),
                fileConfiguration.getStringList("items.next.lore"), false), true), "next");
        int nextPageItemSlot = fileConfiguration.getInt("items.next.slot");
        inventory.setItem(nextPageItemSlot, storageNumber >= PluginDataManager.getClanDatabase(getClanName()).getMaxStorage() ? getStorageItemStack(noStorageItem, true) : nextPageItem);

        ItemStack previousPageItem = ClansPlus.nms.addCustomData(getStorageItemStack(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.previous.type").toUpperCase()),
                fileConfiguration.getString("items.previous.value"),
                fileConfiguration.getInt("items.previous.customModelData"),
                fileConfiguration.getString("items.previous.name"),
                fileConfiguration.getStringList("items.previous.lore"), false), false), "previous");
        int previousPageItemSlot = fileConfiguration.getInt("items.previous.slot");
        inventory.setItem(previousPageItemSlot, storageNumber <= 1 ? getStorageItemStack(noStorageItem, false) : previousPageItem);
    }

    private @NotNull ItemStack getStorageItemStack(ItemStack itemStack, boolean nextStorage) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        int prevStorageNumber = getStorageNumber() - 1;
        int nextStorageNumber = getStorageNumber() + 1;
        int usedSlots = getUsedSlot(nextStorage ? nextStorageNumber : prevStorageNumber);

        String displayName = itemMeta.getDisplayName();
        displayName = displayName.replace("%previousStorageNumber%", String.valueOf(prevStorageNumber))
                .replace("%nextStorageNumber%", String.valueOf(nextStorageNumber));
        itemMeta.setDisplayName(displayName);

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> string
                .replace("%usedSlots%", String.valueOf(usedSlots))
                .replace("%clanMaxStorageNumber%", String.valueOf(PluginDataManager.getClanDatabase(getClanName()).getMaxStorage()))
                .replace("%currentStorageNumber%", String.valueOf(storageNumber)));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    int getUsedSlot(int storageNumber) {
        if (getClanName() == null)
            return 0;

        if (!PluginDataManager.getClanDatabase(getClanName()).getStorageHashMap().containsKey(storageNumber))
            return 0;

        int usedSlot = 0;
        for (ItemStack itemStack : PluginDataManager.getClanDatabase(getClanName()).getStorageHashMap().get(storageNumber).getContents()) {
            if (itemStack == null)
                continue;

            if (ClansPlus.nms.getCustomData(itemStack).equals("next") || ClansPlus.nms.getCustomData(itemStack).equals("previous") || ClansPlus.nms.getCustomData(itemStack).equals("noStorage"))
                continue;

            usedSlot = usedSlot + 1;
        }

        return usedSlot;
    }
}
