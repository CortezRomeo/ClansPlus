package com.cortezromeo.clansplus.clan.skill.plugin;

import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class BoostScoreSkill {

    public static HashMap<Integer, Integer> boostScoreLevel = new HashMap<>();

    public static void registerSkill() {
        FileConfiguration skillFileConfig = SkillsFile.get();
        String pluginSkillPath = "plugin-skills.boost_score.";
        boolean enabled = skillFileConfig.getBoolean(pluginSkillPath + "enabled");
        String name = skillFileConfig.getString(pluginSkillPath + "name");
        String description = skillFileConfig.getString(pluginSkillPath + "description");
        int ID = skillFileConfig.getInt(pluginSkillPath + "ID");
        for (String level : skillFileConfig.getConfigurationSection(pluginSkillPath + "boost.level").getKeys(false))
            boostScoreLevel.put(Integer.valueOf(level), skillFileConfig.getInt(pluginSkillPath + "boost.level." + level));
        SkillData skillData = new SkillData(ID, SkillType.PLUGIN, enabled, name, description, null, 0, 0, null) {
            @Override
            public boolean onDamage(SkillData skillData, EntityDamageByEntityEvent event) {
                return false;
            }

            @Override
            public boolean onDie(SkillData skillData, String killerName, String victimName, boolean isMob) {
                return onDieEvent(skillData, killerName, victimName, isMob);
            }
        };
        SkillManager.registerPluginSkill(ID, skillData);
    }

    public static boolean onDieEvent(SkillData skillData, String killerName, String victimName, boolean isMob) {
        if (!skillData.isEnabled()) return false;

        Player killer = Bukkit.getPlayer(killerName);
        if (killer == null) return false;

        IClanData killerClanData = PluginDataManager.getClanDatabaseByPlayerName(killer.getName());

        if (killerClanData == null) return false;

        int skillLevel = killerClanData.getSkillLevel().get(skillData.getId());

        // player clan's has this skill
        if (skillLevel > 0) {
            PluginDataManager.getClanDatabaseByPlayerName(killer.getName()).setScore(killerClanData.getScore() + boostScoreLevel.get(skillLevel));
            return true;
        }

        return false;
    }

}
