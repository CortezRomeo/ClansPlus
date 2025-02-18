package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.Chat;
import com.cortezromeo.clansplus.clan.subject.Create;
import com.cortezromeo.clansplus.clan.subject.SetCustomName;
import com.cortezromeo.clansplus.clan.subject.SetMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class AsyncPlayerChatListener implements Listener {

    public static List<Player> createClan = new ArrayList<>();
    public static List<Player> setCustomName = new ArrayList<>();
    public static List<Player> setMessage = new ArrayList<>();

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

        if (setCustomName.contains(player)) {
            event.setCancelled(true);
            setCustomName.remove(player);
            new SetCustomName(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETCUSTOMNAME), player, player.getName(), message).execute();
        }

        if (setMessage.contains(player)) {
            event.setCancelled(true);
            setMessage.remove(player);
            new SetMessage(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETMESSAGE), player, player.getName(), message).execute();
        }

        if (ClanManager.getPlayerUsingClanChat().contains(player)) {
            event.setCancelled(true);
            if (!new Chat(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.CHAT), player, player.getName(), message).execute())
                ClanManager.getPlayerUsingClanChat().remove(player);
        }
    }
}
