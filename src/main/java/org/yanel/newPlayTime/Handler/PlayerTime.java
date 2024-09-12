package org.yanel.newPlayTime.Handler;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.yanel.newPlayTime.NewPlayTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTime {

    private final NewPlayTime plugin;
    private final DataManager dataManager;  // Use DataManager to handle data.yml
    private final Map<UUID, Long> joinTimes = new HashMap<>();
    private final Map<UUID, Boolean> afkStatus = new HashMap<>();  // To track if the player is AFK

    public PlayerTime(NewPlayTime plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();  // Access DataManager from the plugin
    }

    // Called when the player joins
    public void playerJoined(Player player) {
        UUID uuid = player.getUniqueId();
        long joinTime = System.currentTimeMillis();
        joinTimes.put(uuid, joinTime);
        afkStatus.put(uuid, false);  // Assume player is not AFK at join

        FileConfiguration dataConfig = dataManager.getDataConfig();

        // Check if it's the player's first join by checking if the firstJoin value is already set
        if (!dataConfig.contains("players." + uuid + ".firstJoin")) {
            // If firstJoin is not recorded, register it with the current time
            dataConfig.set("players." + uuid + ".firstJoin", joinTime);
        }

        // Update other player data
        int joinCount = dataConfig.getInt("players." + uuid + ".joinCount", 0) + 1;
        dataConfig.set("players." + uuid + ".joinCount", joinCount);
        dataManager.saveData();

        // Send the welcome message with colors
        String message = plugin.getConfigs().getMessagesConfig().getString("messages.first_join")
                .replace("%player%", player.getName())
                .replace("%joins%", String.valueOf(joinCount));
        player.sendMessage(colorize(message));
    }

    public void playerLeft(Player player) {
        UUID uuid = player.getUniqueId();
        long joinTime = joinTimes.getOrDefault(uuid, System.currentTimeMillis());
        long sessionTime = System.currentTimeMillis() - joinTime;

        FileConfiguration dataConfig = dataManager.getDataConfig();
        long totalTime = dataConfig.getLong("players." + uuid + ".totalTime", 0) + sessionTime;
        dataConfig.set("players." + uuid + ".totalTime", totalTime);
        dataConfig.set("players." + uuid + ".lastLeave", System.currentTimeMillis());  // Store lastLeave as long (timestamp)
        dataManager.saveData();

        // Send the player the total playtime message
        String message = plugin.getConfigs().getMessagesConfig().getString("messages.last_leave")
                .replace("%player%", player.getName())
                .replace("%time%", formatTime(sessionTime));
        player.sendMessage(colorize(message));  // Make sure to send the message with colors
    }

    // Start the timer for playtime tracking (e.g., player returned from AFK)
    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (afkStatus.getOrDefault(uuid, false)) {
            afkStatus.put(uuid, false);  // Mark the player as not AFK
            long currentTime = System.currentTimeMillis();
            joinTimes.put(uuid, currentTime);  // Restart the session time

            // Fetch AFK exit message and send it with colors
            String afkExitMessage = plugin.getConfigs().getMessagesConfig().getString("messages.afk_exit");

            // Make sure message is only sent once
            if (afkExitMessage != null && !afkExitMessage.isEmpty()) {
                player.sendMessage(colorize(afkExitMessage));
            }
        }
    }

    // Stop the timer for playtime tracking (e.g., player went AFK)
    public void stopTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!afkStatus.getOrDefault(uuid, false)) {
            afkStatus.put(uuid, true);  // Mark the player as AFK
            long joinTime = joinTimes.get(uuid);
            long sessionTime = System.currentTimeMillis() - joinTime;

            FileConfiguration dataConfig = dataManager.getDataConfig();
            long totalTime = dataConfig.getLong("players." + uuid + ".totalTime", 0) + sessionTime;
            dataConfig.set("players." + uuid + ".totalTime", totalTime);
            dataManager.saveData();

            // Fetch AFK message and send it with colors
            String afkMessage = plugin.getConfigs().getMessagesConfig().getString("messages.afk_message");

            // Make sure message is only sent once
            if (afkMessage != null && !afkMessage.isEmpty()) {
                player.sendMessage(colorize(afkMessage));
            }
        }
    }

    // Helper method to format time into HH:mm:ss
    private String formatTime(long timeMillis) {
        long seconds = (timeMillis / 1000) % 60;
        long minutes = (timeMillis / (1000 * 60)) % 60;
        long hours = (timeMillis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Helper method to translate color codes
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
