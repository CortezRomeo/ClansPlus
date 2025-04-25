package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class Invite extends SubjectManager {

    private final int estimatedTime;

    public Invite(Rank rank, Player player, String playerName, Player target, String targetName, int estimatedTime) {
        super(rank, player, playerName, target, targetName);
        this.estimatedTime = estimatedTime;
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.INVITE));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        if (!PluginDataManager.getPlayerDatabase().containsKey(targetName)) {
            MessageUtil.sendMessage(player, Messages.TARGET_DOES_NOT_EXIST.replace("%player%", targetName));
            return false;
        }

        if (isTargetInClan()) {
            MessageUtil.sendMessage(player, Messages.TARGET_ALREADY_IN_CLAN.replace("%player%", targetName));
            return false;
        }

        IClanData playerClanData = getPlayerClanData();
        if (playerClanData.getMembers().size() >= playerClanData.getMaxMembers()) {
            MessageUtil.sendMessage(player, Messages.NOT_ENOUGH_MEMBER_SLOTS);
            return false;
        }

        if (ClanManager.beingInvitedPlayers.containsKey(targetName)) {
            MessageUtil.sendMessage(player, Messages.TARGET_INVITATION.replace("%player%", targetName));
            return false;
        }

        ClanManager.beingInvitedPlayers.put(targetName, playerClanData.getName());
        MessageUtil.sendMessage(player, Messages.INVITATION_SUCCESS.replace("%player%", targetName).replace("%seconds%", String.valueOf(Settings.CLAN_SETTING_TIME_TO_ACCEPT)));
        MessageUtil.sendMessage(target, Messages.INCOMING_CLAN_INVITE.replace("%player%", playerName).replace("%seconds%", String.valueOf(Settings.CLAN_SETTING_TIME_TO_ACCEPT)).replace("%clan%", playerClanData.getName()));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_INVITE_NOTIFICATION.replace("%player%", playerName).replace("%target%", targetName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));

        ClansPlus.plugin.foliaLib.getScheduler().runLaterAsync(() -> {
            if (ClanManager.beingInvitedPlayers.containsKey(targetName)) {
                MessageUtil.sendMessage(target, Messages.INVITE_EXPIRED.replace("%clan%", ClanManager.beingInvitedPlayers.get(targetName)));
                MessageUtil.sendMessage(player, Messages.INVITER_INVITE_EXPIRED.replace("%player%", targetName));
                ClanManager.beingInvitedPlayers.remove(targetName);
            }
        }, 20L * estimatedTime);

        return true;
    }
}
