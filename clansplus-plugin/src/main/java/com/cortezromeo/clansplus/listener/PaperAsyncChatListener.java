package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

// Use this class for server using paper fork software
public class PaperAsyncChatListener implements Listener {

    public PaperAsyncChatListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        TextComponent textComponent = (TextComponent) event.message();
        if (ChatListenerHandle.handlePlayerChat(event.getPlayer(), textComponent.content())) event.setCancelled(true);
    }

}
