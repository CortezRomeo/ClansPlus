package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
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

        IClanData playerClanData = getPlayerClanData();
        playerClanData.setCustomName(customName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.sendMessage(player, Messages.SET_CUSTOM_NAME_SUCCESS.replaceAll("%clan%", playerClanData.getName()).replaceAll("%newCustomName%", customName));
        ClanManager.alertClan(playerClanData.getName(), Messages.CLAN_BROADCAST_SET_CUSTOM_NAME.replace("%player%", playerName).replace("%newCustomName%", customName).replace("%rank%", ClanManager.getFormatRank(PluginDataManager.getPlayerDatabase(playerName).getRank())));

        return true;
    }
}
