package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    public PlayerDeathListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        Entity entityKiller = event.getEntity().getKiller();
        Entity entityVictim = event.getEntity();

        if (entityKiller == null || entityVictim == null)
            return;
        if (entityKiller.getType() != EntityType.PLAYER || entityVictim.getType() != EntityType.PLAYER)
            return;

        Player killer = (Player) entityKiller;
        IClanData killerClanData = PluginDataManager.getClanDatabaseByPlayerName(killer.getName());

        // TODO check if event is on the progress

        if (killerClanData == null)
            return;

        for (int skillID : killerClanData.getSkillLevel().keySet()) {
            SkillData skillData = SkillManager.getSkillData().get(skillID);
            if (skillData != null)
                if (SkillManager.getSkillID(PluginSkill.DODGE) != skillData.getId())
                    skillData.onDeath(skillData, event);
        }
    }

}
