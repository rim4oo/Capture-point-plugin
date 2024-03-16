package com.rim4oo.cpp.rcpp;
import com.rim4oo.cpp.rcpp.commads.pointCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Rim4oosCapturePointsPlugin extends JavaPlugin {

    private Storage data;
    private static Rim4oosCapturePointsPlugin instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        data = new Storage("Points.yml");
        // Создайте новый экземпляр pointCommand
        pointCommand pointCommandInstance = new pointCommand();
        // Зарегистрируйте pointCommandInstance как исполнителя команды "point"
        Objects.requireNonNull(this.getCommand("point")).setExecutor(pointCommandInstance);
        // Зарегистрируйте pointCommandInstance как обработчик ввода
        Objects.requireNonNull(this.getCommand("point")).setTabCompleter(pointCommandInstance);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static Rim4oosCapturePointsPlugin getInstance(){
        return instance;
    }
    public static Storage getData() {
        return instance.data;
    }
}

