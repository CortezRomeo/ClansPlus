![logo](https://i.imgur.com/9t2WM27.png)

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
- Clan storages
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
        - **%clanplus_player_format_lastactivated%** - Get player last activated (mm/dd/yyyy
  	- To get top clans:
  		- **%clanplus_top_score_name_<number (start from 1)>%** - Get the name of clan score top #<number>
	  	- **%clanplus_top_score_value_<number (start from 1)>%** - Get the value of clan score top #<number>

- [PlayerPoints](https://www.spigotmc.org/resources/playerpoints.80745/)
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- [VaultUnlocked](https://www.spigotmc.org/resources/vaultunlocked.117277/) - For Folia Servers - A replacement for Vault
> [!CAUTION]
> If your server software is folia, using vault will cause error. I would recommend to use VaultUnlocked to replace Vault and use BetterEconomy to replace any economy plugin.

## Commands & subcommands & permissions
- /clansplus
    - The main clan command for all players to use
        - create
        - accept
        - reject
        - leave
        - spawn
        - list
        - menu
        - chat
        - pvp
        - event
        - info
        - setting
        - upgrade
        - invite
        - kick
        - setspawn `clanplus.setspawn`
        - seticon `clanplus.seticon`
        - setpermission `clanplus.setpermission`
        - setowner
        - setmanager
        - removemanager
        - requestally
        - setcustomname `clanplus.setcustomname`
        - setmessage `clanplus.setmessage`
        - disband
> [!IMPORTANT]  
> Each clan will have its own permission for its members to use. For instance, owners of clans can allow their managers to set clan's message but can also allow their members to invite or kick somebody.

> [!NOTE]  
> Only owners can disband their clans and set permission for their own clans. 
- /clansplusadmin `clanplus.admin`
    - For the administrators to use to adjust clans stats or reload plugin.
        - reload
        - transferPluginDatabaseType
        - setClanData
        - setClanSkillData
        - setPlayerData
        - openClanStorage
        - event
        - backup
        - delete
        - chatspy
        - clanresetall
## Update history
<details>
<summary>2.4</summary>

	- Fixed: War Event still starts after being disabled in config.
	- Fixed: "Icon Invalid" message always appears when setting icon using custom head.
	- Fixed: English language file not reloading after plugin reload.
	- Fixed: Players using sign to search in inventory turn the block they're in into air.
	- Fixed: Console spam when transferring database to H2.
	- Fixed: Error retrieving player database causing multiple database issues.
	- Added: Open Storage to Subject.
	- Added: Max Storage to Clan Data.
	- Added: Open Clan Storage to Clan Manager.
	- Added: Sub-command openClanStorage to clanadmin command.
	- Added: Clan Storage List inventory.
	- Added: Clan Storage inventory.
	- Added: Messages: invalid-location, invalid-number, storage-locked, storage-number-exceed-limit, clan-broadcast-upgrade-max-storages.
	- Added: max-storage-default to config.yml.
	- Added: clan-creation-broadcast to config.yml that appears randomly after creating a clan.
	- Added: storage-settings to config.yml.
	- Altered: Set Permission Inventory changed from regular inventory to paginated inventory.
	- Optimized: Setting up inventory files.
	- Optimized: Inventory handling menu code.
</details>
<details>
<summary>2.3</summary>

    - Fixed: Fixed an error related to sign input.
    - Fixed: Fixed an error that causes the sort item in add member inventory to be loaded.
    - Added: Added a setting in config.yml to disable clan spawn.
</details>
<details>
<summary>2.2</summary>

    - Support 1.21.6, and 1.21.7 (These 2 newest version are very unstable and may occur errors)
    - Fixed: Fixed an error pops up whenever an unknown/unsuporrted entity fired at players.
    - Fixed: Sort Item missing in add ally inventory config file led to occur an error to load the item.
    - Optimization: Item type
</details>
<details>
<summary>2.1</summary>

    - Fixed: Fixed miscalculating the length of custom name when player use gradient.
    - Fixed: Fixed clanadmin command cannot use space to set clan's custom name and message.
</details>
<details>
<summary>2.0</summary>

    - Fixed: Console spam when shutting down the server.
    - Fixed: Bossbar for clan war events not showing to players who just joined the server.
    - Fixed: Clan war score history was retained even when players left the clan or the clan was deleted. This fix also resolves related bugs such as data errors and score bugs.
    - Fixed: Items in the icon type material menu not displaying properly and causing console spam. Unavailable items will now display as "N/A" and can be customized in the inventory config.
    - Fixed: Console spam when players use END_CRYSTAL.
	- Optimization: Switched to Paper's async chat system to optimize features using chat boxes.
    - Feature Added: Option to toggle join notifications, including clan war and player clan announcements.
    - Feature Added: Configurable delay for the two notifications above when a player joins the server.
    - Command Added: /clanadmin clanresetall (type) to reset all clan stats including score, war points, and warnings.
    - Behavior Change: Due to Folia API limitations, chat box input for inventory list searches has been replaced with a sign-based input method and player timeout.
    - Localization: Prepared "inventory" data for setup, but not yet implemented due to complexity (update time is uncertain 😦).
    - Optimization: Further code refactoring and cleanup in several areas.
    - Hot fix 1: Fixed sign input does not work properly
    - Hot fix 2: Fixed event to handle player shooting not working properly
</details>
<details>
<summary>1.9</summary>

	- Fixed: Players using the default clan name can still use spaces and the & character.
    - Fixed: Several issues that caused excessive console spam.
    - Fixed: Menu errors in the latest Minecraft server versions.
    - Fixed: Server crashes or lag caused by using Discord webhooks.
    - Fixed: Clan members (non-leaders) were able to upgrade clan slots.
    - Fixed: /clan deny could not be used to reject invitations.
    - Optimization: Refactored and optimized code in several areas.
    - Localization: Translated some parts of the plugin/interface to English.
</details>
<details>
<summary>1.8</summary>

    - Support for Folia (Folia on PaperMC) (Related PR) (Thanks to @TypicalShavonne)
    - Restored support for some particles
    - Utilized async teleportation, which makes teleporting smoother and reduces server lag
    - Added English (en) language file
    - Support for SuperVanish and other vanish plugins: Players who are vanished will no longer appear in the invite list and will be shown as OFFLINE in the member list if they are vanished. Added an option in config.yml to toggle this feature (vanish-settings)
    - Fixed: Stats from previous clan wars no longer carry over into new clan wars
    - Fixed: Players using ender pearls or other throwable items (except arrows) could previously damage clan members — this is now resolved
    - Fixed: A display bug where transferring manager role incorrectly said "leader role transferred"
    - Fixed: The %by% placeholder not working when transferring the manager role
</details>
<details>
<summary>1.7</summary>

    - Fixed the %rank% placeholder not working when upgrading clans
    - Added /clan upgrade point button in the upgrade interface
    - Added a config option to disable using chat boxes with a keyword
    - Fixed PlaceholderAPI being spammed: https://mclo.gs/MZzRTyy
    - Fixed error: https://mclo.gs/Z6LN5Ka (from @tepriu)
    - Fixed issue where creating a clan did not cost money
    - Fixed several other minor bugs
</details>
<details>
<summary>1.6</summary>

    - Added clanplus.setspawn permission to allow setting the clan spawn.
    - Added a blacklist for worlds when setting spawn (configurable in the config file).
    - Players will not be able to set spawn in worlds that are blacklisted.
    - If a player had set a spawn in a blacklisted world before this update, they will not be able to teleport to that world.
    - Fixed a bug where players could shoot and damage members of their own clan or allied clans with a bow, even if PvP was disabled for them (Thanks to @henshino)
    - Added PlaceholderAPI support for clan ranking placeholders:
    - %clanplus_top_score_name_<top>% – Gets the name of the clan in the top rank.
    - %clanplus_top_score_value_<top>% – Gets the score of the clan in the top rank.
</details>
<details>
<summary>1.5</summary>

    - No longer supporting DiscordSRV for sending messages on Discord; instead, Discord Webhook will be used to send messages.
</details>
<details>
<summary>1.4</summary>

	- Fixed an error that causes money is not given for floodgate player
    - Fixed the issue where faction war did not grant points for mobs and MythicMobs.
    - Fixed the issue where the console was spammed, and the faction war event could not be ended using a command or when the time ran out (Error log) (Reported by @henshino).
    - Fixed the issue where the console was spammed during combat when a faction war event was active (Error log) (Reported by @henshino).
    - Added support for all the latest Minecraft versions and updated the API.
    - Added new permissions:
    - clanplus.setpermission - Customize faction permissions.
    - clanplus.setmessage - Customize faction messages.
    - clanplus.seticon - Customize faction icons.
    - clanplus.setcustomname - Customize faction custom names.
</details>

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
    if (Bukkit.getPluginManager().getPlugin("ClansPlus") == null) {
        getLogger().severe("ClansPlus is not in the server!");
        Bukkit.getPluginManager().disablePlugin(this);
        return;
    }
}
```

Initializing ClansPlus's API:

```java
ClanPlus clansPlusAPI = Bukkit.getServicesManager().getRegistration(ClanPlus.class).getProvider();
```

Example of using the API:

```java
// Initialize plugin API.
ClanPlus clansPlusAPI = Bukkit.getServicesManager().getRegistration(ClanPlus.class).getProvider();

// Get clan name and clan data.
String clanName = "HelloClan";

// If clan does not exist, create a new clan.
if(!clansPlusAPI.getPluginDataManager().getClanDatabase().containsKey(clanName))
        clansPlusAPI.getPluginDataManager().

loadClanDatabase(clanName);

IClanData clanData = clanPlusAPI.getPluginDataManager().getClanDatabase(clanName);

// Add a player to a clan.
String playerName = "Cortez_Romeo";
clansPlusAPI.getClanManager().addPlayerToAClan(playerName, clanName, false);

// Promote this player to leader of the clan.
clansPlusAPI.getPluginDataManager().getPlayerDatabase(playerName).setRank(Rank.LEADER);
clanData.setOwner(playerName);

// Adjust clan's database
clanData.setMessage("This is the first message of this clan!");
clanData.setCustomName("&bSuper Clan");

// One of the stuffs you can do with clan manager.
clansPlusAPI.getClanManager().alertClan(clanName, "Have a good day!");

// Save database
clansPlusAPI.getPluginDataManager().saveClanDatabaseToStorage(clanName, clanData);
```

## Contact

[![Discord Server](https://discord.com/api/guilds/1187827789664096267/widget.png?style=banner3)](https://discord.gg/NWbTVddmBM)

## 3rd party libraries

- [JetBrains Java Annotations](https://mvnrepository.com/artifact/org.jetbrains/annotations)
- [ConfigUpdater](https://github.com/tchristofferson/Config-Updater)
- [XSeries](https://github.com/CryptoMorin/XSeries)
- [NBTEditor](https://github.com/BananaPuncher714/NBTEditor)
- [H2](https://h2database.com/html/main.html)
