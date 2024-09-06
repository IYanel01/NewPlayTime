package org.yanel.newPlayTime.Handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PlayerTimerGUI {

    private final JavaPlugin plugin;
    private final Player player;

    public PlayerTimerGUI(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        // Create an inventory with 9 slots and a title "Playtime GUI"
        Inventory gui = Bukkit.createInventory(null, 9, "Playtime GUI");

        // Set the player's head in the center slot (index 4) with custom lore (player stats)
        gui.setItem(4, returnHeadWithStats(player));

        // Open the GUI for the player
        player.openInventory(gui);
    }

    // Method to return the player's head with stats in the lore
    public ItemStack returnHeadWithStats(Player p) {
        ItemStack head = getHead(p);
        ItemMeta headMeta = head.getItemMeta();

        if (headMeta != null) {
            headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7" + p.getName()));

            // Retrieve the player's stats
            String joinCount = String.valueOf(plugin.getConfig().getInt("players." + p.getUniqueId() + ".joinCount", 0));
            String firstJoin = plugin.getConfig().getString("players." + p.getUniqueId() + ".firstJoin", "Unknown");
            String lastLeave = plugin.getConfig().getString("players." + p.getUniqueId() + ".lastLeave", "Unknown");
            String totalTime = formatTime(plugin.getConfig().getLong("players." + p.getUniqueId() + ".totalTime", 0));

            // Add lore with the player's stats
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Join Count: " + ChatColor.WHITE + joinCount);
            lore.add(ChatColor.GOLD + "First Join: " + ChatColor.WHITE + firstJoin);
            lore.add(ChatColor.GOLD + "Last Leave: " + ChatColor.WHITE + lastLeave);
            lore.add(ChatColor.GOLD + "Total Playtime: " + ChatColor.WHITE + totalTime);

            headMeta.setLore(lore);
            head.setItemMeta(headMeta);
        }
        return head;
    }

    // Method to get the player's head (compatible with Minecraft 1.8.8)
    public ItemStack getHead(Player player) {
        // Use SKULL_ITEM with data value 3 to represent a player's head in 1.8.8
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player.getName());  // In 1.8.8, use setOwner instead of setOwningPlayer
        item.setItemMeta(meta);
        return item;
    }

    private String formatTime(long timeMillis) {
        long seconds = (timeMillis / 1000) % 60;
        long minutes = (timeMillis / (1000 * 60)) % 60;
        long hours = (timeMillis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
