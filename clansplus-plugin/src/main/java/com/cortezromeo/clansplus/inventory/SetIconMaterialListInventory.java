package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.subject.SetIcon;
import com.cortezromeo.clansplus.file.inventory.SetIconMaterialListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SetIconMaterialListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = SetIconMaterialListInventoryFile.get();
    private List<String> materials = new ArrayList<>();

    public SetIconMaterialListInventory(Player owner) {
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
            if (!((index + 1) >= materials.size())) {
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
        if (itemCustomData.contains("value=")) {
            playClickSound(fileConfiguration, "material");
            new SetIcon(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETICON), getOwner(), getOwner().getName(), IconType.MATERIAL, itemCustomData.replace("value=", "")).execute();
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

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            materials.clear();

            for (Material material : Material.values()) {
                if (material == Material.AIR)
                    continue;
                materials.add(material.toString().toUpperCase());
            }

            if (getSearch() != null) {
                List<String> newMaterials = new ArrayList<>();
                for (String material : materials) {
                    if (material.toLowerCase().contains(getSearch().toLowerCase())) {
                        newMaterials.add(material);
                    }
                }
                materials.clear();
                materials.addAll(newMaterials);
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * getPage() + i;
                if (index >= materials.size())
                    break;
                if (materials.get(index) != null) {
                    ItemStack materialItem = ItemUtil.getItem(
                            "material",
                            materials.get(index),
                            0,
                            fileConfiguration.getString("items.material.name"),
                            fileConfiguration.getStringList("items.material.lore"), false);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(materialItem, "value=" + materials.get(index));
                    inventory.addItem(itemStack);
                }
            }
        });
    }
}
