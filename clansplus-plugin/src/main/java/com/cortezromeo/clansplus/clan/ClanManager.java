package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ClanManager {

    // playerName, clanName
    public static HashMap<String, String> beingInvitedPlayers = new HashMap<>();
    // playerName, clanName
    public static HashMap<String, String> managersFromOldData = new HashMap<>();

    public static boolean isClanExisted(String clanName) {
        return PluginDataManager.getClanDatabase().containsKey(clanName);
    }

    public static void alertClan(String clanName, String message) {
        if (!isClanExisted(clanName) || message == null)
            return;

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        for (String playerInClan : clanData.getMembers()) {
            Player player = Bukkit.getPlayer(playerInClan);
            MessageUtil.devMessage(player, clanName + " [ALERT]: " + message);
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
}
