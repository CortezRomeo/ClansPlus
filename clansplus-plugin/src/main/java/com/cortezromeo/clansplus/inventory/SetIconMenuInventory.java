package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;
import com.cortezromeo.clansplus.file.inventory.SetIconMenuInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SetIconMenuInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = SetIconMenuInventoryFile.get();

    public SetIconMenuInventory(Player owner) {
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
            new ClanSettingsInventory(getOwner()).open();
        if (itemCustomData.equals("material"))
            new SetIconMaterialListInventory(getOwner()).open();
        if (itemCustomData.equals("customHead"))
            new SetIconCustomHeadListInventory(getOwner(), CustomHeadCategory.ALPHABET).open();

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            ItemStack materialItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.material.type").toUpperCase()),
                    fileConfiguration.getString("items.material.value"),
                    fileConfiguration.getInt("items.material.customModelData"),
                    fileConfiguration.getString("items.material.name"),
                    fileConfiguration.getStringList("items.material.lore"), false), "material");
            int materialItemSlot = fileConfiguration.getInt("items.material.slot");
            inventory.setItem(materialItemSlot, materialItem);

            ItemStack customHeadItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.customHead.type").toUpperCase()),
                    fileConfiguration.getString("items.customHead.value"),
                    fileConfiguration.getInt("items.customHead.customModelData"),
                    fileConfiguration.getString("items.customHead.name"),
                    fileConfiguration.getStringList("items.customHead.lore"), false), "customHead");
            int customHeadItemSlot = fileConfiguration.getInt("items.customHead.slot");
            inventory.setItem(customHeadItemSlot, customHeadItem);
        });
    }

}
