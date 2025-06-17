package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.SetSpawn;
import com.cortezromeo.clansplus.file.inventory.ClanSettingsInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.listener.AsyncPlayerChatListener;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClanSettingsInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ClanSettingsInventoryFile.get();

    public ClanSettingsInventory(Player owner) {
        super(owner);
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        return ClansPlus.nms.addColor(title);
    }

    @Override
    public int getSlots() {
        int rows = fileConfiguration.getInt("rows") * 9;
        if (rows < 27 || rows > 54)
            return 54;
        return rows;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);
        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("setIcon"))
            new SetIconMenuInventory(getOwner()).open();
        if (itemCustomData.equals("setCustomName")) {
            if (ClanManager.isPlayerRankSatisfied(getOwner().getName(), playerClanData.getSubjectPermission().get(Subject.SETCUSTOMNAME))) {
                getOwner().closeInventory();
                if (!AsyncPlayerChatListener.setCustomName.contains(getOwner()))
                    AsyncPlayerChatListener.setCustomName.add(getOwner());
                MessageUtil.sendMessage(getOwner(), Messages.USING_CHAT_BOX_SET_CUSTOM_NAME);
                MessageUtil.sendMessage(getOwner(), Messages.USING_CHAT_BOX_CANCEL_USING_CHAT_BOX.replace("%word%", Settings.CHAT_SETTING_STOP_USING_CHAT_WORD));
            }
        }
        if (itemCustomData.equals("setMessage")) {
            if (ClanManager.isPlayerRankSatisfied(getOwner().getName(), playerClanData.getSubjectPermission().get(Subject.SETMESSAGE))) {
                getOwner().closeInventory();
                if (!AsyncPlayerChatListener.setMessage.contains(getOwner()))
                    AsyncPlayerChatListener.setMessage.add(getOwner());
                MessageUtil.sendMessage(getOwner(), Messages.USING_CHAT_BOX_SET_MESSAGE);
                MessageUtil.sendMessage(getOwner(), Messages.USING_CHAT_BOX_CANCEL_USING_CHAT_BOX.replace("%word%", Settings.CHAT_SETTING_STOP_USING_CHAT_WORD));
            }
        }
        if (itemCustomData.equals("setSpawn"))
            new SetSpawn(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETSPAWN), getOwner(), getOwner().getName()).execute();
        if (itemCustomData.equals("setPermission")) {
            if (ClanManager.isPlayerRankSatisfied(getOwner().getName(), Rank.LEADER))
                new SetPermissionInventory(getOwner()).open();
        }
        if (itemCustomData.equals("setDiscord")) {
            // TODO set discord
            MessageUtil.sendMessage(getOwner(), Messages.FEATURE_IN_PROGRESS);
        }
        if (itemCustomData.equals("disband")) {
            new DisbandConfirmationInventory(getOwner()).open();
        }
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"), false);
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                    inventory.setItem(itemSlot, ClansPlus.nms.addCustomData(borderItem, "border"));
            }

            ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                    fileConfiguration.getString("items.close.value"),
                    fileConfiguration.getInt("items.close.customModelData"),
                    fileConfiguration.getString("items.close.name"),
                    fileConfiguration.getStringList("items.close.lore"), false), "close");
            int closeItemSlot = fileConfiguration.getInt("items.close.slot");
            inventory.setItem(closeItemSlot, closeItem);

            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            inventory.setItem(backItemSlot, backItem);

            ItemStack setIconItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setIcon.type"),
                    fileConfiguration.getString("items.setIcon.value"),
                    fileConfiguration.getInt("items.setIcon.customModelData"),
                    fileConfiguration.getString("items.setIcon.name"),
                    fileConfiguration.getStringList("items.setIcon.lore"), false), "setIcon");
            int setIconItemSlot = fileConfiguration.getInt("items.setIcon.slot");
            inventory.setItem(setIconItemSlot, setIconItem);

            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

            List<String> setCustomNameItemLore = new ArrayList<>();
            Rank setCustomNameRequiredRank = playerClanData.getSubjectPermission().get(Subject.SETCUSTOMNAME);
            for (String lore : fileConfiguration.getStringList("items.setCustomName.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), setCustomNameRequiredRank) ? fileConfiguration.getString("items.setCustomName.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setCustomName.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(setCustomNameRequiredRank)));
                setCustomNameItemLore.add(lore);
            }
            ItemStack setCustomNameItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setCustomName.type"),
                    fileConfiguration.getString("items.setCustomName.value"),
                    fileConfiguration.getInt("items.setCustomName.customModelData"),
                    fileConfiguration.getString("items.setCustomName.name"),
                    setCustomNameItemLore, false), "setCustomName");
            int setCustomNameItemSlot = fileConfiguration.getInt("items.setCustomName.slot");
            inventory.setItem(setCustomNameItemSlot, setCustomNameItem);

            List<String> setMessageItemLore = new ArrayList<>();
            Rank setMessageRequiredRank = playerClanData.getSubjectPermission().get(Subject.SETMESSAGE);
            for (String lore : fileConfiguration.getStringList("items.setMessage.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), setMessageRequiredRank) ? fileConfiguration.getString("items.setMessage.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setMessage.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(setMessageRequiredRank)));
                setMessageItemLore.add(lore);
            }
            ItemStack setMessageItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setMessage.type"),
                    fileConfiguration.getString("items.setMessage.value"),
                    fileConfiguration.getInt("items.setMessage.customModelData"),
                    fileConfiguration.getString("items.setMessage.name"),
                    setMessageItemLore, false), "setMessage");
            int setMessageItemSlot = fileConfiguration.getInt("items.setMessage.slot");
            inventory.setItem(setMessageItemSlot, setMessageItem);

            List<String> setSpawnItemLore = new ArrayList<>();
            Rank setSpawnRequiredRank = playerClanData.getSubjectPermission().get(Subject.SETSPAWN);
            for (String lore : fileConfiguration.getStringList("items.setSpawn.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), setSpawnRequiredRank) ? fileConfiguration.getString("items.setSpawn.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setSpawn.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(setSpawnRequiredRank)));
                setSpawnItemLore.add(lore);
            }
            ItemStack setSpawnItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setSpawn.type"),
                    fileConfiguration.getString("items.setSpawn.value"),
                    fileConfiguration.getInt("items.setSpawn.customModelData"),
                    fileConfiguration.getString("items.setSpawn.name"),
                    setSpawnItemLore, false), "setSpawn");
            int setSpawnItemSlot = fileConfiguration.getInt("items.setSpawn.slot");
            inventory.setItem(setSpawnItemSlot, setSpawnItem);

            List<String> setPermissionItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.setPermission.lore")) {
                for (Subject subject : Subject.values())
                    lore = lore.replace("%" + subject.toString().toLowerCase() + "_rank%", ClanManager.getFormatRank(playerClanData.getSubjectPermission().get(subject)));
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), Rank.LEADER) ? fileConfiguration.getString("items.setPermission.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setPermission.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(Rank.LEADER)));
                setPermissionItemLore.add(lore);
            }
            ItemStack setPermissionItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setPermission.type"),
                    fileConfiguration.getString("items.setPermission.value"),
                    fileConfiguration.getInt("items.setPermission.customModelData"),
                    fileConfiguration.getString("items.setPermission.name"),
                    setPermissionItemLore, false), "setPermission");
            int setPermissionItemSlot = fileConfiguration.getInt("items.setPermission.slot");
            inventory.setItem(setPermissionItemSlot, setPermissionItem);

            List<String> setDiscordItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.setDiscord.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), Rank.LEADER) ? fileConfiguration.getString("items.setDiscord.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setDiscord.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(Rank.LEADER)));
                setDiscordItemLore.add(lore);
            }
            ItemStack setDiscordItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setDiscord.type"),
                    fileConfiguration.getString("items.setDiscord.value"),
                    fileConfiguration.getInt("items.setDiscord.customModelData"),
                    fileConfiguration.getString("items.setDiscord.name"),
                    setDiscordItemLore, false), "setDiscord");
            int setDiscordItemSlot = fileConfiguration.getInt("items.setDiscord.slot");
            inventory.setItem(setDiscordItemSlot, setDiscordItem);

            List<String> disbandItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.disband.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), Rank.LEADER) ? fileConfiguration.getString("items.disband.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.disband.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(Rank.LEADER)));
                disbandItemLore.add(lore);
            }
            ItemStack disbandItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.disband.type"),
                    fileConfiguration.getString("items.disband.value"),
                    fileConfiguration.getInt("items.disband.customModelData"),
                    fileConfiguration.getString("items.disband.name"),
                    disbandItemLore, false), "disband");
            int disbandItemSlot = fileConfiguration.getInt("items.disband.slot");
            inventory.setItem(disbandItemSlot, disbandItem);
        });
    }

}
