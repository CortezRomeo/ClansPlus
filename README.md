![logo](https://i.imgur.com/fK4D1sm.png)

# Description

A minecraft plugin that allows players to create and manage their own clan.

## System requirements

This software runs on [Spigot](https://www.spigotmc.org/) and NMS.
Spigot forks without compiled NMS code are not supported.
Officially supported servers are [spigot](https://www.spigotmc.org/) and [paper](https://papermc.io/) and [folia](https://github.com/PaperMC/Folia/).
It is required to use [**Java 11**](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or
newer.

## Main features

- A lot of options for players to manage and control their clan.
- Automatically updating files if there is a new update.
- Configable messages, gui, etc..
- Supporting API.
- Supporting GUI
- Supporting Hex Color
- Supporting BossBar
- Supporting Floodgate (GeyserMC)
- Easily managing plugin database

## Soft-depend plugins

You might need these plugins to utilize my plugin resources totally.

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
    - To get clan information:
        - **%clanplus_clan_name%** - Get clan name
        - **%clanplus_clan_customname%** - Get clan custom name
        - **%clanplus_clan_formatname%** - If clan has a custom name, it will display clan custom name. Otherwise, it
          will display clan name.
        - **%clanplus_clan_owner%** - Get clan owner
        - **%clanplus_clan_message%** - Get clan message
        - **%clanplus_clan_score%** - Get clan score
        - **%clanplus_clan_warpoint%** - Get clan warpoint
        - **%clanplus_clan_warning%** - Get clan warning
        - **%clanplus_clan_maxmembers%** - Get clan max members
        - **%clanplus_clan_createddate%** - Get clan created date as milliseconds
        - **%clanplus_clan_format_createddate%** - Get clan created date (mm/dd/yyyy)
        - **%clanplus_clan_members%** - Get clan members
        - **%clanplus_clan_allies%** - Get clan allies
        - **%clanplus_clan_skilllevel_<skillid>%** - Get clan Skill ID level
        - **%clanplus_clan_subjectpermission_<subject>%** - Get the required rank to utilize the subject
        - **%clanplus_clan_format_subjectpermission_<subject>%** - Get the required rank to utilize the subject (
          Formatted)
        - **%clanplus_clan_discordchannelid%** - Get clan discord channel ID
        - **%clanplus_clan_discordjoinlink%** - Get clan discord join link
    - To get player information:
        - **%clanplus_player_rank%** - Get player rank
        - **%clanplus_player_format_rank%** - Get player rank (Formatted)
        - **%clanplus_player_joindate%** - Get player join date as milliseconds
        - **%clanplus_player_format_joindate%** - Get player join date (mm/dd/yyyy)
        - **%clanplus_player_scorecollected%** - Get player score collected
        - **%clanplus_player_lastactivated%** - Get player last activated as milliseconds
        - **%clanplus_player_format_lastactivated%** - Get player last activated (mm/dd/yyyy)

- [PlayerPoints](https://www.spigotmc.org/resources/playerpoints.80745/)
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- [VaultUnlocked](https://www.spigotmc.org/resources/vaultunlocked.117277/) - For Folia Servers - A replacement for Vault

## API Usage

Setting up maven:

```maven
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```maven
	<dependency>
	    <groupId>com.github.CortezRomeo</groupId>
	    <artifactId>ClansPlus</artifactId>
	    <version><VERSION></version>
	</dependency>
```

Checking if ClanPlus in on the server:

```java

@Override
public void onEnable() {
    if (Bukkit.getPluginManager().getPlugin("ClanPlus") == null) {
        getLogger().severe("ClanPlus is not in the server!");
        Bukkit.getPluginManager().disablePlugin(this);
        return;
    }
}
```

Initializing ClanPlus's API:

```java
ClanPlus clanPlusAPI = Bukkit.getServicesManager().getRegistration(ClanPlus.class).getProvider();
```

Example of using the API:

```java
        // Initialize plugin API.
ClanPlus clanPlusAPI = Bukkit.getServicesManager().getRegistration(ClanPlus.class).getProvider();

// Get clan name and clan data.
String clanName = "HelloClan";

// If clan does not exist, create a new clan.
        if(!clanPlusAPI.

getPluginDataManager().

getClanDatabase().

containsKey(clanName))
        clanPlusAPI.

getPluginDataManager().

loadClanDatabase(clanName);

IClanData clanData = clanPlusAPI.getPluginDataManager().getClanDatabase(clanName);

// Add a player to a clan.
String playerName = "Cortez_Romeo";
        clanPlusAPI.

getClanManager().

addPlayerToAClan(playerName, clanName, false);

// Promote this player to leader of the clan.
        clanPlusAPI.

getPluginDataManager().

getPlayerDatabase(playerName).

setRank(Rank.LEADER);
        clanData.

setOwner(playerName);

// Adjust clan's database
        clanData.

setMessage("This is the first message of this clan!");
        clanData.

setCustomName("&bSuper Clan");

// One of the stuffs you can do with clan manager.
        clanPlusAPI.

getClanManager().

alertClan(clanName, "Have a good day!");

// Save database
        clanPlusAPI.

getPluginDataManager().

saveClanDatabaseToStorage(clanName, clanData);
```

## Contact

[![Discord Server](https://discord.com/api/guilds/1187827789664096267/widget.png?style=banner3)](https://discord.gg/XdJfN2X)

## 3rd party libraries

- [JetBrains Java Annotations](https://mvnrepository.com/artifact/org.jetbrains/annotations)
- [ConfigUpdater](https://github.com/tchristofferson/Config-Updater)
- [XSeries](https://github.com/CryptoMorin/XSeries)
- [NBTEditor](https://github.com/BananaPuncher714/NBTEditor)
- [H2](https://h2database.com/html/main.html)
