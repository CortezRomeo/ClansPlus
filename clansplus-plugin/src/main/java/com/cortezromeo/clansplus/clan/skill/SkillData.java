package com.cortezromeo.clansplus.clan.skill;

import com.cortezromeo.clansplus.api.enums.SkillType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public abstract class SkillData {

    protected SkillType skillType;
    protected boolean enabled;
    protected int id;
    protected String name;
    protected String description;
    protected String soundName;
    protected int soundPitch;
    protected int soundVolume;
    protected HashMap<Integer, Double> rateToActivate;

    public SkillData(int id, SkillType skillType, boolean enabled, String name, String description, String soundName, int soundPitch, int soundVolume, HashMap<Integer, Double> chanceToActivate) {
        this.id = id;
        this.skillType = skillType;
        this.enabled = enabled;
        this.name = name;
        this.description = description;
        this.soundName = soundName;
        this.soundPitch = soundPitch;
        this.soundVolume = soundVolume;
        this.rateToActivate = chanceToActivate;
    }

    public abstract boolean onDamage(SkillData skillData, EntityDamageByEntityEvent event);

    public abstract boolean onDie(SkillData skillData, String killerName, String victimName, boolean isMob);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public int getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(int soundPitch) {
        this.soundPitch = soundPitch;
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
    }

    public HashMap<Integer, Double> getRateToActivate() {
        return rateToActivate;
    }

    public void setRateToActivate(HashMap<Integer, Double> rateToActivate) {
        this.rateToActivate = rateToActivate;
    }
}
