package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.RemoveManager;
import com.cortezromeo.clansplus.clan.subject.SetManager;
import com.cortezromeo.clansplus.clan.subject.SetOwner;
import com.cortezromeo.clansplus.file.inventory.ManageMembersRankInventoryFile;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageMembersRankInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ManageMembersRankInventoryFile.get();
    private String playerName;

    public ManageMembersRankInventory(Player owner, String playerName) {
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
            new ManageMembersInventory(getOwner(), playerName).open();
        if (itemCustomData.contains("setOwner=")) {
            itemCustomData = itemCustomData.replace("setOwner=", "");
            new SetOwner(Rank.LEADER, getOwner(), getOwner().getName(), Bukkit.getPlayer(itemCustomData), itemCustomData).execute();
            super.open();
        }
        if (itemCustomData.contains("setManager=")) {
            itemCustomData = itemCustomData.replace("setManager=", "");
            new SetManager(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETMANAGER), getOwner(), getOwner().getName(), Bukkit.getPlayer(itemCustomData), itemCustomData).execute();
            super.open();
        }
        if (itemCustomData.contains("removeManager=")) {
            itemCustomData = itemCustomData.replace("removeManager=", "");
            new RemoveManager(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.REMOVEMANAGER), getOwner(), getOwner().getName(), Bukkit.getPlayer(itemCustomData), itemCustomData).execute();
            super.open();
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

            List<String> setOwnerItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.setOwner.lore")) {
                lore = lore.replace("%playerName%", playerName);
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), Rank.LEADER) ? fileConfiguration.getString("items.setOwner.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setOwner.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(Rank.LEADER)));
                setOwnerItemLore.add(lore);
            }
            ItemStack setOwnerItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setOwner.type"),
                    fileConfiguration.getString("items.setOwner.value"),
                    fileConfiguration.getInt("items.setOwner.customModelData"),
                    fileConfiguration.getString("items.setOwner.name"),
                    setOwnerItemLore), "setOwner=" + playerName);
            int setOwnerItemSlot = fileConfiguration.getInt("items.setOwner.slot");
            inventory.setItem(setOwnerItemSlot, setOwnerItem);

            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(playerName);
            boolean isPlayerAManager = (PluginDataManager.getPlayerDatabase(playerName).getRank() == Rank.MANAGER);
            String getPlayerRankPath = isPlayerAManager ? "isAManager" : "isAMember";
            HashMap<Subject, Rank> clanSubjectPermission = playerClanData.getSubjectPermission();
            List<String> setManagerItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.setManager.lore")) {
                lore = lore.replace("%playerName%", playerName);
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), (isPlayerAManager) ? clanSubjectPermission.get(Subject.REMOVEMANAGER) : playerClanData.getSubjectPermission().get(Subject.SETMANAGER)) ? fileConfiguration.getString("items.setManager." + getPlayerRankPath + ".placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.setManager" + getPlayerRankPath + "placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(clanSubjectPermission.get(isPlayerAManager ? Subject.REMOVEMANAGER : Subject.SETMANAGER))));
                setManagerItemLore.add(lore);
            }
            ItemStack setManagerItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.setManager." + getPlayerRankPath + ".type"),
                    fileConfiguration.getString("items.setManager." + getPlayerRankPath + ".value"),
                    fileConfiguration.getInt("items.setManager." + getPlayerRankPath + ".customModelData"),
                    fileConfiguration.getString("items.setManager." + getPlayerRankPath + ".name"),
                    setOwnerItemLore), (isPlayerAManager ? "removeManager=" : "setManager=") + playerName);
            int setManagerItemSlot = fileConfiguration.getInt("items.setManager.slot");
            inventory.setItem(setManagerItemSlot, setManagerItem);

        });
    }

}
