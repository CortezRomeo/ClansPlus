package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.subject.SetIcon;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;
import com.cortezromeo.clansplus.file.inventory.SetIconCustomHeadListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.listener.AsyncPlayerChatListener;
import com.cortezromeo.clansplus.listener.PlayerChatListener;
import com.cortezromeo.clansplus.storage.CustomHeadData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetIconCustomHeadListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = SetIconCustomHeadListInventoryFile.get();
    private CustomHeadCategory category;
    private List<CustomHeadData> customheads = new ArrayList<>();
    private String search;

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
        title = title.replace("%search%", search != null ? fileConfiguration.getString("title-placeholders.search").replace("%search%", search) : "");
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

        if (itemCustomData.equals("prevPage")) {
            if (page != 0) {
                page = page - 1;
                open();
            }
        }
        if (itemCustomData.equals("nextPage")) {
            if (!((index + 1) >= customheads.size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new AlliesMenuInventory(getOwner()).open();
        if (itemCustomData.equals("search")) {
            if (event.getClick().isRightClick()) {
                search = null;
                page = 0;
                PlayerChatListener.removeSearchPlayerQuery(getOwner());
                open();
            } else {
                getOwner().closeInventory();
                PlayerChatListener.addSearchPlayerQuery(getOwner(), this);
            }
        }
        if (itemCustomData.contains("value=")) {
            new SetIcon(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETICON), getOwner(), getOwner().getName(), IconType.CUSTOMHEAD, itemCustomData.replace("value=", "")).execute();
        }
    }

    @Override
    public void setMenuItems() {
        Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
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

            ItemStack searchItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.search.type"),
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

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            customheads.clear();

            if (PluginDataManager.getCustomHeadDatabase().get(category) != null) {
                customheads.addAll(PluginDataManager.getCustomHeadDatabase(category));
            } else
                return;

            if (search != null) {
                List<CustomHeadData> newCustomHeads = new ArrayList<>();
                for (CustomHeadData customHeadData: customheads) {
                    if (customHeadData.getName().toLowerCase().contains(search.toLowerCase())) {
                        newCustomHeads.add(customHeadData);
                    }
                }
                customheads.clear();
                customheads.addAll(newCustomHeads);
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
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

    @Override
    public void onSearch(PlayerChatEvent event) {
        event.setCancelled(true);
        search = event.getMessage();
        page = 0;
        open();
    }
}
