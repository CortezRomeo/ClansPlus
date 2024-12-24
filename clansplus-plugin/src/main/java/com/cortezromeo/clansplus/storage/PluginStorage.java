package com.cortezromeo.clansplus.storage;

public interface PluginStorage {
    ClanData getClanData(String clanName);
    void saveClanData(String clanName, ClanData clanData);
    PlayerData getPlayerData(String playerName);
    void savePlayerData(String playerName, PlayerData playerData);
}
