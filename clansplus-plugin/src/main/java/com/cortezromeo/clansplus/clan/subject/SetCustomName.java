package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
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
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        setRequiredRank(getPlayerClanData().getSubjectPermission().get(Subject.SETCUSTOMNAME));

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + super.getRequiredRank() + " to set custom name!");
            return;
        }

        if (PluginDataManager.getClanDatabase().containsKey(ClansPlus. nms.stripColor(customName))) {
            MessageUtil.devMessage(player, "You cannot use this custom name because there is a clan with the same name.");
            return;
        }

        IClanData playerClanData = getPlayerClanData();
        playerClanData.setCustomName(customName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.devMessage(player, "Successfully set clan's custom name to " + customName);
        ClanManager.alertClan(playerClanData.getName(), playerName + " set clan's custom name to " + customName);
    }
}
