package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.enums.Rank;

public class PlayerData {

    String clan;
    Rank rank;
    long joinDate;
    long scoreCollected;

    public PlayerData(String clan, Rank rank, long joinDate, long scoreCollected) {
        this.clan = clan;
        this.rank = rank;
        this.joinDate = joinDate;
        this.scoreCollected = scoreCollected;
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
