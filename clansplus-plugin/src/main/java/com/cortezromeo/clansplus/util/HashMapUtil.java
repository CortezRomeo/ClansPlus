package com.cortezromeo.clansplus.util;

import java.util.*;

public class HashMapUtil {

    public static List<String> shortFromGreatestToLowestI(HashMap<String, Integer> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> shortFromGreatestToLowestL(HashMap<String, Long> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> shortFromLowestToGreatestI(HashMap<String, Integer> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> list.add(entry.getKey()));
        return list;
    }

    public static List<String> shortFromLowestToGreatestL(HashMap<String, Long> hashMap) {
        List<String> list = new ArrayList<>();
        hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> list.add(entry.getKey()));
        return list;
    }

}
