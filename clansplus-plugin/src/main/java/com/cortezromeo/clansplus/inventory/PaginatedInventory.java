package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PaginatedInventory extends ClanPlusInventoryBase {

    protected int page = 0;
    protected boolean isUsingBorder;
    protected int index = 0;

    public PaginatedInventory(Player owner) {
        super(owner);
    }

    //Set the border and menu buttons for the menu
    public void addPaginatedMenuItems(FileConfiguration fileConfiguration) {
        ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.close.type"),
                fileConfiguration.getString("items.close.value"),
                fileConfiguration.getInt("items.close.customModelData"),
                fileConfiguration.getString("items.close.name"),
                fileConfiguration.getStringList("items.close.lore")), "closeItem");
        int closeItemSlot = fileConfiguration.getInt("items.close.slot");
        if (closeItemSlot < 0)
            closeItemSlot = 0;
        if (closeItemSlot > 8)
            closeItemSlot = 8;
        closeItemSlot = (getSlots() - 9) + closeItemSlot;

        ItemStack prevItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.prevPage.type"),
                fileConfiguration.getString("items.prevPage.value"),
                fileConfiguration.getInt("items.prevPage.customModelData"),
                fileConfiguration.getString("items.prevPage.name"),
                fileConfiguration.getStringList("items.prevPage.lore")), "prevPage");
        int prevPageItemSlot = fileConfiguration.getInt("items.prevPage.slot");
        if (prevPageItemSlot < 0)
            prevPageItemSlot = 0;
        if (prevPageItemSlot > 8)
            prevPageItemSlot = 8;
        prevPageItemSlot = (getSlots() - 9) + prevPageItemSlot;

        ItemStack nextItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.nextPage.type"),
                fileConfiguration.getString("items.nextPage.value"),
                fileConfiguration.getInt("items.nextPage.customModelData"),
                fileConfiguration.getString("items.nextPage.name"),
                fileConfiguration.getStringList("items.nextPage.lore")), "nextPage");
        int nextPageItemSlot = fileConfiguration.getInt("items.nextPage.slot");
        if (nextPageItemSlot < 0)
            nextPageItemSlot = 0;
        if (nextPageItemSlot > 8)
            nextPageItemSlot = 8;
        nextPageItemSlot = (getSlots() - 9) + nextPageItemSlot;

        if (page > 0)
            inventory.setItem(prevPageItemSlot, getPageItemStack(prevItem));
        inventory.setItem(closeItemSlot, closeItem);
        inventory.setItem(nextPageItemSlot, getPageItemStack(nextItem));

        isUsingBorder = fileConfiguration.getBoolean("items.border.enabled");
        if (isUsingBorder) {
            ItemStack borderItem = ItemUtil.getItem(fileConfiguration.getString("items.border.type"),
                    fileConfiguration.getString("items.border.value"),
                    fileConfiguration.getInt("items.border.customModelData"),
                    fileConfiguration.getString("items.border.name"),
                    fileConfiguration.getStringList("items.border.lore"));
            if (getSlots() > 18)
                for (int i = 0; i < 10; i++) {
                    if (inventory.getItem(i) == null) {
                        inventory.setItem(i, borderItem);
                    }
                }
            if (getSlots() > 27) {
                inventory.setItem(17, borderItem);
                inventory.setItem(18, borderItem);
            }
            if (getSlots() > 36) {
                inventory.setItem(26, borderItem);
                inventory.setItem(27, borderItem);
            }
            if (getSlots() > 45) {
                inventory.setItem(35, borderItem);
                inventory.setItem(36, borderItem);
            }
            for (int i = getSlots() - 10; i < getSlots(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, borderItem);
                }
            }
        }
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
        if (getSlots() == 54)
            return 28 + (isUsingBorder ? 0 : 17);
        else if (getSlots() == 45)
            return 21 + (isUsingBorder ? 0 : 15);
        else if (getSlots() == 36)
            return 14 + (isUsingBorder ? 0 : 13);
        else if (getSlots() == 27)
            return 7 + (isUsingBorder ? 0 : 11);
        else
            return 28 + (isUsingBorder ? 0 : 17);
    }
}
