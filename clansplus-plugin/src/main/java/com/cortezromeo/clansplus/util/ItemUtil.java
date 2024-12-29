package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUtil {

    public static ItemStack getItem(String type, String value, int customModelData, String name, List<String> lore) {
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (type.equalsIgnoreCase("customhead"))
            material.set(ClansPlus.nms.getHeadItemFromBase64(value));
        if (type.equalsIgnoreCase("playerhead"))
            material.set(ClansPlus.nms.getHeadItemFromPlayerName(value));
        if (type.equalsIgnoreCase("material"))
            material.set(ClansPlus.nms.createItemStack(value, 1, customModelData));

        ItemMeta materialMeta = material.get().getItemMeta();

        materialMeta.setDisplayName(ClansPlus.nms.addColor(name));

        List<String> newList = new ArrayList<>();

        for (String string : lore)
            newList.add(ClansPlus.nms.addColor(string));
        materialMeta.setLore(newList);

        material.get().setItemMeta(materialMeta);
        return material.get();
    }

}
