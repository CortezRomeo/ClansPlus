package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.clan.subject.Leave;
import com.cortezromeo.clansplus.file.inventory.LeaveConfirmationInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
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
    public boolean handleMenu(InventoryClickEvent event) {
        if (!super.handleMenu(event))
            return false;

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            getOwner().closeInventory();
            return false;
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

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            ItemStack confirmItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.confirm.type").toUpperCase()),
                    fileConfiguration.getString("items.confirm.value"),
                    fileConfiguration.getInt("items.confirm.customModelData"),
                    fileConfiguration.getString("items.confirm.name"),
                    fileConfiguration.getStringList("items.confirm.lore"), false), "confirm");
            int confirmItemSlot = fileConfiguration.getInt("items.confirm.slot");
            inventory.setItem(confirmItemSlot, confirmItem);

            ItemStack declineItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.decline.type").toUpperCase()),
                    fileConfiguration.getString("items.decline.value"),
                    fileConfiguration.getInt("items.decline.customModelData"),
                    fileConfiguration.getString("items.decline.name"),
                    fileConfiguration.getStringList("items.decline.lore"), false), "decline");
            int declineItemSlot = fileConfiguration.getInt("items.decline.slot");
            inventory.setItem(declineItemSlot, declineItem);
        });
    }

}
