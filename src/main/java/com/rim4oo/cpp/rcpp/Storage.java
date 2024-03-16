package com.rim4oo.cpp.rcpp;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Storage {

    private File file;
    private FileConfiguration config;

    public Storage(String name) {
        File dataFolder = Rim4oosCapturePointsPlugin.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, name);
        try {
            if (!file.exists() && !file.createNewFile()) throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException("Ты че чурбан?", e);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }


    public FileConfiguration getConfig() {
        return config;
    }// Полное получение конфига

    public void save() {
        try {
            config.save(file);
        }catch (IOException e) {
            throw new RuntimeException("Failed to save " + file, e);
        }
        //MainFile.getData().getConfig().set(key, data);

    }

    public void load() {
        try {
            config.load(file);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException("InvalidConfiguration " + file, e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(file + " not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + file, e);
        }
    }
    public String getKey(String key){
        return config.getString(key);
    } //сосёт значения ключа
}
