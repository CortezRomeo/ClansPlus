package com.cortezromeo.clansplus.api.storage;

import com.cortezromeo.clansplus.api.enums.Rank;

public interface IPlayerData {

    String getPlayerName();

    void setPlayerName(String playerName);

    String getUUID();

    void setUUID(String UUID);

    String getClan();

    void setClan(String clan);

    Rank getRank();

    void setRank(Rank rank);

    long getJoinDate();

    void setJoinDate(long joinDate);

    long getScoreCollected();

    void setScoreCollected(long scoreCollected);

    long getLastActivated();

    void setLastActivated(long lastActivated);

}
