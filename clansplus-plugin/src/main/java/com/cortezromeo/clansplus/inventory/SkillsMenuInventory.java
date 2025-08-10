package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.SkillsMenuInventoryFile;
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

public class SkillsMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = SkillsMenuInventoryFile.get();
    private String clanName;
    private boolean fromViewClan;

    public SkillsMenuInventory(Player owner, String clanName, boolean fromViewClan) {
        super(owner);
        this.clanName = clanName;
        this.fromViewClan = fromViewClan;
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
    public boolean handleMenu(InventoryClickEvent event) {
        if (!super.handleMenu(event))
            return false;

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back")) {
            if (fromViewClan) {
                new ViewClanInformationInventory(getOwner(), clanName).open();
                return true;
            }
            IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {
                    new UpgradeMenuInventory(getOwner()).open();
                    return true;
                }
            }
            new ViewClanInformationInventory(getOwner(), clanName).open();
        }
        if (itemCustomData.contains("pluginskill=")) {
            itemCustomData = itemCustomData.replace("pluginskill=", "");
            new UpgradePluginSkillInventory(getOwner(), clanName, PluginSkill.valueOf(itemCustomData.toUpperCase()), fromViewClan).open();
        }

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            FileConfiguration skillFile = SkillsFile.get();
            for (PluginSkill pluginSkill : PluginSkill.values()) {
                List<String> pluginSkillItemLore = new ArrayList<>();
                String pluginSkillName = pluginSkill.toString().toLowerCase();
                int clanSkillCurrentLevel = PluginDataManager.getClanDatabase(clanName).getSkillLevel().get(SkillManager.getSkillID(pluginSkill));
                int skillMaxLevel = UpgradeFile.get().getConfigurationSection("upgrade.plugin-skills." + pluginSkillName + ".price").getKeys(false).size();
                for (String lore : fileConfiguration.getStringList("items." + pluginSkillName + ".lore")) {
                    lore = lore.replace("%status%", StringUtil.getStatus(skillFile.getBoolean("plugin-skills." + pluginSkillName + ".enabled")));
                    lore = lore.replace("%skillDescription%", skillFile.getString("plugin-skills." + pluginSkillName + ".description"));
                    lore = lore.replace("%progressBar%", StringUtil.getProgressBar(clanSkillCurrentLevel, skillMaxLevel));
                    lore = lore.replace("%currentLevel%", String.valueOf(clanSkillCurrentLevel));
                    lore = lore.replace("%maxLevel%", String.valueOf(skillMaxLevel));
                    pluginSkillItemLore.add(lore);
                }
                ItemStack pluginSkillItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                        ItemType.valueOf(skillFile.getString("plugin-skills." + pluginSkillName + ".display.type").toUpperCase()),
                        skillFile.getString("plugin-skills." + pluginSkillName + ".display.value"),
                        fileConfiguration.getInt("items." + pluginSkillName + ".customModelData"),
                        fileConfiguration.getString("items." + pluginSkillName + ".name").replace("%skillName%", skillFile.getString("plugin-skills." + pluginSkillName + ".name")),
                        pluginSkillItemLore, false), "pluginskill=" + pluginSkillName);
                int pluginSkillItemSlot = fileConfiguration.getInt("items." + pluginSkillName + ".slot");
                inventory.setItem(pluginSkillItemSlot, pluginSkillItem);
            }
        });
    }

}
