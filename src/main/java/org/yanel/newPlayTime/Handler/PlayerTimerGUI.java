package org.yanel.newPlayTime.Handler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yanel.newPlayTime.NewPlayTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerTimerGUI {

    private final NewPlayTime plugin;
    private final Player player;

    public PlayerTimerGUI(NewPlayTime plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        // Create an inventory with 9 slots and a title "Playtime GUI"
        Inventory gui = plugin.getServer().createInventory(null, 9, "Playtime GUI");

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

            // Retrieve the player's stats from data.yml via DataManager
            DataManager dataManager = plugin.getDataManager();
            String uuid = p.getUniqueId().toString();

            // Get the firstJoin and lastLeave as timestamps from data.yml
            long firstJoinTimestamp = dataManager.getDataConfig().getLong("players." + uuid + ".firstJoin", 0);
            long lastLeaveTimestamp = dataManager.getDataConfig().getLong("players." + uuid + ".lastLeave", 0);
            int joinCount = dataManager.getDataConfig().getInt("players." + uuid + ".joinCount", 0);
            long totalTime = dataManager.getDataConfig().getLong("players." + uuid + ".totalTime", 0);

            // Format the timestamps into human-readable dates
            String firstJoin = formatDate(firstJoinTimestamp);
            String lastLeave = formatDate(lastLeaveTimestamp);
            String playtime = formatTime(totalTime);

            // Add lore with the player's stats
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Join Count: " + ChatColor.WHITE + joinCount);
            lore.add(ChatColor.GOLD + "First Join: " + ChatColor.WHITE + firstJoin);
            lore.add(ChatColor.GOLD + "Last Leave: " + ChatColor.WHITE + lastLeave);  // Show Last Leave
            lore.add(ChatColor.GOLD + "Total Playtime: " + ChatColor.WHITE + playtime);

            headMeta.setLore(lore);
            head.setItemMeta(headMeta);
        }
        return head;
    }

    // Method to format the Unix timestamp into a readable date
    private String formatDate(long timestamp) {
        if (timestamp == 0) {
            return "Unknown";  // Return "Unknown" if no timestamp is found
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    // Method to get the player's head
    public ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwner(player.getName());
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
