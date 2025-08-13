package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.Settings;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class PlayerUtil {

    public static boolean isVanished(Player player) {
        if (!Settings.VANISH_SETTING_HIDE_VANISH_PLAYER_ENABLED) return false;

        if (player == null) return false;

        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

}
