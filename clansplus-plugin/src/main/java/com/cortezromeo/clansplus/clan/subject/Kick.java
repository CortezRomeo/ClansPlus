package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
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
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.KICK));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + super.getRequiredRank() + " to kick somebody!");
            return;
        }

        if (playerName.equals(targetName)) {
            MessageUtil.devMessage(player, "You cannot kick yourself.");
            return;
        }

        if (!isTargetInClan()) {
            MessageUtil.devMessage(player, "Target need to be in a clan!");
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        IClanData targetClanData = getTargetClanData();

        if (!playerClanData.getName().equals(targetClanData.getName())) {
            MessageUtil.devMessage(player, "Target need to be in your clan!");
            return;
        }

        PluginDataManager.clearPlayerDatabase(targetName);
        playerClanData.getMembers().remove(targetName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.devMessage(player, "Successfully removed " + targetName + " from clan!");
        MessageUtil.devMessage(target, "You has been kicked from clan " + playerClanData.getName() + " by " + playerName);
        ClanManager.alertClan(playerClanData.getName(), targetName + " has been kicked!");
    }
}
