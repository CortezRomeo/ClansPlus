package com.cortezromeo.clansplus.api;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.TreeMap;

public interface ClanPlus {

    PluginDataManagerUtil getPluginDataManager();

    interface PluginDataManagerUtil {
        HashMap<String, IPlayerData> getPlayerDatabase();

        TreeMap<String, IClanData> getClanDatabase();

        IClanData getClanDatabase(String clanName);

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

    ClanManagerUtil getClanManager();

    interface ClanManagerUtil {

    }

}
