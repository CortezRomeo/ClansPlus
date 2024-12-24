package com.cortezromeo.clansplus.skill;

import java.util.HashMap;

public class SkillManager {

    public static HashMap<Integer, SkillData> skillDatalHashMap = new HashMap<>();

    public static void registerSkill(int id, SkillData skillData) {
        skillDatalHashMap.put(id, skillData);
    }

}
