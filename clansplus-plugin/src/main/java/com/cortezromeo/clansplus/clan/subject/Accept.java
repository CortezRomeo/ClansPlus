package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
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
            MessageUtil.devMessage(player, "You are not being invited!");
            return;
        }

        if (isPlayerInClan()) {
            MessageUtil.devMessage(player, "You are already in a clan!");
            ClanManager.beingInvitedPlayers.remove(playerName);
            return;
        }

        String clanName = ClanManager.beingInvitedPlayers.get(playerName);
        if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.devMessage(player, "Clan " + clanName + " is no longer exist!");
            ClanManager.beingInvitedPlayers.remove(playerName);
            return;
        }

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        if (clanData.getMembers().size() == clanData.getMaxMember()) {
            MessageUtil.devMessage(player, "Clan " + clanName + " is full!");
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

        MessageUtil.devMessage(player, "Successfully joined clan " + clanName);
        ClanManager.alertClan(clanName, playerName + " just joined clan!");
        ClanManager.beingInvitedPlayers.remove(playerName);
    }
}
