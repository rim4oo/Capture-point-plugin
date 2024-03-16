package com.rim4oo.cpp.rcpp.commads;

import com.rim4oo.cpp.rcpp.Rim4oosCapturePointsPlugin;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class aCommand implements CommandExecutor, TabCompleter {

    protected PluginCommand pluginCommand;

    public aCommand(String command){
        this.pluginCommand =  Rim4oosCapturePointsPlugin.getInstance().getCommand(command);
        if(this.pluginCommand != null) {
            this.pluginCommand.setExecutor(this);
            this.pluginCommand.setTabCompleter(this);
        }
    }
    public abstract void execute(CommandSender commandSender, String s, String[] strings);

    public List<String> comlete(CommandSender sender, String[] strings) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings){
        execute(commandSender, s, strings);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings){
        return filter(comlete(commandSender, strings), strings);
    }

    private List<String> filter(List<String> list, String[] strings){
        if(list == null) return null;
        String last = strings[strings.length - 1];
        List<String> result = new ArrayList<>();
        for (String str: list ){
            if(str.toLowerCase().startsWith(last.toLowerCase())) result.add(str);
        }
        return result;
    }

    public abstract List<String> complete(CommandSender sender, String[] strings);
}
