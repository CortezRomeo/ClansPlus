package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
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
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SETMESSAGE));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.setMessage(clanMessage);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.sendMessage(player, Messages.SET_MESSAGE_SUCCESS.replace("%newMessage%", clanMessage));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_SET_MESSAGE.replace("%player%", playerName).replace("%newMessage%", clanMessage).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));

        return true;
    }
}
