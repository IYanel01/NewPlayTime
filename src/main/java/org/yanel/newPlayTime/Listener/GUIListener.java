package org.yanel.newPlayTime.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yanel.newPlayTime.NewPlayTime;
import org.yanel.newPlayTime.Handler.DataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GUIListener implements Listener {

    private final NewPlayTime plugin;
    private final DataManager dataManager;

    public GUIListener(NewPlayTime plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();  // Access the DataManager to fetch player data
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Check if the player clicked in the "Playtime GUI"
        if (event.getView().getTitle().equalsIgnoreCase("Playtime GUI")) {
            event.setCancelled(true);  // Prevent taking the item

            // Get the clicked item
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            // If the clicked item is the player's head, open the stats GUI
            if (clickedItem.getType() == Material.SKULL_ITEM) {
                openStatsGUI(player);
            }
        }

        // Check if the player clicked in the "Player Stats" GUI
        if (event.getView().getTitle().equalsIgnoreCase("Player Stats")) {
            event.setCancelled(true);  // Prevent taking the item

            // Get the clicked item
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            ItemMeta itemMeta = clickedItem.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();

                // Send the relevant stat message to the player and close the inventory
                player.closeInventory();  // Close the GUI

                if (displayName.contains("Join Count")) {
                    player.sendMessage(ChatColor.GOLD + "Join Count: " + itemMeta.getLore().get(0));
                } else if (displayName.contains("First Join")) {
                    player.sendMessage(ChatColor.GOLD + "First Join: " + itemMeta.getLore().get(0));
                } else if (displayName.contains("Last Leave")) {
                    player.sendMessage(ChatColor.GOLD + "Last Leave: " + itemMeta.getLore().get(0));
                } else if (displayName.contains("Total Playtime")) {
                    player.sendMessage(ChatColor.GOLD + "Total Playtime: " + itemMeta.getLore().get(0));
                }
            }
        }
    }

    // Opens a new inventory showing each stat as an item
    public void openStatsGUI(Player player) {
        Inventory statsGUI = Bukkit.createInventory(null, 27, "Player Stats");

        // Retrieve the player's UUID
        UUID uuid = player.getUniqueId();

        // Fetch the stats from data.yml
        int joinCount = dataManager.getDataConfig().getInt("players." + uuid + ".joinCount", 0);
        long firstJoinTimestamp = dataManager.getDataConfig().getLong("players." + uuid + ".firstJoin", 0);
        long lastLeaveTimestamp = dataManager.getDataConfig().getLong("players." + uuid + ".lastLeave", 0);
        long totalTime = dataManager.getDataConfig().getLong("players." + uuid + ".totalTime", 0);

        // Convert timestamps into readable dates
        String firstJoin = firstJoinTimestamp > 0 ? formatDate(firstJoinTimestamp) : "Unknown";
        String lastLeave = lastLeaveTimestamp > 0 ? formatDate(lastLeaveTimestamp) : "Unknown";
        String totalPlaytime = formatTime(totalTime);

        // Add the stats as items
        statsGUI.setItem(22, createStatItem(Material.BOOK, ChatColor.GOLD + "Join Count", String.valueOf(joinCount)));
        statsGUI.setItem(12, createStatItem(Material.WATCH, ChatColor.GOLD + "First Join", firstJoin));
        statsGUI.setItem(14, createStatItem(Material.WATCH, ChatColor.GOLD + "Last Leave", lastLeave));
        statsGUI.setItem(4, createStatItem(Material.PAPER, ChatColor.GOLD + "Total Playtime", totalPlaytime));

        player.openInventory(statsGUI);
    }

    // Create an item with a name and value
    private ItemStack createStatItem(Material material, String name, String value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + value);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Format Unix timestamp into a readable date (MM/dd/yyyy)
    private String formatDate(long timestamp) {
        if (timestamp == 0) {
            return "Unknown";  // Return "Unknown" if no timestamp is found
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    // Format time into HH:mm:ss
    private String formatTime(long timeMillis) {
        long seconds = (timeMillis / 1000) % 60;
        long minutes = (timeMillis / (1000 * 60)) % 60;
        long hours = (timeMillis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
