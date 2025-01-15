package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.ClanMenuInventoryFile;
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

public class ClanMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ClanMenuInventoryFile.get();

    public ClanMenuInventory(Player owner) {
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
        String playerClanName = PluginDataManager.getPlayerDatabase(getOwner().getName()).getClan();
        if (playerClanName != null) {
            title = title.replace("%formatClanName%", ClanManager.getFormatClanName(PluginDataManager.getClanDatabase(playerClanName)));
            title = title.replace("%clanName%", playerClanName);
        }
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
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("members"))
            new MembersMenuInventory(getOwner()).open();
        if (itemCustomData.equals("clanList"))
            new ClanListInventory(getOwner()).open();
        if (itemCustomData.equals("allies"))
            new AlliesMenuInventory(getOwner()).open();
        if (itemCustomData.equals("upgrade"))
            new UpgradeMenuInventory(getOwner()).open();
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

            IClanData clanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

            List<String> membersItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.members.lore")) {
                lore = lore.replace("%totalMembers%", String.valueOf(clanData.getMembers().size()));
                membersItemLore.add(lore);
            }
            ItemStack membersClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.members.type"),
                    fileConfiguration.getString("items.members.value"),
                    fileConfiguration.getInt("items.members.customModelData"),
                    fileConfiguration.getString("items.members.name"),
                    membersItemLore, false), "members");
            int membersItemSlot = fileConfiguration.getInt("items.members.slot");
            inventory.setItem(membersItemSlot, membersClanItem);

            List<String> alliesItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.allies.lore")) {
                lore = lore.replace("%totalAllies%", String.valueOf(clanData.getAllies().size()));
                alliesItemLore.add(lore);
            }
            ItemStack alliesClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.allies.type"),
                    fileConfiguration.getString("items.allies.value"),
                    fileConfiguration.getInt("items.allies.customModelData"),
                    fileConfiguration.getString("items.allies.name"),
                    alliesItemLore, false), "allies");
            int alliesItemSlot = fileConfiguration.getInt("items.allies.slot");
            inventory.setItem(alliesItemSlot, alliesClanItem);

            List<String> listClanItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.clanList.lore")) {
                lore = lore.replace("%totalClans%", String.valueOf(PluginDataManager.getClanDatabase().size()));
                listClanItemLore.add(lore);
            }
            ItemStack listClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.clanList.type"),
                    fileConfiguration.getString("items.clanList.value"),
                    fileConfiguration.getInt("items.clanList.customModelData"),
                    fileConfiguration.getString("items.clanList.name"),
                    listClanItemLore, false), "clanList");
            int listClanItemSlot = fileConfiguration.getInt("items.clanList.slot");
            inventory.setItem(listClanItemSlot, listClanItem);

            ItemStack upgradeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.upgrade.type"),
                    fileConfiguration.getString("items.upgrade.value"),
                    fileConfiguration.getInt("items.upgrade.customModelData"),
                    fileConfiguration.getString("items.upgrade.name"),
                    fileConfiguration.getStringList("items.upgrade.lore"), false), "upgrade");
            int upgradeItemSlot = fileConfiguration.getInt("items.upgrade.slot");
            inventory.setItem(upgradeItemSlot, upgradeItem);

            ItemStack eventsItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.events.type"),
                    fileConfiguration.getString("items.events.value"),
                    fileConfiguration.getInt("items.events.customModelData"),
                    fileConfiguration.getString("items.events.name"),
                    fileConfiguration.getStringList("items.events.lore"), false), "events");
            int eventsItemSlot = fileConfiguration.getInt("items.events.slot");
            inventory.setItem(eventsItemSlot, eventsItem);

            ItemStack settingsItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.settings.type"),
                    fileConfiguration.getString("items.settings.value"),
                    fileConfiguration.getInt("items.settings.customModelData"),
                    fileConfiguration.getString("items.settings.name"),
                    fileConfiguration.getStringList("items.settings.lore"), false), "settings");
            int settingsItemSlot = fileConfiguration.getInt("items.settings.slot");
            inventory.setItem(settingsItemSlot, settingsItem);

            ItemStack leaveItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.leave.type"),
                    fileConfiguration.getString("items.leave.value"),
                    fileConfiguration.getInt("items.leave.customModelData"),
                    fileConfiguration.getString("items.leave.name"),
                    fileConfiguration.getStringList("items.leave.lore"), false), "leave");
            int leaveItemSlot = fileConfiguration.getInt("items.leave.slot");
            inventory.setItem(leaveItemSlot, leaveItem);
        });
    }

}
