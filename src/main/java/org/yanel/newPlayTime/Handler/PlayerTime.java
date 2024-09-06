package org.yanel.newPlayTime.Handler;

import org.bukkit.Bukkit;
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
        int joinCount = dataConfig.getInt("players." + uuid + ".joinCount", 0) + 1;
        dataConfig.set("players." + uuid + ".firstJoin", dataConfig.getString("players." + uuid + ".firstJoin", player.getFirstPlayed() + ""));
        dataConfig.set("players." + uuid + ".lastLeave", dataConfig.getString("players." + uuid + ".lastLeave", ""));
        dataConfig.set("players." + uuid + ".joinCount", joinCount);
        dataManager.saveData();

        // Send the welcome message
        String message = plugin.getConfigs().getMessagesConfig().getString("messages.first_join")
                .replace("%player%", player.getName())
                .replace("%joins%", String.valueOf(joinCount));
        player.sendMessage(message);
    }

    // Called when the player leaves
    public void playerLeft(Player player) {
        UUID uuid = player.getUniqueId();
        long joinTime = joinTimes.getOrDefault(uuid, System.currentTimeMillis());
        long sessionTime = System.currentTimeMillis() - joinTime;

        FileConfiguration dataConfig = dataManager.getDataConfig();
        long totalTime = dataConfig.getLong("players." + uuid + ".totalTime", 0) + sessionTime;
        dataConfig.set("players." + uuid + ".totalTime", totalTime);
        dataConfig.set("players." + uuid + ".lastLeave", System.currentTimeMillis() + "");
        dataManager.saveData();

        // Send the player the total playtime message
        String message = plugin.getConfigs().getMessagesConfig().getString("messages.last_leave")
                .replace("%player%", player.getName())
                .replace("%time%", formatTime(sessionTime));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Start the timer for playtime tracking (e.g., player returned from AFK)
    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (afkStatus.getOrDefault(uuid, false)) {
            afkStatus.put(uuid, false);  // Mark the player as not AFK
            long currentTime = System.currentTimeMillis();
            joinTimes.put(uuid, currentTime);  // Restart the session time
            player.sendMessage(plugin.getConfigs().getMessagesConfig().getString("messages.afk_exit"));
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

            player.sendMessage(plugin.getConfigs().getMessagesConfig().getString("messages.afk_message"));
        }
    }

    private String formatTime(long timeMillis) {
        long seconds = (timeMillis / 1000) % 60;
        long minutes = (timeMillis / (1000 * 60)) % 60;
        long hours = (timeMillis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
