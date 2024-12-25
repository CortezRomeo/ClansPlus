package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.enums.Rank;

public class PlayerData {

    String playerName;
    String UUID;
    String clan;
    Rank rank;
    long joinDate;
    long scoreCollected;

    public PlayerData(String playerName, String UUID, String clan, Rank rank, long joinDate, long scoreCollected) {
        this.playerName = playerName;
        this.UUID = UUID;
        this.clan = clan;
        this.rank = rank;
        this.joinDate = joinDate;
        this.scoreCollected = scoreCollected;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public long getScoreCollected() {
        return scoreCollected;
    }

    public void setScoreCollected(long scoreCollected) {
        this.scoreCollected = scoreCollected;
    }
}
