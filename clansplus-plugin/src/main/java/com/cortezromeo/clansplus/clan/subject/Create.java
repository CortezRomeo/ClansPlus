package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.storage.ClanData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Create extends SubjectManager {

    private final String clanName;

    public Create(Player player, String playerName, String clanName) {
        super(null, player, playerName, null, null);
        this.clanName = clanName;
    }

    @Override
    public void execute() {
        if (isPlayerInClan()) {
            MessageUtil.devMessage(player, "You're already in a clan!");
            return;
        }

        if (PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.devMessage(player, "This clan is already existed!");
            return;
        }

        Date date = new Date();
        long dateLong = date.getTime();
        List<String> members = new ArrayList<>();
        members.add(playerName);
        List<String> allies = new ArrayList<>();
        HashMap<Integer, Integer> skillLevel = new HashMap<>();
        if (!Settings.CLAN_SETTING_SKILL_DEFAULT.isEmpty())
            skillLevel = Settings.CLAN_SETTING_SKILL_DEFAULT;

        ClanData clanData = new ClanData(
                clanName,
                null,
                player.getName(),
                null,
                0,
                0,
                0,
                Settings.CLAN_SETTING_MAXIMUM_MEMBER_DEFAULT,
                dateLong,
                IconType.valueOf(Settings.CLAN_SETTING_ICON_DEFAULT_TYPE),
                Settings.CLAN_SETTING_ICON_DEFAULT_VALUE,
                members,
                null,
                allies,
                skillLevel,
                Settings.CLAN_SETTING_PERMISSION_DEFAULT);

        PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        playerData.setClan(clanName);
        playerData.setRank(Rank.LEADER);
        playerData.setJoinDate(dateLong);

        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);

        MessageUtil.devMessage(player, "Successfully created bang hoi " + clanName);
    }
}
