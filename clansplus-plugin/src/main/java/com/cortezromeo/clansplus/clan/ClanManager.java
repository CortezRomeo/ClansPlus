package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClanManager {

    // playerName, clanName
    public static HashMap<String, String> beingInvitedPlayers = new HashMap<>();
    // playerName, clanName
    public static HashMap<String, String> managersFromOldData = new HashMap<>();

    public static boolean isClanExisted(String clanName) {
        return PluginDataManager.getClanDatabase().containsKey(clanName);
    }

    public static boolean isPlayerInClan(String playerName) {
        if (!PluginDataManager.getPlayerDatabase().containsKey(playerName))
            return false;
        return PluginDataManager.getPlayerDatabase(playerName).getClan() != null;
    }

    public static boolean isPlayerInClan(Player player) {
        if (player == null)
            return false;
        if (!PluginDataManager.getPlayerDatabase().containsKey(player.getName()))
            return false;
        return PluginDataManager.getPlayerDatabase(player.getName()).getClan() != null;
    }

    public static void alertClan(String clanName, String message) {
        if (!isClanExisted(clanName) || message == null)
            return;

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        for (String playerInClan : clanData.getMembers()) {
            Player player = Bukkit.getPlayer(playerInClan);
            MessageUtil.sendMessage(player, message.replace("%prefix%", Messages.CLAN_BROADCAST_PREFIX.replace("%formatClanName%", getFormatClanName(clanData))));
        }
    }

    public static HashMap<String, Integer> getClansScoreHashMap() {
        if (PluginDataManager.getClanDatabase().isEmpty())
            return null;

        HashMap<String, Integer> clansScore = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansScore.put(clanName, PluginDataManager.getClanDatabase(clanName).getScore());
        return clansScore;
    }

    public static HashMap<String, Integer> getClansPlayerSize() {
        if (PluginDataManager.getClanDatabase().isEmpty())
            return null;

        HashMap<String, Integer> clansScore = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansScore.put(clanName, PluginDataManager.getClanDatabase(clanName).getMembers().size());
        return clansScore;
    }

    public static HashMap<String, Integer> getClansWarpointHashMap() {
        if (PluginDataManager.getClanDatabase().isEmpty())
            return null;

        HashMap<String, Integer> clansScore = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansScore.put(clanName, PluginDataManager.getClanDatabase(clanName).getWarPoint());
        return clansScore;
    }

    public static HashMap<String, Long> getClansCreatedDate() {
        if (PluginDataManager.getClanDatabase().isEmpty())
            return null;

        HashMap<String, Long> clansScore = new HashMap<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet())
            clansScore.put(clanName, PluginDataManager.getClanDatabase(clanName).getCreatedDate());
        return clansScore;
    }

    public static List<String> getClansCustomName() {
        if (PluginDataManager.getClanDatabase().isEmpty())
            return null;

        List<String> clansCustomName = new ArrayList<>();
        for (String clanName : PluginDataManager.getClanDatabase().keySet()) {
            String clanCustomName = PluginDataManager.getClanDatabase(clanName).getCustomName();
            if (clanCustomName != null)
                clansCustomName.add(clanCustomName);
        }
        return clansCustomName;
    }

    public static String getFormatClanName(IClanData clanData) {
        return clanData.getCustomName() != null ? ClansPlus.nms.addColor(clanData.getCustomName()) : clanData.getName();
    }

    public static String getFormatMessage(IClanData clanData) {
        if (clanData.getMessage() == null)
            return ClansPlus.nms.addColor( "<Không có thông báo>");
        return ClansPlus.nms.addColor(clanData.getMessage());
    }

    public static String getFormatCustomName(IClanData clanData) {
        if (clanData.getCustomName() == null)
            return ClansPlus.nms.addColor("<Không có tên custom>");
        return ClansPlus.nms.addColor(clanData.getCustomName());
    }

    public static String getFormatRank(Rank rank) {
        if (rank == Rank.MANAGER)
            return Messages.RANK_DISPLAY_MANAGER;
        if (rank == Rank.LEADER)
            return Messages.RANK_DISPLAY_LEADER;
        return Messages.RANK_DISPLAY_MEMBER;
    }
}
