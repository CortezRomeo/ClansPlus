package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.Kick;
import com.cortezromeo.clansplus.file.inventory.ManageMembersInventoryFile;
import com.cortezromeo.clansplus.file.inventory.MembersMenuInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManageMembersInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ManageMembersInventoryFile.get();
    private String playerName;

    public ManageMembersInventory(Player owner, String playerName) {
        super(owner);
        this.playerName = playerName;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabaseByPlayerName(playerName) != null)
            super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title").replace("%playerName%", playerName);
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

        // how can they get here?
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null || PluginDataManager.getClanDatabaseByPlayerName(playerName) == null) {
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new MemberListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName())).open();
        if (itemCustomData.contains("manageMembersRank=")) {
            itemCustomData = itemCustomData.replace("manageMembersRank=", "");
            new ManageMembersRankInventory(getOwner(), itemCustomData).open();
        }
        if (itemCustomData.contains("kick=")) {
            itemCustomData = itemCustomData.replace("kick=", "");
            if (new Kick(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.KICK), getOwner(), getOwner().getName(), Bukkit.getPlayer(itemCustomData), itemCustomData).execute())
                new MemberListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName())).open();
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {

            if (fileConfiguration.getBoolean("items.border.enabled")) {
                ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                        fileConfiguration.getString("items.border.value"),
                        fileConfiguration.getInt("items.border.customModelData"),
                        fileConfiguration.getString("items.border.name"),
                        fileConfiguration.getStringList("items.border.lore"));
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                    inventory.setItem(itemSlot, borderItem);
            }

            ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                    fileConfiguration.getString("items.close.value"),
                    fileConfiguration.getInt("items.close.customModelData"),
                    fileConfiguration.getString("items.close.name"),
                    fileConfiguration.getStringList("items.close.lore")), "closeItem");
            int closeItemSlot = fileConfiguration.getInt("items.close.slot");
            inventory.setItem(closeItemSlot, closeItem);

            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore")), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            inventory.setItem(backItemSlot, backItem);

            ItemStack memberItem = getPlayerItemStack(ItemUtil.getItem("playerhead",
                    playerName,
                    fileConfiguration.getInt("items.member.customModelData"),
                    fileConfiguration.getString("items.member.name"),
                    fileConfiguration.getStringList("items.member.lore")), playerName);
            int memberItemSlot = fileConfiguration.getInt("items.member.slot");
            inventory.setItem(memberItemSlot, memberItem);

            ItemStack manageMembersRankItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.manageMembersRank.type"),
                    fileConfiguration.getString("items.manageMembersRank.value"),
                    fileConfiguration.getInt("items.manageMembersRank.customModelData"),
                    fileConfiguration.getString("items.manageMembersRank.name"),
                    fileConfiguration.getStringList("items.manageMembersRank.lore")), "manageMembersRank=" + playerName);
            int manageMembersRankItemSlot = fileConfiguration.getInt("items.manageMembersRank.slot");
            inventory.setItem(manageMembersRankItemSlot, manageMembersRankItem);

            List<String> kicmMemberItemLore = new ArrayList<>();
            Rank kickMemberRequiredRank = PluginDataManager.getClanDatabaseByPlayerName(playerName).getSubjectPermission().get(Subject.KICK);
            for (String lore : fileConfiguration.getStringList("items.kickMember.lore")) {
                lore = lore.replace("%playerName%", playerName);
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), kickMemberRequiredRank) ? fileConfiguration.getString("items.kickMember.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.kickMember.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(kickMemberRequiredRank)));
                kicmMemberItemLore.add(lore);
            }
            ItemStack kickMemberItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.kickMember.type"),
                    fileConfiguration.getString("items.kickMember.value"),
                    fileConfiguration.getInt("items.kickMember.customModelData"),
                    fileConfiguration.getString("items.kickMember.name"),
                    kicmMemberItemLore), "kick=" + playerName);
            int kickMemberItemSlot = fileConfiguration.getInt("items.kickMember.slot");
            inventory.setItem(kickMemberItemSlot, kickMemberItem);

        });
    }

    private @NotNull ItemStack getPlayerItemStack(ItemStack itemStack, String playerName) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = itemName.replace("%playerName%", playerName);
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%playerName%", playerName)
                        .replace("%playerUUID%", playerData.getUUID())
                        .replace("%rank%", ClanManager.getFormatRank(playerData.getRank()))
                        .replace("%joinDate%", StringUtil.dateTimeToDateFormat(playerData.getJoinDate()))
                        .replace("%onlineStatus%", (Bukkit.getPlayer(playerName) != null ? Messages.ONLINE_STATUS_ONLINE : Messages.ONLINE_STATUS_OFFLINE))
                        .replace("%lastActivated%", StringUtil.dateTimeToDateFormat(playerData.getLastActivated()))
                        .replace("%scoreCollected%", String.valueOf(playerData.getScoreCollected()))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

}
