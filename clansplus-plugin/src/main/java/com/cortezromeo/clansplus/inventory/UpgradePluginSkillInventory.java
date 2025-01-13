package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.clan.skill.SkillManager;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.UpgradePluginSkillListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpgradePluginSkillInventory extends UpgradeSkillPaginatedInventory {

    FileConfiguration fileConfiguration = UpgradePluginSkillListInventoryFile.get();
    private List<String> skillLevels = new ArrayList<>();
    private IClanData clanData;
    private PluginSkill pluginSkill;
    private List<Integer> slotsUsed;

    public UpgradePluginSkillInventory(Player owner, IClanData clanData, PluginSkill pluginSkill) {
        super(owner);
        this.clanData = clanData;
        this.pluginSkill = pluginSkill;
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title").replace("%skillName%", SkillsFile.get().getString("plugin-skills." + pluginSkill.toString().toLowerCase() + ".name"));
        return ClansPlus.nms.addColor(title);
    }

    @Override
    public int getSlots() {
        return 6 * 9;
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

        if (itemCustomData.equals("prevPage")) {
            if (page != 0) {
                page = page - 1;
                open();
            }
        }
        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= skillLevels.size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());

        if (itemCustomData.equals("back")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanData.getName())) {
                    new MembersMenuInventory(getOwner()).open();
                    return;
                }
            }
            new ViewClanInventory(getOwner(), clanData.getName()).open();
        }
        if (itemCustomData.contains("player=")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanData.getName())) {
                    itemCustomData = itemCustomData.replace("player=", "");
                    new ManageMemberInventory(getOwner(), itemCustomData).open();
                    return;
                }
            }
            MessageUtil.sendMessage(getOwner(), Messages.TARGET_CLAN_MEMBERSHIP_ERROR.replace("%player%", itemCustomData.replace("player=", "")));
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
            super.addPaginatedMenuItems(fileConfiguration);
            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            if (backItemSlot < 0)
                backItemSlot = 1;
            if (backItemSlot > 8)
                backItemSlot = 7;
            backItemSlot = (getSlots() - 9) + backItemSlot;
            inventory.setItem(backItemSlot, backItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            skillLevels.clear();
            FileConfiguration upgradeFile = UpgradeFile.get();
            HashMap<Integer, Integer> levelCost = new HashMap<>();
            String pricePath = "upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".price";
            for (String levelAvailable : upgradeFile.getConfigurationSection(pricePath).getKeys(false)) {
                levelCost.put(Integer.parseInt(levelAvailable), upgradeFile.getInt(pricePath + "." + levelAvailable));
                skillLevels.add(levelAvailable);
            }

            int skillID = SkillManager.getSkillID(pluginSkill);
            CurrencyType skillCurrencyType = CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".currency-type").toUpperCase());
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= skillLevels.size())
                    break;
                if (skillLevels.get(index) != null) {
                    int skillLevel = Integer.parseInt(skillLevels.get(index));
                    try {
                        String skillConfigPath = "skills." + pluginSkill.toString().toLowerCase() + "." + (clanData.getSkillLevel().get(skillID) >= skillLevel ? "unlocked." : "locked.");
                        List<String> skillLLevelItemLore = new ArrayList<>();
                        if (fileConfiguration.getBoolean(skillConfigPath + "customLore.enabled") && fileConfiguration.getString(skillConfigPath + "customLore.level." + skillLevel) != null) {
                            for (String lore : fileConfiguration.getStringList(skillConfigPath + "customLore.level." + skillLevel))
                                skillLLevelItemLore.add(addSkillLevelItemPlaceholders(skillLevel, skillID, skillCurrencyType, lore));
                        } else
                            for (String lore : fileConfiguration.getStringList(skillConfigPath + "lore"))
                                skillLLevelItemLore.add(addSkillLevelItemPlaceholders(skillLevel, skillID, skillCurrencyType, lore));
                        ItemStack skillLevelItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString(skillConfigPath + "type"),
                                fileConfiguration.getString(skillConfigPath + "value"),
                                fileConfiguration.getInt(skillConfigPath + "customModelData"),
                                fileConfiguration.getString(skillConfigPath + "name").replace("%level%", String.valueOf(skillLevel)),
                                skillLLevelItemLore, false), "upgrade=" + skillLevel);
                        skillLevelItem.setAmount(skillLevel > skillLevelItem.getMaxStackSize() ? 1 : skillLevel);
                        inventory.setItem(transferSlot(i), skillLevelItem);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        MessageUtil.throwErrorMessage("KỸ NĂNG " + pluginSkill + " CHƯA ĐƯỢC CONFIG HOÀN CHỈNH, VUI LÒNG CONFIG KỸ NĂNG TRONG SKILLS.YML HOẶC UPGRADE.YML CHO LEVEL " + skillLevel);
                    }
                }
            }
        });
    }

    public int transferSlot(int number) {
        int[] values = {
                0, 9, 18,
                27, 28, 29, 20,
                11, 2, 3, 4,
                13, 22, 31, 32,
                33, 24, 15, 6,
                7, 8, 17, 26,
                35, 44, 53
        };

        int index = number % values.length;

        return values[index];
    }

    public String addSkillLevelItemPlaceholders(int skillLevel, int skillID, CurrencyType skillCurrencyType, String lore) {
        FileConfiguration skillConfig = SkillsFile.get();
        if (SkillManager.getSkillData().get(skillID).getRateToActivate() != null) {
            lore = lore.replace("%oldRate%", String.valueOf(skillLevel - 1 < 1 ? 0 : SkillManager.getSkillData().get(skillID).getRateToActivate().get(skillLevel - 1)));
            lore = lore.replace("%newRate%", String.valueOf(SkillManager.getSkillData().get(skillID).getRateToActivate().get(skillLevel)));
        }
        lore = lore.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(skillCurrencyType));
        lore = lore.replace("%currencyName%", StringUtil.getCurrencyNameFormat(skillCurrencyType));
        lore = lore.replace("%price%", String.valueOf(UpgradeFile.get().getInt("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".price." + skillLevel)));
        if (pluginSkill == PluginSkill.CRITICAL_HIT) {
            lore = lore.replace("%oldOnHitDamage%", skillLevel - 1 < 1 ? "0" : skillConfig.getString("plugin-skills.critical_hit.on-hit-damage.level." + (skillLevel - 1)).replace("%damage%", "<DMG>"));
            lore = lore.replace("%newOnHitDamage%", skillConfig.getString("plugin-skills.critical_hit.on-hit-damage.level." + (skillLevel)).replace("%damage%", "<DMG>"));
        }
        if (pluginSkill == PluginSkill.DODGE)
            lore = lore.replace("%damageReflectionEvaluate%", skillConfig.getString("plugin-skills.dodge.damage-reflection.damage-reflection").replace("%damage%", "<DMG>"));
        if (pluginSkill == PluginSkill.LIFE_STEAL) {
            lore = lore.replace("%oldHeal%", skillLevel - 1 < 1 ? "0" : skillConfig.getString("plugin-skills.life_steal.heal.level." + (skillLevel - 1)).replace("%playerMaxHealth%", "<MAX HP>"));
            lore = lore.replace("%newHeal%", skillConfig.getString("plugin-skills.life_steal.heal.level." + (skillLevel)).replace("%playerMaxHealth%", "<MAX HP>"));
        }
        if (pluginSkill == PluginSkill.BOOST_SCORE) {
            lore = lore.replace("%oldBoostScore%", skillLevel - 1 < 1 ? "0" : skillConfig.getString("plugin-skills.boost_score.boost.level." + (skillLevel - 1)));
            lore = lore.replace("%newBoostScore%", skillConfig.getString("plugin-skills.boost_score.boost.level." + (skillLevel)));
        }

        return lore;
    }
}
