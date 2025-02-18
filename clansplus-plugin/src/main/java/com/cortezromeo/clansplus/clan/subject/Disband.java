package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Disband extends SubjectManager {

    public Disband(Rank rank, Player player, String playerName) {
        super(rank, player, playerName, null, null);
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        setRequiredRank(Rank.LEADER);

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        IClanData clanData = getPlayerClanData();
        for (String memberName : clanData.getMembers())
            if (!memberName.equals(playerName))
                MessageUtil.sendMessage(Bukkit.getPlayer(memberName), Messages.DISBAND_NOTIFICATION.replace("%clan%", clanData.getName()));

        if (PluginDataManager.deleteClanData(clanData.getName())) {
            MessageUtil.sendMessage(player, Messages.DISBAND_SUCCESS.replace("%clan%", clanData.getName()));
        } else
            MessageUtil.sendMessage(player, Messages.DISBAND_FAIL);

        return true;
    }
}
