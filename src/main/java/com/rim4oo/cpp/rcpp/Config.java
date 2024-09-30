package com.rim4oo.cpp.rcpp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class Config {
    private final File file;
    private final FileConfiguration config;

    public Config(String name) {
        File dataFolder = Rim4oosCapturePointsPlugin.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, name);
        try {
            if (!file.exists() && !file.createNewFile()) throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException("aa", e);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public int getKey(String key) {
        Rim4oosCapturePointsPlugin.getDataOp().isConfigEmpty();
        return config.getInt(key);
    }
    public String getLang() {
        Rim4oosCapturePointsPlugin.getDataOp().isConfigEmpty();
        return config.getString("Language");
    }

    public void isConfigEmpty() {
        if (config.getKeys(false).isEmpty()){
            config.set("Language", "en");
            config.set("MinOnline", 1);
            config.set("MinimalOnlineInOpposingTeams", 2);
            config.set("DelayBetweenResourceSpawns", 400);
            config.set("Distance2MaintainPointWork", 24);
            config.set("Distance2StartPointWork", 16);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
