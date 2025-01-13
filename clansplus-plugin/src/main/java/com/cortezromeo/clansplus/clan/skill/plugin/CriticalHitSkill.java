package com.cortezromeo.clansplus.clan.skill.plugin;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.clan.skill.SkillManager;
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

public class CriticalHitSkill {

    public static void registerSkill() {
        FileConfiguration skillFileConfig = SkillsFile.get();
        String pluginSkillPath = "plugin-skills.critical_hit.";
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
            public void onDamage(SkillData skillData, EntityDamageByEntityEvent event) {
                onDamageEvent(skillData, event);
            }

            @Override
            public void onDeath(SkillData skillData, PlayerDeathEvent event) {}
        };
        SkillManager.registerPluginSkill(ID, skillData);
    }

    public static void onDamageEvent(SkillData skillData, EntityDamageByEntityEvent event) {
        if (!skillData.isEnabled())
            return;

        Player damager = (Player) event.getDamager();
        IClanData damagerClanData = PluginDataManager.getClanDatabaseByPlayerName(damager.getName());

        if (damagerClanData == null)
            return;

        int skillLevel = damagerClanData.getSkillLevel().get(skillData.getId());

        // player clan's has this skill
        if (skillLevel > 0) {
            double chanceToActivate = skillData.getRateToActivate().get(skillLevel) / 100;

            if (new Random().nextDouble() < chanceToActivate) {
                try {
                    double damage = CalculatorUtil.evaluate(SkillsFile.get().getString("plugin-skills.critical_hit.on-hit").replace("%damage%", String.valueOf(event.getDamage())));
                    event.setDamage(damage);

                    Location victimLocation = event.getEntity().getLocation();
                    victimLocation.getWorld().spawnParticle(XParticle.EXPLOSION.get(), victimLocation, 2);
                    if (!skillData.getSoundName().equals(""))
                        victimLocation.getWorld().playSound(victimLocation, ClansPlus.nms.createSound(skillData.getSoundName()), skillData.getSoundPitch(), skillData.getSoundVolume());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
