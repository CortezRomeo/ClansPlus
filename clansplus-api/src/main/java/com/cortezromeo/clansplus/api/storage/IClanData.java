package com.cortezromeo.clansplus.api.storage;

import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public interface IClanData {

    String getName();

    void setName(String name);

    String getCustomName();

    void setCustomName(String customName);

    String getOwner();

    void setOwner(String owner);

    String getMessage();

    void setMessage(String message);

    int getScore();

    void setScore(int score);

    long getWarPoint();

    void setWarPoint(long warPoint);

    int getWarning();

    void setWarning(int warning);

    List<String> getMembers();

    void setMembers(List<String> members);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    long getCreatedDate();

    void setCreatedDate(long createdDate);

    IconType getIconType();

    void setIconType(IconType iconType);

    String getIconValue();

    void setIconValue(String iconValue);

    Location getSpawnPoint();

    void setSpawnPoint(Location spawnPoint);

    List<String> getAllies();

    void setAllies(List<String> allies);

    HashMap<Integer, Integer> getSkillLevel();

    void setSkillLevel(HashMap<Integer, Integer> skillLevel);

    HashMap<Subject, Rank> getSubjectPermission();

    void setSubjectPermission(HashMap<Subject, Rank> subjectPermission);

    List<String> getAllyInvitation();

    void setAllyInvitation(List<String> allyInvitation);

    long getDiscordChannelID();

    void setDiscordChannelID(long discordChannelID);

    String getDiscordJoinLink();

    void setDiscordJoinLink(String discordJoinLink);

    HashMap<Integer, String> getInventory();

    void setInventory(HashMap<Integer, String> inventory);

}
