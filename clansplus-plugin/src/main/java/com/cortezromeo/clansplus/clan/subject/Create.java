package com.cortezromeo.clansplus.clan.subject;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.*;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.SubjectManager;
import com.cortezromeo.clansplus.clan.UpgradeManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.ClanData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class Create extends SubjectManager {

    private final String clanName;

    public Create(Player player, String playerName, String clanName) {
        super(null, player, playerName, null, null);
        this.clanName = clanName;
    }

    @Override
    public boolean execute() {
        if (isPlayerInClan()) {
            MessageUtil.sendMessage(player, Messages.ALREADY_IN_CLAN);
            return false;
        }

        if (PluginDataManager.getClanDatabase().containsKey(clanName)) {
            MessageUtil.sendMessage(player, Messages.CLAN_ALREADY_EXIST.replace("%clan%", clanName));
            return false;
        }

        if (clanName.length() < Settings.CLAN_SETTING_NAME_MINIMUM_LENGTH) {
            MessageUtil.sendMessage(player, Messages.ILLEGAL_MINIMUM_CLAN_LENGTH.replace("%minimumClanNameLength%", String.valueOf(Settings.CLAN_SETTING_NAME_MINIMUM_LENGTH)));
            return false;
        }

        if (clanName.length() > Settings.CLAN_SETTING_NAME_MAXIMUM_LENGTH) {
            MessageUtil.sendMessage(player, Messages.ILLEGAL_MAXIMUM_CLAN_LENGTH.replace("%maximumClanNameLength%", String.valueOf(Settings.CLAN_SETTING_NAME_MAXIMUM_LENGTH)));
            return false;
        }

        for (String prohibitedClanName : Settings.CLAN_SETTING_PROHIBITED_NAME) {
            if (clanName.equalsIgnoreCase(prohibitedClanName)) {
                MessageUtil.sendMessage(player, Messages.PROHIBITED_CLAN_NAME.replace("%clanName%", clanName));
                return false;
            }
        }

        List<String> prohibitedCharacters = new ArrayList<>(Settings.CLAN_SETTING_PROHIBITED_CHARACTER);

        // Space and & should not be in the clan name
        prohibitedCharacters.add(" ");
        prohibitedCharacters.add("&");

        // Because of windows limited, these characters cannot be contained in the clan name
        if (ClansPlus.databaseType == DatabaseType.YAML) {
            prohibitedCharacters.add("\\");
            prohibitedCharacters.add("/");
            prohibitedCharacters.add(":");
            prohibitedCharacters.add("*");
            prohibitedCharacters.add("?");
            prohibitedCharacters.add("<");
            prohibitedCharacters.add(">");
            prohibitedCharacters.add("|");
        }
        for (String prohibitedCharacter : prohibitedCharacters) {
            if (clanName.contains(prohibitedCharacter)) {
                MessageUtil.sendMessage(player, Messages.PROHIBITED_CHARACTER.replace("%character%", prohibitedCharacter));
                return false;
            }
        }

        if (Settings.CLAN_SETTING_CREATE_ENABLED)
            if (!UpgradeManager.checkPlayerCurrency(player, CurrencyType.valueOf(Settings.CLAN_SETTING_CREATE_TYPE.toUpperCase()), Settings.CLAN_SETTING_CREATE_CURRENCY, true))
                return false;

        Date date = new Date();
        long dateLong = date.getTime();
        List<String> members = new ArrayList<>();
        members.add(playerName);
        List<String> allies = new ArrayList<>();
        List<String> allyInvitation = new ArrayList<>();
        HashMap<Integer, Integer> skillLevel = new HashMap<>();
        HashMap<Subject, Rank> permissionDefault = new HashMap<>();
        HashMap<Integer, Inventory> inventory = new HashMap<>();
        for (Subject subject : Subject.values())
            permissionDefault.put(subject, Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(subject));
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
                ItemType.valueOf(Settings.CLAN_SETTING_ICON_DEFAULT_TYPE.toUpperCase()),
                Settings.CLAN_SETTING_ICON_DEFAULT_VALUE,
                members,
                null,
                allies,
                skillLevel,
                permissionDefault,
                allyInvitation,
                0,
                null,
                inventory,
                Settings.CLAN_SETTINGS_MAX_INVENTORY_DEFAULT);

        PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        playerData.setClan(clanName);
        playerData.setRank(Rank.LEADER);
        playerData.setJoinDate(dateLong);

        PluginDataManager.savePlayerDatabaseToStorage(playerName, playerData);

        MessageUtil.sendMessage(player, Messages.CREATE_CLAN_SUCCESS.replace("%clan%", clanName));

        int randomBroadcastLine = new Random().nextInt(Settings.CLAN_SETTINGS_CREATION_BROADCAST.size());
        String selectedMessage = Settings.CLAN_SETTINGS_CREATION_BROADCAST.get(randomBroadcastLine).replace("%clanName%", clanName).replace("%player%", playerName);
        MessageUtil.sendBroadCast(selectedMessage);

        return true;
    }
}
