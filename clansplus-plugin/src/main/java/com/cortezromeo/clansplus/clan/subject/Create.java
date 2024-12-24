package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.enums.IconType;
import com.cortezromeo.clansplus.enums.Rank;
import com.cortezromeo.clansplus.storage.BangHoiData;
import com.cortezromeo.clansplus.storage.PlayerData;
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
        if (super.isPlayerInClan()) {
            MessageUtil.devMessage(player, "You're already in a clan!");
            return;
        }

        if (PluginDataManager.getBangHoiDatabase().containsKey(clanName)) {
            MessageUtil.devMessage(player, "This clan is already existed!");
            return;
        }

        Date date = new Date();
        long dateLong = date.getTime();
        List<String> members = new ArrayList<>();
        members.add(playerName);
        List<String> allies = new ArrayList<>();
        HashMap<Integer, Integer> skillLevel = new HashMap<>();

        BangHoiData bangHoiData = new BangHoiData(clanName, null, player.getName()
                , 0, 0, 0, Settings.BANG_HOI_OPTION_MAXIMUM_MEMBER_DEFAULT,
                dateLong, IconType.MATERIAL, "DIRT", members, null, allies
                , skillLevel);

        PluginDataManager.saveBangHoiDatabaseToStorage(clanName, bangHoiData);

        PlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        playerData.setClan(clanName);
        playerData.setRank(Rank.LEADER);
        playerData.setJoinDate(dateLong);

        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);

        MessageUtil.devMessage(player, "Successfully created bang hoi " + clanName);
    }
}
