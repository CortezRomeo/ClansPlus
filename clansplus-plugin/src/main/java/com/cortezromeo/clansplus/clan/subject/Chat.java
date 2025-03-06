package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Chat extends SubjectManager {

    private String message;

    public Chat(Rank rank, Player player, String playerName, String message) {
        super(rank, player, playerName, null, null);
        this.message = message;
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        if (!Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.CHAT));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        IClanData playerClanData = getPlayerClanData();

        String clanChatFormat = Messages.USING_CHAT_BOX_CLAN_CHAT;
        clanChatFormat = StringUtil.setClanNamePlaceholder(clanChatFormat, playerClanData.getName());
        clanChatFormat = clanChatFormat.replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank()));
        clanChatFormat = clanChatFormat.replace("%player%", playerName);
        clanChatFormat = clanChatFormat.replace("%message%", message);

        for (String clanMember : playerClanData.getMembers())
            MessageUtil.sendMessage(Bukkit.getPlayer(clanMember), clanChatFormat);

        // for the spy
        String spyMessage = "[BANG HOI CHAT SPY] (" + PluginDataManager.getPlayerDatabase(playerName).getRank() + ") " + playerName + " (" + playerClanData.getName() + "): " + message;
        for (Player playerSpy : ClanManager.getPlayerUsingChatSpy())
            MessageUtil.sendMessage(playerSpy, spyMessage);
        if (ClanManager.isConsoleUsingChatSpy())
            MessageUtil.log(spyMessage);
        return true;
    }
}
