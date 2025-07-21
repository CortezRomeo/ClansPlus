package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.inventory.PaginatedInventory;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

public class SignChangeListener implements Listener {

    private static final HashMap<String, PlayerSignDatabase> searchingQueryInventoryList = new HashMap<>();

    public SignChangeListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    public static void addSearchPlayerQuery(Player player, InventoryHolder inventoryHolder) {
        Location loc = player.getLocation().add(0, 1, 0);
        Block block = loc.getBlock();
        block.setType(Material.OAK_SIGN);
        player.openSign((Sign) block.getState(), Side.FRONT);
        ClansPlus.support.getFoliaLib().getScheduler().runLater(wrappedTask -> {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    other.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                }
            }
        }, 1);
        searchingQueryInventoryList.put(player.getName(), new PlayerSignDatabase(block, inventoryHolder, new TimeOutTask(player)));
        MessageUtil.sendMessage(player, Messages.USING_SIGN_INPUT_INVENTORY_LIST_SEARCH.replace("%seconds%", String.valueOf(Settings.SIGN_INPUT_SETTINGS_TIME_OUT)));
    }

    private static PlayerSignDatabase getPlayerSignDatabase(String playerName) {
        if (Bukkit.getPlayer(playerName) == null)
            return null;
        if (searchingQueryInventoryList.containsKey(playerName))
            return searchingQueryInventoryList.get(playerName);
        return null;
    }

    public static void removeSearchPlayerQuery(Player player) {
        if (player != null) {
            getPlayerSignDatabase(player.getName()).getBlock().setType(Material.AIR);
            getPlayerSignDatabase(player.getName()).getTimeOutTask().cancel();
            searchingQueryInventoryList.remove(player);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (getPlayerSignDatabase(player.getName()) != null) {
            InventoryHolder holder = getPlayerSignDatabase(player.getName()).getInventoryHolder().getInventory().getHolder();
            if (holder instanceof PaginatedInventory)
                ((PaginatedInventory) holder).onSearch(event);
            removeSearchPlayerQuery(player);
        }
    }

    static class PlayerSignDatabase {

        private final Block block;
        private final InventoryHolder inventoryHolder;
        private final TimeOutTask timeOutTask;

        public PlayerSignDatabase(Block block, InventoryHolder inventoryHolder, TimeOutTask timeOutTask) {
            this.block = block;
            this.inventoryHolder = inventoryHolder;
            this.timeOutTask = timeOutTask;
        }

        public Block getBlock() {
            return block;
        }

        public InventoryHolder getInventoryHolder() {
            return inventoryHolder;
        }

        public TimeOutTask getTimeOutTask() {
            return timeOutTask;
        }
    }

    static class TimeOutTask implements Runnable {

        private final Player player;
        private final WrappedTask timeOutTask;
        boolean cancelled = false;

        public TimeOutTask(Player player) {
            this.player = player;
            this.timeOutTask = ClansPlus.support.getFoliaLib().getScheduler().runAtLocationLater(player.getLocation(), this,  20L * (long) Settings.SIGN_INPUT_SETTINGS_TIME_OUT);
        }

        @Override
        public void run() {
            if (timeOutTask.isCancelled() || cancelled)
                return;

            if (getPlayerSignDatabase(player.getName()) != null) {
                if (player == null)
                    return;

                MessageUtil.sendMessage(player, Messages.USING_SIGN_INPUT_TIME_OUT);
                removeSearchPlayerQuery(player);
            }
        }

        public void cancel() {
            timeOutTask.cancel();
            cancelled = true;
        }
    }

}
