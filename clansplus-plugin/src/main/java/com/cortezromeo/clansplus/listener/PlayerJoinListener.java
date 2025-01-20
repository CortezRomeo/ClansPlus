package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Date;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
            PluginDataManager.loadPlayerDatabase(event.getPlayer().getName());
            PluginDataManager.getPlayerDatabase(event.getPlayer().getName()).setLastActivated(new Date().getTime());

            Bukkit.getScheduler().runTaskLaterAsynchronously(ClansPlus.plugin, () -> EventManager.getWarEvent().onJoin(event), 30);
        });
    }

}
