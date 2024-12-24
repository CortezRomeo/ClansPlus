package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.enums.IconType;
import com.cortezromeo.clansplus.enums.Rank;
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
                skillLevel);

        if (!storage.contains("data"))
            return clanData;

        clanData.setName(storage.getString("data.ten"));
        clanData.setCustomName(storage.getString("data.ten_custom"));
        clanData.setOwner(storage.getString("data.leader"));
        clanData.setScore(storage.getInt("data.diem"));
        clanData.setCreatedDate(storage.getLong("data.ngay_thanh_lap"));
        clanData.setMaxMember(storage.getInt("data.thanh_vien_toi_da"));
        for (String player : storage.getStringList("data.thanh_vien"))
            clanData.getMembers().add(player);
        // TODO <DATA FIX 3.3> All managers from the list will have rank MANAGER
/*        if (storage.getString("data.managers") != null) {
            for (String key : storage.getStringList("data.managers"))
                data.addManager(key);
        }*/
        clanData.setWarning(storage.getInt("data.warn"));
        clanData.setWarPoint(storage.getInt("data.warpoint"));

        // TODO <DATA FIX 3.3>
        if (storage.getString("data.banghoiicon") != null) {
            storage.set("data.banghoiicon", null);
        }

        String iconType = storage.getString("data.icon.type");
        if (iconType != null) {
            clanData.setIconType(IconType.valueOf(storage.getString("data.icon.type")));
            clanData.setIconValue("data.icon.value");
        }
/*        if (storage.getString("data.banghoiicon") != null)
            data.setBangHoiIcon(storage.getString("data.banghoiicon"));
        else
            data.setBangHoiIcon(null);*/

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

        return clanData;
    }

    @Override
    public void saveClanData(String clanName, ClanData clanData) {
        File file = getClanFile(clanName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        storage.set("data.ten", clanData.getName());
        storage.set("data.ten_custom", clanData.getCustomName());
        storage.set("data.leader", clanData.getOwner());
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

        PlayerData playerData = new PlayerData(null, null, 0, 0);

        if (!storage.contains("data"))
            return playerData;

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
    public void savePlayerData(String playerName, PlayerData playerData) {
        File file = getPlayerFile(playerName);
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

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
}
