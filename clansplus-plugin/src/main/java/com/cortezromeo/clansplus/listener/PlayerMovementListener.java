package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerMovementListener implements Listener {

    public static List<Player> spawnCountDownPlayers = new ArrayList<>();

    public PlayerMovementListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        spawnCountDownPlayers.remove(event.getPlayer());
    }

}
