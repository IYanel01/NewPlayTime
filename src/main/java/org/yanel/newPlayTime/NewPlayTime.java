package org.yanel.newPlayTime;

import org.bukkit.plugin.java.JavaPlugin;
import org.yanel.newPlayTime.Handler.Commands;
import org.yanel.newPlayTime.Listener.GUIListener;
import org.yanel.newPlayTime.Listener.PlayerJoined;
import org.yanel.newPlayTime.Listener.PlayerLeft;
import org.yanel.newPlayTime.Handler.DataManager;
import org.yanel.newPlayTime.Handler.Configs;
import org.yanel.newPlayTime.Handler.PlayerTime;
import org.yanel.newPlayTime.Handler.AFKCheck;

public final class NewPlayTime extends JavaPlugin {

    private PlayerTime playerTime;
    private AFKCheck afkCheck;
    private Configs configs;
    private DataManager dataManager;
    private boolean debugMode = false;

    @Override
    public void onEnable() {
        // Initialize DataManager first
        dataManager = new DataManager(this);

        // Initialize other components
        playerTime = new PlayerTime(this);
        afkCheck = new AFKCheck(this);
        configs = new Configs(this);

        // Register commands
        if (getCommand("npt") != null) {
            getCommand("npt").setExecutor(new Commands(this));
        } else {
            getLogger().severe("Command 'npt' is not defined in plugin.yml");
        }

        if (getCommand("playtime") != null) {
            getCommand("playtime").setExecutor(new Commands(this));
        } else {
            getLogger().severe("Command 'playtime' is not defined in plugin.yml");
        }

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerJoined(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeft(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

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
        return configs;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
