package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.AllyInivtationConfirmInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AllyInvitationConfirmInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = AllyInivtationConfirmInventoryFile.get();
    private String clanName;
    private String targetClan;

    public AllyInvitationConfirmInventory(Player owner, String clanName, String targetClan) {
        super(owner);
        this.clanName = clanName;
        this.targetClan = targetClan;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }
        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            return;
        }
        if (PluginDataManager.getClanDatabase(targetClan) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", targetClan));
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

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            return false;
        }
        if (PluginDataManager.getClanDatabase(targetClan) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", targetClan));
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);
        Rank requiredRank = PluginDataManager.getClanDatabase(clanName).getSubjectPermission().get(Subject.MANAGEALLY);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close")) {
            getOwner().closeInventory();
            return true;
        }
        if (itemCustomData.equals("back")) {
            new AllyInvitationListInventory(getOwner()).open();
            return true;
        }
        if (!ClanManager.isPlayerRankSatisfied(getOwner().getName(), requiredRank)) {
            MessageUtil.sendMessage(getOwner(), Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(requiredRank)));
            getOwner().closeInventory();
        } else {
            if (itemCustomData.equals("accept")) {
                PluginDataManager.getClanDatabase(clanName).getAllyInvitation().remove(targetClan);
                PluginDataManager.getClanDatabase(clanName).getAllies().add(targetClan);
                PluginDataManager.getClanDatabase(targetClan).getAllies().add(clanName);
                PluginDataManager.saveClanDatabaseToStorage(clanName);
                PluginDataManager.saveClanDatabaseToStorage(targetClan);
                MessageUtil.sendMessage(getOwner(), Messages.ACCEPT_ALLY_INVITE_SUCCESS.replace("%clan%", targetClan));
                ClanManager.alertClan(clanName, Messages.CLAN_BROADCAST_NEW_ALLY_NOTIFICATION.replace("%clan%", targetClan));
                ClanManager.alertClan(targetClan, Messages.CLAN_BROADCAST_NEW_ALLY_NOTIFICATION.replace("%clan%", clanName));
                new AllyInvitationListInventory(getOwner()).open();
            }
            if (itemCustomData.equals("reject")) {
                PluginDataManager.getClanDatabase(clanName).getAllyInvitation().remove(targetClan);
                PluginDataManager.saveClanDatabaseToStorage(clanName);
                MessageUtil.sendMessage(getOwner(), Messages.REJECT_ALLY_INVITE_SUCCESS.replace("%clan%", targetClan));
                new AllyInvitationListInventory(getOwner()).open();
            }
        }

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            ItemStack acceptItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.accept.type").toUpperCase()),
                    fileConfiguration.getString("items.accept.value"),
                    fileConfiguration.getInt("items.accept.customModelData"),
                    fileConfiguration.getString("items.accept.name"),
                    fileConfiguration.getStringList("items.accept.lore"), false), "accept");
            int acceptItemSlot = fileConfiguration.getInt("items.accept.slot");
            inventory.setItem(acceptItemSlot, acceptItem);

            ItemStack rejectItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.reject.type").toUpperCase()),
                    fileConfiguration.getString("items.reject.value"),
                    fileConfiguration.getInt("items.reject.customModelData"),
                    fileConfiguration.getString("items.reject.name"),
                    fileConfiguration.getStringList("items.reject.lore"), false), "reject");
            int rejectItemSlot = fileConfiguration.getInt("items.reject.slot");
            inventory.setItem(rejectItemSlot, rejectItem);

            ItemStack clanItem = ItemUtil.getClanItemStack(ItemUtil.getItem(
                    PluginDataManager.getClanDatabase(clanName).getIconType(),
                    PluginDataManager.getClanDatabase(clanName).getIconValue(),
                    0,
                    fileConfiguration.getString("items.clan.name"),
                    fileConfiguration.getStringList("items.clan.lore"), false), PluginDataManager.getClanDatabase(targetClan));
            int clanItemSlot = fileConfiguration.getInt("items.clan.slot");
            inventory.setItem(clanItemSlot, clanItem);
        });
    }

}
