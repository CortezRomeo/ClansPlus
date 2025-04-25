package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.listener.PlayerMovementListener;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class Spawn extends SubjectManager {

    public Spawn(Rank rank, Player player, String playerName) {
        super(rank, player, playerName, null, null);
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SPAWN));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        IClanData playerClanData = getPlayerClanData();

        if (playerClanData.getSpawnPoint() == null) {
            MessageUtil.sendMessage(player, Messages.UNKNOWN_SPAWN_POINT);
            return false;
        }

        if (Settings.CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_ENABLED)
            if (Settings.CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_WORLDS.contains(playerClanData.getSpawnPoint().getWorld().getName())) {
                MessageUtil.sendMessage(player, Messages.CLAN_SPAWN_BLACK_LIST_WORLD);
                return false;
            }

        if (Settings.CLAN_SETTING_SPAWN_COUNTDOWN_ENABLED) {
            if (PlayerMovementListener.spawnCountDownPlayers.contains(player))
                return false;
            PlayerMovementListener.spawnCountDownPlayers.add(player);

            AtomicInteger countDownSeconds = new AtomicInteger(Settings.CLAN_SETTING_SPAWN_COUNTDOWN_SECONDS);
            MessageUtil.sendMessage(player, Messages.SPAWN_POINT_COUNT_DOWN.replace("%seconds%", String.valueOf(countDownSeconds.get())));
            ClansPlus.plugin.foliaLib.getScheduler().runAtEntityTimer(player, task -> {
                if (!PlayerMovementListener.spawnCountDownPlayers.contains(player)) {
                    MessageUtil.sendMessage(player, Messages.MOVE_WHILE_SPAWNING);
                    task.cancel();
                    return;
                }

                countDownSeconds.set(countDownSeconds.get() - 1);
                if (countDownSeconds.get() <= 0) {
                    PlayerMovementListener.spawnCountDownPlayers.remove(player);
                    spawn();
                    task.cancel();
                }
            }, 1, 20L);
        } else {
            spawn();
            return true;
        }

        return true;
    }

    public void spawn() {
        IClanData playerClanData = getPlayerClanData();
        if (ClansPlus.plugin.foliaLib.isSpigot()) {
            getPlayer().teleport(playerClanData.getSpawnPoint());
        }
        if (ClansPlus.plugin.foliaLib.isPaper()) {
            getPlayer().teleportAsync(playerClanData.getSpawnPoint());
        }
        if (ClansPlus.plugin.foliaLib.isFolia()) {
            getPlayer().teleportAsync(playerClanData.getSpawnPoint());
        }
        if (ClansPlus.plugin.foliaLib.isUnsupported()) {
            getPlayer().teleport(playerClanData.getSpawnPoint());
        }
        MessageUtil.sendMessage(player, Messages.SPAWN_SUCCESS);
    }
}
