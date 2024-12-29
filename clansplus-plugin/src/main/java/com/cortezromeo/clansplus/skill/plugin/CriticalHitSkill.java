package com.cortezromeo.clansplus.skill.plugin;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.SkillType;
import com.cortezromeo.clansplus.skill.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CriticalHitSkill implements Listener {

    private final SkillData skillData;

    public CriticalHitSkill(int id, SkillType skillType, boolean enabled, String name, String description) {
        skillData = new SkillData(id, skillType, enabled, name, description);
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void event(EntityDamageByEntityEvent event) {
/*        if (!WarManager.eventStarted || event.getDamage() == 0)
            return;

        Entity d = event.getDamager();
        Entity v = event.getEntity();

        if (d == null || v == null)
            return;

        if (d.getType() != EntityType.PLAYER || v.getType() != EntityType.PLAYER)
            return;

        Player victim = (Player) v;

        if (!DatabaseManager.playerDatabase.containsKey(victim.getName()))
            return;

        String clanName = DatabaseManager.getPlayerData(d.getName()).getBangHoi();

        if (clanName == null || DatabaseManager.getPlayerData(v.getName()).getBangHoi() == null)
            return;

        if (DatabaseManager.getBangHoiData(clanName).getSkillLevel(SkillType.critDamage) == 0)
            return;

        Player damager = (Player) d;
        if (new Random().nextDouble() < SkillManager.getSkillChance(SkillType.critDamage) / 100) {
            try {
                double damage = StringUtil.evaluate(SkillManager.getSkillValue(SkillType.critDamage).replace("%damage%", String.valueOf(event.getDamage())));
                long formatDamage = Math.round(damage);
                event.setDamage(damage);

                Location victimLocation = victim.getLocation();
                victimLocation.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, victimLocation, 2);
                victimLocation.getWorld().playSound(victimLocation, Sound.ENTITY_BLAZE_HURT, 1, 2);

                damager.sendMessage(BangHoi.nms.addColor(mse.getString("skill.1CritDamage.damager")
                        .replace("%player%", victim.getName())
                        .replace("%dmg%", String.valueOf(formatDamage))));
                victim.sendMessage(BangHoi.nms.addColor(mse.getString("skill.1CritDamage.victim")
                        .replace("%player%", damager.getName())));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void register() {
        skillData.register(skillData.getId(), skillData);
    }
}
