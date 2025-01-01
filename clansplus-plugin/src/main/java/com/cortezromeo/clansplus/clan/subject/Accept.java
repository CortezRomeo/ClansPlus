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

import java.util.Date;

public class Accept extends SubjectManager {

    public Accept(Player player, String playerName) {
        super(null, player, playerName, null, null);
    }

    @Override
    public void execute() {
        if (!ClanManager.beingInvitedPlayers.containsKey(playerName)) {
            MessageUtil.sendMessage(player, Messages.INVITATION_REJECTION);
            return;
        }

        if (isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.ALREADY_IN_CLAN);
            ClanManager.beingInvitedPlayers.remove(playerName);
            return;
        }

        String clanName = ClanManager.beingInvitedPlayers.get(playerName);
        if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.sendMessage(player, Messages.CLAN_NO_LONGER_EXIST.replace("%clan%", clanName));
            ClanManager.beingInvitedPlayers.remove(playerName);
            return;
        }

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        if (clanData.getMembers().size() == clanData.getMaxMember()) {
            MessageUtil.sendMessage(player, Messages.CLAN_IS_FULL.replace("%clan%", clanName));
            ClanManager.beingInvitedPlayers.remove(playerName);
            return;
        }

        clanData.getMembers().add(playerName);
        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        playerData.setClan(clanName);
        playerData.setRank(Rank.MEMBER);
        playerData.setJoinDate(new Date().getTime());
        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);
        PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);

        MessageUtil.sendMessage(player, Messages.JOIN_CLAN_SUCCESS.replace("%clan%", clanName));
        ClanManager.alertClan(clanName, Messages.CLAN_BROADCAST_PLAYER_JOIN_CLAN.replace("%player%", playerName));
        ClanManager.beingInvitedPlayers.remove(playerName);
    }
}
