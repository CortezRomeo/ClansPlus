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
    public static String PREFIX;
    public static String NO_PERMISSION;
    public static String NON_CONSOLE_COMMAND;
    public static String COMMAND_CONFIRMATION;
    public static String MUST_BE_IN_CLAN;
    public static String TARGET_DOES_NOT_EXIST;
    public static String TARGET_MUST_BE_IN_CLAN;
    public static String TARGET_CLAN_MEMBERSHIP_ERROR;
    public static String REQUIRED_RANK;
    public static String INVITATION_REJECTION;
    public static String ALREADY_IN_CLAN;
    public static String CLAN_NO_LONGER_EXIST;
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
    public static String LAST_PAGE;
    public static List<String> COMMAND_CLANPLUS_MESSAGES_NON_CLAN;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS_PLACEHOLDER_COMMAND;
    public static String USING_CHAT_BOX_CREATE_CLAN;
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
        PREFIX = fileConfiguration.getString("messages.prefix");
        NO_PERMISSION = fileConfiguration.getString("messages.no-permission");
        NON_CONSOLE_COMMAND = fileConfiguration.getString("messages.non-console-command");
        COMMAND_CONFIRMATION = fileConfiguration.getString("messages.command-confirmation");
        MUST_BE_IN_CLAN = fileConfiguration.getString("messages.must-be-in-clan");
        TARGET_DOES_NOT_EXIST = fileConfiguration.getString("messages.target-does-not-exist");
        TARGET_MUST_BE_IN_CLAN = fileConfiguration.getString("messages.target-must-be-in-clan");
        TARGET_CLAN_MEMBERSHIP_ERROR = fileConfiguration.getString("messages.target-clan-membership-error");
        REQUIRED_RANK = fileConfiguration.getString("messages.required-rank");
        INVITATION_REJECTION = fileConfiguration.getString("messages.invitation-rejection");
        ALREADY_IN_CLAN = fileConfiguration.getString("messages.already-in-clan");
        CLAN_NO_LONGER_EXIST = fileConfiguration.getString("messages.clan-no-longer-exist");
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
        LAST_PAGE = fileConfiguration.getString("messages.last-page");
        for (Subject subject : Subject.values())
            for (String subjectDescription : fileConfiguration.getConfigurationSection("subject-description").getKeys(false))
                if (subjectDescription.equalsIgnoreCase(subject.toString()))
                    subject.setDescription(fileConfiguration.getString("subject-description." + subject.toString().toLowerCase()));
        COMMAND_CLANPLUS_MESSAGES_NON_CLAN = fileConfiguration.getStringList("messages.commands.clanplus.messages.non-clan-commands");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.memberCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.managerCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.leaderCommands.messages");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.memberCommands.placeholder.command");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.managerCommands.placeholder.command");
        COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS_PLACEHOLDER_COMMAND = fileConfiguration.getString("messages.commands.clanplus.messages.in-clan-commands.placeholder.leaderCommands.placeholder.command");
        USING_CHAT_BOX_CREATE_CLAN = fileConfiguration.getString("messages.using-chat-box.create-clan");
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

        MessageUtil.debug("LOADING MESSAGES", "Loaded message file name: " + locale + ".");
    }

}
