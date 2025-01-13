package com.cortezromeo.clansplus.clan.upgrade;

import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.file.UpgradeFile;
import org.bukkit.configuration.file.FileConfiguration;

public class UpgradeManager {

    public CurrencyType getSkillCurrencyType(PluginSkill pluginSkill, int skillID) {
        FileConfiguration upgradeFilConfig = UpgradeFile.get();
        return CurrencyType.valueOf(upgradeFilConfig.getString("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".currency-type").toUpperCase());
    }

    public int getSkillCost(PluginSkill pluginSkill, int level) {
        FileConfiguration upgradeFilConfig = UpgradeFile.get();
        return upgradeFilConfig.getInt("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".price." + level);
    }
}
