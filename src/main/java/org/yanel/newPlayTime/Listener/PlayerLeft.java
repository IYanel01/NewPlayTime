package org.yanel.newPlayTime.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.yanel.newPlayTime.NewPlayTime;

public class PlayerLeft implements Listener {

    private final NewPlayTime plugin;

    public PlayerLeft(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerTime().playerLeft(event.getPlayer());
    }
}
