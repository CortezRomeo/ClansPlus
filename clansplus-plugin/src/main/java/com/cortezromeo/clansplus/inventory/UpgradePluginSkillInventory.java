package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.UpgradeManager;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.UpgradePluginSkillListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import com.google.common.primitives.Ints;
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
    private String clanName;
    private PluginSkill pluginSkill;
    private boolean fromViewClan;

    public UpgradePluginSkillInventory(Player owner, String clanName, PluginSkill pluginSkill, boolean fromViewClan) {
        super(owner);
        this.clanName = clanName;
        this.pluginSkill = pluginSkill;
        this.fromViewClan = fromViewClan;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            return;
        }
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title").replace("%skillName%", SkillsFile.get().getString("plugin-skills." + pluginSkill.toString().toLowerCase() + ".name"));
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

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

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

        if (itemCustomData.equals("back"))
            new SkillsMenuInventory(getOwner(), clanName, fromViewClan).open();

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null)
            return;

        if (!PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName().equals(clanName))
            return;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());
        if (itemCustomData.contains("upgrade=")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {

                    // check rank
                    Rank upgradeRequiredrank = Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.UPGRADE);
                    if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
                        upgradeRequiredrank = PluginDataManager.getClanDatabase(clanName).getSubjectPermission().get(Subject.UPGRADE);
                    if (!ClanManager.isPlayerRankSatisfied(getOwner().getName(), upgradeRequiredrank)) {
                        MessageUtil.sendMessage(getOwner(), Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(upgradeRequiredrank)));
                        return;
                    }

                    int levelChosen = Integer.parseInt(itemCustomData.replace("upgrade=", ""));

                    int skillID = SkillManager.getSkillID(pluginSkill);
                    int clanDataSkillLevel = PluginDataManager.getClanDatabase(clanName).getSkillLevel().get(skillID);
                    int newSkillLevel = clanDataSkillLevel + 1;

                    if (levelChosen > newSkillLevel) {
                        MessageUtil.sendMessage(getOwner(), Messages.ILLEGALLY_UPGRADE_SKILL.replace("%skillLevel%", String.valueOf(newSkillLevel)));
                        return;
                    }

                    if (clanDataSkillLevel >= levelChosen)
                        return;

                    long value = UpgradeFile.get().getLong("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".price." + newSkillLevel);
                    if (UpgradeManager.checkPlayerCurrency(getOwner(), CurrencyType.valueOf(UpgradeFile.get().getString("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".currency-type").toUpperCase()), value, true)) {
                        PluginDataManager.getClanDatabase(clanName).getSkillLevel().put(skillID, newSkillLevel);
                        ClanManager.alertClan(clanName, Messages.CLAN_BROADCAST_UPGRADE_PLUGIN_SKILL.replace("%player%", getOwner().getName()).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(getOwner().getName()).getRank())).replace("%skillName%", SkillsFile.get().getString("plugin-skills." + pluginSkill.toString().toLowerCase() + ".name")).replace("%newLevel%", String.valueOf(newSkillLevel)));
                        super.open();
                    }
                    return;
                }
            }
            MessageUtil.sendMessage(getOwner(), Messages.TARGET_CLAN_MEMBERSHIP_ERROR.replace("%player%", itemCustomData.replace("player=", "")));
        }
    }

    @Override
    public void setMenuItems() {
        ClansPlus.plugin.foliaLib.getScheduler().runAsync(task -> {
            super.addPaginatedMenuItems(fileConfiguration);
            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            inventory.setItem(backItemSlot, backItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            String skillName = pluginSkill.toString().toLowerCase();
            FileConfiguration upgradeFile = UpgradeFile.get();
            FileConfiguration skillFile = SkillsFile.get();
            List<String> skillReviewItemLore = new ArrayList<>();
            int clanSkillCurrentLevel = PluginDataManager.getClanDatabase(clanName).getSkillLevel().get(SkillManager.getSkillID(pluginSkill));
            int skillMaxLevel = upgradeFile.getConfigurationSection("upgrade.plugin-skills." + skillName + ".price").getKeys(false).size();
            for (String lore : fileConfiguration.getStringList("items.skillReview.lore")) {
                lore = lore.replace("%status%", StringUtil.getStatus(skillFile.getBoolean("plugin-skills." + skillName + ".enabled")));
                lore = lore.replace("%skillDescription%", skillFile.getString("plugin-skills." + skillName + ".description"));
                lore = lore.replace("%progressBar%", StringUtil.getProgressBar(clanSkillCurrentLevel, skillMaxLevel));
                lore = lore.replace("%currentLevel%", String.valueOf(clanSkillCurrentLevel));
                lore = lore.replace("%maxLevel%", String.valueOf(skillMaxLevel));
                skillReviewItemLore.add(lore);
            }
            ItemStack skillReviewItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(skillFile.getString("plugin-skills." + skillName + ".display.type"),
                    skillFile.getString("plugin-skills." + skillName + ".display.value"),
                    fileConfiguration.getInt("items.skillReview.customModelData"),
                    fileConfiguration.getString("items.skillReview.name").replace("%skillName%", skillFile.getString("plugin-skills." + skillName + ".name")),
                    skillReviewItemLore, false), "skillReview");
            int skillReviewItemSlot = fileConfiguration.getInt("items.skillReview.slot");
            inventory.setItem(skillReviewItemSlot, skillReviewItem);

            skillLevels.clear();
            HashMap<Integer, Integer> levelCost = new HashMap<>();
            String pricePath = "upgrade.plugin-skills." + skillName + ".price";
            for (String levelAvailable : upgradeFile.getConfigurationSection(pricePath).getKeys(false)) {
                levelCost.put(Integer.parseInt(levelAvailable), upgradeFile.getInt(pricePath + "." + levelAvailable));
                skillLevels.add(levelAvailable);
            }

            int skillID = SkillManager.getSkillID(pluginSkill);
            CurrencyType skillCurrencyType = UpgradeManager.getSkillCurrencyType(pluginSkill);
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= skillLevels.size())
                    break;
                if (skillLevels.get(index) != null) {
                    int skillLevel = Integer.parseInt(skillLevels.get(index));
                    try {
                        String skillConfigPath = "skills." + skillName + "." + (PluginDataManager.getClanDatabase(clanName).getSkillLevel().get(skillID) >= skillLevel ? "unlocked." : "locked.");
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
        int index = number % getSkillTrack().length;
        return getSkillTrack()[index];
    }

    public String addSkillLevelItemPlaceholders(int skillLevel, int skillID, CurrencyType skillCurrencyType, String lore) {
        FileConfiguration skillConfig = SkillsFile.get();
        if (SkillManager.getSkillData().get(skillID).getRateToActivate() != null) {
            lore = lore.replace("%oldRate%", String.valueOf(skillLevel - 1 < 1 ? 0 : SkillManager.getSkillData().get(skillID).getRateToActivate().get(skillLevel - 1)));
            lore = lore.replace("%newRate%", String.valueOf(SkillManager.getSkillData().get(skillID).getRateToActivate().get(skillLevel)));
        }
        lore = lore.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(skillCurrencyType));
        lore = lore.replace("%currencyName%", StringUtil.getCurrencyNameFormat(skillCurrencyType));
        lore = lore.replace("%price%", String.valueOf(UpgradeManager.getSkillCost(pluginSkill, skillLevel)));
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

    @Override
    public int[] getSkillTrack() {
        List<Integer> skillTrackList = fileConfiguration.getIntegerList("skill-track");
        return Ints.toArray(skillTrackList);
    }
}
