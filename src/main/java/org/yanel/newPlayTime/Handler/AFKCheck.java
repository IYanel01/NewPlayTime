package org.yanel.newPlayTime.Handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yanel.newPlayTime.NewPlayTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKCheck {

    private final NewPlayTime plugin;
    private final Map<UUID, Location> playerLocations = new HashMap<>();

    public AFKCheck(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("afk_detection")) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    Location currentLocation = player.getLocation();

                    if (playerLocations.containsKey(uuid)) {
                        Location previousLocation = playerLocations.get(uuid);

                        // If the player hasn't moved
                        if (previousLocation.equals(currentLocation)) {
                            plugin.getPlayerTime().stopTimer(player);  // Stops the timer when AFK
                        } else {
                            plugin.getPlayerTime().startTimer(player);  // Restarts the timer when player moves
                        }
                    }

                    // Update player location
                    playerLocations.put(uuid, currentLocation);
                }
            }
        }.runTaskTimer(plugin, 20 * 60, 20 * 60);  // Runs every minute
    }
}
