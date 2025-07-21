package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUtil {

    public static ItemStack getItem(ItemType itemType, String value, int customModelData, String name, List<String> lore, boolean glow) {
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (itemType.equals(ItemType.CUSTOMHEAD))
            material.set(ClansPlus.nms.getHeadItemFromBase64(value));
        if (itemType.equals(ItemType.PLAYERHEAD))
            material.set(ClansPlus.nms.getHeadItemFromPlayerName(value));
        if (itemType.equals(ItemType.MATERIAL)) {
            material.set(ClansPlus.nms.createItemStack(value, 1, customModelData, glow));
        }

        ItemMeta materialMeta = material.get().getItemMeta();

        if (materialMeta == null)
            return material.get();

        materialMeta.setDisplayName(ClansPlus.nms.addColor(name));

        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String string : lore)
                newLore.add(ClansPlus.nms.addColor(string));
            materialMeta.setLore(newLore);
        }

        material.get().setItemMeta(materialMeta);
        return material.get();
    }

    public static @NotNull ItemStack getClanItemStack(ItemStack itemStack, IClanData clanData) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = StringUtil.setClanNamePlaceholder(itemName, clanData.getName());
        itemName = itemName.replace("%clanCustomName%", ClanManager.getFormatClanCustomName(clanData));
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%score%", String.valueOf(clanData.getScore()))
                        .replace("%warPoint%", String.valueOf(clanData.getWarPoint()))
                        .replace("%formatClanName%", ClanManager.getFormatClanName(clanData))
                        .replace("%clanName%", String.valueOf(clanData.getName()))
                        .replace("%clanCustomName%", ClanManager.getFormatClanCustomName(clanData)))
                .replace("%owner%", String.valueOf(clanData.getOwner()))
                .replace("%memberSize%", String.valueOf(clanData.getMembers().size()))
                .replace("%maxMembers%", String.valueOf(clanData.getMaxMembers()))
                .replace("%allySize%", String.valueOf(clanData.getAllies().size()))
                .replace("%message%", ClanManager.getFormatClanMessage(clanData))
                .replace("%createdDate%", StringUtil.dateTimeToDateFormat(clanData.getCreatedDate()))
                .replace("%warning%", String.valueOf(clanData.getWarning())));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public static @NotNull ItemStack getPlayerItemStack(ItemStack itemStack, String playerName) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = itemName.replace("%player%", playerName);
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        List<String> itemLore = itemMeta.getLore();

        Player player = Bukkit.getPlayer(playerName);
        boolean onlineStatus;
        if (player == null)
            onlineStatus = false;
        else
            onlineStatus = !PlayerUtil.isVanished(player);

        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%player%", playerName)
                .replace("%uuid%", playerData.getUUID() == null ? ClansPlus.nms.addColor(Messages.UNKNOWN) : playerData.getUUID())
                .replace("%rank%", ClanManager.getFormatRank(playerData.getRank()))
                .replace("%joinDate%", StringUtil.dateTimeToDateFormat(playerData.getJoinDate()))
                .replace("%onlineStatus%", (onlineStatus ? Messages.ONLINE_STATUS_ONLINE : Messages.ONLINE_STATUS_OFFLINE))
                .replace("%lastActivated%", StringUtil.dateTimeToDateFormat(playerData.getLastActivated()))
                .replace("%scoreCollected%", String.valueOf(playerData.getScoreCollected()))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }
}
