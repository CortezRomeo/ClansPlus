package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class Leave extends SubjectManager {

    public Leave(Player player, String playerName) {
        super(null, player, playerName, null, null);
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return;
        }

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        if (playerData.getRank() == Rank.LEADER) {
            MessageUtil.sendMessage(player, Messages.LEADER_CANNOT_LEAVE);
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.getMembers().remove(playerName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
        PluginDataManager.clearPlayerDatabase(playerName);

        MessageUtil.sendMessage(player, Messages.LEAVE_CLAN_SUCCESS.replace("%clan%", playerClanData.getName()));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_PLAYER_LEAVE_CLAN.replace("%player%", playerName));
    }
}
