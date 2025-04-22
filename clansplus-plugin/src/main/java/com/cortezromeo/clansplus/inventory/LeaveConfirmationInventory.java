package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.clan.subject.Leave;
import com.cortezromeo.clansplus.file.inventory.LeaveConfirmationInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveConfirmationInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = LeaveConfirmationInventoryFile.get();

    public LeaveConfirmationInventory(Player owner) {
        super(owner);
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }
        if (PluginDataManager.getPlayerDatabase(getOwner().getName()).getRank() == Rank.LEADER) {
            MessageUtil.sendMessage(getOwner(), Messages.LEADER_CANNOT_LEAVE);
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
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("confirm")) {
            if (new Leave(getOwner(), getOwner().getName()).execute())
                getOwner().closeInventory();
            else
                new ClanMenuInventory(getOwner()).open();
        }
        if (itemCustomData.equals("decline")) {
            new ClanMenuInventory(getOwner()).open();
        }
    }

    @Override
    public void setMenuItems() {
        ClansPlus.plugin.foliaLib.getScheduler().runAsync(task -> {
            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"), false);
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                    inventory.setItem(itemSlot, ClansPlus.nms.addCustomData(borderItem, "border"));
            }

            ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                    fileConfiguration.getString("items.close.value"),
                    fileConfiguration.getInt("items.close.customModelData"),
                    fileConfiguration.getString("items.close.name"),
                    fileConfiguration.getStringList("items.close.lore"), false), "close");
            int closeItemSlot = fileConfiguration.getInt("items.close.slot");
            inventory.setItem(closeItemSlot, closeItem);

            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            inventory.setItem(backItemSlot, backItem);

            ItemStack confirmItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.confirm.type"),
                    fileConfiguration.getString("items.confirm.value"),
                    fileConfiguration.getInt("items.confirm.customModelData"),
                    fileConfiguration.getString("items.confirm.name"),
                    fileConfiguration.getStringList("items.confirm.lore"), false), "confirm");
            int confirmItemSlot = fileConfiguration.getInt("items.confirm.slot");
            inventory.setItem(confirmItemSlot, confirmItem);

            ItemStack declineItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.decline.type"),
                    fileConfiguration.getString("items.decline.value"),
                    fileConfiguration.getInt("items.decline.customModelData"),
                    fileConfiguration.getString("items.decline.name"),
                    fileConfiguration.getStringList("items.decline.lore"), false), "decline");
            int declineItemSlot = fileConfiguration.getInt("items.decline.slot");
            inventory.setItem(declineItemSlot, declineItem);
        });
    }

}
