package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.subject.SetIcon;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;
import com.cortezromeo.clansplus.file.inventory.SetIconCustomHeadListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.CustomHeadData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetIconCustomHeadListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = SetIconCustomHeadListInventoryFile.get();
    private CustomHeadCategory category;
    private List<CustomHeadData> customheads = new ArrayList<>();

    public SetIconCustomHeadListInventory(Player owner, CustomHeadCategory category) {
        super(owner);
        this.category = category;
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
        title = title.replace("%search%", getSearch() != null ? fileConfiguration.getString("title-placeholders.search").replace("%search%", getSearch()) : "");
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

        super.handleMenu(event);
        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("prevPage")) {
            if (getPage() != 0) {
                setPage(getPage() - 1);
                open();
            }
        }
        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= customheads.size())) {
                setPage(getPage() + 1);
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new SetIconMenuInventory(getOwner()).open();
        if (itemCustomData.equals("sort")) {
            if (category == CustomHeadCategory.ALPHABET)
                category = CustomHeadCategory.ANIMALS;
            else if (category == CustomHeadCategory.ANIMALS)
                category = CustomHeadCategory.MONSTERS;
            else if (category == CustomHeadCategory.MONSTERS)
                category = CustomHeadCategory.BLOCKS;
            else if (category == CustomHeadCategory.BLOCKS)
                category = CustomHeadCategory.DECORATION;
            else if (category == CustomHeadCategory.DECORATION)
                category = CustomHeadCategory.FOOD_DRINKS;
            else if (category == CustomHeadCategory.FOOD_DRINKS)
                category = CustomHeadCategory.HUMANOID;
            else if (category == CustomHeadCategory.HUMANOID)
                category = CustomHeadCategory.HUMANS;
            else if (category == CustomHeadCategory.HUMANS)
                category = CustomHeadCategory.PLANTS;
            else if (category == CustomHeadCategory.PLANTS)
                category = CustomHeadCategory.MISCELLANEOUS;
            else if (category == CustomHeadCategory.MISCELLANEOUS)
                category = CustomHeadCategory.ALPHABET;
            setSearch(null);
            setPage(0);
            super.open();
        }
        if (itemCustomData.contains("value=")) {
            playClickSound(fileConfiguration, "customHead");
            new SetIcon(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETICON), getOwner(), getOwner().getName(), IconType.CUSTOMHEAD, itemCustomData.replace("value=", "")).execute();
        }
    }

    @Override
    public void setMenuItems() {
        ClansPlus.plugin.foliaLib.getScheduler().runAsync(task -> {
            addPaginatedMenuItems(fileConfiguration);
            ItemStack backItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.back.type"),
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

            ItemStack sortItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.sort.type"),
                    fileConfiguration.getString("items.sort.value"),
                    fileConfiguration.getInt("items.sort.customModelData"),
                    fileConfiguration.getString("items.sort.name"),
                    fileConfiguration.getStringList("items.sort.lore." + category.toString()), false), "sort");
            int sortItemSlot = fileConfiguration.getInt("items.sort.slot");
            if (sortItemSlot < 0)
                sortItemSlot = 0;
            if (sortItemSlot > 8)
                sortItemSlot = 8;
            sortItemSlot = (getSlots() - 9) + sortItemSlot;
            inventory.setItem(sortItemSlot, sortItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            customheads.clear();

            if (PluginDataManager.getCustomHeadDatabase().get(category) != null) {
                customheads.addAll(PluginDataManager.getCustomHeadDatabase(category));
            } else
                return;

            if (getSearch() != null) {
                List<CustomHeadData> newCustomHeads = new ArrayList<>();
                for (CustomHeadData customHeadData : customheads) {
                    if (customHeadData.getName().toLowerCase().contains(getSearch().toLowerCase())) {
                        newCustomHeads.add(customHeadData);
                    }
                }
                customheads.clear();
                customheads.addAll(newCustomHeads);
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * getPage() + i;
                if (index >= customheads.size())
                    break;
                if (customheads.get(index) != null) {
                    String customHeadName = customheads.get(index).getName();
                    String customHeadValue = customheads.get(index).getValue();
                    ArrayList<String> customHeadItemLore = new ArrayList<>();
                    ItemStack customHeadItem = ItemUtil.getItem(
                            "customhead",
                            customHeadValue,
                            0,
                            fileConfiguration.getString("items.customHead.name").replace("%name%", customHeadName),
                            fileConfiguration.getStringList("items.customHead.lore"), false);
                    ItemMeta customHeadItemMeta = customHeadItem.getItemMeta();
                    for (String lore : customHeadItemMeta.getLore()) {
                        lore = lore.replace("%value%", customHeadValue);
                        customHeadItemLore.add(lore);
                    }
                    customHeadItemMeta.setLore(customHeadItemLore);
                    customHeadItem.setItemMeta(customHeadItemMeta);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(customHeadItem, "value=" + customHeadValue);
                    inventory.addItem(itemStack);
                }
            }
        });
    }
}
