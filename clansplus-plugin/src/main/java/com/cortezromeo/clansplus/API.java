package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.TreeMap;

public class API implements com.cortezromeo.clansplus.api.ClanPlus {

    private final PluginDataManagerUtil getPluginDataManager = new PluginDataManagerUtil() {
        @Override
        public HashMap<String, IPlayerData> getPlayerDatabase() {
            return PluginDataManager.getPlayerDatabase();
        }

        @Override
        public TreeMap<String, IClanData> getClanDatabase() {
            return PluginDataManager.getClanDatabase();
        }

        @Override
        public IClanData getClanDatabase(String clanName) {
            return PluginDataManager.getClanDatabase(clanName);
        }

        @Override
        public IClanData getClanDatabaseByPlayerName(String playerName) {
            return PluginDataManager.getClanDatabaseByPlayerName(playerName);
        }

        @Override
        public IPlayerData getPlayerDatabase(String playerName) {
            return PluginDataManager.getPlayerDatabase(playerName);
        }

        @Override
        public void loadClanDatabase(String clanName) {
            PluginDataManager.loadClanDatabase(clanName);
        }

        @Override
        public void loadPlayerDatabase(String playerName) {
            PluginDataManager.loadPlayerDatabase(playerName);
        }

        @Override
        public void saveClanDatabaseToHashMap(String clanName, IClanData clanData) {
            PluginDataManager.saveClanDatabaseToHashMap(clanName, clanData);
        }

        @Override
        public void savePlayerDatabaseToHashMap(String playerName, IPlayerData playerData) {
            PluginDataManager.savePlayerDatabaseToHashMap(playerName, playerData);
        }

        @Override
        public void saveClanDatabaseToStorage(String clanName, IClanData clanData) {
            PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);
        }

        @Override
        public void saveClanDatabaseToStorage(String clanName) {
            PluginDataManager.saveClanDatabaseToStorage(clanName);
        }

        @Override
        public void savePlayerDatabaseToStorage(String playerName, IPlayerData playerData) {
            PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);
        }

        @Override
        public void savePlayerDatabaseToStorage(String playerName) {
            PluginDataManager.savePlayerDatabaseToStorage(playerName);
        }

        @Override
        public void clearPlayerDatabase(String playerName) {
            PluginDataManager.clearPlayerDatabase(playerName);
        }

        @Override
        public void transferDatabase(CommandSender commandSender, DatabaseType toDatabaseType) {
            PluginDataManager.transferDatabase(commandSender, toDatabaseType);
        }

        @Override
        public boolean deleteClanData(String clanName) {
            return PluginDataManager.deleteClanData(clanName);
        }

        @Override
        public void loadAllDatabase() {
            PluginDataManager.loadAllDatabase();
        }

        @Override
        public void saveAllDatabase() {
            PluginDataManager.saveAllDatabase();
        }
    };

    @Override
    public PluginDataManagerUtil getPluginDataManager() {
        return getPluginDataManager;
    }

    @Override
    public ClanManagerUtil getClanManager() {
        return null;
    }
}
