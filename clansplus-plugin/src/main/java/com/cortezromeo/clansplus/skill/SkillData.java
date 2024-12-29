package com.cortezromeo.clansplus.skill;

import com.cortezromeo.clansplus.api.enums.SkillType;

public class SkillData {

    private int id;
    private SkillType skillType;
    private boolean enabled;
    private String name;
    private String description;

    public SkillData(int id, SkillType skillType, boolean enabled, String name, String description) {
        this.id = id;
        this.skillType = skillType;
        this.enabled = enabled;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void register(int id, SkillData skillData) {
        SkillManager.registerSkill(id, skillData);
    }

}
