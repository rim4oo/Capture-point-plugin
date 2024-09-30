package com.rim4oo.cpp.rcpp;
import com.rim4oo.cpp.rcpp.commads.pointCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Rim4oosCapturePointsPlugin extends JavaPlugin {

    private Config data2;
    private Storage data;
    private static Rim4oosCapturePointsPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        data = new Storage("Points.yml");
        data2 = new Config("config.yml");
        Rim4oosCapturePointsPlugin.getDataOp().isConfigEmpty();
        List<String> allPoints = new ArrayList<>(Rim4oosCapturePointsPlugin.getData().getConfig().getKeys(false));
        if (!allPoints.isEmpty()) {
            for (String point : allPoints) {
                Rim4oosCapturePointsPlugin.getData().setWork(point, false);
            }
        }
        saveDefaultConfig();
        // Создайте новый экземпляр pointCommand
        pointCommand pointCommandInstance = new pointCommand();
        // Зарегистрируйте pointCommandInstance как исполнителя команды "point"
        Objects.requireNonNull(this.getCommand("point")).setExecutor(pointCommandInstance);
        // Зарегистрируйте pointCommandInstance как обработчик ввода
        Objects.requireNonNull(this.getCommand("point")).setTabCompleter(pointCommandInstance);
    }
    @Override
    public void onDisable() {

    }
    public static Rim4oosCapturePointsPlugin getInstance(){
        return instance;
    }
    public static Storage getData() {
        return instance.data;
    }
    public static Config getDataOp() {
        return instance.data2;
    }
}
