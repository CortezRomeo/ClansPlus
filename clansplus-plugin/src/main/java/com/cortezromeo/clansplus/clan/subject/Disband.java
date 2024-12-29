package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Disband extends SubjectManager {

    public Disband(Rank rank, Player player, String playerName) {
        super(rank, player, playerName, null, null);
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.DISBAND));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + getRequiredRank());
            return;
        }

        IClanData clanData = getPlayerClanData();
        for (String memberName : clanData.getMembers())
            if (!memberName.equals(playerName))
                MessageUtil.devMessage(Bukkit.getPlayer(memberName), "Your clan has been disbanded.");

        if (PluginDataManager.deleteClanData(clanData.getName())) {
            MessageUtil.devMessage(player, "Clan " + clanData.getName() + " has been disbanded!");
        } else
            MessageUtil.devMessage(player, "An error occurred when trying to delete this clan! Please contact the admins.");
    }
}
