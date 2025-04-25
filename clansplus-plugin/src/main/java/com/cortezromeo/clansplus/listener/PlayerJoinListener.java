package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        ClansPlus.plugin.foliaLib.getScheduler().runAsync(task -> {
            Player player = event.getPlayer();
            PluginDataManager.loadPlayerDatabase(player.getName());
            PluginDataManager.getPlayerDatabase(player.getName()).setLastActivated(new Date().getTime());

            ClansPlus.plugin.foliaLib.getScheduler().runLaterAsync(task1 -> EventManager.getWarEvent().onJoin(event), 30);
            ClansPlus.plugin.foliaLib.getScheduler().runLaterAsync(task2 -> ClanManager.sendClanBroadCast(player), 40);
        });
    }

}
