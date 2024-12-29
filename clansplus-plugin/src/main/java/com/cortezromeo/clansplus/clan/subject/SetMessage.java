package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class SetMessage extends SubjectManager {

    private final String clanMessage;

    public SetMessage(Rank rank, Player player, String playerName, String clanMessage) {
        super(rank, player, playerName, null, null);
        this.clanMessage = clanMessage;
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SETMESSAGE));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + super.getRequiredRank() + " to set clan message!");
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.setMessage(clanMessage);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.devMessage(player, "Successfully set clan's message to " + clanMessage);
        ClanManager.alertClan(playerClanData.getName(), playerName + " set clan's message to " + clanMessage);
    }
}
