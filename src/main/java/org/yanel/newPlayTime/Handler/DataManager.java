package org.yanel.newPlayTime.Handler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yanel.newPlayTime.NewPlayTime;

import java.io.File;
import java.io.IOException;

public class DataManager {

    private final NewPlayTime plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(NewPlayTime plugin) {
        this.plugin = plugin;
        createDataFile();
    }

    private void createDataFile() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
