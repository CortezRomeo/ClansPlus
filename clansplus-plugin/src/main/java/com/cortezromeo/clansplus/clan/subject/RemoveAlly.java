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
import org.bukkit.entity.Player;

public class RemoveAlly extends SubjectManager {

    private String targetClanName;

    public RemoveAlly(Rank rank, Player player, String playerName, String targetClanName) {
        super(rank, player, playerName, null, null);
        this.targetClanName = targetClanName;
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.MANAGEALLY));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        if (!PluginDataManager.getClanDatabase().containsKey(targetClanName)) {
            MessageUtil.sendMessage(player, Messages.CLAN_NO_LONGER_EXIST.replace("%clan%", targetClanName));
            return false;
        }

        IClanData playerClanData = getPlayerClanData();

        if (playerClanData.getName().equals(targetClanName)) {
            MessageUtil.sendMessage(player, Messages.CLAN_CANNOT_BE_THE_SAME);
            return false;
        }

        if (!playerClanData.getAllies().contains(targetClanName)) {
            MessageUtil.sendMessage(player, Messages.TARGET_CLAN_ALLY_MEMBERSHIP_ERROR.replace("%clan%", targetClanName));
            return false;
        }

        IClanData targetClanData = PluginDataManager.getClanDatabase(targetClanName);
        targetClanData.getAllies().remove(playerClanData.getName());
        playerClanData.getAllies().remove(targetClanName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
        PluginDataManager.saveClanDatabaseToStorage(targetClanName, targetClanData);

        MessageUtil.sendMessage(player, Messages.REMOVE_ALLY_SUCCESS.replace("%clan%", targetClanName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_REMOVE_ALLY_NOTIFICATION.replace("%player%", playerName).replace("%clan%", targetClanName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));
        ClanManager.alertClan(targetClanName, Messages.CLAN_BROADCAST_ALLY_REMOVED_NOTIFICATION.replace("%player%", playerName).replace("%clan%", playerClanData.getName()));
        return true;
    }
}
