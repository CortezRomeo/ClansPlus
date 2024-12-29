package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
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
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        if (playerData.getRank() == Rank.LEADER) {
            MessageUtil.devMessage(player, "You cannot leave because you are the leader of this clan!");
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.getMembers().remove(playerName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
        PluginDataManager.clearPlayerDatabase(playerName);

        MessageUtil.devMessage(player, "Successfully leaved clan " + playerClanData.getName());
        ClanManager.alertClan(playerClanData.getName(), playerName + " just leaved clan!");
    }
}
