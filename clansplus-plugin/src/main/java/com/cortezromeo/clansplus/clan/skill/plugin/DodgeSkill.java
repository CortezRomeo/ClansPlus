package com.cortezromeo.clansplus.clan.skill.plugin;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.CalculatorUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Random;

public class DodgeSkill {

    private static int levelDamageReflection;
    private static String levelDamageEvaluation;

    public static void registerSkill() {
        FileConfiguration skillFileConfig = SkillsFile.get();
        String pluginSkillPath = "plugin-skills.dodge.";
        boolean enabled = skillFileConfig.getBoolean(pluginSkillPath + "enabled");
        String name = skillFileConfig.getString(pluginSkillPath + "name");
        String description = skillFileConfig.getString(pluginSkillPath + "description");
        int ID = skillFileConfig.getInt(pluginSkillPath + "ID");
        String soundName = skillFileConfig.getString(pluginSkillPath + "activate-sound.name");
        int soundPitch = skillFileConfig.getInt(pluginSkillPath + "activate-sound.pitch");
        int soundVolume = skillFileConfig.getInt(pluginSkillPath + "activate-sound.volume");
        HashMap<Integer, Double> rateToActivate = new HashMap<>();
        if (skillFileConfig.get(pluginSkillPath + "rate-to-activate") != null)
            for (String level : skillFileConfig.getConfigurationSection(pluginSkillPath + "rate-to-activate.level").getKeys(false))
                rateToActivate.put(Integer.parseInt(level), skillFileConfig.getDouble(pluginSkillPath + "rate-to-activate.level." + level));
        levelDamageReflection = skillFileConfig.getInt(pluginSkillPath + "damage-reflection.level-to-activate");
        levelDamageEvaluation = skillFileConfig.getString(pluginSkillPath + "damage-reflection.damage-reflection");
        SkillData skillData = new SkillData(ID, SkillType.PLUGIN, enabled, name, description, soundName, soundPitch, soundVolume, rateToActivate) {
            @Override
            public boolean onDamage(SkillData skillData, EntityDamageByEntityEvent event) {
                return onDamageEvent(skillData, event);
            }

            @Override
            public boolean onDie(SkillData skillData, String killerName, String victimName, boolean isMob) {
                return false;
            }
        };
        SkillManager.registerPluginSkill(ID, skillData);
    }

    public static boolean onDamageEvent(SkillData skillData, EntityDamageByEntityEvent event) {
        if (!skillData.isEnabled())
            return false;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        IClanData victimClanData = PluginDataManager.getClanDatabaseByPlayerName(victim.getName());

        if (victimClanData == null)
            return false;

        int skillLevel = victimClanData.getSkillLevel().get(skillData.getId());

        // player clan's has this skill
        if (skillLevel > 0) {
            double chanceToActivate = skillData.getRateToActivate().get(skillLevel) / 100;

            if (new Random().nextDouble() < chanceToActivate) {
                try {
                    event.setCancelled(true);

                    if (skillLevel >= levelDamageReflection)
                        damager.damage(CalculatorUtil.evaluate(levelDamageEvaluation.replace("%damage%", String.valueOf(event.getDamage()))));

                    Location victimLocation = event.getEntity().getLocation();
                    if (!skillData.getSoundName().equals(""))
                        victimLocation.getWorld().playSound(victimLocation, ClansPlus.nms.createSound(skillData.getSoundName()), skillData.getSoundVolume(), skillData.getSoundPitch());
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

}
