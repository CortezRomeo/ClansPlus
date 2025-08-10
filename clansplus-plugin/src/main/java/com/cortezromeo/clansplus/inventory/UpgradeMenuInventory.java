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
                playerClanData.setMaxMembers(playerClanData.getMaxMembers() + 1);
                PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
                ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_UPGRADE_MAX_MEMBERS.replace("%player%", getOwner().getName()).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(getOwner().getName()).getRank())).replace("%newMaxMembers%", String.valueOf(playerClanData.getMaxMembers())));
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

            List<String> upgradeMaxMemberItemLore = new ArrayList<>();
            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());
            int newMaxMember = playerClanData.getMaxMembers() + 1;
            CurrencyType upgradeMaxMemberCurrencyType = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.max-members.currency-type").toUpperCase());
            for (String lore : fileConfiguration.getStringList("items.upgradeMaxMember.lore")) {
                lore = lore.replace("%totalMembers%", String.valueOf(playerClanData.getMembers().size()));
                lore = lore.replace("%newMaxMembers%", String.valueOf(newMaxMember));
                lore = lore.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(upgradeMaxMemberCurrencyType));
                lore = lore.replace("%currencyName%", StringUtil.getCurrencyNameFormat(upgradeMaxMemberCurrencyType));
                if (UpgradeFile.get().getConfigurationSection("upgrade.max-members.price").getKeys(false).contains(String.valueOf(newMaxMember))) {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-members.price." + newMaxMember)));
                } else {
                    lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getLong("upgrade.max-members.price.else")));
                }
                upgradeMaxMemberItemLore.add(lore);
            }
            ItemStack upgradeMaxMemberItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.upgradeMaxMember.type").toUpperCase()),
                    fileConfiguration.getString("items.upgradeMaxMember.value"),
                    fileConfiguration.getInt("items.upgradeMaxMember.customModelData"),
                    fileConfiguration.getString("items.upgradeMaxMember.name"),
                    upgradeMaxMemberItemLore, false), "upgradeMaxMember");
            int upgradeMaxMemberItemSlot = fileConfiguration.getInt("items.upgradeMaxMember.slot");
            inventory.setItem(upgradeMaxMemberItemSlot, upgradeMaxMemberItem);

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
