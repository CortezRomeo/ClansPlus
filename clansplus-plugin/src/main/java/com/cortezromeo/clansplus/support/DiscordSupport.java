package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.clan.event.WarEvent;
import com.cortezromeo.clansplus.util.HashMapUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscordSupport implements SlashCommandProvider {

    private final boolean discordsrvEnabled;
    private final Long channelID;

    public DiscordSupport(ClansPlus plugin) {
        this.discordsrvEnabled = Settings.SOFT_DEPEND_DISCORDSRV_ENABLED;
        this.channelID = Settings.SOFT_DEPEND_DISCORDSRV_CHANNELID;
    }

    public Guild getGuild() {
        return DiscordSRV.getPlugin().getJda().getGuilds().stream().findFirst().orElse(null);
    }

    public void sendMessage(String message) {
        if (!discordsrvEnabled) {
            return;
        }
        TextChannel messageChannel = getGuild().getTextChannelById(channelID);
        if (messageChannel == null) {
            MessageUtil.throwErrorMessage("[DiscordSRV] Không thể kết nối tới channel ID " + channelID + ", vui lòng kiểm tra lại!");
            return;
        }
        messageChannel.sendMessage(message).queue();
    }

    public void sendMessage(Message message) {
        if (!discordsrvEnabled) {
            return;
        }
        TextChannel messageChannel = getGuild().getTextChannelById(channelID);
        if (messageChannel == null) {
            MessageUtil.throwErrorMessage("[DiscordSRV] Không thể kết nối tới channel ID " + channelID + ", vui lòng kiểm tra lại!");
            return;
        }
        DiscordUtil.queueMessage(messageChannel, message);
    }

    public Message getWarEventStartingMessage(String jsonFile, WarEvent warEvent) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (jsonObject.has("title")) {
            embedBuilder.setTitle(formatWarEventStartingMessage(jsonObject.getString("title"), jsonObject, warEvent));
        }
        if (jsonObject.has("description")) {
            embedBuilder.setDescription(formatWarEventStartingMessage(jsonObject.getString("description"), jsonObject, warEvent));
        }
        if (jsonObject.has("thumbnail")) {
            embedBuilder.setThumbnail(jsonObject.getString("thumbnail"));
        }
        if (jsonObject.has("color")) {
            embedBuilder.setColor(jsonObject.getInt("color"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedBuilder.addBlankField(false);
                } else {
                    String name = formatWarEventStartingMessage(field.getString("name"), jsonObject, warEvent);
                    String value = formatWarEventStartingMessage(field.getString("value"), jsonObject, warEvent);
                    embedBuilder.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatWarEventStartingMessage(object.getString("text"), jsonObject, warEvent);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedBuilder.setFooter(text);
            else
                embedBuilder.setFooter(text, icon_url);
        }
        return new MessageBuilder().setEmbeds(embedBuilder.build()).build();
    }

    public Message getWarEventEndingMessage(String jsonFile, WarEvent warEvent) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (jsonObject.has("title")) {
            embedBuilder.setTitle(formatWarEventEndingMessage(jsonObject.getString("title"), jsonObject, warEvent));
        }
        if (jsonObject.has("description")) {
            embedBuilder.setDescription(formatWarEventEndingMessage(jsonObject.getString("description"), jsonObject, warEvent));
        }
        if (jsonObject.has("thumbnail")) {
            embedBuilder.setThumbnail(jsonObject.getString("thumbnail"));
        }
        if (jsonObject.has("color")) {
            embedBuilder.setColor(jsonObject.getInt("color"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedBuilder.addBlankField(false);
                } else {
                    String name = formatWarEventEndingMessage(field.getString("name"), jsonObject, warEvent);
                    String value = formatWarEventEndingMessage(field.getString("value"), jsonObject, warEvent);
                    embedBuilder.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatWarEventEndingMessage(object.getString("text"), jsonObject, warEvent);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedBuilder.setFooter(text);
            else
                embedBuilder.setFooter(text, icon_url);
        }
        return new MessageBuilder().setEmbeds(embedBuilder.build()).build();
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

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return new HashSet<>(Arrays.asList(
                // ping pong
                new PluginSlashCommand(ClansPlus.plugin, new CommandData("ping", "A classic match of ping pong")),

                // bests
                new PluginSlashCommand(ClansPlus.plugin, new CommandData("best", "Best _____")
                        .addSubcommands(new SubcommandData("friend", "Best friend"))
                        .addSubcommands(new SubcommandData("plugin", "Best plugin"))
                )
        ));
    }

    @SlashCommand(path = "ping")
    public void pingCommand(SlashCommandEvent event) {
        event.reply("Pong!").queue();
    }
}
