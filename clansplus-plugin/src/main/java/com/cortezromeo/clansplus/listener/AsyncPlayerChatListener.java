package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    public AsyncPlayerChatListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (ChatListenerHandle.handlePlayerChat(event.getPlayer(), event.getMessage()))
            event.setCancelled(true);
    }
}
