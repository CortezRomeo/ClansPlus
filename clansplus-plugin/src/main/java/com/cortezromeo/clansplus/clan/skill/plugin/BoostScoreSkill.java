package com.cortezromeo.clansplus.clan.skill.plugin;

import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BoostScoreSkill {

    public static void registerSkill() {
        FileConfiguration skillFileConfig = SkillsFile.get();
        String pluginSkillPath = "plugin-skills.boost_score.";
        boolean enabled = skillFileConfig.getBoolean(pluginSkillPath + "enabled");
        String name = skillFileConfig.getString(pluginSkillPath + "name");
        String description = skillFileConfig.getString(pluginSkillPath + "description");
        int ID = skillFileConfig.getInt(pluginSkillPath + "ID");
        SkillData skillData = new SkillData(ID, SkillType.PLUGIN, enabled, name, description, null, 0, 0, null) {
            @Override
            public void onDamage(SkillData skillData, EntityDamageByEntityEvent event) {}

            @Override
            public void onDeath(SkillData skillData, PlayerDeathEvent event) {
                onDie(skillData, event);
            }
        };
        SkillManager.registerPluginSkill(ID, skillData);
    }

    public static void onDie(SkillData skillData, PlayerDeathEvent event) {
        if (!skillData.isEnabled())
            return;

        Player killer = event.getEntity().getKiller();
        IClanData killerClanData = PluginDataManager.getClanDatabaseByPlayerName(killer.getName());

        if (killerClanData == null)
            return;

        int skillLevel = killerClanData.getSkillLevel().get(skillData.getId());

        // player clan's has this skill
        if (skillLevel > 0) {
            // TODO add more score
        }
    }

}
