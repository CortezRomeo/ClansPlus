package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.subject.Create;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class AsyncPlayerChatListener implements Listener {

    public static List<Player> createClan = new ArrayList<>();

    public AsyncPlayerChatListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (createClan.contains(player)) {
            event.setCancelled(true);
            createClan.remove(player);
            new Create(player, player.getName(), message).execute();
        }
    }
}
