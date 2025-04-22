package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
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
        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("upgradeMaxMember")) {
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
            ItemStack upgradeMaxMemberItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.upgradeMaxMember.type"),
                    fileConfiguration.getString("items.upgradeMaxMember.value"),
                    fileConfiguration.getInt("items.upgradeMaxMember.customModelData"),
                    fileConfiguration.getString("items.upgradeMaxMember.name"),
                    upgradeMaxMemberItemLore, false), "upgradeMaxMember");
            int upgradeMaxMemberItemSlot = fileConfiguration.getInt("items.upgradeMaxMember.slot");
            inventory.setItem(upgradeMaxMemberItemSlot, upgradeMaxMemberItem);

            ItemStack skillsMenuItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.skillsMenu.type"),
                    fileConfiguration.getString("items.skillsMenu.value"),
                    fileConfiguration.getInt("items.skillsMenu.customModelData"),
                    fileConfiguration.getString("items.skillsMenu.name"),
                    fileConfiguration.getStringList("items.skillsMenu.lore"), false), "skillsMenu");
            int skillsMenuItemSlot = fileConfiguration.getInt("items.skillsMenu.slot");
            inventory.setItem(skillsMenuItemSlot, skillsMenuItem);
        });
    }

}
