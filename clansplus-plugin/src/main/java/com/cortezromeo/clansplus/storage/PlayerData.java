package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IPlayerData;

public class PlayerData implements IPlayerData {

    String playerName;
    String UUID;
    String clan;
    Rank rank;
    long joinDate;
    long scoreCollected;
    long lastActivated;

    public PlayerData(String playerName, String UUID, String clan, Rank rank, long joinDate, long scoreCollected, long lastActivated) {
        this.playerName = playerName;
        this.UUID = UUID;
        this.clan = clan;
        this.rank = rank;
        this.joinDate = joinDate;
        this.scoreCollected = scoreCollected;
        this.lastActivated = lastActivated;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    @Override
    public String getClan() {
        return clan;
    }

    @Override
    public void setClan(String clan) {
        this.clan = clan;
    }

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public long getJoinDate() {
        return joinDate;
    }

    @Override
    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public long getScoreCollected() {
        return scoreCollected;
    }

    @Override
    public void setScoreCollected(long scoreCollected) {
        this.scoreCollected = scoreCollected;
    }

    @Override
    public long getLastActivated() {
        return lastActivated;
    }

    @Override
    public void setLastActivated(long lastActivated) {
        this.lastActivated = lastActivated;
    }
}
