package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawn extends SubjectManager {

    public SetSpawn(Rank rank, Player player, String playerName) {
        super(rank, player, playerName, null, null);
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SETSPAWN));

        String commandPermission = "clanplus.setspawn";
        if (!player.hasPermission(commandPermission)) {
            MessageUtil.sendMessage(player, Messages.PERMISSION_REQUIRED.replace("%permission%", commandPermission));
            return false;
        }

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        if (Settings.CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_ENABLED)
            if (Settings.CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_WORLDS.contains(getPlayer().getWorld().getName())) {
                MessageUtil.sendMessage(player, Messages.CLAN_SET_SPAWN_BLACK_LIST_WORLD);
                return false;
            }

        IClanData playerClanData = getPlayerClanData();
        Location playerLocation = player.getLocation();
        playerClanData.setSpawnPoint(playerLocation);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        int x = (int) playerLocation.getX();
        int y = (int) playerLocation.getY();
        int z = (int) playerLocation.getZ();
        String worldName = playerLocation.getWorld().getName();

        MessageUtil.sendMessage(player, Messages.SET_SPAWN_SUCCESS.replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z)).replace("%world%", worldName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_SET_SPAWN.replace("%player%", playerName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())).replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z)).replace("%world%", worldName));
        return true;
    }
}
