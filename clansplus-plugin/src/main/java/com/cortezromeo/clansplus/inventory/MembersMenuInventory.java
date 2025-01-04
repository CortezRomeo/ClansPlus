package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.file.inventory.MembersMenuInventoryFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MembersMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = MembersMenuInventoryFile.get();

    public MembersMenuInventory(Player owner) {
        super(owner);
    }

    @Override
    public void open() {
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

        // how can they get here?
        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            getOwner().closeInventory();
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("addMember"))
            new AddMemberListInventory(getOwner()).open();
        if (itemCustomData.equals("memberList"))
            new MemberListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName())).open();
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

            ItemStack addMemberItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.addMember.type"),
                    fileConfiguration.getString("items.addMember.value"),
                    fileConfiguration.getInt("items.addMember.customModelData"),
                    fileConfiguration.getString("items.addMember.name"),
                    fileConfiguration.getStringList("items.addMember.lore")), "addMember");
            int addMemberItemSlot = fileConfiguration.getInt("items.addMember.slot");
            inventory.setItem(addMemberItemSlot, addMemberItem);

            ItemStack memberListItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.memberList.type"),
                    fileConfiguration.getString("items.memberList.value"),
                    fileConfiguration.getInt("items.memberList.customModelData"),
                    fileConfiguration.getString("items.memberList.name"),
                    fileConfiguration.getStringList("items.memberList.lore")), "memberList");
            int memberListItemSlot = fileConfiguration.getInt("items.memberList.slot");
            inventory.setItem(memberListItemSlot, memberListItem);
        });
    }

}
