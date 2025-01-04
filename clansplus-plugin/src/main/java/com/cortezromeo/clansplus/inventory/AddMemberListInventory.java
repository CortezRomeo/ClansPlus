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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddMemberListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = AddMemberListInventoryFile.get();
    private ShortItemsType shortItemsType;
    private List<String> players = new ArrayList<>();
    private BukkitTask bukkitRunnable;

    public AddMemberListInventory(Player owner) {
        super(owner);
        shortItemsType = ShortItemsType.NOCLAN;
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

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null) {
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
        if (itemCustomData.equals("closeItem"))
            getOwner().closeInventory();
        if (itemCustomData.equals("back"))
            new MembersMenuInventory(getOwner()).open();
        if (itemCustomData.equals("shortItemsItem")) {
            if (shortItemsType == ShortItemsType.NOCLAN)
                shortItemsType = ShortItemsType.BEINGINVITED;
            else if (shortItemsType == ShortItemsType.BEINGINVITED)
                shortItemsType = ShortItemsType.NOCLAN;
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
                    fileConfiguration.getStringList("items.back.lore")), "back");
            int backItemSlot = fileConfiguration.getInt("items.back.slot");
            if (backItemSlot < 0)
                backItemSlot = 0;
            if (backItemSlot > 8)
                backItemSlot = 8;
            backItemSlot = (getSlots() - 9) + backItemSlot;
            inventory.setItem(backItemSlot, backItem);

            ItemStack shortItemsItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.shortItems.type"),
                    fileConfiguration.getString("items.shortItems.value"),
                    fileConfiguration.getInt("items.shortItems.customModelData"),
                    fileConfiguration.getString("items.shortItems.name"),
                    fileConfiguration.getStringList("items.shortItems.lore." + shortItemsType.toString())), "shortItemsItem");
            int shortItemsItemSlot = fileConfiguration.getInt("items.shortItems.slot");
            if (shortItemsItemSlot < 0)
                shortItemsItemSlot = 0;
            if (shortItemsItemSlot > 8)
                shortItemsItemSlot = 8;
            shortItemsItemSlot = (getSlots() - 9) + shortItemsItemSlot;
            inventory.setItem(shortItemsItemSlot, shortItemsItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            players.clear();

            if (shortItemsType == ShortItemsType.NOCLAN)
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!ClanManager.isPlayerInClan(player) && !ClanManager.beingInvitedPlayers.containsKey(player.getName()))
                        players.add(player.getName());
                }
            if (shortItemsType == ShortItemsType.BEINGINVITED)
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
                            fileConfiguration.getStringList("items.player.lore"));
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

    public enum ShortItemsType {
        NOCLAN, BEINGINVITED
    }

}
