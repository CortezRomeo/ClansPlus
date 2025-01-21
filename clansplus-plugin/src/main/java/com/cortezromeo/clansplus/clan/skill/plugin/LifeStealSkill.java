package com.cortezromeo.clansplus.clan.skill.plugin;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.CalculatorUtil;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Random;

public class LifeStealSkill {

    public static void registerSkill() {
        FileConfiguration skillFileConfig = SkillsFile.get();
        String pluginSkillPath = "plugin-skills.life_steal.";
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
        SkillData skillData = new SkillData(ID, SkillType.PLUGIN, enabled, name, description, soundName, soundPitch, soundVolume, rateToActivate) {
            @Override
            public boolean onDamage(SkillData skillData, EntityDamageByEntityEvent event) {
                return onDamageEvent(skillData, event);
            }
            @Override
            public boolean onDie(SkillData skillData, PlayerDeathEvent event) {
                return false;
            }
        };
        SkillManager.registerPluginSkill(ID, skillData);
    }

    public static boolean onDamageEvent(SkillData skillData, EntityDamageByEntityEvent event) {
        if (!skillData.isEnabled())
            return false;

        Player damager = (Player) event.getDamager();
        IClanData damagerClanData = PluginDataManager.getClanDatabaseByPlayerName(damager.getName());

        if (damagerClanData == null)
            return false;

        int skillLevel = damagerClanData.getSkillLevel().get(skillData.getId());

        // player clan's has this skill
        if (skillLevel > 0) {
            double chanceToActivate = skillData.getRateToActivate().get(skillLevel) / 100;

            if (new Random().nextDouble() < chanceToActivate) {
                try {
                    double revivingHealth = CalculatorUtil.evaluate(SkillsFile.get().getString("plugin-skills.life_steal.heal.level." + skillLevel).replace("%playerMaxHealth%", String.valueOf(damager.getMaxHealth())));
                    if (damager.getHealth() + revivingHealth > damager.getMaxHealth())
                        damager.setHealth(damager.getMaxHealth());
                    else
                        damager.setHealth(damager.getHealth() + revivingHealth);

                    Location damagerLocation = damager.getLocation();
                    damagerLocation.getWorld().spawnParticle(XParticle.HAPPY_VILLAGER.get(), damagerLocation, 2);
                    if (!skillData.getSoundName().equals(""))
                        damagerLocation.getWorld().playSound(damagerLocation, ClansPlus.nms.createSound(skillData.getSoundName()), skillData.getSoundVolume(), skillData.getSoundPitch());
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

}
