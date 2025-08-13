package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandUtil {
    public static void dispatchCommand(Player player, String command) {
        if (command == null || command.equals("")) return;
        String MATCH = "(?ium)^(player:|op:|console:|)(.*)$";
        ClansPlus.support.getFoliaLib().getScheduler().runAtEntity(player, task -> {
            String type = command.replaceAll(MATCH, "$1").replace(":", "").toLowerCase();
            String cmd = command.replaceAll(MATCH, "$2").replaceAll("(?ium)([{]Player[}])", player == null ? "" : player.getName());
            switch (type) {
                case "op":
                    if (player != null) {
                        if (player.isOp()) {
                            player.performCommand(cmd);
                        } else {
                            player.setOp(true);
                            player.performCommand(cmd);
                            player.setOp(false);
                        }
                    }
                    break;
                case "":
                case "player":
                    if (player != null) player.performCommand(cmd);
                    break;
                case "console":
                default:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    break;
            }
        });
    }

}
