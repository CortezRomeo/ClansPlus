package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        if (Settings.CHAT_SETTING_USE_PAPER_ASYNC_CHAT) {
            TextComponent textComponent = (TextComponent) event.message();
            if (ChatListenerHandler.handlePlayerChat(event.getPlayer(), textComponent.content()))
                event.setCancelled(true);
        }
    }

}
