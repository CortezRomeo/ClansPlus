package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.inventory.ClanPlusStorageInventoryBase;
import com.cortezromeo.clansplus.inventory.ClanStorageInventory;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ClanManager {

    // playerName, clanName
    public static HashMap<String, String> beingInvitedPlayers = new HashMap<>();
    // playerName, clanName
    public static HashMap<String, String> managersFromOldData = new HashMap<>();
    public static List<Player> playerUsingClanChat = new ArrayList<>();
    public static List<Player> playerTogglingPvP = new ArrayList<>();
    public static List<Player> playerUsingChatSpy = new ArrayList<>();
    public static boolean consoleUsingChatSpy = true;

    public static boolean isClanExisted(String clanName) {
        return PluginDataManager.getClanDatabase().containsKey(clanName);
    }

    public static boolean isPlayerInClan(String playerName) {
        if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) return false;
        return PluginDataManager.getPlayerDatabase(playerName).getClan() != null;
    }

    public static boolean isPlayerInClan(Player player) {
        if (player == null) return false;
        if (!PluginDataManager.getPlayerDatabase().containsKey(player.getName())) return false;
        if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() == null) return false;
        return PluginDataManager.getClanDatabaseByPlayerName(player.getName()) != null;
    }

    public static void alertClan(String clanName, String message) {
        if (!isClanExisted(clanName) || message == null) return;

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        for (String playerInClan : clanData.getMembers()) {
            Player player = Bukkit.getPlayer(playerInClan);
            MessageUtil.sendMessage(player, StringUtil.setClanNamePlaceholder(message.replace("%prefix%", Messages.CLAN_BROADCAST_PREFIX), clanName));
        }
    }

    public static void addPlayerToAClan(String playerName, String clanName, boolean forceToLeaveOldClan) {
        if (!PluginDataManager.getClanDatabase().containsKey(clanName) || !PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
            return;
        }

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);

        if (playerData.getClan() != null && forceToLeaveOldClan) {
            PluginDataManager.getClanDatabase(playerData.getClan()).getMembers().remove(playerName);
            PluginDataManager.clearPlayerDatabase(playerName);
        }

        PluginDataManager.getClanDatabase(clanName).getMembers().add(playerName);
        playerData.setClan(clanName);
        playerData.setRank(Rank.MEMBER);
        playerData.setJoinDate(new Date().getTime());
        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);
        PluginDataManager.saveClanDatabaseToStorage(clanName);
    }

    public static HashMap<String, Integer> getClansScoreHashMap() {
        if (PluginDataManager.getClanDatabase().isEmpty()) return null;

        HashMap<String, Integer> clansScore = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansScore.put(clanName, PluginDataManager.getClanDatabase(clanName).getScore());
        return clansScore;
    }

    public static HashMap<String, Integer> getClansPlayerSize() {
        if (PluginDataManager.getClanDatabase().isEmpty()) return null;

        HashMap<String, Integer> clansPlayerSize = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansPlayerSize.put(clanName, PluginDataManager.getClanDatabase(clanName).getMembers().size());
        return clansPlayerSize;
    }

    public static HashMap<String, Long> getClansWarpointHashMap() {
        if (PluginDataManager.getClanDatabase().isEmpty()) return null;

        HashMap<String, Long> clansWarPoint = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansWarPoint.put(clanName, PluginDataManager.getClanDatabase(clanName).getWarPoint());
        return clansWarPoint;
    }

    public static HashMap<String, Long> getClansCreatedDate() {
        if (PluginDataManager.getClanDatabase().isEmpty()) return null;

        HashMap<String, Long> clansCreatedDate = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansCreatedDate.put(clanName, PluginDataManager.getClanDatabase(clanName).getCreatedDate());
        return clansCreatedDate;
    }

    public static List<String> getClansCustomName() {
        if (PluginDataManager.getClanDatabase().isEmpty()) return null;

        List<String> clansCustomName = new ArrayList<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet()) {
            String clanCustomName = PluginDataManager.getClanDatabase(clanName).getCustomName();
            if (clanCustomName != null) clansCustomName.add(clanCustomName);
        }
        return clansCustomName;
    }

    public static boolean isPlayerRankSatisfied(String playerName, Rank requiredRank) {
        if (!isPlayerInClan(playerName)) return false;

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);

        if (playerData.getRank() == null) return false;

        if (playerData.getRank() == Rank.LEADER) return true;

        if (playerData.getRank().equals(Rank.MANAGER) && requiredRank == Rank.MEMBER) return true;
        else return playerData.getRank() == requiredRank;
    }

    public static String getFormatClanName(IClanData clanData) {
        return clanData.getCustomName() != null ? ClansPlus.nms.addColor(clanData.getCustomName()) : clanData.getName();
    }

    public static void sendClanBroadCast(Player player) {
        if (PluginDataManager.getClanDatabaseByPlayerName(player.getName()) == null) return;

        IClanData clanData = PluginDataManager.getClanDatabaseByPlayerName(player.getName());

        if (clanData.getMessage() == null) {
            return;
        }

        for (String clanMessageFormat : Messages.CLAN_MESSAGE) {
            clanMessageFormat = StringUtil.setClanNamePlaceholder(clanMessageFormat, clanData.getName());
            clanMessageFormat = clanMessageFormat.replace("%message%", clanData.getMessage());
            MessageUtil.sendMessage(player, clanMessageFormat);
        }
    }

    public static String getFormatClanMessage(IClanData clanData) {
        if (clanData.getMessage() == null) return ClansPlus.nms.addColor(Messages.NO_MESSAGES);
        return ClansPlus.nms.addColor(clanData.getMessage());
    }

    public static String getFormatClanCustomName(IClanData clanData) {
        if (clanData.getCustomName() == null) return ClansPlus.nms.addColor(Messages.NO_CUSTOMNAME);
        return ClansPlus.nms.addColor(clanData.getCustomName());
    }

    public static String getFormatRank(Rank rank) {
        if (rank == Rank.MANAGER) return Messages.RANK_DISPLAY_MANAGER;
        if (rank == Rank.LEADER) return Messages.RANK_DISPLAY_LEADER;
        return Messages.RANK_DISPLAY_MEMBER;
    }

    public static void openInventory(Player player, String clanName, int inventoryNumber) {
        if (!isClanExisted(clanName)) return;
        IClanData clanData = PluginDataManager.getClanDatabase(clanName);

        if (inventoryNumber > clanData.getMaxInventory()) {
            MessageUtil.sendMessage(player, Messages.INVENTORY_LOCKED.replace("%storageNumber%", String.valueOf(inventoryNumber)));
            return;
        }

        if (inventoryNumber > Settings.INVENTORY_SETTINGS_MAX_INVENTORY) {
            MessageUtil.sendMessage(player, Messages.INVENTORY_NUMBER_EXCEED_LIMIT.replace("%maxInventoryNumber%", String.valueOf(Settings.INVENTORY_SETTINGS_MAX_INVENTORY)));
            return;
        }

        // to prevent open an invalid inventory number lower than 1
        if (inventoryNumber < 1) inventoryNumber = 1;

        // create a new inventory if clan did not have this inventory beforfe
        if (clanData.getInventoryHashMap().get(inventoryNumber) == null) {
            HashMap<Integer, Inventory> newInventoryHashMap = clanData.getInventoryHashMap();
            ClanStorageInventory clanStorageInventory = new ClanStorageInventory(inventoryNumber);
            clanStorageInventory.setClanName(clanName);
            newInventoryHashMap.put(inventoryNumber, clanStorageInventory.getInventory());
            clanData.setInventoryHashMap(newInventoryHashMap);
        }
        PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);

        Inventory inventory = clanData.getInventoryHashMap().get(inventoryNumber);
        ClanPlusStorageInventoryBase inventoryHolder = (ClanPlusStorageInventoryBase) inventory.getHolder();
        inventoryHolder.setMenuItems();
        player.openInventory(inventoryHolder.getInventory());

    }

    public static List<Player> getPlayerUsingClanChat() {
        return playerUsingClanChat;
    }

    public static List<Player> getPlayerTogglingPvP() {
        return playerTogglingPvP;
    }

    public static List<Player> getPlayerUsingChatSpy() {
        return playerUsingChatSpy;
    }

    public static boolean isConsoleUsingChatSpy() {
        return consoleUsingChatSpy;
    }
}
