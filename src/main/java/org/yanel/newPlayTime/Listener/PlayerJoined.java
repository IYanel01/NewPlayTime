package org.yanel.newPlayTime.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.yanel.newPlayTime.NewPlayTime;

public class PlayerJoined implements Listener {

    private final NewPlayTime plugin;

    public PlayerJoined(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerTime().playerJoined(event.getPlayer());
    }
}
