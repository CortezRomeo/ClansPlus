package com.cortezromeo.clansplus.api;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public interface ClanPlus {

    PluginDataManagerUtil getPluginDataManager();

    ClanManagerUtil getClanManager();

    interface PluginDataManagerUtil {
        HashMap<String, IPlayerData> getPlayerDatabase();

        TreeMap<String, IClanData> getClanDatabase();

        IClanData getClanDatabase(String clanName);

        IClanData getClanDatabaseByPlayerName(String playerName);

        IPlayerData getPlayerDatabase(String playerName);

        void loadClanDatabase(String clanName);

        void loadPlayerDatabase(String playerName);

        void saveClanDatabaseToHashMap(String clanName, IClanData clanData);

        void savePlayerDatabaseToHashMap(String playerName, IPlayerData playerData);

        void saveClanDatabaseToStorage(String clanName, IClanData clanData);

        void saveClanDatabaseToStorage(String clanName);

        void savePlayerDatabaseToStorage(String playerName, IPlayerData playerData);

        void savePlayerDatabaseToStorage(String playerName);

        void clearPlayerDatabase(String playerName);

        void transferDatabase(CommandSender commandSender, DatabaseType toDatabaseType);

        boolean deleteClanData(String clanName);

        void loadAllDatabase();

        void saveAllDatabase();
    }

    interface ClanManagerUtil {
        boolean isClanExisted(String clanName);

        boolean isPlayerInClan(String playerName);

        boolean isPlayerInClan(Player player);

        void alertClan(String clanName, String message);

        void addPlayerToAClan(String playerName, String clanName, boolean forceToLeaveOldClan);

        HashMap<String, Integer> getClansScoreHashMap();

        HashMap<String, Integer> getClansPlayerSize();

        HashMap<String, Long> getClansWarpointHashMap();

        HashMap<String, Long> getClansCreatedDate();

        List<String> getClansCustomName();

        boolean isPlayerRankSatisfied(String playerName, Rank requiredRank);

        String getFormatClanName(IClanData clanData);

        void sendClanBroadCast(Player player);

        String getFormatClanMessage(IClanData clanData);

        String getFormatClanCustomName(IClanData clanData);

        String getFormatRank(Rank rank);

        List<Player> getPlayerUsingClanChat();

        List<Player> getPlayerTogglingPvP();

        List<Player> getPlayerUsingChatSpy();

        void openClanStorage(Player player, String clanName, int storageNumber);

        boolean isConsoleUsingChatSpy();
    }

}
