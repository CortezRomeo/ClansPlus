package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.event.WarEvent;
import com.cortezromeo.clansplus.util.HashMapUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DiscordSupport {

    String SOFT_DEPEND_DISCORDWEBHOOK_URL;

    public DiscordSupport(String webHookUrl) {
        SOFT_DEPEND_DISCORDWEBHOOK_URL = webHookUrl;
    }

    public void sendMessage(String message) {
        DiscordWebhook discordWebhook = new DiscordWebhook(SOFT_DEPEND_DISCORDWEBHOOK_URL);
        if (SOFT_DEPEND_DISCORDWEBHOOK_URL == null || SOFT_DEPEND_DISCORDWEBHOOK_URL.equals(""))
            return;

        ClansPlus.support.getFoliaLib().getScheduler().runAsync(wrappedTask -> {
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription(message));
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                MessageUtil.throwErrorMessage("[Discord Web Hook] Occur an error while trying to connect to discord web hook! (" + exception.getMessage() + ")");
            }
        });
    }

    public void sendMessage(DiscordWebhook.EmbedObject embedObject) {
        DiscordWebhook discordWebhook = new DiscordWebhook(SOFT_DEPEND_DISCORDWEBHOOK_URL);
        if (SOFT_DEPEND_DISCORDWEBHOOK_URL == null || SOFT_DEPEND_DISCORDWEBHOOK_URL.equals(""))
            return;

        ClansPlus.support.getFoliaLib().getScheduler().runAsync(wrappedTask -> {
            discordWebhook.addEmbed(embedObject);
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                MessageUtil.throwErrorMessage("[Discord Web Hook] Occur an error while trying to connect to discord web hook! (" + exception.getMessage() + ")");
            }
        });
    }

    public DiscordWebhook.EmbedObject getWarEventStartingMessage(String jsonFile, WarEvent warEvent) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (jsonObject.has("title")) {
            embedObject.setTitle(formatWarEventStartingMessage(jsonObject.getString("title"), jsonObject, warEvent));
        }
        if (jsonObject.has("description")) {
            embedObject.setDescription(formatWarEventStartingMessage(jsonObject.getString("description"), jsonObject, warEvent));
        }
        if (jsonObject.has("thumbnail")) {
            embedObject.setThumbnail(jsonObject.getString("thumbnail"));
        }
        if (jsonObject.has("color")) {
            embedObject.setColor(jsonObject.getInt("color"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedObject.addBlankField(false);
                } else {
                    String name = formatWarEventStartingMessage(field.getString("name"), jsonObject, warEvent);
                    String value = formatWarEventStartingMessage(field.getString("value"), jsonObject, warEvent);
                    embedObject.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatWarEventStartingMessage(object.getString("text"), jsonObject, warEvent);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedObject.setFooter(text);
            else
                embedObject.setFooter(text, icon_url);
        }
        return embedObject;
    }

    public DiscordWebhook.EmbedObject getWarEventEndingMessage(String jsonFile, WarEvent warEvent) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (jsonObject.has("title")) {
            embedObject.setTitle(formatWarEventEndingMessage(jsonObject.getString("title"), jsonObject, warEvent));
        }
        if (jsonObject.has("description")) {
            embedObject.setDescription(formatWarEventEndingMessage(jsonObject.getString("description"), jsonObject, warEvent));
        }
        if (jsonObject.has("thumbnail")) {
            embedObject.setThumbnail(jsonObject.getString("thumbnail"));
        }
        if (jsonObject.has("color")) {
            embedObject.setColor(jsonObject.getInt("color"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedObject.addBlankField(false);
                } else {
                    String name = formatWarEventEndingMessage(field.getString("name"), jsonObject, warEvent);
                    String value = formatWarEventEndingMessage(field.getString("value"), jsonObject, warEvent);
                    embedObject.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatWarEventEndingMessage(object.getString("text"), jsonObject, warEvent);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedObject.setFooter(text);
            else
                embedObject.setFooter(text, icon_url);
        }
        return embedObject;
    }

    private String formatWarEventStartingMessage(String string, JSONObject jsonObject, WarEvent warEvent) {
        if (string.contains("%timeLeft%")) {
            JSONObject timeLeftJO = jsonObject.getJSONObject("placeholders").getJSONObject("timeLeft");
            string = string.replace("%timeLeft%", StringUtil.getTimeFormat(warEvent.getTimeLeft(), timeLeftJO.getString("hhmmss"), timeLeftJO.getString("mmss"), timeLeftJO.getString("ss")));
        }
        string = string.replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        return string;
    }

    private String formatWarEventEndingMessage(String string, JSONObject jsonObject, WarEvent warEvent) {
        List<String> topClanScoreCollected = HashMapUtil.sortFromGreatestToLowestL(warEvent.getClanScoreCollected());
        List<String> topPlayerDamagesCaused = HashMapUtil.sortFromGreatestToLowestL(warEvent.getPlayerDamagesCaused());
        List<String> topPlayerDamagesCollected = HashMapUtil.sortFromGreatestToLowestL(warEvent.getPlayerDamagesCollected());

        for (int i = 0; i <= jsonObject.getInt("maxtop"); i++) {
            // start with 1
            int top = i + 1;

            try {
                String clanScoreCollected = topClanScoreCollected.get(i);
                string = string.replace("%topScoreClaimed_" + top + "_name%", clanScoreCollected);
                string = string.replace("%topScoreClaimed_" + top + "_score%", String.valueOf(warEvent.getClanScoreCollected(clanScoreCollected)));
            } catch (Exception exception) {
                string = string.replace("%topScoreClaimed_" + top + "_name%", jsonObject.getJSONObject("placeholders").getString("unknown"));
                string = string.replace("%topScoreClaimed_" + top + "_score%", "0");
            }

            try {
                String playerDamagesCaused = topPlayerDamagesCaused.get(i);
                string = string.replace("%topDamage_" + top + "_name%", playerDamagesCaused);
                string = string.replace("%topDamage_" + top + "_score%", String.valueOf(warEvent.getPlayerDamagesCaused(playerDamagesCaused)));
            } catch (Exception exception) {
                string = string.replace("%topDamage_" + top + "_name%", jsonObject.getJSONObject("placeholders").getString("unknown"));
                string = string.replace("%topDamage_" + top + "_score%", "0");
            }

            try {
                String playerDamagesCollected = topPlayerDamagesCollected.get(i);
                string = string.replace("%topTank_" + top + "_name%", playerDamagesCollected);
                string = string.replace("%topTank_" + top + "_score%", String.valueOf(warEvent.getPlayerDamagesCollected(playerDamagesCollected)));
            } catch (Exception exception) {
                string = string.replace("%topTank_" + top + "_name%", jsonObject.getJSONObject("placeholders").getString("unknown"));
                string = string.replace("%topTank_" + top + "_score%", "0");
            }
        }
        string = string.replace("%totalDamagesCaused%", String.valueOf(warEvent.getTotalDamageCaused()));
        string = string.replace("%totalDamagesCollected%", String.valueOf(warEvent.getTotalDamageCollected()));
        string = string.replace("%totalScoreCollected%", String.valueOf(warEvent.getTotalScoreCollected()));

        return string;
    }


}
