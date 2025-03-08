package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
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

    private final ClanManagerUtil getClanManager = new ClanManagerUtil() {
        @Override
        public boolean isClanExisted(String clanName) {
            return ClanManager.isClanExisted(clanName);
        }

        @Override
        public boolean isPlayerInClan(String playerName) {
            return ClanManager.isPlayerInClan(playerName);
        }

        @Override
        public boolean isPlayerInClan(Player player) {
            return ClanManager.isPlayerInClan(player);
        }

        @Override
        public void alertClan(String clanName, String message) {
            ClanManager.alertClan(clanName, message);
        }

        @Override
        public void addPlayerToAClan(String playerName, String clanName, boolean forceToLeaveOldClan) {
            ClanManager.addPlayerToAClan(playerName, clanName, forceToLeaveOldClan);
        }

        @Override
        public HashMap<String, Integer> getClansScoreHashMap() {
            return ClanManager.getClansScoreHashMap();
        }

        @Override
        public HashMap<String, Integer> getClansPlayerSize() {
            return ClanManager.getClansPlayerSize();
        }

        @Override
        public HashMap<String, Long> getClansWarpointHashMap() {
            return ClanManager.getClansWarpointHashMap();
        }

        @Override
        public HashMap<String, Long> getClansCreatedDate() {
            return ClanManager.getClansCreatedDate();
        }

        @Override
        public List<String> getClansCustomName() {
            return ClanManager.getClansCustomName();
        }

        @Override
        public boolean isPlayerRankSatisfied(String playerName, Rank requiredRank) {
            return ClanManager.isPlayerRankSatisfied(playerName, requiredRank);
        }

        @Override
        public String getFormatClanName(IClanData clanData) {
            return ClanManager.getFormatClanName(clanData);
        }

        @Override
        public void sendClanBroadCast(Player player) {
            ClanManager.sendClanBroadCast(player);
        }

        @Override
        public String getFormatClanMessage(IClanData clanData) {
            return ClanManager.getFormatClanMessage(clanData);
        }

        @Override
        public String getFormatClanCustomName(IClanData clanData) {
            return ClanManager.getFormatClanCustomName(clanData);
        }

        @Override
        public String getFormatRank(Rank rank) {
            return ClanManager.getFormatRank(rank);
        }

        @Override
        public List<Player> getPlayerUsingClanChat() {
            return ClanManager.getPlayerUsingClanChat();
        }

        @Override
        public List<Player> getPlayerTogglingPvP() {
            return ClanManager.getPlayerTogglingPvP();
        }

        @Override
        public List<Player> getPlayerUsingChatSpy() {
            return ClanManager.getPlayerUsingChatSpy();
        }

        @Override
        public boolean isConsoleUsingChatSpy() {
            return ClanManager.isConsoleUsingChatSpy();
        }
    };

    @Override
    public ClanManagerUtil getClanManager() {
        return getClanManager;
    }
}
