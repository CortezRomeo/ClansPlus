package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.enums.Rank;
import com.cortezromeo.clansplus.storage.PlayerData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.entity.Player;

public abstract class SubjectManager {

    protected Rank rank;
    protected Player player;
    protected String playerName;
    protected Player target;
    protected String targetName;

    public SubjectManager(Rank rank, Player player, String playerName, Player target, String targetName) {
        this.rank = rank;
        this.player = player;
        this.playerName = playerName;
        this.target = target;
        this.targetName = targetName;
    }

    public abstract void execute();

    public boolean isPlayerInClan() {
        if (!PluginDataManager.getPlayerDatabase().containsKey(playerName))
            return false;

        PlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        return playerData.getClan() != null;
    }

    public boolean isRankSatisfied() {
        if (!isPlayerInClan())
            return false;

        PlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        return playerData.getRank() == rank;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getTarget() {
        return target;
    }

    public String getTargetName() {
        return targetName;
    }
}
