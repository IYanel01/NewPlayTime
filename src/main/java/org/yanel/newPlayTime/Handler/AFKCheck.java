package org.yanel.newPlayTime.Handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yanel.newPlayTime.NewPlayTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKCheck {

    private final NewPlayTime plugin;
    private final Map<UUID, Location> playerLocations = new HashMap<>();
    private final Map<UUID, Long> afkStartTimes = new HashMap<>();  // Track when player might be AFK
    private final Map<UUID, Boolean> warningIssued = new HashMap<>();  // Track if the warning has been issued
    private final Map<UUID, Boolean> isAfk = new HashMap<>();  // Track if the player is currently AFK
    private final Map<UUID, Boolean> afkMessageSent = new HashMap<>();  // Track if AFK message has been sent
    private final long afkTimeout;  // AFK timeout in milliseconds
    private final long afkWarningTime;  // Half of AFK timeout, after which the player is warned

    public AFKCheck(NewPlayTime plugin) {
        this.plugin = plugin;
        // Get the afk_timeout value from config (default is 5 minutes)
        this.afkTimeout = plugin.getConfig().getLong("afk_timeout", 5) * 60 * 1000;  // Convert minutes to milliseconds
        this.afkWarningTime = afkTimeout / 2;  // Half of the timeout for the warning
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("afk_detection")) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    Location currentLocation = player.getLocation();

                    // Debugging: Log current player status
                    if (plugin.isDebugMode()) {
                        plugin.getLogger().info("Checking AFK for player: " + player.getName());
                    }

                    if (playerLocations.containsKey(uuid)) {
                        Location previousLocation = playerLocations.get(uuid);

                        // Check if the player has moved
                        if (!previousLocation.equals(currentLocation)) {
                            if (plugin.isDebugMode()) {
                                plugin.getLogger().info("Player moved: " + player.getName());
                            }

                            // Player has moved, immediately remove AFK status if they were AFK
                            if (isAfk.getOrDefault(uuid, false)) {
                                plugin.getPlayerTime().startTimer(player);  // Restart the timer when player moves

                                // Send exit AFK message once
                                if (!afkMessageSent.getOrDefault(uuid, false)) {
                                    player.sendMessage(colorize(plugin.getConfigs().getMessagesConfig().getString("messages.afk_exit")));
                                    afkMessageSent.put(uuid, true);  // Mark AFK exit message as sent
                                }

                                isAfk.put(uuid, false);  // Mark the player as no longer AFK
                            }

                            // Reset AFK tracking
                            afkStartTimes.remove(uuid);  // Remove AFK start time since they moved
                            warningIssued.remove(uuid);  // Reset the warning flag
                        } else {
                            // Player hasn't moved
                            if (!afkStartTimes.containsKey(uuid)) {
                                // Player hasn't moved, start tracking AFK start time
                                afkStartTimes.put(uuid, System.currentTimeMillis());
                                warningIssued.put(uuid, false);  // Reset warning flag

                                if (plugin.isDebugMode()) {
                                    plugin.getLogger().info("Started tracking AFK for player: " + player.getName());
                                }
                            } else {
                                // Check if the player has been AFK for longer than the warning time
                                long timeAFK = System.currentTimeMillis() - afkStartTimes.get(uuid);
                                if (timeAFK >= afkWarningTime && !warningIssued.get(uuid)) {
                                    // Play a sound to warn the player
                                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);

                                    // Get the AFK warning message from messages.yml
                                    String afkWarningMessage = plugin.getConfigs().getMessagesConfig().getString("messages.afk_warning");
                                    player.sendMessage(colorize(afkWarningMessage));  // Send warning with color codes

                                    warningIssued.put(uuid, true);  // Mark that the warning has been issued

                                    if (plugin.isDebugMode()) {
                                        plugin.getLogger().info("AFK warning sent to player: " + player.getName());
                                    }
                                }

                                // Check if the player has been AFK for longer than the timeout
                                if (timeAFK >= afkTimeout && !isAfk.getOrDefault(uuid, false)) {
                                    plugin.getPlayerTime().stopTimer(player);  // Stop the timer when AFK

                                    // Send AFK message once
                                    if (!afkMessageSent.getOrDefault(uuid, false)) {
                                        player.sendMessage(colorize(plugin.getConfigs().getMessagesConfig().getString("messages.afk_message")));
                                        afkMessageSent.put(uuid, true);  // Mark AFK message as sent
                                    }

                                    isAfk.put(uuid, true);  // Mark the player as AFK

                                    if (plugin.isDebugMode()) {
                                        plugin.getLogger().info("Player marked as AFK: " + player.getName());
                                    }
                                }
                            }
                        }
                    }

                    // Update player location
                    playerLocations.put(uuid, currentLocation);
                }
            }
        }.runTaskTimer(plugin, 20, 20);  // Runs every tick (every 1 second)
    }

    // Helper method to translate color codes
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
