package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.entity.Player;

public abstract class SubjectManager {

    protected Rank requiredRank;
    protected Player player;
    protected String playerName;
    protected Player target;
    protected String targetName;

    public SubjectManager(Rank rank, Player player, String playerName, Player target, String targetName) {
        this.requiredRank = rank;
        this.player = player;
        this.playerName = playerName;
        this.target = target;
        this.targetName = targetName;
    }

    public abstract void execute();

    public boolean isPlayerInClan() {
        if (!PluginDataManager.getPlayerDatabase().containsKey(playerName))
            return false;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        return playerData.getClan() != null;
    }

    public boolean isTargetInClan() {
        if (!PluginDataManager.getPlayerDatabase().containsKey(targetName))
            return false;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(targetName);
        return playerData.getClan() != null;
    }

    public boolean isTargetRankSatisfied() {
        if (!isTargetInClan())
            return false;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(targetName);

        if (playerData.getRank() == null)
            return false;

        if (playerData.getRank().equals(Rank.LEADER) && getRequiredRank() == Rank.MANAGER)
            return true;
        else
            return playerData.getRank() == requiredRank;
    }

    public boolean isPlayerRankSatisfied() {
        if (!isPlayerInClan())
            return false;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);

        if (playerData.getRank() == null)
            return false;

        if (playerData.getRank().equals(Rank.LEADER) && getRequiredRank() == Rank.MANAGER)
            return true;
        else
            return playerData.getRank() == requiredRank;
    }

    public Rank getRequiredRank() {
        return requiredRank;
    }

    public void setRequiredRank(Rank requiredRank) {
        this.requiredRank = requiredRank;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public IClanData getPlayerClanData() {
        if (isPlayerInClan())
            return PluginDataManager.getClanDatabase(PluginDataManager.getPlayerDatabase(playerName).getClan());
        return null;
    }

    public Player getTarget() {
        return target;
    }

    public String getTargetName() {
        return targetName;
    }

    public IClanData getTargetClanData() {
        if (isTargetInClan())
            return PluginDataManager.getClanDatabase(PluginDataManager.getPlayerDatabase(targetName).getClan());
        return null;
    }
}
