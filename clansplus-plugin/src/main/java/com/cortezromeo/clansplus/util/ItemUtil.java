package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUtil {

    public static ItemStack getItem(String type, String value, int customModelData, String name, List<String> lore, boolean glow) {
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (type.equalsIgnoreCase("customhead"))
            material.set(ClansPlus.nms.getHeadItemFromBase64(value));
        if (type.equalsIgnoreCase("playerhead"))
            material.set(ClansPlus.nms.getHeadItemFromPlayerName(value));
        if (type.equalsIgnoreCase("material"))
            material.set(ClansPlus.nms.createItemStack(value, 1, customModelData, glow));

        ItemMeta materialMeta = material.get().getItemMeta();

        materialMeta.setDisplayName(ClansPlus.nms.addColor(name));

        List<String> newList = new ArrayList<>();

        for (String string : lore)
            newList.add(ClansPlus.nms.addColor(string));
        materialMeta.setLore(newList);

        material.get().setItemMeta(materialMeta);
        return material.get();
    }

    public static @NotNull ItemStack getClanItemStack(ItemStack itemStack, IClanData clanData) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = itemName.replace("%formatClanName%", ClanManager.getFormatClanName(clanData));
        itemName = itemName.replace("%clanName%", clanData.getName());
        itemName = itemName.replace("%clanCustomName%", ClanManager.getFormatCustomName(clanData));
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%score%", String.valueOf(clanData.getScore()))
                        .replace("%warPoint%", String.valueOf(clanData.getWarPoint()))
                        .replace("%formatClanName%", ClanManager.getFormatClanName(clanData))
                        .replace("%clanName%", String.valueOf(clanData.getName()))
                        .replace("%clanCustomName%", ClanManager.getFormatCustomName(clanData)))
                .replace("%owner%", String.valueOf(clanData.getOwner()))
                .replace("%memberSize%", String.valueOf(clanData.getMembers().size()))
                .replace("%maxMembers%", String.valueOf(clanData.getMaxMembers()))
                .replace("%allySize%", String.valueOf(clanData.getAllies().size()))
                .replace("%message%", ClanManager.getFormatMessage(clanData))
                .replace("%createdDate%", StringUtil.dateTimeToDateFormat(clanData.getCreatedDate()))
                .replace("%warning%", String.valueOf(clanData.getWarning())));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

}
