package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.RemoveAlly;
import com.cortezromeo.clansplus.file.inventory.ManageAllyInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ManageAllyInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ManageAllyInventoryFile.get();
    private String allyName;

    public ManageAllyInventory(Player owner, String allyName) {
        super(owner);
        this.allyName = allyName;
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
        title = title.replace("%formatClanName%", ClanManager.getFormatClanName(PluginDataManager.getClanDatabase(allyName)));
        title = title.replace("%clanName%", allyName);
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

        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

        // how can they get here?
        if (playerClanData == null || !PluginDataManager.getClanDatabase().containsKey(allyName)) {
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new AllyListInventory(getOwner(), playerClanData.getName()).open();
        if (itemCustomData.contains("remove=")) {
            itemCustomData = itemCustomData.replace("remove=", "");
            if (new RemoveAlly(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.MANAGEALLY), getOwner(), getOwner().getName(), itemCustomData).execute())
                new AllyListInventory(getOwner(), playerClanData.getName()).open();
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
                        fileConfiguration.getStringList("items.border.lore"), false);
                for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                    inventory.setItem(itemSlot, borderItem);
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

            IClanData allyClanData = PluginDataManager.getClanDatabase(allyName);

            ItemStack allyClanItem = ItemUtil.getClanItemStack(ItemUtil.getItem(
                    allyClanData.getIconType().toString(),
                    allyClanData.getIconValue(),
                    0,
                    fileConfiguration.getString("items.clan.name"),
                    fileConfiguration.getStringList("items.clan.lore"), false), allyClanData);
            int allyClanItemSlot = fileConfiguration.getInt("items.clan.slot");
            inventory.setItem(allyClanItemSlot, allyClanItem);

            List<String> removeAllyItemLore = new ArrayList<>();
            Rank removeAllyRequiredRank = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getSubjectPermission().get(Subject.MANAGEALLY);
            for (String lore : fileConfiguration.getStringList("items.removeAlly.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), removeAllyRequiredRank) ? fileConfiguration.getString("items.removeAlly.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.removeAlly.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(removeAllyRequiredRank)));
                removeAllyItemLore.add(lore);
            }
            ItemStack removeAllyItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.removeAlly.type"),
                    fileConfiguration.getString("items.removeAlly.value"),
                    fileConfiguration.getInt("items.removeAlly.customModelData"),
                    fileConfiguration.getString("items.removeAlly.name"),
                    removeAllyItemLore, false), "remove=" + allyName);
            int removeAllyItemSlot = fileConfiguration.getInt("items.removeAlly.slot");
            inventory.setItem(removeAllyItemSlot, removeAllyItem);
        });
    }
}
