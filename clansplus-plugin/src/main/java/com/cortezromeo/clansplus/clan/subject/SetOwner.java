package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

public class SetOwner extends SubjectManager {

    public SetOwner(Rank rank, Player player, String playerName, Player target, String targetName) {
        super(rank, player, playerName, target, targetName);
    }

    @Override
    public void execute() {
        if (!isPlayerInClan()) {
            MessageUtil.devMessage(player, "You need to be in a clan!");
            return;
        }

        if (!isPlayerRankSatisfied()) {
            MessageUtil.devMessage(player, "Your rank need to be " + super.getRequiredRank() + " to promote somebody to owner!");
            return;
        }

        if (playerName.equals(targetName)) {
            MessageUtil.devMessage(player, "You promote yourself to owner.");
            return;
        }

        if (!isTargetInClan()) {
            MessageUtil.devMessage(player, "Target need to be in a clan!");
            return;
        }

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        IPlayerData targetData =  PluginDataManager.getPlayerDatabase(targetName);
        IClanData playerClanData = getPlayerClanData();
        IClanData targetClanData = getTargetClanData();

        if (!playerClanData.getName().equals(targetClanData.getName())) {
            MessageUtil.devMessage(player, "Target need to be in your clan!");
            return;
        }

        targetData.setRank(Rank.LEADER);
        playerData.setRank(Rank.MEMBER);
        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);
        PluginDataManager.savePlayerDatabaseToStorage(targetName, targetData);
        playerClanData.setOwner(targetName);
        PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);

        MessageUtil.devMessage(player, "Successfully promoted " + targetName + " to owner!");
        MessageUtil.devMessage(target, "You have been promoted to owner of clan " + targetData.getClan());
        ClanManager.alertClan(playerClanData.getName(), targetName + " is a new owner of this clan! Previous owner: " + playerName);
    }
}
