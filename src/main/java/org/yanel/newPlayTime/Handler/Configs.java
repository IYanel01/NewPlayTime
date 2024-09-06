package org.yanel.newPlayTime.Handler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yanel.newPlayTime.NewPlayTime;

import java.io.File;
import java.io.IOException;

public class Configs {

    private final NewPlayTime plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public Configs(NewPlayTime plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
    }

    public void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
