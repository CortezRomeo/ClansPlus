package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.subject.Spawn;
import com.cortezromeo.clansplus.file.inventory.ClanMenuInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
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
        if (playerClanName != null)
            title = StringUtil.setClanNamePlaceholder(title, playerClanName);
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

        playClickSound(fileConfiguration, itemCustomData);

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
        if (itemCustomData.equals("events"))
            new EventsMenuInventory(getOwner()).open();
        if (itemCustomData.equals("settings"))
            new ClanSettingsInventory(getOwner()).open();
        if (itemCustomData.equals("spawn"))
            new Spawn(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SPAWN), getOwner(), getOwner().getName()).execute();
        if (itemCustomData.equals("leave"))
            new LeaveConfirmationInventory(getOwner()).open();
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"), false);
                List<String> leaveBlankSlot = fileConfiguration.getStringList("items.border.leave-blank");
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++) {
                    if (!leaveBlankSlot.isEmpty() && leaveBlankSlot.contains(String.valueOf(itemSlot)))
                        continue;
                    inventory.setItem(itemSlot, ClansPlus.nms.addCustomData(borderItem, "border"));
                }
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

            ItemStack clanInfoItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(clanData.getIconType().toString(),
                    clanData.getIconValue(),
                    fileConfiguration.getInt("items.clanInfo.customModelData"),
                    fileConfiguration.getString("items.clanInfo.name"),
                    fileConfiguration.getStringList("items.clanInfo.lore"), false), "clanInfo");
            int clanInfoItemSlot = fileConfiguration.getInt("items.clanInfo.slot");
            inventory.setItem(clanInfoItemSlot, ItemUtil.getClanItemStack(clanInfoItem, clanData));

            List<String> spawnItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.spawn.lore." + (clanData.getSpawnPoint() != null ? "valid-spawn-point" : "invalid-spawn-point"))) {
                if (clanData.getSpawnPoint() != null) {
                    lore = lore.replace("%x%", String.valueOf((int) clanData.getSpawnPoint().getX()));
                    lore = lore.replace("%y%", String.valueOf((int) clanData.getSpawnPoint().getY()));
                    lore = lore.replace("%z%", String.valueOf((int) clanData.getSpawnPoint().getZ()));
                    lore = lore.replace("%worldName%", clanData.getSpawnPoint().getWorld().getName());
                }
                spawnItemLore.add(lore);
            }
            ItemStack spawnItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.spawn.type"),
                    fileConfiguration.getString("items.spawn.value"),
                    fileConfiguration.getInt("items.spawn.customModelData"),
                    fileConfiguration.getString("items.spawn.name"),
                    spawnItemLore, false), "spawn");
            int spawnItemSlot = fileConfiguration.getInt("items.spawn.slot");
            inventory.setItem(spawnItemSlot, spawnItem);
        });
    }

}
