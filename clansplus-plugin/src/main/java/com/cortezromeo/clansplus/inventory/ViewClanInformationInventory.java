package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.file.inventory.ViewClanInventoryFile;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViewClanInformationInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ViewClanInventoryFile.get();
    private String clanName;

    public ViewClanInformationInventory(Player owner, String clanName) {
        super(owner);
        this.clanName = clanName;
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        title = StringUtil.setClanNamePlaceholder(title, clanName);
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

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new ClanListInventory(getOwner()).open();
        if (itemCustomData.equals("members"))
            new MemberListInventory(getOwner(), clanName, true).open();
        if (itemCustomData.equals("allies"))
            new AllyListInventory(getOwner(), clanName, true).open();
        if (itemCustomData.equals("skillsMenu"))
            new SkillsMenuInventory(getOwner(), clanName, true).open();
    }

    @Override
    public void setMenuItems() {
        ClansPlus.plugin.foliaLib.getScheduler().runAsync(task -> {

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

            IClanData clanData = PluginDataManager.getClanDatabase(clanName);

            ItemStack clanItem = ItemUtil.getClanItemStack(ItemUtil.getItem(
                    clanData.getIconType().toString(),
                    clanData.getIconValue(),
                    0,
                    fileConfiguration.getString("items.clan.name"),
                    fileConfiguration.getStringList("items.clan.lore"), false), clanData);
            int clanItemSlot = fileConfiguration.getInt("items.clan.slot");
            inventory.setItem(clanItemSlot, clanItem);

            List<String> membersItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.members.lore")) {
                lore = lore.replace("%totalMembers%", String.valueOf(clanData.getMembers().size()));
                membersItemLore.add(lore);
            }
            ItemStack membersClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.members.type"),
                    fileConfiguration.getString("items.members.value"),
                    fileConfiguration.getInt("items.members.customModelData"),
                    fileConfiguration.getString("items.members.name"),
                    membersItemLore, false), "members");
            int membersItemSlot = fileConfiguration.getInt("items.members.slot");
            inventory.setItem(membersItemSlot, membersClanItem);

            List<String> alliesItemLore = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.allies.lore")) {
                lore = lore.replace("%totalAllies%", String.valueOf(clanData.getAllies().size()));
                alliesItemLore.add(lore);
            }
            ItemStack alliesClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.allies.type"),
                    fileConfiguration.getString("items.allies.value"),
                    fileConfiguration.getInt("items.allies.customModelData"),
                    fileConfiguration.getString("items.allies.name"),
                    alliesItemLore, false), "allies");
            int alliesItemSlot = fileConfiguration.getInt("items.allies.slot");
            inventory.setItem(alliesItemSlot, alliesClanItem);

            ItemStack skillsMenuItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.skillsMenu.type"),
                    fileConfiguration.getString("items.skillsMenu.value"),
                    fileConfiguration.getInt("items.skillsMenu.customModelData"),
                    fileConfiguration.getString("items.skillsMenu.name"),
                    fileConfiguration.getStringList("items.skillsMenu.lore"), false), "skillsMenu");
            int skillsMenuItemSlot = fileConfiguration.getInt("items.skillsMenu.slot");
            inventory.setItem(skillsMenuItemSlot, skillsMenuItem);
        });
    }

}
