package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.Invite;
import com.cortezromeo.clansplus.file.inventory.AddMemberListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddMemberListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = AddMemberListInventoryFile.get();
    private SortItemType sortItemType;
    private List<String> players = new ArrayList<>();

    public AddMemberListInventory(Player owner) {
        super(owner);
        sortItemType = SortItemType.NOCLAN;
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
            if (!((index + 1) >= players.size())) {
                page = page + 1;
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new MembersMenuInventory(getOwner()).open();
        if (itemCustomData.equals("sortItem")) {
            if (sortItemType == SortItemType.NOCLAN)
                sortItemType = SortItemType.BEINGINVITED;
            else if (sortItemType == SortItemType.BEINGINVITED)
                sortItemType = SortItemType.NOCLAN;
            page = 0;
            super.open();
        }
        if (itemCustomData.contains("player=")) {
            itemCustomData = itemCustomData.replace("player=", "");
            new Invite(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.INVITE), getOwner(), getOwner().getName(), Bukkit.getPlayer(itemCustomData), itemCustomData, Settings.CLAN_SETTING_TIME_TO_ACCEPT).execute();
            super.open();
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

            ItemStack sortItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.sortItem.type"),
                    fileConfiguration.getString("items.sortItem.value"),
                    fileConfiguration.getInt("items.sortItem.customModelData"),
                    fileConfiguration.getString("items.sortItem.name"),
                    fileConfiguration.getStringList("items.sortItem.lore." + sortItemType.toString()), false), "sortItem");
            int sortItemSlot = fileConfiguration.getInt("items.sortItem.slot");
            if (sortItemSlot < 0)
                sortItemSlot = 0;
            if (sortItemSlot > 8)
                sortItemSlot = 8;
            sortItemSlot = (getSlots() - 9) + sortItemSlot;
            inventory.setItem(sortItemSlot, sortItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            players.clear();

            if (sortItemType == SortItemType.NOCLAN)
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!ClanManager.isPlayerInClan(player) && !ClanManager.beingInvitedPlayers.containsKey(player.getName()))
                        players.add(player.getName());
                }
            if (sortItemType == SortItemType.BEINGINVITED)
                if (!ClanManager.beingInvitedPlayers.isEmpty())
                    players.addAll(ClanManager.beingInvitedPlayers.keySet());


            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= players.size())
                    break;
                if (players.get(index) != null) {
                    String playerName = players.get(index);
                    ItemStack playerItem = ItemUtil.getItem(
                            "playerhead",
                            playerName,
                            0,
                            fileConfiguration.getString("items.player.name"),
                            fileConfiguration.getStringList("items.player.lore"), false);
                    ItemStack itemStack = ClansPlus.nms.addCustomData(getPlayerItemStack(playerItem, playerName), "player=" + playerName);
                    inventory.addItem(itemStack);
                }
            }
        });
    }

    private @NotNull ItemStack getPlayerItemStack(ItemStack itemStack, String playerName) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        String itemName = itemMeta.getDisplayName();
        itemName = itemName.replace("%playerName%", playerName);
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%playerName%", playerName)
                .replace("%scoreCollected%", String.valueOf(playerData.getScoreCollected()))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public enum SortItemType {
        NOCLAN, BEINGINVITED
    }

}
