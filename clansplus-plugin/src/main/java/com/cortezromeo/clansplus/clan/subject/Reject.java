package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class Reject extends SubjectManager {

    public Reject(Player player, String playerName) {
        super(null, player, playerName, null, null);
    }

    @Override
    public void execute() {
        if (!ClanManager.beingInvitedPlayers.containsKey(playerName)) {
            MessageUtil.devMessage(player, "You are not being invited!");
            return;
        }

        String clanName = ClanManager.beingInvitedPlayers.get(playerName);
        ClanManager.beingInvitedPlayers.remove(playerName);

        if (isPlayerInClan()) {
            MessageUtil.devMessage(player, "You are already in a clan!");
            return;
        }

        if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.devMessage(player, "Clan " + clanName + " is no longer exist!");
            return;
        }

        MessageUtil.devMessage(player, "Rejected invite to join clan " + clanName + " successfully.");
        ClanManager.alertClan(clanName, playerName + " rejected to join clan!");
    }
}
