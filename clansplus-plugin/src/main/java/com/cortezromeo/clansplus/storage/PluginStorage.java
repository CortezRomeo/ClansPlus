package com.cortezromeo.clansplus.storage;

public interface PluginStorage {
    BangHoiData getBangHoiData(String bangHoiName);
    void saveBangHoiData(String bangHoiName, BangHoiData bangHoiData);
    PlayerData getPlayerData(String playerName);
    void savePlayerData(String playerName, PlayerData playerData);
}
