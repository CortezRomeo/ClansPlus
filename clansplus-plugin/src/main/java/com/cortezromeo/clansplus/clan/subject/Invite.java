package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Invite extends SubjectManager {

    private final int estimatedTime;

    public Invite(Rank rank, Player player, String playerName, Player target, String targetName, int estimatedTime) {
        super(rank, player, playerName, target, targetName);
        this.estimatedTime = estimatedTime;
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.INVITE));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + super.getRequiredRank() + " to invite somebody!");
            return;
        }

        if (isTargetInClan()) {
            MessageUtil.devMessage(player, "Target is already in a clan!");
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        if (playerClanData.getMembers().size() >= playerClanData.getMaxMember()) {
            MessageUtil.devMessage(player, "There is not enough member slots in your clan to invite a new member!");
            return;
        }

        if (ClanManager.beingInvitedPlayers.containsKey(targetName)) {
            MessageUtil.devMessage(player, targetName + " is being invited!");
            return;
        }

        ClanManager.beingInvitedPlayers.put(targetName, playerClanData.getName());
        MessageUtil.devMessage(player, "Successfully invited " + targetName + " to your clan, they have 30 seconds to accept the invite.");
        MessageUtil.devMessage(target, "You are being invited to clan " + playerClanData.getName() + " from " + playerName + ", you have 30 seconds to accept the invite.");

        Bukkit.getScheduler().runTaskLaterAsynchronously(ClansPlus.plugin, () -> {
            if (ClanManager.beingInvitedPlayers.containsKey(targetName)) {
                MessageUtil.devMessage("The invite request to join clan " + playerClanData.getName() + " has been expired!");
                MessageUtil.devMessage(player, "The invite to join of " + targetName + " has been expired.");
                ClanManager.beingInvitedPlayers.remove(targetName);
            }
        }, 20L * estimatedTime);
    }
}
