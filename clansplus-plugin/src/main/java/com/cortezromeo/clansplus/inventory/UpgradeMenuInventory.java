package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.UpgradeManager;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.UpgradeMenuInventoryFile;
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

public class UpgradeMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = UpgradeMenuInventoryFile.get();

    public UpgradeMenuInventory(Player owner) {
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
        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("upgradeMaxMember")) {
            // check rank
            Rank upgradeRequiredrank = Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.UPGRADE);
            if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
                upgradeRequiredrank = PluginDataManager.getClanDatabase(playerClanData.getName()).getSubjectPermission().get(Subject.UPGRADE);
            if (!ClanManager.isPlayerRankSatisfied(getOwner().getName(), upgradeRequiredrank)) {
                MessageUtil.sendMessage(getOwner(), Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(upgradeRequiredrank)));
                return true;
            }

            CurrencyType upgradeMaxMembersCT = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.max-members.currency-type").toUpperCase());
            int newMaxMembers = playerClanData.getMaxMembers() + 1;
            long value = UpgradeFile.get().getLong("upgrade.max-members.price." + newMaxMembers);
            if (!(UpgradeFile.get().getConfigurationSection("upgrade.max-members.price").getKeys(false).contains(String.valueOf(newMaxMembers))))
                value = UpgradeFile.get().getLong("upgrade.max-members.price.else");
            if (UpgradeManager.checkPlayerCurrency(getOwner(), upgradeMaxMembersCT, value, true)) {
                playerClanData.setMaxMembers(newMaxMembers);
                PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
                ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_UPGRADE_MAX_MEMBERS.replace("%player%", getOwner().getName()).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(getOwner().getName()).getRank())).replace("%newMaxMembers%", String.valueOf(playerClanData.getMaxMembers())));
                super.open();
            }
        }
        if (itemCustomData.equals("upgradeStorage")) {
            // check rank
            Rank upgradeRequiredrank = Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.UPGRADE);
            if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
                upgradeRequiredrank = PluginDataManager.getClanDatabase(playerClanData.getName()).getSubjectPermission().get(Subject.UPGRADE);
            if (!ClanManager.isPlayerRankSatisfied(getOwner().getName(), upgradeRequiredrank)) {
                MessageUtil.sendMessage(getOwner(), Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(upgradeRequiredrank)));
                return true;
            }

            CurrencyType upgradeStoragesCT = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.max-storages.currency-type").toUpperCase());
            int newMaxStorages = playerClanData.getMaxStorage() + 1;
            long value = UpgradeFile.get().getLong("upgrade.max-storages.price." + newMaxStorages);
            if (!(UpgradeFile.get().getConfigurationSection("upgrade.max-storages.price").getKeys(false).contains(String.valueOf(newMaxStorages))))
                value = UpgradeFile.get().getLong("upgrade.max-storages.price.else");
            if (UpgradeManager.checkPlayerCurrency(getOwner(), upgradeStoragesCT, value, true)) {
                playerClanData.setMaxStorage(newMaxStorages);
                PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
                ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_UPGRADE_MAX_STORAGES.replace("%player%", getOwner().getName()).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(getOwner().getName()).getRank())).replace("%newMaxStorages%", String.valueOf(playerClanData.getMaxStorage())));
                super.open();
            }
        }
        if (itemCustomData.equals("skillsMenu"))
            new SkillsMenuInventory(getOwner(), playerClanData.getName(), false).open();

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
            addBasicButton(fileConfiguration, true);

            List<String> upgradeMaxStoragesItemLore = new ArrayList<>();
            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());
            int newMaxStorages = playerClanData.getMaxStorage() + 1;
            CurrencyType upgradeStorageCT = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.max-storages.currency-type").toUpperCase());
            for (String lore : fileConfiguration.getStringList("items.upgradeMaxStorage.lore")) {
                lore = lore.replace("%totalStorages%", String.valueOf(playerClanData.getMaxStorage()));
                lore = lore.replace("%newMaxStorages%", String.valueOf(newMaxStorages));
                lore = lore.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(upgradeStorageCT));
                lore = lore.replace("%currencyName%", StringUtil.getCurrencyNameFormat(upgradeStorageCT));
                if (UpgradeFile.get().getConfigurationSection("upgrade.max-storages.price").getKeys(false).contains(String.valueOf(newMaxStorages))) {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-storages.price." + newMaxStorages)));
                } else {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-storages.price.else")));
                }
                upgradeMaxStoragesItemLore.add(lore);
            }
            ItemStack upgradeMaxStoragesItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.upgradeMaxStorage.type").toUpperCase()),
                    fileConfiguration.getString("items.upgradeMaxStorage.value"),
                    fileConfiguration.getInt("items.upgradeMaxStorage.customModelData"),
                    fileConfiguration.getString("items.upgradeMaxStorage.name"),
                    upgradeMaxStoragesItemLore, false), "upgradeStorage");
            int upgradeMaxMemberItemSlot = fileConfiguration.getInt("items.upgradeMaxStorage.slot");
            inventory.setItem(upgradeMaxMemberItemSlot, upgradeMaxStoragesItem);

            List<String> upgradeMaxMembersItemLore = new ArrayList<>();
            int newMaxMembers = playerClanData.getMaxMembers() + 1;
            CurrencyType upgradeMaxMembersCurrencyType = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.max-members.currency-type").toUpperCase());
            for (String lore : fileConfiguration.getStringList("items.upgradeMaxMember.lore")) {
                lore = lore.replace("%totalMembers%", String.valueOf(playerClanData.getMembers().size()));
                lore = lore.replace("%newMaxMembers%", String.valueOf(newMaxMembers));
                lore = lore.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(upgradeMaxMembersCurrencyType));
                lore = lore.replace("%currencyName%", StringUtil.getCurrencyNameFormat(upgradeMaxMembersCurrencyType));
                if (UpgradeFile.get().getConfigurationSection("upgrade.max-members.price").getKeys(false).contains(String.valueOf(newMaxMembers))) {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-members.price." + newMaxMembers)));
                } else {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-members.price.else")));
                }
                upgradeMaxMembersItemLore.add(lore);
            }
            ItemStack upgradeMaxMembersItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.upgradeMaxMember.type").toUpperCase()),
                    fileConfiguration.getString("items.upgradeMaxMember.value"),
                    fileConfiguration.getInt("items.upgradeMaxMember.customModelData"),
                    fileConfiguration.getString("items.upgradeMaxMember.name"),
                    upgradeMaxMembersItemLore, false), "upgradeMaxMember");
            int upgradeMaxMembersItemSlot = fileConfiguration.getInt("items.upgradeMaxMember.slot");
            inventory.setItem(upgradeMaxMembersItemSlot, upgradeMaxMembersItem);

            ItemStack skillsMenuItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.skillsMenu.type").toUpperCase()),
                    fileConfiguration.getString("items.skillsMenu.value"),
                    fileConfiguration.getInt("items.skillsMenu.customModelData"),
                    fileConfiguration.getString("items.skillsMenu.name"),
                    fileConfiguration.getStringList("items.skillsMenu.lore"), false), "skillsMenu");
            int skillsMenuItemSlot = fileConfiguration.getInt("items.skillsMenu.slot");
            inventory.setItem(skillsMenuItemSlot, skillsMenuItem);
        });
    }

}
