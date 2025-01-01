package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class RemoveManager extends SubjectManager {

    public RemoveManager(Rank rank, Player player, String playerName, Player target, String targetName) {
        super(rank, player, playerName, target, targetName);
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.REMOVEMANAGER));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return;
        }

        if (playerName.equals(targetName)) {
            MessageUtil.sendMessage(player, Messages.SELF_TARGETED_ERROR);
            return;
        }

        if (!isTargetInClan()) {
            MessageUtil.sendMessage(player, Messages.TARGET_MUST_BE_IN_CLAN.replace("%player%", targetName));
            return;
        }

        IPlayerData targetData =  PluginDataManager.getPlayerDatabase(targetName);
        IClanData playerClanData = getPlayerClanData();

        if (!isTargetAndPlayerInTheSameClan()) {
            MessageUtil.sendMessage(player, Messages.TARGET_CLAN_MEMBERSHIP_ERROR.replace("%player%", targetName));
            return;
        }

        if (targetData.getRank() != Rank.MANAGER) {
            MessageUtil.sendMessage(player, Messages.NOT_A_MANAGER.replace("%player%", targetName));
            return;
        }

        targetData.setRank(Rank.MEMBER);
        PluginDataManager.savePlayerDatabaseToStorage(targetName, targetData);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.sendMessage(player, Messages.REMOVE_A_MANAGER_SUCCESS.replace("%clan%", getPlayerClanData().getName()).replace("%player%", targetName));
        MessageUtil.sendMessage(target, Messages.MANAGER_REMOVED.replace("%clan%", getPlayerClanData().getName()).replace("%player%", playerName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_MANAGER_REMOVED.replace("%player%", playerName).replace("%target%", targetName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));
    }
}
