package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.StringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "Cortez_Romeo";
    }

    @Override
    public String getIdentifier() {
        return "clanplus";
    }

    @Override
    public String getVersion() {
        return ClansPlus.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s == null)
            return null;

        if (!ClanManager.isPlayerInClan(player))
            return "";

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(player.getName());
        IClanData clanData = PluginDataManager.getClanDatabaseByPlayerName(player.getName());

        if (s.equalsIgnoreCase("clan_name"))
            return clanData.getName();
        if (s.equalsIgnoreCase("clan_customname"))
            return clanData.getCustomName() != null ? ClansPlus.nms.addColor(clanData.getCustomName()) : "";
        if (s.equalsIgnoreCase("clan_formatname"))
            return clanData.getCustomName() != null ? ClansPlus.nms.addColor(clanData.getCustomName()) : clanData.getName();
        if (s.equalsIgnoreCase("clan_owner"))
            return clanData.getOwner();
        if (s.equalsIgnoreCase("clan_message"))
            return clanData.getMessage() != null ? ClansPlus.nms.addColor(clanData.getMessage()) : "";
        if (s.equalsIgnoreCase("clan_score"))
            return String.valueOf(clanData.getScore());
        if (s.equalsIgnoreCase("clan_warpoint"))
            return String.valueOf(clanData.getWarPoint());
        if (s.equalsIgnoreCase("clan_warning"))
            return String.valueOf(clanData.getWarning());
        if (s.equalsIgnoreCase("clan_maxmembers"))
            return String.valueOf(clanData.getMaxMembers());
        if (s.equalsIgnoreCase("clan_createddate"))
            return String.valueOf(clanData.getCreatedDate());
        if (s.equalsIgnoreCase("clan_format_createddate"))
            return StringUtil.dateTimeToDateFormat(clanData.getCreatedDate());
        if (s.equalsIgnoreCase("clan_members"))
            return String.valueOf(clanData.getMembers());
        if (s.equalsIgnoreCase("clan_allies"))
            return !clanData.getAllies().isEmpty() ? String.valueOf(clanData.getAllies()) : "";
        if (s.startsWith("clan_skilllevel_")) {
            String skillID = s.replace("clan_skilllevel_", "");
            try {
                return String.valueOf(clanData.getSkillLevel().getOrDefault(Integer.parseInt(skillID), 0));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (s.startsWith("clan_subjectpermission_")) {
            String subject = s.replace("clan_subjectpermission_", "");
            try {
                return String.valueOf(clanData.getSubjectPermission().get(Subject.valueOf(subject.toUpperCase())));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (s.startsWith("clan_format_subjectpermission_")) {
            String subject = s.replace("clan_format_subjectpermission_", "");
            try {
                return ClansPlus.nms.addColor(ClanManager.getFormatRank(clanData.getSubjectPermission().get(Subject.valueOf(subject.toUpperCase()))));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (s.equalsIgnoreCase("clan_discordchannelid"))
            return String.valueOf(clanData.getDiscordChannelID());
        if (s.equalsIgnoreCase("clan_discordjoinlink"))
            return clanData.getDiscordJoinLink() != null ? clanData.getDiscordJoinLink() : "";

        // player placeholders
        if (s.equalsIgnoreCase("player_rank"))
            return String.valueOf(playerData.getRank());
        if (s.equalsIgnoreCase("player_format_rank"))
            return ClansPlus.nms.addColor(ClanManager.getFormatRank(playerData.getRank()));
        if (s.equalsIgnoreCase("player_joindate"))
            return String.valueOf(playerData.getJoinDate());
        if (s.equalsIgnoreCase("player_format_joindate"))
            return StringUtil.dateTimeToDateFormat(playerData.getJoinDate());
        if (s.equalsIgnoreCase("player_scorecollected"))
            return String.valueOf(playerData.getScoreCollected());
        if (s.equalsIgnoreCase("player_lastactivated"))
            return String.valueOf(playerData.getLastActivated());
        if (s.equalsIgnoreCase("player_format_lastactivated"))
            return StringUtil.dateTimeToDateFormat(playerData.getLastActivated());

        return null;
    }
}
