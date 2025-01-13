package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class UpgradeSkillPaginatedInventory extends ClanPlusInventoryBase {

    protected int page = 0;
    protected int index = 0;

    public UpgradeSkillPaginatedInventory(Player owner) {
        super(owner);
    }

    public void addPaginatedMenuItems(FileConfiguration fileConfiguration) {
        if (fileConfiguration.getBoolean("items.border.enabled")) {
            ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                    fileConfiguration.getString("items.border.value"),
                    fileConfiguration.getInt("items.border.customModelData"),
                    fileConfiguration.getString("items.border.name"),
                    fileConfiguration.getStringList("items.border.lore"), false);
            for (int itemSlot = 0; itemSlot < getSlots(); itemSlot++)
                inventory.setItem(itemSlot, borderItem);
        }

        int[] values = {
                0, 9, 18,
                27, 28, 29, 20,
                11, 2, 3, 4,
                13, 22, 31, 32,
                33, 24, 15, 6,
                7, 8, 17, 26,
                35, 44, 53,
        };

        ItemStack invalidSkillLevelItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.invalidSkillLevel.type"),
                fileConfiguration.getString("items.invalidSkillLevel.value"),
                fileConfiguration.getInt("items.invalidSkillLevel.customModelData"),
                fileConfiguration.getString("items.invalidSkillLevel.name"),
                fileConfiguration.getStringList("items.invalidSkillLevel.lore"), false), "invalidSkillLevel");
        for (int invalidSkillItemLevelSlot : values)
            inventory.setItem(invalidSkillItemLevelSlot, invalidSkillLevelItem);

        ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                fileConfiguration.getString("items.close.value"),
                fileConfiguration.getInt("items.close.customModelData"),
                fileConfiguration.getString("items.close.name"),
                fileConfiguration.getStringList("items.close.lore"), false), "close");
        int closeItemSlot = fileConfiguration.getInt("items.close.slot");
        if (closeItemSlot < 0)
            closeItemSlot = 1;
        if (closeItemSlot > 8)
            closeItemSlot = 7;
        closeItemSlot = (getSlots() - 9) + closeItemSlot;

        ItemStack prevItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.prevPage.type"),
                fileConfiguration.getString("items.prevPage.value"),
                fileConfiguration.getInt("items.prevPage.customModelData"),
                fileConfiguration.getString("items.prevPage.name"),
                fileConfiguration.getStringList("items.prevPage.lore"), false), "prevPage");
        int prevPageItemSlot = fileConfiguration.getInt("items.prevPage.slot");
        if (prevPageItemSlot < 0)
            prevPageItemSlot = 1;
        if (prevPageItemSlot > 8)
            prevPageItemSlot = 7;
        prevPageItemSlot = (getSlots() - 9) + prevPageItemSlot;

        ItemStack nextItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.nextPage.type"),
                fileConfiguration.getString("items.nextPage.value"),
                fileConfiguration.getInt("items.nextPage.customModelData"),
                fileConfiguration.getString("items.nextPage.name"),
                fileConfiguration.getStringList("items.nextPage.lore"), false), "nextPage");
        int nextPageItemSlot = fileConfiguration.getInt("items.nextPage.slot");
        if (nextPageItemSlot < 0)
            nextPageItemSlot = 1;
        if (nextPageItemSlot > 8)
            nextPageItemSlot = 7;
        nextPageItemSlot = (getSlots() - 9) + nextPageItemSlot;

        if (page > 0)
            inventory.setItem(prevPageItemSlot, getPageItemStack(prevItem));
        inventory.setItem(closeItemSlot, closeItem);
        inventory.setItem(nextPageItemSlot, getPageItemStack(nextItem));
    }

    private @NotNull ItemStack getPageItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%page%", String.valueOf(page))
                .replace("%nextPage%", String.valueOf(page + 2))
                .replace("%prevPage%", String.valueOf(page > 0 ? page : 0))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public int getMaxItemsPerPage() {
        return 26;
    }
}
