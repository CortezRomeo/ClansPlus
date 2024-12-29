package com.cortezromeo.clansplus.support.version;

import com.cortezromeo.clansplus.api.server.VersionSupport;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class CrossVersionSupport extends VersionSupport {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");
    private static final String NBT_KEY = "ClansPlus";

    public CrossVersionSupport(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ItemStack getItemStack(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        ItemStack xItemStack = XMaterial.matchXMaterial(itemStack).parseItem();
        xItemStack.setAmount(itemStack.getAmount());
        xItemStack.setItemMeta(itemStack.getItemMeta());
        if (getCustomData(itemStack) != null && !getCustomData(itemStack).equals(""))
            xItemStack = addCustomData(xItemStack, getCustomData(itemStack));
        return xItemStack;
    }

    @Override
    public ItemStack createItemStack(String material, int amount, int customModelData) {
        return XMaterial.matchXMaterial(material)
                .map(XMaterial::parseItem)
                .map(item -> {
                    item.setAmount(amount);
                    if (customModelData != 0) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setCustomModelData(customModelData);
                        item.setItemMeta(itemMeta);
                    }
                    return item;
                })
                .orElseGet(() -> {
                    getPlugin().getLogger().severe("----------------------------------------------------");
                    getPlugin().getLogger().severe("MATERIAL NAME " + material + " DOES NOT EXIST!");
                    getPlugin().getLogger().severe("Maybe you type it wrong or it does not exist in this server version.");
                    getPlugin().getLogger().severe("Please take a look at those valid materials right here:");
                    getPlugin().getLogger().severe("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                    getPlugin().getLogger().severe("----------------------------------------------------");
                    return new ItemStack(Material.BEDROCK);
                });
    }

    @Override
    public Sound createSound(String soundName) {
        try {
            return XSound.matchXSound(soundName).map(XSound::parseSound).orElseGet(() -> {
                getPlugin().getLogger().severe("----------------------------------------------------");
                getPlugin().getLogger().severe("SOUND NAME " + soundName + " DOES NOT EXIST!");
                getPlugin().getLogger().severe("Maybe you type it wrong or it does not exist in this server version.");
                getPlugin().getLogger().severe("Please take a look at those valid sounds right here:");
                getPlugin().getLogger().severe("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
                getPlugin().getLogger().severe("----------------------------------------------------");
                return XSound.BLOCK_AMETHYST_CLUSTER_BREAK.parseSound();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            return XSound.BLOCK_AMETHYST_CLUSTER_BREAK.parseSound();
        }
    }

    @Override
    public ItemStack getHeadItemFromBase64(String headValue) {
        return XSkull.createItem().profile(Profileable.of(ProfileInputType.BASE64, headValue)).apply();
    }

    public ItemStack getHeadItemFromPlayerName(String playerName) {
        if (Bukkit.getPlayer(playerName) != null)
            playerName = Bukkit.getPlayer(playerName).getUniqueId().toString();
        else if (!Bukkit.getServer().getOnlineMode()) {
            String offlinePlayerString = "OfflinePlayer:" + playerName;
            playerName = UUID.nameUUIDFromBytes(offlinePlayerString.getBytes(StandardCharsets.UTF_8)).toString();
        }
        return XSkull.createItem().profile(Profileable.of(UUID.fromString(playerName))).apply();
    }

    @Override
    public ItemStack addCustomData(ItemStack itemStack, String data) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(NBT_KEY + ".customdata", data);
        return nbtItem.getItem();
    }

    @Override
    public String getCustomData(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.getString(NBT_KEY + ".customdata") != null) {
            return nbtItem.getString(NBT_KEY + ".customdata");
        }
        return "";
    }

    @Override
    public String addColor(String textToTranslate) {
        if (textToTranslate == null)
            return "NULL";

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder buffer = new StringBuilder(textToTranslate.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        String hexTranslated = matcher.appendTail(buffer).toString();

        return ChatColor.translateAlternateColorCodes('&', hexTranslated);
    }

    @Override
    public String stripColor(String textToStrip) {
        return textToStrip == null ? null : STRIP_COLOR_PATTERN.matcher(textToStrip).replaceAll("");
    }

}
