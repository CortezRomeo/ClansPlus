package com.cortezromeo.clansplus.storage;

import java.util.List;

public interface PluginStorage {
    ClanData getClanData(String clanName);
    void saveClanData(String clanName, ClanData clanData);
    PlayerData getPlayerData(String playerName);
    void savePlayerData(String playerName, PlayerData playerData);
    List<String> getAllClans();
    List<String> getAllPlayers();
    void disableStorage();
}
