package org.yanel.newPlayTime;

import org.bukkit.plugin.java.JavaPlugin;
import org.yanel.newPlayTime.Handler.*;
import org.yanel.newPlayTime.Listener.GUIListener;
import org.yanel.newPlayTime.Listener.PlayerJoined;
import org.yanel.newPlayTime.Listener.PlayerLeft;

public final class NewPlayTime extends JavaPlugin {

    private PlayerTime playerTime;
    private AFKCheck afkCheck;
    private Configs configs;
    private DataManager dataManager;  // Add DataManager instance

    @Override
    public void onEnable() {
        // Initialize DataManager first
        dataManager = new DataManager(this);  // Initialize DataManager to handle data.yml

        // Initialize PlayerTime and other components
        playerTime = new PlayerTime(this);
        afkCheck = new AFKCheck(this);
        configs = new Configs(this);  // Initialize the Configs class to manage messages.yml

        // Register commands
        getCommand("npt").setExecutor(new Commands(this));
        getCommand("playtime").setExecutor(new Commands(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerJoined(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeft(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);  // Register GUI listener

        // Load configuration
        saveDefaultConfig();

        // Start AFK detection
        afkCheck.start();
    }

    public PlayerTime getPlayerTime() {
        return playerTime;
    }

    public AFKCheck getAfkCheck() {
        return afkCheck;
    }

    public Configs getConfigs() {
        return configs;  // Expose the Configs class to access messages.yml
    }

    public DataManager getDataManager() {
        return dataManager;  // Expose the DataManager class to access data.yml
    }
}
