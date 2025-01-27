package com.cortezromeo.clansplus.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.file.inventory.MemberListInventoryFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.HashMapUtil;
import com.cortezromeo.clansplus.util.ItemUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MemberListInventory extends PaginatedInventory {

    FileConfiguration fileConfiguration = MemberListInventoryFile.get();
    private SortItemType sortItemType;
    private List<String> players = new ArrayList<>();
    private String clanName;
    private boolean fromViewClan;

    public MemberListInventory(Player owner, String clanName, boolean fromViewClan) {
        super(owner);
        sortItemType = SortItemType.PERMISSION;
        this.clanName = clanName;
        this.fromViewClan = fromViewClan;
    }

    @Override
    public void open() {
        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_NO_LONGER_EXIST.replace("%clan%", clanName));
            return;
        }
        super.open();
    }

    @Override
    public String getMenuName() {
        String title = fileConfiguration.getString("title");
        title = title.replace("%search%", getSearch() != null ? fileConfiguration.getString("title-placeholders.search").replace("%search%", getSearch()) : "");
        title = title.replace("%totalMembers%", String.valueOf(PluginDataManager.getClanDatabase().size()));
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

        if (PluginDataManager.getClanDatabase(clanName) == null) {
            MessageUtil.sendMessage(getOwner(), Messages.CLAN_NO_LONGER_EXIST.replace("%clan%", clanName));
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
            if (!((index + 1) >= players.size())) {
                setPage(getPage() + 1);
                open();
            } else {
                MessageUtil.sendMessage(getOwner(), Messages.LAST_PAGE);
            }
        }
        if (itemCustomData.equals("close"))
            getOwner().closeInventory();

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(getOwner().getName());

        if (itemCustomData.equals("back")) {
            if (fromViewClan) {
                new ViewClanInventory(getOwner(), clanName).open();
                return;
            }
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {
                    new MembersMenuInventory(getOwner()).open();
                    return;
                }
            }
            new ViewClanInventory(getOwner(), clanName).open();
        }
        if (itemCustomData.equals("sort")) {
            if (sortItemType == SortItemType.PERMISSION)
                sortItemType = SortItemType.SCORECOLLECTED;
            else if (sortItemType == SortItemType.SCORECOLLECTED)
                sortItemType = SortItemType.JOINDATE;
            else if (sortItemType == SortItemType.JOINDATE)
                sortItemType = SortItemType.PERMISSION;
            setSearch(null);
            setPage(0);
            super.open();
        }

        if (PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()) == null)
            return;

        if (!PluginDataManager.getClanDatabaseByPlayerName(getOwner().getName()).getName().equals(clanName))
            return;

        if (itemCustomData.contains("player=")) {
            if (playerData.getClan() != null) {
                if (playerData.getClan().equals(clanName)) {
                    playClickSound(fileConfiguration, "player");
                    itemCustomData = itemCustomData.replace("player=", "");
                    new ManageMemberInventory(getOwner(), itemCustomData).open();
                    return;
                }
            }
            MessageUtil.sendMessage(getOwner(), Messages.TARGET_CLAN_MEMBERSHIP_ERROR.replace("%player%", itemCustomData.replace("player=", "")));
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

            ItemStack sortItem = ClansPlus.nms.addCustomData(ItemUtil.getItem(fileConfiguration.getString("items.sort.type"),
                    fileConfiguration.getString("items.sort.value"),
                    fileConfiguration.getInt("items.sort.customModelData"),
                    fileConfiguration.getString("items.sort.name"),
                    fileConfiguration.getStringList("items.sort.lore." + sortItemType.toString()), false), "sort");
            int sortItemSlot = fileConfiguration.getInt("items.sort.slot");
            if (sortItemSlot < 0)
                sortItemSlot = 0;
            if (sortItemSlot > 8)
                sortItemSlot = 8;
            sortItemSlot = (getSlots() - 9) + sortItemSlot;
            inventory.setItem(sortItemSlot, sortItem);

            if (PluginDataManager.getClanDatabase().isEmpty())
                return;

            players.clear();

            if (sortItemType == SortItemType.PERMISSION) {
                for (String member : PluginDataManager.getClanDatabase(clanName).getMembers()) {
                    IPlayerData memberData = PluginDataManager.getPlayerDatabase(member);
                    List<IPlayerData> playerDataList = new ArrayList<>();
                    playerDataList.add(memberData);
                    playerDataList.sort(Comparator.comparing((IPlayerData playerData) -> PluginDataManager.getPlayerDatabase(member).getRank()).reversed());
                    for (IPlayerData playerData : playerDataList)
                        players.add(playerData.getPlayerName());
                }
            }
            if (sortItemType == SortItemType.SCORECOLLECTED) {
                HashMap<String, Long> playersScore = new HashMap<>();
                for (String member : PluginDataManager.getClanDatabase(clanName).getMembers()) {
                    playersScore.put(member, PluginDataManager.getPlayerDatabase(member).getScoreCollected());
                }
                players = HashMapUtil.sortFromGreatestToLowestL(playersScore);
            }
            if (sortItemType == SortItemType.JOINDATE) {
                HashMap<String, Long> playersJoinDate = new HashMap<>();
                for (String member : PluginDataManager.getClanDatabase(clanName).getMembers()) {
                    playersJoinDate.put(member, PluginDataManager.getPlayerDatabase(member).getJoinDate());
                }
                players = HashMapUtil.sortFromLowestToGreatestL(playersJoinDate);
            }

            if (getSearch() != null) {
                List<String> newPlayers = new ArrayList<>();
                for (String player : players) {
                    if (player.toLowerCase().contains(getSearch().toLowerCase())) {
                        newPlayers.add(player);
                    }
                }
                players.clear();
                players.addAll(newPlayers);
            }

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * getPage() + i;
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
        itemName = itemName.replace("%player%", playerName);
        itemMeta.setDisplayName(ClansPlus.nms.addColor(itemName));

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> ClansPlus.nms.addColor(string.replace("%player%", playerName)
                        .replace("%uuid%", playerData.getUUID() == null ? ClansPlus.nms.addColor(Messages.UNKNOWN) : playerData.getUUID())
                        .replace("%rank%", ClanManager.getFormatRank(playerData.getRank()))
                        .replace("%joinDate%", StringUtil.dateTimeToDateFormat(playerData.getJoinDate()))
                        .replace("%onlineStatus%", (Bukkit.getPlayer(playerName) != null ? Messages.ONLINE_STATUS_ONLINE : Messages.ONLINE_STATUS_OFFLINE))
                        .replace("%lastActivated%", StringUtil.dateTimeToDateFormat(playerData.getLastActivated()))
                        .replace("%scoreCollected%", String.valueOf(playerData.getScoreCollected()))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public enum SortItemType {
        PERMISSION, SCORECOLLECTED, JOINDATE
    }

}