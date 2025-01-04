package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.file.inventory.NoClanInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.listener.PlayerChatListener;
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

public class NoClanInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = NoClanInventoryFile.get();

    public NoClanInventory(Player owner) {
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

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("createNewClan")) {
            getOwner().closeInventory();
            if (!PlayerChatListener.createClan.contains(getOwner()))
                PlayerChatListener.createClan.add(getOwner());
            MessageUtil.sendMessage(getOwner(), Messages.USING_CHAT_BOX_CREATE_CLAN);
        }
        if (itemCustomData.equals("clanList"))
            new ClanListInventory(getOwner()).open();
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

            ItemStack createNewClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.createNewClan.type"),
                    fileConfiguration.getString("items.createNewClan.value"),
                    fileConfiguration.getInt("items.createNewClan.customModelData"),
                    fileConfiguration.getString("items.createNewClan.name"),
                    fileConfiguration.getStringList("items.createNewClan.lore")), "createNewClan");
            int createNewClanItemSlot = fileConfiguration.getInt("items.createNewClan.slot");
            inventory.setItem(createNewClanItemSlot, createNewClanItem);

            List<String> listClanItemLores = new ArrayList<>();
            for (String lore : fileConfiguration.getStringList("items.clanList.lore")) {
                lore = lore.replace("%totalClans%", String.valueOf(PluginDataManager.getClanDatabase().size()));
                listClanItemLores.add(lore);
            }
            ItemStack listClanItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.clanList.type"),
                    fileConfiguration.getString("items.clanList.value"),
                    fileConfiguration.getInt("items.clanList.customModelData"),
                    fileConfiguration.getString("items.clanList.name"),
                    listClanItemLores), "clanList");
            int listClanItemSlot = fileConfiguration.getInt("items.clanList.slot");
            inventory.setItem(listClanItemSlot, listClanItem);
        });
    }

}
