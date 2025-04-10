package com.cortezromeo.clansplus.language;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Messages {

    public static String RANK_DISPLAY_MEMBER;
    public static String RANK_DISPLAY_MANAGER;
    public static String RANK_DISPLAY_LEADER;
    public static String CURRENCY_DISPLAY_VAULT_SYMBOL;
    public static String CURRENCY_DISPLAY_VAULT_NAME;
    public static String CURRENCY_DISPLAY_PLAYERPOINTS_SYMBOL;
    public static String CURRENCY_DISPLAY_PLAYERPOINTS_NAME;
    public static String CURRENCY_DISPLAY_WARPOINT_SYMBOL;
    public static String CURRENCY_DISPLAY_WARPOINT_NAME;
    public static String STATUS_ENABLE;
    public static String STATUS_DISABLE;
    public static String ONLINE_STATUS_OFFLINE;
    public static String ONLINE_STATUS_ONLINE;
    public static String PREFIX;
    public static String UNKNOWN;
    public static String TIME_FORMAT_HHMMSS;
    public static String TIME_FORMAT_MMSS;
    public static String TIME_FORMAT_SS;
    public static List<String> CLAN_MESSAGE;
    public static String PERMISSION_REQUIRED;
    public static String NON_CONSOLE_COMMAND;
    public static String COMMAND_CONFIRMATION;
    public static String NOT_ENOUGH_CURRENCY;
    public static String ILLEGAL_MINIMUM_CLAN_LENGTH;
    public static String ILLEGAL_MAXIMUM_CLAN_LENGTH;
    public static String PROHIBITED_CLAN_NAME;
    public static String PROHIBITED_CHARACTER;
    public static String MUST_BE_IN_CLAN;
    public static String CLAN_CANNOT_BE_THE_SAME;
    public static String TARGET_DOES_NOT_EXIST;
    public static String TARGET_MUST_BE_IN_CLAN;
    public static String TARGET_CLAN_MEMBERSHIP_ERROR;
    public static String TARGET_CLAN_ALLY_MEMBERSHIP_ERROR;
    public static String REQUIRED_RANK;
    public static String INVITATION_REJECTION;
    public static String ALREADY_IN_CLAN;
    public static String CLAN_DOES_NOT_EXIST;
    public static String CLAN_IS_FULL;
    public static String JOIN_CLAN_SUCCESS;
    public static String CLAN_ALREADY_EXIST;
    public static String CREATE_CLAN_SUCCESS;
    public static String DISBAND_NOTIFICATION;
    public static String DISBAND_SUCCESS;
    public static String DISBAND_FAIL;
    public static String TARGET_ALREADY_IN_CLAN;
    public static String NOT_ENOUGH_MEMBER_SLOTS;
    public static String TARGET_INVITATION;
    public static String INVITATION_SUCCESS;
    public static String INCOMING_CLAN_INVITE;
    public static String INVITE_EXPIRED;
    public static String INVITER_INVITE_EXPIRED;
    public static String SELF_KICK_ERROR;
    public static String KICK_LEADER_ERROR;
    public static String TARGET_REMOVAL_SUCCESS;
    public static String KICKED_FROM_CLAN;
    public static String LEADER_CANNOT_LEAVE;
    public static String LEAVE_CLAN_SUCCESS;
    public static String REJECTED_CLAN_INVITE;
    public static String SET_CUSTOM_NAME_SUCCESS;
    public static String SET_MESSAGE_SUCCESS;
    public static String SELF_TARGETED_ERROR;
    public static String PROMOTE_TO_OWNER_SUCCESS;
    public static String PROMOTED_TO_OWNER;
    public static String ALREADY_A_MANAGER;
    public static String PROMOTE_TO_MANAGER_SUCCESS;
    public static String PROMOTED_TO_MANAGER;
    public static String NOT_A_MANAGER;
    public static String REMOVE_A_MANAGER_SUCCESS;
    public static String MANAGER_REMOVED;
    public static String ALREADY_AN_ALLY;
    public static String ALREADY_SENT_ALLY_INVITE;
    public static String SEND_ALLY_INVITE_SUCCESS;
    public static String ACCEPT_ALLY_INVITE_SUCCESS;
    public static String REJECT_ALLY_INVITE_SUCCESS;
    public static String REMOVE_ALLY_SUCCESS;
    public static String ILLEGALLY_UPGRADE_SKILL;
    public static String INVALID_ICON_VALUE;
    public static String SET_ICON_SUCCESS;
    public static String SET_SPAWN_SUCCESS;
    public static String UNKNOWN_SPAWN_POINT;
    public static String SPAWN_POINT_COUNT_DOWN;
    public static String MOVE_WHILE_SPAWNING;
    public static String SPAWN_SUCCESS;
    public static String CHANGE_PERMISSION;
    public static String INVALID_ICON_TYPE;
    public static String TOGGLE_CLAN_CHAT_ON;
    public static String TOGGLE_CLAN_CHAT_OFF;
    public static String TOGGLE_CLAN_PVP_ON;
    public static String TOGGLE_CLAN_PVP_OFF;
    public static String CLAN_MEMBER_PVP_DENY;
    public static String CLAN_MEMBER_PVP_DENY_VICTIM;
    public static String CLAN_SET_SPAWN_BLACK_LIST_WORLD;
    public static String CLAN_SPAWN_BLACK_LIST_WORLD;
    public static String LAST_PAGE;
    public static String FEATURE_IN_PROGRESS;
    public static String USING_CHAT_BOX_CLAN_CHAT;
    public static String USING_CHAT_BOX_CREATE_CLAN;
    public static String USING_CHAT_BOX_SET_CUSTOM_NAME;
    public static String USING_CHAT_BOX_SET_MESSAGE;
    public static String USING_CHAT_BOX_INVENTORY_LIST_SEARCH;
    public static List<String> COMMAND_CLANPLUS_MESSAGES_NON_CLAN;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String CLAN_BROADCAST_PREFIX;
    public static String CLAN_BROADCAST_INVITE_NOTIFICATION;
    public static String CLAN_BROADCAST_PLAYER_JOIN_CLAN;
    public static String CLAN_BROADCAST_PLAYER_REMOVED_FROM_CLAN;
    public static String CLAN_BROADCAST_PLAYER_LEAVE_CLAN;
    public static String CLAN_BROADCAST_PLAYER_REJECT_TO_JOIN;
    public static String CLAN_BROADCAST_SET_CUSTOM_NAME;
    public static String CLAN_BROADCAST_SET_MESSAGE;
    public static String CLAN_BROADCAST_MEMBER_PROMOTED_TO_OWNER;
    public static String CLAN_BROADCAST_MEMBER_PROMOTED_TO_MANAGER;
    public static String CLAN_BROADCAST_MANAGER_REMOVED;
    public static String CLAN_BROADCAST_RECEIVE_ALLY_INVITE;
    public static String CLAN_BROADCAST_NEW_ALLY_NOTIFICATION;
    public static String CLAN_BROADCAST_REMOVE_ALLY_NOTIFICATION;
    public static String CLAN_BROADCAST_ALLY_REMOVED_NOTIFICATION;
    public static String CLAN_BROADCAST_UPGRADE_MAX_MEMBERS;
    public static String CLAN_BROADCAST_UPGRADE_PLUGIN_SKILL;
    public static String CLAN_BROADCAST_SET_ICON;
    public static String CLAN_BROADCAST_SET_SPAWN;
    public static String CLAN_BROADCAST_CHANGE_PERMISSION;

    public static void setupValue(String locale) {
        locale = locale.toLowerCase();
        File messageFile = new File(ClansPlus.plugin.getDataFolder() + "/languages/language_" + locale + ".yml");
        FileConfiguration fileConfiguration;
        if (!messageFile.exists()) {
            fileConfiguration = Vietnamese.get();
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4ERROR");
            MessageUtil.log("&eLocale &c&l" + locale + "&r&e does not exist!");
            MessageUtil.log("&ePlease check it again in config.yml.");
            MessageUtil.log("&eMessages will automatically be loaded using &b&lVietnamese&e.");
            MessageUtil.log("&c--------------------------------------");
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(messageFile);
        }

        RANK_DISPLAY_MEMBER = fileConfiguration.getString("rank-display.MEMBER");
        RANK_DISPLAY_MANAGER = fileConfiguration.getString("rank-display.MANAGER");
        RANK_DISPLAY_LEADER = fileConfiguration.getString("rank-display.LEADER");
        CURRENCY_DISPLAY_VAULT_SYMBOL = fileConfiguration.getString("currency-display.VAULT.symbol");
        CURRENCY_DISPLAY_VAULT_NAME = fileConfiguration.getString("currency-display.VAULT.name");
        CURRENCY_DISPLAY_PLAYERPOINTS_SYMBOL = fileConfiguration.getString("currency-display.PLAYERPOINTS.symbol");
        CURRENCY_DISPLAY_PLAYERPOINTS_NAME = fileConfiguration.getString("currency-display.PLAYERPOINTS.name");
        CURRENCY_DISPLAY_WARPOINT_SYMBOL = fileConfiguration.getString("currency-display.WARPOINT.symbol");
        CURRENCY_DISPLAY_WARPOINT_NAME = fileConfiguration.getString("currency-display.WARPOINT.name");
        STATUS_ENABLE = fileConfiguration.getString("status.ENABLE");
        STATUS_DISABLE = fileConfiguration.getString("status.DISABLE");
        ONLINE_STATUS_OFFLINE = fileConfiguration.getString("online-status.offline");
        ONLINE_STATUS_ONLINE = fileConfiguration.getString("online-status.online");
        PREFIX = fileConfiguration.getString("messages.prefix");
        UNKNOWN = fileConfiguration.getString("unknown");
        TIME_FORMAT_HHMMSS = fileConfiguration.getString("time-format.hh-mm-ss");
        TIME_FORMAT_MMSS = fileConfiguration.getString("time-format.mm-ss");
        TIME_FORMAT_SS = fileConfiguration.getString("time-format.ss");
        CLAN_MESSAGE = fileConfiguration.getStringList("messages.clan-message");
        PERMISSION_REQUIRED = fileConfiguration.getString("messages.permission-required");
        NON_CONSOLE_COMMAND = fileConfiguration.getString("messages.non-console-command");
        COMMAND_CONFIRMATION = fileConfiguration.getString("messages.command-confirmation");
        NOT_ENOUGH_CURRENCY = fileConfiguration.getString("messages.not-enough-currency");
        ILLEGAL_MINIMUM_CLAN_LENGTH = fileConfiguration.getString("messages.illegal-minimum-clan-name-length");
        ILLEGAL_MAXIMUM_CLAN_LENGTH = fileConfiguration.getString("messages.illegal-maximum-clan-name-length");
        PROHIBITED_CLAN_NAME = fileConfiguration.getString("messages.prohibited-clan-name");
        PROHIBITED_CHARACTER = fileConfiguration.getString("messages.prohibited-character");
        MUST_BE_IN_CLAN = fileConfiguration.getString("messages.must-be-in-clan");
        CLAN_CANNOT_BE_THE_SAME = fileConfiguration.getString("messages.clan-cannot-be-the-same");
        TARGET_DOES_NOT_EXIST = fileConfiguration.getString("messages.target-does-not-exist");
        TARGET_MUST_BE_IN_CLAN = fileConfiguration.getString("messages.target-must-be-in-clan");
        TARGET_CLAN_MEMBERSHIP_ERROR = fileConfiguration.getString("messages.target-clan-membership-error");
        TARGET_CLAN_ALLY_MEMBERSHIP_ERROR = fileConfiguration.getString("messages.target-clan-ally-membership-error");
        REQUIRED_RANK = fileConfiguration.getString("messages.required-rank");
        INVITATION_REJECTION = fileConfiguration.getString("messages.invitation-rejection");
        ALREADY_IN_CLAN = fileConfiguration.getString("messages.already-in-clan");
        CLAN_DOES_NOT_EXIST = fileConfiguration.getString("messages.clan-does-not-exist");
        CLAN_IS_FULL = fileConfiguration.getString("messages.clan-is-full");
        JOIN_CLAN_SUCCESS = fileConfiguration.getString("messages.join-clan-success");
        CLAN_ALREADY_EXIST = fileConfiguration.getString("messages.clan-already-exist");
        CREATE_CLAN_SUCCESS = fileConfiguration.getString("messages.create-clan-success");
        DISBAND_NOTIFICATION = fileConfiguration.getString("messages.disband-notification");
        DISBAND_SUCCESS = fileConfiguration.getString("messages.disband-success");
        DISBAND_FAIL = fileConfiguration.getString("messages.disband-fail");
        TARGET_ALREADY_IN_CLAN = fileConfiguration.getString("messages.target-already-in-clan");
        NOT_ENOUGH_MEMBER_SLOTS = fileConfiguration.getString("messages.not-enough-member-slots");
        TARGET_INVITATION = fileConfiguration.getString("messages.target-invitation");
        INVITATION_SUCCESS = fileConfiguration.getString("messages.invitation-success");
        INCOMING_CLAN_INVITE = fileConfiguration.getString("messages.incoming-clan-invite");
        INVITE_EXPIRED = fileConfiguration.getString("messages.invite-expired");
        INVITER_INVITE_EXPIRED = fileConfiguration.getString("messages.inviter-invite-expired");
        SELF_KICK_ERROR = fileConfiguration.getString("messages.self-kick-error");
        KICK_LEADER_ERROR = fileConfiguration.getString("messages.kick-leader-error");
        TARGET_REMOVAL_SUCCESS = fileConfiguration.getString("messages.target-removal-success");
        KICKED_FROM_CLAN = fileConfiguration.getString("messages.kicked-from-clan");
        LEADER_CANNOT_LEAVE = fileConfiguration.getString("messages.leader-cannot-leave");
        LEAVE_CLAN_SUCCESS = fileConfiguration.getString("messages.leave-clan-success");
        REJECTED_CLAN_INVITE = fileConfiguration.getString("messages.rejected-clan-invite");
        SET_CUSTOM_NAME_SUCCESS = fileConfiguration.getString("messages.set-custom-name-success");
        SET_MESSAGE_SUCCESS = fileConfiguration.getString("messages.set-message-success");
        SELF_TARGETED_ERROR = fileConfiguration.getString("messages.self-targeted-error");
        PROMOTE_TO_OWNER_SUCCESS = fileConfiguration.getString("messages.promote-to-owner-success");
        PROMOTED_TO_OWNER = fileConfiguration.getString("messages.promoted-to-owner");
        ALREADY_A_MANAGER = fileConfiguration.getString("messages.already-a-manager");
        PROMOTE_TO_MANAGER_SUCCESS = fileConfiguration.getString("messages.promote-to-manager-success");
        PROMOTED_TO_MANAGER = fileConfiguration.getString("messages.promoted-to-manager");
        NOT_A_MANAGER = fileConfiguration.getString("messages.not-a-manager");
        REMOVE_A_MANAGER_SUCCESS = fileConfiguration.getString("messages.remove-a-manager-success");
        MANAGER_REMOVED = fileConfiguration.getString("messages.manager-removed");
        ALREADY_AN_ALLY = fileConfiguration.getString("messages.already-an-ally");
        ALREADY_SENT_ALLY_INVITE = fileConfiguration.getString("messages.already-sent-ally-invite");
        SEND_ALLY_INVITE_SUCCESS = fileConfiguration.getString("messages.send-ally-invite-success");
        ACCEPT_ALLY_INVITE_SUCCESS = fileConfiguration.getString("messages.accept-ally-invite-success");
        REJECT_ALLY_INVITE_SUCCESS = fileConfiguration.getString("messages.reject-ally-invite-success");
        REMOVE_ALLY_SUCCESS = fileConfiguration.getString("messages.remove-ally-success");
        ILLEGALLY_UPGRADE_SKILL = fileConfiguration.getString("messages.illegally-upgrade-skill");
        INVALID_ICON_VALUE = fileConfiguration.getString("messages.invalid-icon-value");
        SET_ICON_SUCCESS = fileConfiguration.getString("messages.set-icon-success");
        SET_SPAWN_SUCCESS = fileConfiguration.getString("messages.set-spawn-success");
        UNKNOWN_SPAWN_POINT = fileConfiguration.getString("messages.unknown-spawn-point");
        SPAWN_POINT_COUNT_DOWN = fileConfiguration.getString("messages.spawn-point-count-down");
        MOVE_WHILE_SPAWNING = fileConfiguration.getString("messages.move-while-spawning");
        SPAWN_SUCCESS = fileConfiguration.getString("messages.spawn-success");
        CHANGE_PERMISSION = fileConfiguration.getString("messages.change-permission");
        INVALID_ICON_TYPE = fileConfiguration.getString("messages.invalid-icon-type");
        TOGGLE_CLAN_CHAT_ON = fileConfiguration.getString("messages.toggle-clan-chat-on");
        TOGGLE_CLAN_CHAT_OFF = fileConfiguration.getString("messages.toggle-clan-chat-off");
        TOGGLE_CLAN_PVP_ON = fileConfiguration.getString("messages.toggle-clan-pvp-on");
        TOGGLE_CLAN_PVP_OFF = fileConfiguration.getString("messages.toggle-clan-pvp-off");
        CLAN_MEMBER_PVP_DENY = fileConfiguration.getString("messages.clan-member-pvp-deny");
        CLAN_MEMBER_PVP_DENY_VICTIM = fileConfiguration.getString("messages.clan-member-pvp-deny-victim");
        CLAN_SET_SPAWN_BLACK_LIST_WORLD = fileConfiguration.getString("messages.clan-set-spawn-black-list-world");
        CLAN_SPAWN_BLACK_LIST_WORLD = fileConfiguration.getString("messages.clan-spawn-black-list-world");
        LAST_PAGE = fileConfiguration.getString("messages.last-page");
        FEATURE_IN_PROGRESS = fileConfiguration.getString("messages.feature-in-progress");
        USING_CHAT_BOX_CLAN_CHAT = fileConfiguration.getString("messages.using-chat-box.clan-chat");
        USING_CHAT_BOX_CREATE_CLAN = fileConfiguration.getString("messages.using-chat-box.create-clan");
        USING_CHAT_BOX_SET_CUSTOM_NAME = fileConfiguration.getString("messages.using-chat-box.set-custom-name");
        USING_CHAT_BOX_SET_MESSAGE = fileConfiguration.getString("messages.using-chat-box.set-message");
        USING_CHAT_BOX_INVENTORY_LIST_SEARCH = fileConfiguration.getString("messages.using-chat-box.inventory-list-search");
        for (Subject subject : Subject.values()) {
            subject.setName(fileConfiguration.getString("subject.name." + subject));
            subject.setDescription(fileConfiguration.getString("subject.description." + subject));
        }
        COMMAND_CLANPLUS_MESSAGES_NON_CLAN = fileConfiguration.getStringList("messages.commands.clanplus.messages.non-clan-commands");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.memberCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.managerCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.leaderCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.memberCommands.placeholder.command");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.managerCommands.placeholder.command");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.leaderCommands.placeholder.command");
        CLAN_BROADCAST_PREFIX = fileConfiguration.getString("messages.clan-broadcast.prefix");
        CLAN_BROADCAST_INVITE_NOTIFICATION = fileConfiguration.getString("messages.clan-broadcast.invite-notification");
        CLAN_BROADCAST_PLAYER_JOIN_CLAN = fileConfiguration.getString("messages.clan-broadcast.player-join-clan");
        CLAN_BROADCAST_PLAYER_REMOVED_FROM_CLAN = fileConfiguration.getString("messages.clan-broadcast.player-removed-from-clan");
        CLAN_BROADCAST_PLAYER_LEAVE_CLAN = fileConfiguration.getString("messages.clan-broadcast.player-leave-clan");
        CLAN_BROADCAST_PLAYER_REJECT_TO_JOIN = fileConfiguration.getString("messages.clan-broadcast.player-reject-to-join");
        CLAN_BROADCAST_SET_CUSTOM_NAME = fileConfiguration.getString("messages.clan-broadcast.set-custom-name");
        CLAN_BROADCAST_SET_MESSAGE = fileConfiguration.getString("messages.clan-broadcast.set-message");
        CLAN_BROADCAST_MEMBER_PROMOTED_TO_OWNER = fileConfiguration.getString("messages.clan-broadcast.member-promoted-to-owner");
        CLAN_BROADCAST_MEMBER_PROMOTED_TO_MANAGER = fileConfiguration.getString("messages.clan-broadcast.member-promoted-to-manager");
        CLAN_BROADCAST_MANAGER_REMOVED = fileConfiguration.getString("messages.clan-broadcast.manager-removed");
        CLAN_BROADCAST_RECEIVE_ALLY_INVITE = fileConfiguration.getString("messages.clan-broadcast.receive-ally-invite");
        CLAN_BROADCAST_NEW_ALLY_NOTIFICATION = fileConfiguration.getString("messages.clan-broadcast.new-ally-notification");
        CLAN_BROADCAST_REMOVE_ALLY_NOTIFICATION = fileConfiguration.getString("messages.clan-broadcast.remove-ally-notification");
        CLAN_BROADCAST_ALLY_REMOVED_NOTIFICATION = fileConfiguration.getString("messages.clan-broadcast.ally-removed-notification");
        CLAN_BROADCAST_UPGRADE_MAX_MEMBERS = fileConfiguration.getString("messages.clan-broadcast.upgrade-max-members");
        CLAN_BROADCAST_UPGRADE_PLUGIN_SKILL = fileConfiguration.getString("messages.clan-broadcast.upgrade-plugin-skill");
        CLAN_BROADCAST_SET_ICON = fileConfiguration.getString("messages.clan-broadcast.set-icon");
        CLAN_BROADCAST_SET_SPAWN = fileConfiguration.getString("messages.clan-broadcast.set-spawn");
        CLAN_BROADCAST_CHANGE_PERMISSION = fileConfiguration.getString("messages.clan-broadcast.change-permission");

        MessageUtil.debug("LOADING MESSAGES", "Loaded message file name: " + locale + ".");
    }

}
