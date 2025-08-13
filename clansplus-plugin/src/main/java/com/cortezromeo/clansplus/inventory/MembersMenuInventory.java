package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.file.inventory.MembersMenuInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
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
    public boolean handleMenu(InventoryClickEvent event) {
        if (!super.handleMenu(event))
            return false;

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.MUST_BE_IN_CLAN);
            getOwner().closeInventory();
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanMenuInventory(getOwner()).open();
        if (itemCustomData.equals("addMember"))
            new AddMemberListInventory(getOwner()).open();
        if (itemCustomData.equals("memberList"))
            new MemberListInventory(getOwner(), PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName(), false).open();

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            ItemStack addMemberItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.addMember.type").toUpperCase()),
                    fileConfiguration.getString("items.addMember.value"),
                    fileConfiguration.getInt("items.addMember.customModelData"),
                    fileConfiguration.getString("items.addMember.name"),
                    fileConfiguration.getStringList("items.addMember.lore"), false), "addMember");
            int addMemberItemSlot = fileConfiguration.getInt("items.addMember.slot");
            inventory.setItem(addMemberItemSlot, addMemberItem);

            ItemStack memberListItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.memberList.type").toUpperCase()),
                    fileConfiguration.getString("items.memberList.value"),
                    fileConfiguration.getInt("items.memberList.customModelData"),
                    fileConfiguration.getString("items.memberList.name"),
                    fileConfiguration.getStringList("items.memberList.lore"), false), "memberList");
            int memberListItemSlot = fileConfiguration.getInt("items.memberList.slot");
            inventory.setItem(memberListItemSlot, memberListItem);
        });
    }

}
