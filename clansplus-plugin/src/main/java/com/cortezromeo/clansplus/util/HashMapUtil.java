package com.cortezromeo.clansplus.util;

import java.util.*;

public class HashMapUtil {

    public static List<String> sortFromGreatestToLowestI(HashMap<String, Integer> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> sortFromGreatestToLowestL(HashMap<String, Long> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> sortFromLowestToGreatestI(HashMap<String, Integer> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> sortFromLowestToGreatestL(HashMap<String, Long> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> list.add(entry.getKey()));
        return list;
    }

}
