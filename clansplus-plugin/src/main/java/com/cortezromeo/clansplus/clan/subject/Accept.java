package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class Accept extends SubjectManager {

    public Accept(Player player, String playerName) {
        super(null, player, playerName, null, null);
    }

    @Override
    public boolean execute() {
        if (!ClanManager.beingInvitedPlayers.containsKey(playerName)) {
            MessageUtil.sendMessage(player, Messages.INVITATION_REJECTION);
            return false;
        }

        if (isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.ALREADY_IN_CLAN);
            ClanManager.beingInvitedPlayers.remove(playerName);
            return false;
        }

        String clanName = ClanManager.beingInvitedPlayers.get(playerName);
        if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.sendMessage(player, Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", clanName));
            ClanManager.beingInvitedPlayers.remove(playerName);
            return false;
        }

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        if (clanData.getMembers().size() == clanData.getMaxMembers()) {
            MessageUtil.sendMessage(player, Messages.CLAN_IS_FULL.replace("%clan%", clanName));
            ClanManager.beingInvitedPlayers.remove(playerName);
            return false;
        }

        ClanManager.addPlayerToAClan(playerName, clanName, true);

        MessageUtil.sendMessage(player, Messages.JOIN_CLAN_SUCCESS.replace("%clan%", clanName));
        ClanManager.alertClan(clanName, Messages.CLAN_BROADCAST_PLAYER_JOIN_CLAN.replace("%player%", playerName));
        ClanManager.beingInvitedPlayers.remove(playerName);

        return true;
    }
}
