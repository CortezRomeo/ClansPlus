package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.EventManager;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

    public EntityDamageListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        EventManager.getWarEvent().onDamage(event);
    }

}
