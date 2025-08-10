package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.listener.SignChangeListener;
import com.cortezromeo.clansplus.util.ItemUtil;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PaginatedInventory extends ClanPlusInventoryBase {

    protected int page = 0;
    protected boolean isUsingBorder;
    protected String search;
    protected int index = 0;

    public PaginatedInventory(Player owner) {
        super(owner);
    }

    public void onSearch(SignChangeEvent event) {
        event.setCancelled(true);
        String signLine1 = ((TextComponent) event.line(0)).content();
        String signLine2 = ((TextComponent) event.line(1)).content();
        String signLine3 = ((TextComponent) event.line(2)).content();
        String signLine4 = ((TextComponent) event.line(3)).content();
        setSearch(new StringBuilder().append(signLine1).append(signLine2).append(signLine3).append(signLine4).toString());
        setPage(0);
        open();
    }

    @Override
    public boolean handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null)
            return false;

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        if (itemCustomData.equals("search")) {
            if (event.getClick().isRightClick()) {
                search = null;
                setPage(0);
                SignChangeListener.removeSearchPlayerQuery(getOwner());
                open();
            } else {
                getOwner().closeInventory();
                SignChangeListener.addSearchPlayerQuery(getOwner(), this);
            }
        }

        return true;
    }

    public void addPaginatedMenuItems(FileConfiguration fileConfiguration, boolean backButton) {
        isUsingBorder = fileConfiguration.getBoolean("items.border.enabled");
        if (isUsingBorder) {
            ItemStack borderItem = ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.border.type").toUpperCase()),
                    fileConfiguration.getString("items.border.value"),
                    fileConfiguration.getInt("items.border.customModelData"),
                    fileConfiguration.getString("items.border.name"),
                    fileConfiguration.getStringList("items.border.lore"), false);
            if (getSlots() > 18)
                for (int i = 0; i < 10; i++) {
                    if (inventory.getItem(i) == null) {
                        inventory.setItem(i, ClansPlus.nms.addCustomData(borderItem, "border"));
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

        ItemStack closeItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.close.type").toUpperCase()),
                fileConfiguration.getString("items.close.value"),
                fileConfiguration.getInt("items.close.customModelData"),
                fileConfiguration.getString("items.close.name"),
                fileConfiguration.getStringList("items.close.lore"), false), "close");
        int closeItemSlot = fileConfiguration.getInt("items.close.slot");
        if (closeItemSlot < 0)
            closeItemSlot = 0;
        if (closeItemSlot > 8)
            closeItemSlot = 8;
        closeItemSlot = (getSlots() - 9) + closeItemSlot;

        if (backButton) {
            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.back.type").toUpperCase()),
                    fileConfiguration.getString("items.back.value"),
                    fileConfiguration.getInt("items.back.customModelData"),
                    fileConfiguration.getString("items.back.name"),
                    fileConfiguration.getStringList("items.back.lore"), false), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            if (backItemSlot < 0)
                backItemSlot = 0;
            if (backItemSlot > 8)
                backItemSlot = 8;
            backItemSlot = (getSlots() - 9) + backItemSlot;
            inventory.setItem(backItemSlot, backItem);
        }

        ItemStack prevItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.prevPage.type").toUpperCase()),
                fileConfiguration.getString("items.prevPage.value"),
                fileConfiguration.getInt("items.prevPage.customModelData"),
                fileConfiguration.getString("items.prevPage.name"),
                fileConfiguration.getStringList("items.prevPage.lore"), false), "prevPage");
        int prevPageItemSlot = fileConfiguration.getInt("items.prevPage.slot");
        if (prevPageItemSlot < 0)
            prevPageItemSlot = 0;
        if (prevPageItemSlot > 8)
            prevPageItemSlot = 8;
        prevPageItemSlot = (getSlots() - 9) + prevPageItemSlot;

        ItemStack nextItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.nextPage.type").toUpperCase()),
                fileConfiguration.getString("items.nextPage.value"),
                fileConfiguration.getInt("items.nextPage.customModelData"),
                fileConfiguration.getString("items.nextPage.name"),
                fileConfiguration.getStringList("items.nextPage.lore"), false), "nextPage");
        int nextPageItemSlot = fileConfiguration.getInt("items.nextPage.slot");
        if (nextPageItemSlot < 0)
            nextPageItemSlot = 0;
        if (nextPageItemSlot > 8)
            nextPageItemSlot = 8;
        nextPageItemSlot = (getSlots() - 9) + nextPageItemSlot;

        ItemStack searchItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                ItemType.valueOf(fileConfiguration.getString("items.search.type").toUpperCase()),
                fileConfiguration.getString("items.search.value"),
                fileConfiguration.getInt("items.search.customModelData"),
                fileConfiguration.getString("items.search.name"),
                fileConfiguration.getStringList("items.search.lore"), false), "search");
        int searchItemSlot = fileConfiguration.getInt("items.search.slot");
        if (searchItemSlot < 0)
            searchItemSlot = 0;
        if (searchItemSlot > 8)
            searchItemSlot = 8;
        searchItemSlot = (getSlots() - 9) + searchItemSlot;
        inventory.setItem(searchItemSlot, searchItem);

        if (getPage() > 0)
            inventory.setItem(prevPageItemSlot, getPageItemStack(prevItem));
        inventory.setItem(closeItemSlot, closeItem);
        inventory.setItem(nextPageItemSlot, getPageItemStack(nextItem));
    }

    @NotNull ItemStack getPageItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%page%", String.valueOf(getPage()))
                .replace("%nextPage%", String.valueOf(getPage() + 2))
                .replace("%prevPage%", String.valueOf(getPage() > 0 ? getPage() : 0))));
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
