package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.enums.IconType;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BangHoiData {

    private String name;
    private String customName;
    private String owner;
    private int score;
    private int warPoint;
    private int warning;
    private int maxMember;
    private long createdDate;
    private IconType iconType;
    private String iconValue;
    private List<String> members;
    private Location spawnPoint;
    private List<String> allies = new ArrayList<>();
    private HashMap<Integer, Integer> skillLevel = new HashMap<>();

    public BangHoiData(String name, String customName, String owner, int score, int warPoint, int warning, int maxMember, long createdDate,
                       IconType iconType, String iconValue, List<String> members, Location spawnPoint, List<String> allies, HashMap<Integer, Integer> skillLevel) {
        this.name = name;
        this.customName = customName;
        this.owner = owner;
        this.score = score;
        this.warPoint = warPoint;
        this.warning = warning;
        this.maxMember = maxMember;
        this.createdDate = createdDate;
        this.iconType = iconType;
        this.iconValue = iconValue;
        this.members = members;
        this.spawnPoint = spawnPoint;
        this.allies = allies;
        this.skillLevel = skillLevel;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomName() {
        return this.customName;
    }

    public void setCustomName(String customName) {
        this.customName= customName;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWarPoint() {
        return warPoint;
    }

    public void setWarPoint(int warPoint) {
        this.warPoint = warPoint;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public IconType getIconType() {
        return iconType;
    }

    public void setIconType(IconType iconType) {
        this.iconType = iconType;
    }

    public String getIconValue() {
        return iconValue;
    }

    public void setIconValue(String iconValue) {
        this.iconValue = iconValue;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public List<String> getAllies() {
        return allies;
    }

    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    public HashMap<Integer, Integer> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(HashMap<Integer, Integer> skillLevel) {
        this.skillLevel = skillLevel;
    }
}
