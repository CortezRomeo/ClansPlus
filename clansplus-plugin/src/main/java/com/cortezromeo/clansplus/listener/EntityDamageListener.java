package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.clan.skill.SkillManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

    public EntityDamageListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityVictim = event.getEntity();

        if (entityDamager == null || entityVictim == null)
            return;
        if (entityDamager.getType() != EntityType.PLAYER || entityVictim.getType() != EntityType.PLAYER)
            return;

        Player damager = (Player) entityDamager;
        IClanData damagerClanData = PluginDataManager.getClanDatabaseByPlayerName(damager.getName());
        IClanData victimClanData = PluginDataManager.getClanDatabaseByPlayerName(entityVictim.getName());

        // TODO check if event is on the progress

        if (victimClanData != null) {
            SkillData skillData = SkillManager.getSkillData().get(SkillManager.getSkillID(PluginSkill.DODGE));
            if (skillData != null)
                skillData.onDamage(skillData, event);
            return;
        }

        if (damagerClanData == null)
            return;

        for (int skillID : damagerClanData.getSkillLevel().keySet()) {
            SkillData skillData = SkillManager.getSkillData().get(skillID);
            if (skillData != null)
                if (SkillManager.getSkillID(PluginSkill.DODGE) != skillData.getId())
                    skillData.onDamage(skillData, event);
        }
    }

}
