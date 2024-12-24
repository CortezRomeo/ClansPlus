package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void throwErrorMessage(String message) {
        Bukkit.getLogger().severe(message);
        log("&4&l[BANG HOI ERROR] &c&lIf this error affect player's experience, please contact me through discord: Cortez_Romeo");
    }

    public static void sendBroadCast(String message) {
        if (message.equals(""))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            sendMessage(p, message);
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ClansPlus.nms.addColor(message));
    }

    public static void debug(String prefix, String message) {
        if (!Settings.DEBUG_ENABLED)
            return;
        Bukkit.getConsoleSender().sendMessage(ClansPlus.nms.addColor(Settings.DEBUG_PREFIX + prefix.toUpperCase() + " >>> " + message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        message = message.replace("%prefix%" , "[prefix] ");
        sender.sendMessage(ClansPlus.nms.addColor(message));
    }

    public static void sendMessage(Player player, String message) {
        if (player == null | message.equals(""))
            return;

        message = message.replace("%prefix%" , "[prefix] ");

        if (!ClansPlus.isPapiSupport())
            player.sendMessage(ClansPlus.nms.addColor(message));
        else
            player.sendMessage(ClansPlus.nms.addColor(PlaceholderAPI.setPlaceholders(player, message)));
    }

    // only use for testing plugin
    public static void devMessage(String message) {
        log("[DEV] " + message);
    }

    public static void devMessage(Player player, String message) {
        if (player != null)
            player.sendMessage("[DEV] " + message);
    }

}
