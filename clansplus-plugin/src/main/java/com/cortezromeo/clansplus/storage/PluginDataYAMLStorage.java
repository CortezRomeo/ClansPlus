package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.util.FileNameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginDataYAMLStorage implements PluginStorage {

    private static File getClanFile(String clanName) {
        File file = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData/" + clanName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private static File getPlayerFile(String playerName) {
        File file = new File(ClansPlus.plugin.getDataFolder() + "/playerData/" + playerName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public ClanData getClanData(String clanName) {
        File clanFile = getClanFile(clanName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(clanFile);

        List<String> members = new ArrayList<>();
        List<String> allies = new ArrayList<>();
        HashMap<Integer, Integer> skillLevel = new HashMap<>();

        ClanData clanData = new ClanData(
                clanName,
                null,
                null,
                null,
                0,
                0,
                0,
                Settings.CLAN_SETTING_MAXIMUM_MEMBER_DEFAULT,
                0,
                IconType.valueOf(Settings.CLAN_SETTING_ICON_DEFAULT_TYPE),
                Settings.CLAN_SETTING_ICON_DEFAULT_VALUE,
                members,
                null,
                allies,
                skillLevel,
                Settings.CLAN_SETTING_PERMISSION_DEFAULT);

        if (!storage.contains("data"))
            return clanData;

        clanData.setName(storage.getString("data.ten"));
        clanData.setCustomName(storage.getString("data.ten_custom"));
        clanData.setOwner(storage.getString("data.leader"));
        clanData.setMessage(storage.getString("data.message"));
        clanData.setScore(storage.getInt("data.diem"));
        clanData.setCreatedDate(storage.getLong("data.ngay_thanh_lap"));
        clanData.setMaxMember(storage.getInt("data.thanh_vien_toi_da"));
        for (String player : storage.getStringList("data.thanh_vien"))
            clanData.getMembers().add(player);
        // TODO <DATA FIX 3.4> All managers from the list will have rank MANAGER, delete managers list
        if (storage.getString("data.managers") != null) {
            for (String manager : storage.getStringList("data.managers"))
                ClanManager.managersFromOldData.put(manager, clanName);
            storage.set("data.managers", null);
        }
        clanData.setWarning(storage.getInt("data.warn"));
        clanData.setWarPoint(storage.getInt("data.warpoint"));

        // TODO <DATA FIX 3.4> Old database does not have icon type
        if (storage.getString("data.banghoiicon") != null) {
            try {
                clanData.setIconType(IconType.MATERIAL);
                clanData.setIconValue(storage.getString("data.banghoiicon"));
            } catch (Exception exception) {
                clanData.setIconType(IconType.valueOf(Settings.CLAN_SETTING_ICON_DEFAULT_TYPE));
                clanData.setIconValue(Settings.CLAN_SETTING_ICON_DEFAULT_VALUE);
            }
            storage.set("data.banghoiicon", null);
        }

        String iconType = storage.getString("data.icon.type");
        if (iconType != null) {
            clanData.setIconType(IconType.valueOf(storage.getString("data.icon.type")));
            clanData.setIconValue(storage.getString("data.icon.value"));
        }

        String spawnWorld = storage.getString("data.spawn.world");
        if (spawnWorld != null) {
            Location location = new Location(Bukkit.getWorld(spawnWorld), storage.getDouble("data.spawn.x"), storage.getDouble("data.spawn.y"), storage.getDouble("data.spawn.z"));
            clanData.setSpawnPoint(location);
        }

        clanData.getSkillLevel().put(1, storage.getInt("data.skill.1"));
        clanData.getSkillLevel().put(2, storage.getInt("data.skill.2"));
        clanData.getSkillLevel().put(3, storage.getInt("data.skill.3"));
        clanData.getSkillLevel().put(4, storage.getInt("data.skill.4"));

/*        data.setSkillLevel(SkillType.critDamage, storage.getInt("data.skill.1"));
        data.setSkillLevel(SkillType.boostScore, storage.getInt("data.skill.2"));
        data.setSkillLevel(SkillType.dodge, storage.getInt("data.skill.3"));
        data.setSkillLevel(SkillType.vampire, storage.getInt("data.skill.4"));*/

        if (storage.getConfigurationSection("data.permission") == null)
            clanData.setSubjectPermission(Settings.CLAN_SETTING_PERMISSION_DEFAULT);
        else
            for (String subjectName : storage.getConfigurationSection("data.permission").getKeys(false)) {
                Subject subject = Subject.valueOf(subjectName);
                Rank rank = Rank.valueOf(storage.getString("data.permission." + subjectName));
                clanData.getSubjectPermission().put(subject, rank);
            }

        return clanData;
    }

    @Override
    public void saveClanData(String clanName, IClanData clanData) {
        File file = getClanFile(clanName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        storage.set("data.ten", clanData.getName());
        storage.set("data.ten_custom", clanData.getCustomName());
        storage.set("data.leader", clanData.getOwner());
        storage.set("data.message", clanData.getMessage());
        storage.set("data.diem", clanData.getScore());
        storage.set("data.ngay_thanh_lap", clanData.getCreatedDate());
        storage.set("data.thanh_vien_toi_da", clanData.getMaxMember());
        storage.set("data.thanh_vien", clanData.getMembers());
        storage.set("data.warn", clanData.getWarning());
        storage.set("data.warpoint", clanData.getWarPoint());
        storage.set("data.icon.type", clanData.getIconType().toString().toUpperCase());
        storage.set("data.icon.value", clanData.getIconValue());
        if (clanData.getSpawnPoint() != null) {
            storage.set("data.spawn.world", clanData.getSpawnPoint().getWorld().getName());
            storage.set("data.spawn.x", clanData.getSpawnPoint().getX());
            storage.set("data.spawn.y", clanData.getSpawnPoint().getY());
            storage.set("data.spawn.z", clanData.getSpawnPoint().getZ());
        }
        for (int skillID : clanData.getSkillLevel().keySet())
            storage.set("data.skill." + skillID, clanData.getSkillLevel().get(skillID));
        for (Subject subject : clanData.getSubjectPermission().keySet()) {
            storage.set("data.permission." + subject.toString(), clanData.getSubjectPermission().get(subject).toString().toUpperCase());
        }
        try {
            storage.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public PlayerData getPlayerData(String playerName) {
        File clanFile = getPlayerFile(playerName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(clanFile);

        PlayerData playerData = new PlayerData(playerName, (Bukkit.getPlayer(playerName) != null ? Bukkit.getPlayer(playerName).getUniqueId().toString() : null), null, null, 0, 0);

        if (!storage.contains("data"))
            return playerData;

        playerData.setPlayerName(storage.getString("data.playerName"));
        if (storage.getString("data.UUID") == null) {
            if (Bukkit.getPlayer(playerName) != null)
                playerData.setUUID(storage.getString(Bukkit.getPlayer(playerName).getUniqueId().toString()));
        } else
            playerData.setUUID(storage.getString("data.UUID"));
        playerData.setClan(storage.getString("data.bang_hoi"));
        try {
            playerData.setRank(Rank.valueOf(storage.getString("data.chuc_vu").toUpperCase()));
        } catch (NullPointerException | IllegalArgumentException exception) {
            playerData.setRank(null);
        }
        playerData.setJoinDate(storage.getLong("data.ngay_tham_gia"));
        playerData.setScoreCollected(storage.getLong("data.diem_kiem_duoc"));

        return playerData;
    }

    @Override
    public void savePlayerData(String playerName, IPlayerData playerData) {
        File file = getPlayerFile(playerName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        storage.set("data.playerName", playerName);
        storage.set("data.UUID", playerData.getUUID());
        storage.set("data.bang_hoi", playerData.getClan());
        storage.set("data.chuc_vu", String.valueOf(playerData.getRank()));
        storage.set("data.ngay_tham_gia", playerData.getJoinDate());
        storage.set("data.diem_kiem_duoc", playerData.getScoreCollected());

        try {
            storage.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteClanData(String clanName) {
        File clanFile = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData/" + clanName + ".yml");
        if (!clanFile.exists())
            return true;

        try {
            return clanFile.delete();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> getAllClans() {
        File clanFolder = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData");
        File[] listOfFilesClan = clanFolder.listFiles();
        List<String> clans = new ArrayList<>();

        if (listOfFilesClan == null)
            return clans;

        for (File file : listOfFilesClan) {
            try {
                if (file.isFile()) {
                    String clanName = FileNameUtil.removeExtension(file.getName());
                    clans.add(clanName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clans;
    }

    @Override
    public List<String> getAllPlayers() {
        File playerFolder = new File(ClansPlus.plugin.getDataFolder() + "/playerData");
        File[] listOfFilesPlayer = playerFolder.listFiles();
        List<String> players = new ArrayList<>();

        if (listOfFilesPlayer == null)
            return players;

        for (File file : listOfFilesPlayer) {
            try {
                if (file.isFile()) {
                    String playerName = FileNameUtil.removeExtension(file.getName());
                    players.add(playerName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return players;
    }

    @Override
    public void disableStorage() {
    }
}
