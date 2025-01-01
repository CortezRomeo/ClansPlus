package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class Kick extends SubjectManager {

    public Kick(Rank rank, Player player, String playerName, Player target, String targetName) {
        super(rank, player, playerName, target, targetName);
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.KICK));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return;
        }

        if (playerName.equals(targetName)) {
            MessageUtil.sendMessage(player, Messages.SELF_KICK_ERROR);
            return;
        }

        if (!isTargetInClan()) {
            MessageUtil.sendMessage(player, Messages.TARGET_MUST_BE_IN_CLAN.replace("%player%", targetName));
            return;
        }

        IClanData playerClanData = getPlayerClanData();

        if (!isTargetAndPlayerInTheSameClan()) {
            MessageUtil.sendMessage(player, Messages.TARGET_CLAN_MEMBERSHIP_ERROR.replace("%player%", targetName));
            return;
        }

        if (PluginDataManager.getPlayerDatabase(targetName).getRank() == Rank.LEADER) {
            MessageUtil.sendMessage(player, Messages.KICK_LEADER_ERROR);
            return;
        }

        PluginDataManager.clearPlayerDatabase(targetName);
        playerClanData.getMembers().remove(targetName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.sendMessage(player, Messages.TARGET_REMOVAL_SUCCESS.replace("%player%", targetName));
        MessageUtil.sendMessage(target, Messages.KICKED_FROM_CLAN.replace("%clan%", playerClanData.getName()).replace("%player%", playerName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_PLAYER_REMOVED_FROM_CLAN.replace("%player%", targetName).replace("%by%", playerName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));
    }
}
