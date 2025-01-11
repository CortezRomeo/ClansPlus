package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.AllyInivtationConfirmInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AllyInvitationConfirmInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = AllyInivtationConfirmInventoryFile.get();
    private IClanData clanData;
    private String targetClan;

    public AllyInvitationConfirmInventory(Player owner, IClanData clanData, String targetClan) {
        super(owner);
        this.clanData = clanData;
        this.targetClan = targetClan;
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

        // how can they get here?
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null | !clanData.getAllyInvitation().contains(targetClan)) {
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);
        Rank requiredRank = clanData.getSubjectPermission().get(Subject.MANAGEALLY);

        if (itemCustomData.equals("close")) {
            getOwner().closeInventory();
            return;
        }
        if (itemCustomData.equals("back")) {
            new AllyInvitationListInventory(getOwner()).open();
            return;
        }
        if (!ClanManager.isPlayerRankSatisfied(getOwner().getName(), requiredRank)) {
            MessageUtil.sendMessage(getOwner(), Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(requiredRank)));
            getOwner().closeInventory();
        } else {
            if (itemCustomData.equals("accept")) {
                clanData.getAllyInvitation().remove(targetClan);
                clanData.getAllies().add(targetClan);
                PluginDataManager.getClanDatabase(targetClan).getAllies().add(clanData.getName());
                PluginDataManager.saveClanDatabaseToStorage(clanData.getName(), clanData);
                PluginDataManager.saveClanDatabaseToStorage(targetClan);
                MessageUtil.sendMessage(getOwner(), Messages.ACCEPT_ALLY_INVITE_SUCCESS.replace("%clan%", targetClan));
                ClanManager.alertClan(clanData.getName(), Messages.CLAN_BROADCAST_NEW_ALLY_NOTIFICATION.replace("%clan%", targetClan));
                ClanManager.alertClan(targetClan, Messages.CLAN_BROADCAST_NEW_ALLY_NOTIFICATION.replace("%clan%", clanData.getName()));
                new AllyInvitationListInventory(getOwner()).open();
            }
            if (itemCustomData.equals("reject")) {
                clanData.getAllyInvitation().remove(targetClan);
                PluginDataManager.saveClanDatabaseToStorage(clanData.getName(), clanData);
                MessageUtil.sendMessage(getOwner(), Messages.REJECT_ALLY_INVITE_SUCCESS.replace("%clan%", targetClan));
                new AllyInvitationListInventory(getOwner()).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {

            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"), false);
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                    inventory.setItem(itemSlot, borderItem);
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

            ItemStack acceptItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.accept.type"),
                    fileConfiguration.getString("items.accept.value"),
                    fileConfiguration.getInt("items.accept.customModelData"),
                    fileConfiguration.getString("items.accept.name"),
                    fileConfiguration.getStringList("items.accept.lore"), false), "accept");
            int acceptItemSlot = fileConfiguration.getInt("items.accept.slot");
            inventory.setItem(acceptItemSlot, acceptItem);

            ItemStack rejectItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.reject.type"),
                    fileConfiguration.getString("items.reject.value"),
                    fileConfiguration.getInt("items.reject.customModelData"),
                    fileConfiguration.getString("items.reject.name"),
                    fileConfiguration.getStringList("items.reject.lore"), false), "reject");
            int rejectItemSlot = fileConfiguration.getInt("items.reject.slot");
            inventory.setItem(rejectItemSlot, rejectItem);

            ItemStack clanItem = ItemUtil.getClanItemStack(ItemUtil.getItem(
                    clanData.getIconType().toString(),
                    clanData.getIconValue(),
                    0,
                    fileConfiguration.getString("items.clan.name"),
                    fileConfiguration.getStringList("items.clan.lore"), false), PluginDataManager.getClanDatabase(targetClan));
            int clanItemSlot = fileConfiguration.getInt("items.clan.slot");
            inventory.setItem(clanItemSlot, clanItem);
        });
    }

}
