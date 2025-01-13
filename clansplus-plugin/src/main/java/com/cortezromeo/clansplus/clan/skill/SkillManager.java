package com.cortezromeo.clansplus.clan.skill;

import com.cortezromeo.clansplus.file.SkillsFile;

import java.util.HashMap;

public class SkillManager {

    public static HashMap<Integer, SkillData> skillData = new HashMap<>();

    public static HashMap<Integer, SkillData> getSkillData() {
        return skillData;
    }

    public static int getSkillID(PluginSkill pluginSkill) {
        return SkillsFile.get().getInt("plugin-skills." + pluginSkill.toString().toLowerCase() + ".ID");
    }

    public static void registerPluginSkill(int pluginSkill, SkillData skillData) {
        getSkillData().put(pluginSkill, skillData);
    }
}
