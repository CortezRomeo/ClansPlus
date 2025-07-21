package com.cortezromeo.clansplus.support.version;

import com.cortezromeo.clansplus.api.server.VersionSupport;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.exceptions.ProfileChangeException;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.*;
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
    public ItemStack createItemStack(String material, int amount, int customModelData, boolean glow) {
        return XMaterial.matchXMaterial(material)
                .map(XMaterial::parseItem)
                .map(item -> {
                    item.setAmount(amount);
                    ItemMeta itemMeta = item.getItemMeta();
                    if (customModelData != 0) {
                        itemMeta.setCustomModelData(customModelData);
                        item.setItemMeta(itemMeta);
                    }
                    if (glow) {
                        itemMeta.addEnchant(XEnchantment.UNBREAKING.get(), 1, false);
                        XItemFlag.HIDE_ENCHANTS.set(itemMeta);
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
        try {
            return XSkull.createItem().profile(Profileable.of(ProfileInputType.BASE64, headValue)).apply();
        } catch (ProfileChangeException exception) {
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    public ItemStack getHeadItemFromPlayerName(String playerName) {
        try {
            if (Bukkit.getPlayer(playerName) != null)
                playerName = Bukkit.getPlayer(playerName).getUniqueId().toString();
            else if (!Bukkit.getServer().getOnlineMode()) {
                String offlinePlayerString = "OfflinePlayer:" + playerName;
                playerName = UUID.nameUUIDFromBytes(offlinePlayerString.getBytes(StandardCharsets.UTF_8)).toString();
            }
            return XSkull.createItem().profile(Profileable.of(UUID.fromString(playerName))).apply();
            // normally, if this account is not a part of microsoft account, it will occur error
        } catch (Exception exception) {
            return XMaterial.PLAYER_HEAD.parseItem();
        }
    }

    @Override
    public ItemStack addCustomData(ItemStack itemStack, String data) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        NBT.modify(itemStack, nbt -> {
            nbt.setString(NBT_KEY + ".customdata", data);
        });

        return itemStack;
    }

    @Override
    public String getCustomData(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        return NBT.get(itemStack, nbt -> (String) (nbt.getString(NBT_KEY + ".customdata")));
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
        return textToStrip == null ? null : HEX_PATTERN.matcher(textToStrip).replaceAll("");
    }

    @Override
    public Particle getParticle(String particleName) {
        try {
            return XParticle.of(particleName).get().get();
        } catch (Exception exception) {
            getPlugin().getLogger().severe("----------------------------------------------------");
            getPlugin().getLogger().severe("PARTICLE NAME " + particleName + " DOES NOT EXIST!");
            getPlugin().getLogger().severe("Maybe you type it wrong or it does not exist in this server version.");
            getPlugin().getLogger().severe("Please take a look at those valid particles right here:");
            getPlugin().getLogger().severe("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html");
            getPlugin().getLogger().severe("----------------------------------------------------");
            return Particle.BARRIER;
        }
    }

}
