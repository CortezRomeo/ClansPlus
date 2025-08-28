package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.Chat;
import com.cortezromeo.clansplus.clan.subject.Create;
import com.cortezromeo.clansplus.clan.subject.SetCustomName;
import com.cortezromeo.clansplus.clan.subject.SetMessage;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatListenerHandler {

    public static List<Player> createClan = new ArrayList<>();
    public static List<Player> setCustomName = new ArrayList<>();
    public static List<Player> setMessage = new ArrayList<>();

    /*
    handle player chat
    */
    public static boolean handlePlayerChat(Player player, String message) {
        // cancel using chat interacting
        if (createClan.contains(player) || setCustomName.contains(player) || setMessage.contains(player))
            if (message.equals(Settings.CHAT_SETTING_STOP_USING_CHAT_WORD)) {
                createClan.remove(player);
                setCustomName.remove(player);
                setMessage.remove(player);
                MessageUtil.sendMessage(player, Messages.USING_CHAT_BOX_CANCEL_USING_CHAT_BOX_SUCCESS);
                return true;
            }

        if (createClan.contains(player)) {
            createClan.remove(player);
            new Create(player, player.getName(), message).execute();
            return true;
        }

        if (setCustomName.contains(player)) {
            setCustomName.remove(player);
            new SetCustomName(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETCUSTOMNAME), player, player.getName(), message).execute();
            return true;

        }

        if (setMessage.contains(player)) {
            setMessage.remove(player);
            new SetMessage(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETMESSAGE), player, player.getName(), message).execute();
            return true;
        }

        if (ClanManager.getPlayerUsingClanChat().contains(player)) {
            if (!new Chat(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.CHAT), player, player.getName(), message).execute())
                ClanManager.getPlayerUsingClanChat().remove(player);
            return true;
        }
        return false;
    }

}
