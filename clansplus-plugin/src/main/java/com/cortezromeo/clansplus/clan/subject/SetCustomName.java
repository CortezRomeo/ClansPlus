package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class SetCustomName extends SubjectManager {

    private final String customName;

    public SetCustomName(Rank rank, Player player, String playerName, String customName) {
        super(rank, player, playerName, null, null);
        this.customName = customName;
    }

    @Override
    public boolean execute() {
        if (!isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
            return false;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SETCUSTOMNAME));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.sendMessage(player, Messages.REQUIRED_RANK.replace("%requiredRank%", ClanManager.getFormatRank(getRequiredRank())));
            return false;
        }

        if (PluginDataManager.getClanDatabase().containsKey(ClansPlus.nms.stripColor(customName))) {
            MessageUtil.sendMessage(player, Messages.CLAN_ALREADY_EXIST.replace("%clan%", customName));
            return false;
        }

        if (ClanManager.getClansCustomName() != null)
            if (!ClanManager.getClansCustomName().isEmpty())
                for (String clanCustomName : ClanManager.getClansCustomName())
                    if (clanCustomName.equalsIgnoreCase(customName)) {
                        MessageUtil.sendMessage(player, Messages.CLAN_ALREADY_EXIST.replace("%clan%", customName));
                        return false;
                    }

        if (customName.length() < Settings.CLAN_SETTING_CUSTOM_NAME_MINIMUM_LENGTH) {
            MessageUtil.sendMessage(player, Messages.ILLEGAL_MINIMUM_CLAN_LENGTH.replace("%minimumClanNameLength%", String.valueOf(Settings.CLAN_SETTING_CUSTOM_NAME_MINIMUM_LENGTH)));
            return false;
        }

        if (customName.length() > Settings.CLAN_SETTING_CUSTOM_NAME_MAXIMUM_LENGTH) {
            MessageUtil.sendMessage(player, Messages.ILLEGAL_MAXIMUM_CLAN_LENGTH.replace("%maximumClanNameLength%", String.valueOf(Settings.CLAN_SETTING_CUSTOM_NAME_MAXIMUM_LENGTH)));
            return false;
        }

        for (String prohibitedClanName : Settings.CLAN_SETTING_PROHIBITED_NAME) {
            if (customName.equalsIgnoreCase(prohibitedClanName)) {
                MessageUtil.sendMessage(player, Messages.PROHIBITED_CLAN_NAME.replace("%clanName%", customName));
                return false;
            }
        }

        for (String prohibitedCharacter : Settings.CLAN_SETTING_PROHIBITED_CHARACTER) {
            if (customName.contains(prohibitedCharacter)) {
                MessageUtil.sendMessage(player, Messages.PROHIBITED_CHARACTER.replace("%character%", prohibitedCharacter));
                return false;
            }
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.setCustomName(customName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.sendMessage(player, Messages.SET_CUSTOM_NAME_SUCCESS.replace("%clan%", playerClanData.getName()).replace("%newCustomName%", customName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_SET_CUSTOM_NAME.replace("%player%", playerName).replace("%newCustomName%", customName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));

        return true;
    }
}
