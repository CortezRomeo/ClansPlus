package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;
import com.cortezromeo.clansplus.support.CustomHeadSupport;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PluginDataManager {

    public static boolean fixClansOldData;
    public static boolean fixMembersOldData;
    public static HashMap<String, IPlayerData> playerDatabase = new HashMap<>();
    public static TreeMap<String, IClanData> clanDatabase = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static HashMap<CustomHeadCategory, List<CustomHeadData>> customHeadDatabase = new HashMap<>();

    public static HashMap<String, IPlayerData> getPlayerDatabase() {
        return playerDatabase;
    }

    public static TreeMap<String, IClanData> getClanDatabase() {
        return clanDatabase;
    }

    public static IClanData getClanDatabase(String clanName) {
        return clanDatabase.get(clanName);
    }

    public static IClanData getClanDatabaseByPlayerName(String playerName) {
        if (!getPlayerDatabase().containsKey(playerName))
            return null;

        String playerClanName = getPlayerDatabase(playerName).getClan();
        if (playerClanName == null || !getClanDatabase().containsKey(playerClanName))
            return null;

        return getClanDatabase(playerClanName);
    }

    public static IPlayerData getPlayerDatabase(String playerName) {
        return playerDatabase.get(playerName);
    }

    public static void loadClanDatabase(String clanName) {
        if (getClanDatabase().containsKey(clanName))
            return;
        getClanDatabase().put(clanName, PluginDataStorage.getClanData(clanName));
    }

    public static void loadPlayerDatabase(String playerName) {
        if (getPlayerDatabase().containsKey(playerName))
            return;
        getPlayerDatabase().put(playerName, PluginDataStorage.getPlayerData(playerName));
        if (ClanManager.managersFromOldData.containsKey(playerName)) {
            IPlayerData playerData = getPlayerDatabase(playerName);
            if (ClanManager.isPlayerInClan(playerName))
                if (ClanManager.managersFromOldData.get(playerName).equals(playerData.getClan()))
                    playerData.setRank(Rank.MANAGER);
            ClanManager.managersFromOldData.remove(playerName);
        }
    }

    public static void saveClanDatabaseToHashMap(String clanName, IClanData clanData) {
        getClanDatabase().put(clanName, clanData);
    }

    public static void savePlayerDatabaseToHashMap(String playerName, IPlayerData IPlayerData) {
        getPlayerDatabase().put(playerName, IPlayerData);
    }

    public static void saveClanDatabaseToStorage(String clanName, IClanData clanData) {
        saveClanDatabaseToHashMap(clanName, clanData);
        PluginDataStorage.saveClanData(clanName, clanData);
    }

    public static void saveClanDatabaseToStorage(String clanName) {
        if (getClanDatabase().containsKey(clanName))
            PluginDataStorage.saveClanData(clanName, getClanDatabase().get(clanName));
    }

    public static void savePlayerDatabaseToStorage(String playerName, IPlayerData playerData) {
        savePlayerDatabaseToHashMap(playerName, playerData);
        PluginDataStorage.savePlayerData(playerName, playerData);
    }

    public static void savePlayerDatabaseToStorage(String playerName) {
        if (getPlayerDatabase().containsKey(playerName))
            PluginDataStorage.savePlayerData(playerName, getPlayerDatabase().get(playerName));
    }

    public static HashMap<CustomHeadCategory, List<CustomHeadData>> getCustomHeadDatabase() {
        return customHeadDatabase;
    }

    public static List<CustomHeadData> getCustomHeadDatabase(CustomHeadCategory customHeadCategory) {
        return getCustomHeadDatabase().get(customHeadCategory);
    }

    public static void clearPlayerDatabase(String playerName) {
        if (!getPlayerDatabase().containsKey(playerName))
            return;

/*        String playerClanName = "";
        if (ClanManager.isPlayerInClan(playerName))
            playerClanName = getPlayerDatabase(playerName).getClan();*/

        getPlayerDatabase(playerName).setClan(null);
        getPlayerDatabase(playerName).setRank(null);
        getPlayerDatabase(playerName).setJoinDate(0);

/*        if (playerClanName != null && !playerClanName.equalsIgnoreCase("")) {
            getClanDatabase(playerClanName).getMembers().remove(playerName);
        }*/

        savePlayerDatabaseToStorage(playerName);
    }

    public static boolean deleteClanData(String clanName) {
        if (!getClanDatabase().containsKey(clanName))
           return false;

        IClanData clanData = getClanDatabase(clanName);

        if (!clanData.getMembers().isEmpty())
            for (String memberName : clanData.getMembers())
                clearPlayerDatabase(memberName);

        PluginDataManager.getClanDatabase().remove(clanName);
        return PluginDataStorage.deleteClanData(clanName);
    }

    public static void transferDatabase(CommandSender commandSender, DatabaseType toDatabaseType) {
        if (ClansPlus.databaseType != toDatabaseType) {
            DatabaseType oldDatabaseType = ClansPlus.databaseType;
            if (commandSender != null)
                commandSender.sendMessage("Đang chuyển đổi dữ liệu từ " + oldDatabaseType + " sang " + toDatabaseType + "...");
            Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
                PluginDataStorage.disableStorage();
                PluginDataStorage.init(toDatabaseType);
                if (!PluginDataManager.getClanDatabase().isEmpty()) {
                    for (String clanName : PluginDataManager.getClanDatabase().keySet()) {
                        PluginDataManager.saveClanDatabaseToStorage(clanName);
                    }
                }
                if (!PluginDataManager.getPlayerDatabase().isEmpty()) {
                    for (String playerName : PluginDataManager.getPlayerDatabase().keySet()) {
                        PluginDataManager.savePlayerDatabaseToStorage(playerName);
                    }
                }
                ClansPlus.databaseType = toDatabaseType;

                // config.yml
                ClansPlus.plugin.saveDefaultConfig();
                File configFile = new File(ClansPlus.plugin.getDataFolder(), "config.yml");
                ClansPlus.plugin.getConfig().set("database.type", toDatabaseType.toString().toUpperCase());
                try {
                    ClansPlus.plugin.getConfig().save(configFile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    ConfigUpdater.update(ClansPlus.plugin, "config.yml", configFile,  "clan-settings.creating-clan-settings.skill-level-default");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClansPlus.plugin.reloadConfig();

                if (commandSender != null)
                    commandSender.sendMessage("Đã chuyển đổi dữ liệu từ " + oldDatabaseType + " sang " + toDatabaseType + " thành công.");
            });
        }
    }

    public static void loadAllCustomHeadsFromJsonFiles() {
        File customHeadsFolder = new File(ClansPlus.plugin.getDataFolder() + "/customheads");
        if (!customHeadsFolder.exists())
            CustomHeadSupport.setupCustomHeadJsonFiles();

        for (CustomHeadCategory customHeadCategory : CustomHeadCategory.values()) {
            String customHeadCategoryString = customHeadCategory.toString().toLowerCase().replace("_", "-");
            List<CustomHeadData> customHeadDataList = new ArrayList<>();
            try {
                String jsonString = new String(Files.readAllBytes(Paths.get(ClansPlus.plugin.getDataFolder() + "/customheads/custom-head-" + customHeadCategoryString + ".json")));
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String value = jsonObject.getString("value");
                    customHeadDataList.add(new CustomHeadData(name, value));
                }
                getCustomHeadDatabase().put(customHeadCategory, customHeadDataList);
            } catch (Exception exception) {}
        }
    }

    public static void loadAllDatabase() {
        if (!PluginDataStorage.getAllClans().isEmpty())
            for (String clanName : PluginDataStorage.getAllClans()) {
                loadClanDatabase(clanName);
                if (Settings.DATABASE_SETTING_SMART_LOADING_ENABLED) {
                    IClanData clanData = getClanDatabase(clanName);
                    for (String playerName : clanData.getMembers()) {
                        loadPlayerDatabase(playerName);
                    }
                }
            }
        if (!Settings.DATABASE_SETTING_SMART_LOADING_ENABLED)
            if (!PluginDataStorage.getAllPlayers().isEmpty())
                for (String playerName : PluginDataStorage.getAllPlayers())
                    loadPlayerDatabase(playerName);
        if (Settings.DATABASAE_SETTING_FIX_BUG_DATABASE_ENABLED)
            fixIllegalDatabase();

        if (fixClansOldData || fixMembersOldData)
            saveAllDatabase();
    }

    public static void saveAllDatabase() {
        if (!getClanDatabase().isEmpty())
            for (String clanName : getClanDatabase().keySet())
                saveClanDatabaseToStorage(clanName);
        if (!getPlayerDatabase().isEmpty())
            for (String playerName : getPlayerDatabase().keySet())
                savePlayerDatabaseToStorage(playerName);
    }

    public static void fixIllegalDatabase() {
        HashMap<Integer, Integer> error = new HashMap<>();
        List<String> deletedClans = new ArrayList<>();
        if (!getClanDatabase().isEmpty()) {
            a: for (String clanName : getClanDatabase().keySet()) {
                IClanData clanData = getClanDatabase(clanName);

                if (getPlayerDatabase().containsKey(clanData.getOwner())) {
                    IPlayerData playerData = getPlayerDatabase(clanData.getOwner());
                    // id 1: leader is not in this clan
                    if (playerData.getClan() == null || !playerData.getClan().equals(clanName)) {
                        boolean isCase2Fixed = false;
                        if (clanData.getMembers().size() <= 1) {
                            MessageUtil.debug("FIXING ILLEGAL DATABASE [ID 1 CASE 1: Owner is not in this clan]", "Delete clan " + clanName + " (clan owner: " + clanData.getOwner() + ")");
                            error.put(1, error.getOrDefault(1, 0) + 1);
                            deletedClans.add(clanName);
                            continue a;
                            // case 2: check whether clan's member is in this clan to determine to delete the clan or transfer clan's leader to member
                        } else if (clanData.getMembers().size() > 1) {
                            b: for (String memberName : clanData.getMembers()) {
                                if (memberName.equals(clanData.getOwner()))
                                    continue b;
                                if (!getPlayerDatabase().containsKey(memberName))
                                    loadPlayerDatabase(memberName);

                                IPlayerData memberData = getPlayerDatabase(memberName);

                                if (memberData.getClan().equals(clanName)) {
                                    MessageUtil.debug("FIXING ILLEGAL DATABASE [ID 1 CASE 2: Owner is not in this clan, but clan's member is]", "Transfer clan's owner to " + memberName + " (previous owner: " + clanData.getOwner() + ")");
                                    error.put(1, error.getOrDefault(1, 0) + 1);
                                    memberData.setRank(Rank.LEADER);
                                    clanData.getMembers().remove(clanData.getOwner());
                                    clanData.setOwner(memberName);
                                    savePlayerDatabaseToStorage(memberName, memberData);
                                    isCase2Fixed = true;
                                    break b;
                                }
                            }
                        }
                        if (!isCase2Fixed) {
                            MessageUtil.debug("FIXING ILLEGAL DATABASE [ID 1 CASE 3: Clan's owner and member are not in this clan]", "Delete clan " + clanName + " (clan owner: " + clanData.getOwner() + ")");
                            error.put(1, error.getOrDefault(1, 0) + 1);
                            deletedClans.add(clanName);
                        }
                    }
                }
                if (clanData.getMembers().isEmpty() || clanData.getMembers() == null) {
                    MessageUtil.debug("FIXING ILLEGAL DATABASE [ID 2 CASE 1: There is no members in this clan]", "Delete clan " + clanName + " (clan owner: " + clanData.getOwner() + ")");
                    error.put(2, error.getOrDefault(2, 0) + 1);
                    deletedClans.add(clanName);
                } else {
                    for (String memberName : clanData.getMembers()) {
                        if (getPlayerDatabase(memberName).getClan() == null)
                            clanData.getMembers().remove(memberName);
                        else
                            if (!getPlayerDatabase(memberName).getClan().equalsIgnoreCase(clanName))
                                clanData.getMembers().remove(memberName);
                    }
                }
                saveClanDatabaseToStorage(clanName, clanData);
            }
        }
        if (!getPlayerDatabase().isEmpty()) {
            for (String playerName : getPlayerDatabase().keySet()) {
                IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
                if (playerData.getClan() != null) {
                    if (!PluginDataManager.getClanDatabase().containsKey(playerData.getClan())) {
                        MessageUtil.debug("FIXING ILLEGAL DATABASE [ID 3 CASE 1: Player's clan does not exist]", "Clear player database " + playerName);
                        clearPlayerDatabase(playerName);
                        error.put(3, error.getOrDefault(3, 0) + 1);
                    }
                }
            }
        }
        if (!deletedClans.isEmpty())
            for (String clanName : deletedClans)
                try {
                    clanDatabase.remove(clanName);
                    PluginDataStorage.deleteClanData(clanName);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
        if (!error.isEmpty())
            for (int errorID : error.keySet())
                MessageUtil.debug("FIXING ILLEGAL DATABASE [ID " + errorID + "]", "Total: " + error.get(errorID));
    }

    public static void backupAll(String backupFileName) {
        String backupPath = ClansPlus.plugin.getDataFolder() + "\\backup\\";
        DatabaseType databaseType = ClansPlus.databaseType;
        MessageUtil.debug("BACKUP (DATABASE: " + databaseType + ")", "Bắt đầu back up dữ liệu...");

        // Save database before backing up
        saveAllDatabase();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Settings.DATABASE_BACK_UP_FILE_NAME_FORMAT);
        Date date = new Date();
        if (backupFileName == null)
            backupFileName = simpleDateFormat.format(date) + " (" + databaseType.toString().toLowerCase() + ")";
        String zipFileName = backupPath + backupFileName + ".zip";

        if (ClansPlus.databaseType == DatabaseType.YAML) {
            Path clanDatabaseFolder = Paths.get(ClansPlus.plugin.getDataFolder() + "\\banghoiData");
            Path playerDatabaseFolder = Paths.get(ClansPlus.plugin.getDataFolder() + "\\playerData");
            Path zipPath = Paths.get(zipFileName);
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
                Files.walkFileTree(clanDatabaseFolder, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String entryName = "banghoiData/" + clanDatabaseFolder.relativize(file).toString();
                        zos.putNextEntry(new ZipEntry(entryName.replace("\\", "/")));
                        Files.copy(file, zos);
                        zos.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
                Files.walkFileTree(playerDatabaseFolder, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String entryName = "playerData/" + playerDatabaseFolder.relativize(file).toString();
                        zos.putNextEntry(new ZipEntry(entryName.replace("\\", "/")));
                        Files.copy(file, zos);
                        zos.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
                zos.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (ClansPlus.databaseType == DatabaseType.H2) {
            if (PluginDataH2Storage.getConnection() ==  null) {
                MessageUtil.debug("BACKUP (DATABASE: " + databaseType + ")", "Không thể backup vì H2 chưa được kết nối!");
                return;
            }
            try {
                Statement statement = PluginDataH2Storage.getConnection().createStatement();
                String sql = "BACKUP TO '" + zipFileName + "'";
                statement.executeUpdate(sql);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        MessageUtil.debug("BACKUP (DATABASE: " + databaseType + ")", "Backup thành công.");
    }
}
