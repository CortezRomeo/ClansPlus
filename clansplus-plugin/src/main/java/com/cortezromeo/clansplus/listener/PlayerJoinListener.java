package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
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
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
            Player player = event.getPlayer();
            PluginDataManager.loadPlayerDatabase(player.getName());
            PluginDataManager.getPlayerDatabase(player.getName()).setLastActivated(new Date().getTime());

            if (Settings.CLAN_SETTINGS_MESSAGES_SETTINGS_ON_JOIN_WAR_EVENT_ENABLED)
                ClansPlus.support.getFoliaLib().getScheduler().runLaterAsync(task1 -> EventManager.getWarEvent().onJoin(event), 20 * Settings.CLAN_SETTINGS_MESSAGES_SETTINGS_ON_JOIN_WAR_EVENT_DELAY);
            if (Settings.CLAN_SETTINGS_MESSAGES_SETTINGS_ON_JOIN_CLAN_BROADCAST_ENABLED)
                ClansPlus.support.getFoliaLib().getScheduler().runLaterAsync(task2 -> ClanManager.sendClanBroadCast(player), 20 * Settings.CLAN_SETTINGS_MESSAGES_SETTINGS_ON_JOIN_CLAN_BROADCAST_DELAY);
        });
    }

}
