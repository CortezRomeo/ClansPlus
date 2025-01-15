package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public class ClanData implements IClanData {

    private String name;
    private String customName;
    private String owner;
    private String message;
    private int score;
    private long warPoint;
    private int warning;
    private int maxMembers;
    private long createdDate;
    private IconType iconType;
    private String iconValue;
    private List<String> members;
    private Location spawnPoint;
    private List<String> allies;
    private HashMap<Integer, Integer> skillLevel;
    private HashMap<Subject, Rank> subjectPermission;
    private List<String> allyInvitation;
    private long discordChannelID;
    private String discordJoinLink;

    public ClanData(String name, String customName, String owner, String message, int score, long warPoint, int warning, int maxMembers, long createdDate,
                    IconType iconType, String iconValue, List<String> members, Location spawnPoint, List<String> allies, HashMap<Integer, Integer> skillLevel, HashMap<Subject, Rank> subjectPermission,
                    List<String> allyInvitation, long discordChannelID, String discordJoinLink) {
        this.name = name;
        this.customName = customName;
        this.owner = owner;
        this.message = message;
        this.score = score;
        this.warPoint = warPoint;
        this.warning = warning;
        this.maxMembers = maxMembers;
        this.createdDate = createdDate;
        this.iconType = iconType;
        this.iconValue = iconValue;
        this.members = members;
        this.spawnPoint = spawnPoint;
        this.allies = allies;
        this.skillLevel = skillLevel;
        this.subjectPermission = subjectPermission;
        this.allyInvitation = allyInvitation;
        this.discordChannelID = discordChannelID;
        this.discordJoinLink = discordJoinLink;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCustomName() {
        return this.customName;
    }

    @Override
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public long getWarPoint() {
        return warPoint;
    }

    @Override
    public void setWarPoint(long warPoint) {
        this.warPoint = warPoint;
    }

    @Override
    public int getWarning() {
        return warning;
    }

    @Override
    public void setWarning(int warning) {
        this.warning = warning;
    }

    @Override
    public List<String> getMembers() {
        return members;
    }

    @Override
    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public int getMaxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public long getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public IconType getIconType() {
        return iconType;
    }

    @Override
    public void setIconType(IconType iconType) {
        this.iconType = iconType;
    }

    @Override
    public String getIconValue() {
        return iconValue;
    }

    @Override
    public void setIconValue(String iconValue) {
        this.iconValue = iconValue;
    }

    @Override
    public Location getSpawnPoint() {
        return spawnPoint;
    }

    @Override
    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public List<String> getAllies() {
        return allies;
    }

    @Override
    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    @Override
    public HashMap<Integer, Integer> getSkillLevel() {
        return skillLevel;
    }

    @Override
    public void setSkillLevel(HashMap<Integer, Integer> skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public HashMap<Subject, Rank> getSubjectPermission() {
        return subjectPermission;
    }

    @Override
    public void setSubjectPermission (HashMap<Subject, Rank> subjectPermission) {
        this.subjectPermission = subjectPermission;
    }

    @Override
    public List<String> getAllyInvitation() {
        return allyInvitation;
    }

    @Override
    public void setAllyInvitation(List<String> allyInvitation) {
        this.allyInvitation = allyInvitation;
    }

    @Override
    public long getDiscordChannelID() {
        return discordChannelID;
    }

    @Override
    public void setDiscordChannelID(long discordChannelID) {
        this.discordChannelID = discordChannelID;
    }

    @Override
    public String getDiscordJoinLink() {
        return discordJoinLink;
    }

    @Override
    public void setDiscordJoinLink(String discordJoinLink) {
        this.discordJoinLink = discordJoinLink;
    }
}
