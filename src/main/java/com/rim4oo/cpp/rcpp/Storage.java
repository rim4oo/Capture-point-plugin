package com.rim4oo.cpp.rcpp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
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
            throw new RuntimeException("aa", e);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }


    public FileConfiguration getConfig() {
        return config;
    }


    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + file, e);
        }

    }

    public String getKey(String key) {
        return config.getString(key);
    }

    public boolean getWork(String key) {
        String data = Rim4oosCapturePointsPlugin.getData().getKey(key);
        String[] values = data.split(",");
        return Boolean.parseBoolean(values[3].trim()); // work находится на 4-м месте, но индексация начинается с 0
    }

    public String getTeamVl(String key) {
        String data = Rim4oosCapturePointsPlugin.getData().getKey(key);
        String[] values = data.split(",");
        return values[4].trim(); // teamVl находится на 5-м месте, но индексация начинается с 0
    }


    public void setWork(String key, boolean work) {
        String data = Rim4oosCapturePointsPlugin.getData().getKey(key);
        String[] values = data.split(",");
        StringBuilder itemIds = new StringBuilder();
        for (int i = 5; i < values.length; i++) {
            itemIds.append(values[i].trim());
            if (i != values.length - 1) {
                itemIds.append(",");
            }
        }

        int cordsX = Integer.parseInt(values[0].trim());
        int cordsY = Integer.parseInt(values[1].trim());
        int cordsZ = Integer.parseInt(values[2].trim());
        String team = (values[4].trim());
        String value = cordsX + "," + cordsY + "," + cordsZ + "," + work + "," + team + "," + itemIds;
        Rim4oosCapturePointsPlugin.getData().getConfig().set(key, value);
    }

    public void setTeamVl(String key, String teamVl) {
        String data = Rim4oosCapturePointsPlugin.getData().getKey(key);
        String[] values = data.split(",");
        StringBuilder itemIds = new StringBuilder();
        for (int i = 5; i < values.length; i++) {
            itemIds.append(values[i].trim());
            if (i != values.length - 1) {
                itemIds.append(",");
            }
        }

        int cordsX = Integer.parseInt(values[0].trim()); // trim удаляет пробелы в начале и конце строки
        int cordsY = Integer.parseInt(values[1].trim());
        int cordsZ = Integer.parseInt(values[2].trim());
        boolean work = Boolean.parseBoolean(values[3]);
        String value = cordsX + "," + cordsY + "," + cordsZ + "," + work + "," + teamVl + "," + itemIds;
        Rim4oosCapturePointsPlugin.getData().getConfig().set(key, value);
    }
}
