package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.file.inventory.AlliesMenuInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AlliesMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = AlliesMenuInventoryFile.get();

    public AlliesMenuInventory(Player owner) {
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

        playClickSound(fileConfiguration, itemCustomData);
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("addAlly"))
            new AddAllyListInventory(getOwner()).open();
        if (itemCustomData.equals("allyInvitation"))
            new AllyInvitationListInventory(getOwner()).open();
        if (itemCustomData.equals("allyList"))
            new AllyListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName(), false).open();
        //new MemberListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName())).open();
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

            ItemStack addAllyItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.addAlly.type"),
                    fileConfiguration.getString("items.addAlly.value"),
                    fileConfiguration.getInt("items.addAlly.customModelData"),
                    fileConfiguration.getString("items.addAlly.name"),
                    fileConfiguration.getStringList("items.addAlly.lore"), false), "addAlly");
            int addAllyItemSlot = fileConfiguration.getInt("items.addAlly.slot");
            inventory.setItem(addAllyItemSlot, addAllyItem);

            List<String> allyInvitationLore = new ArrayList<>();
            int totalAllyInvitations = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getAllyInvitation().size();
            for (String lore : fileConfiguration.getStringList("items.allyInvitation.lore")) {
                lore = lore.replace("%totalAllyInvitations%", String.valueOf(totalAllyInvitations));
                allyInvitationLore.add(lore);
            }
            ItemStack allyInvitationItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.allyInvitation.type"),
                    fileConfiguration.getString("items.allyInvitation.value"),
                    fileConfiguration.getInt("items.allyInvitation.customModelData"),
                    fileConfiguration.getString("items.allyInvitation.name"),
                    allyInvitationLore, totalAllyInvitations > 0), "allyInvitation");
            int allyInvitationItemSlot = fileConfiguration.getInt("items.allyInvitation.slot");
            inventory.setItem(allyInvitationItemSlot, allyInvitationItem);

            ItemStack allyListItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.allyList.type"),
                    fileConfiguration.getString("items.allyList.value"),
                    fileConfiguration.getInt("items.allyList.customModelData"),
                    fileConfiguration.getString("items.allyList.name"),
                    fileConfiguration.getStringList("items.allyList.lore"), false), "allyList");
            int allyListItemSlot = fileConfiguration.getInt("items.allyList.slot");
            inventory.setItem(allyListItemSlot, allyListItem);
        });
    }

}
