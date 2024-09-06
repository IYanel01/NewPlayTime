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

import java.util.ArrayList;
import java.util.List;

public class GUIListener implements Listener {

    private final NewPlayTime plugin;

    public GUIListener(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Check if the player clicked in the "Playtime GUI"
        if (event.getView().getTitle().equalsIgnoreCase("Playtime GUI")) {
            event.setCancelled(true);  // Prevent taking the item

            // Check if the clicked item is the player's head
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                openStatsGUI(player);
            }
        }
    }

    // Opens a new inventory showing each stat as an item
    public void openStatsGUI(Player player) {
        Inventory statsGUI = Bukkit.createInventory(null, 9, "Player Stats");

        // Add the stats as items
        statsGUI.setItem(0, createStatItem(Material.BOOK, ChatColor.GOLD + "Join Count", String.valueOf(plugin.getConfig().getInt("players." + player.getUniqueId() + ".joinCount", 0))));
        statsGUI.setItem(1, createStatItem(Material.WATCH, ChatColor.GOLD + "First Join", plugin.getConfig().getString("players." + player.getUniqueId() + ".firstJoin", "Unknown")));
        statsGUI.setItem(2, createStatItem(Material.WATCH, ChatColor.GOLD + "Last Leave", plugin.getConfig().getString("players." + player.getUniqueId() + ".lastLeave", "Unknown")));
        statsGUI.setItem(3, createStatItem(Material.PAPER, ChatColor.GOLD + "Total Playtime", formatTime(plugin.getConfig().getLong("players." + player.getUniqueId() + ".totalTime", 0))));

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

    private String formatTime(long timeMillis) {
        long seconds = (timeMillis / 1000) % 60;
        long minutes = (timeMillis / (1000 * 60)) % 60;
        long hours = (timeMillis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
