package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;

import java.util.List;

public interface PluginStorage {
    ClanData getClanData(String clanName);
    void saveClanData(String clanName, IClanData clanData);
    PlayerData getPlayerData(String playerName);
    void savePlayerData(String playerName, IPlayerData playerData);
    boolean deleteClanData(String clanName);
    List<String> getAllClans();
    List<String> getAllPlayers();
    void disableStorage();
}
