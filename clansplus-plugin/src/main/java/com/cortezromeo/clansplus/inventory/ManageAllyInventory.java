package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.RemoveAlly;
import com.cortezromeo.clansplus.file.inventory.ManageAllyInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ManageAllyInventory extends ClanPlusInventoryBase {

    FileConfiguration fileConfiguration = ManageAllyInventoryFile.get();
    private String allyName;

    public ManageAllyInventory(Player owner, String allyName) {
        super(owner);
        this.allyName = allyName;
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
        title = StringUtil.setClanNamePlaceholder(title, allyName);
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

        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName());

        // how can they get here?
        if (playerClanData == null || !PluginDataManager.getClanDatabase().containsKey(allyName)) {
            getOwner().closeInventory();
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        String itemCustomData = ClansPlus.nms.getCustomData(itemStack);

        playClickSound(fileConfiguration, itemCustomData);

        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new AllyListInventory(getOwner(), playerClanData.getName(), false).open();
        if (itemCustomData.contains("remove=")) {
            playClickSound(fileConfiguration, "removeAlly");
            itemCustomData = itemCustomData.replace("remove=", "");
            if (new RemoveAlly(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.MANAGEALLY), getOwner(), getOwner().getName(), itemCustomData).execute())
                new AllyListInventory(getOwner(), playerClanData.getName(), false).open();
        }

        return true;
    }

    @Override
    public void setMenuItems() {
        ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {

            addBasicButton(fileConfiguration, true);

            IClanData allyClanData = PluginDataManager.getClanDatabase(allyName);

            ItemStack allyClanItem = ItemUtil.getClanItemStack(ItemUtil.getItem(
                    allyClanData.getIconType(),
                    allyClanData.getIconValue(),
                    0,
                    fileConfiguration.getString("items.clan.name"),
                    fileConfiguration.getStringList("items.clan.lore"), false), allyClanData);
            int allyClanItemSlot = fileConfiguration.getInt("items.clan.slot");
            inventory.setItem(allyClanItemSlot, allyClanItem);

            List<String> removeAllyItemLore = new ArrayList<>();
            Rank removeAllyRequiredRank = PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getSubjectPermission().get(Subject.MANAGEALLY);
            for (String lore : fileConfiguration.getStringList("items.removeAlly.lore")) {
                lore = lore.replace("%checkPermission%", ClanManager.isPlayerRankSatisfied(getOwner().getName(), removeAllyRequiredRank) ? fileConfiguration.getString("items.removeAlly.placeholders.checkPermission.true")
                        : fileConfiguration.getString("items.removeAlly.placeholders.checkPermission.false").replace("%getRequiredRank%", ClanManager.getFormatRank(removeAllyRequiredRank)));
                removeAllyItemLore.add(lore);
            }
            ItemStack removeAllyItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(
                    ItemType.valueOf(fileConfiguration.getString("items.removeAlly.type").toUpperCase()),
                    fileConfiguration.getString("items.removeAlly.value"),
                    fileConfiguration.getInt("items.removeAlly.customModelData"),
                    fileConfiguration.getString("items.removeAlly.name"),
                    removeAllyItemLore, false), "remove=" + allyName);
            int removeAllyItemSlot = fileConfiguration.getInt("items.removeAlly.slot");
            inventory.setItem(removeAllyItemSlot, removeAllyItem);
        });
    }
}
