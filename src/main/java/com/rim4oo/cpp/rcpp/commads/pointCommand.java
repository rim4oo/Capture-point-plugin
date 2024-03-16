package com.rim4oo.cpp.rcpp.commads;

import com.google.common.collect.Lists;
import com.rim4oo.cpp.rcpp.Rim4oosCapturePointsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class pointCommand implements CommandExecutor, TabCompleter {

    public pointCommand() {
        Objects.requireNonNull(Rim4oosCapturePointsPlugin.getInstance().getCommand("point")).setExecutor(this);
        Objects.requireNonNull(Rim4oosCapturePointsPlugin.getInstance().getCommand("point")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {

        if (strings[0].equalsIgnoreCase("test")) {
            String key = strings[1];
            commandSender.sendMessage(Rim4oosCapturePointsPlugin.getData().getKey(key));
        }
        
        if (strings.length == 0) {
            commandSender.sendMessage("Недостаточно аргументов в команде");
        }
        if (commandSender.hasPermission("rcpp.reload")) {
            if (strings[0].equalsIgnoreCase("reload")) {
                Rim4oosCapturePointsPlugin.getInstance().reloadConfig();
                commandSender.sendMessage(ChatColor.AQUA + "Успешно!");
            }
        }
        if (!commandSender.hasPermission("rcpp.reload")) {
            if (strings[0].equalsIgnoreCase("reload")) {
                Rim4oosCapturePointsPlugin.getInstance().reloadConfig();
                commandSender.sendMessage(ChatColor.RED + "Недостаточно прав!");
            }
        }
        if(strings[0].equalsIgnoreCase("add")) {
            String itemId = null;
            if  (commandSender.hasPermission("rcpp.addPoint")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    int cordsX = strings[1].equals("~") ? player.getLocation().getBlockX() : Integer.parseInt(strings[1]);
                    int cordsY = strings[2].equals("~") ? player.getLocation().getBlockY() : Integer.parseInt(strings[2]);
                    int cordsZ = strings[3].equals("~") ? player.getLocation().getBlockZ() : Integer.parseInt(strings[3]);
                    String key = strings[4];
                    if (strings.length > 5) {
                        itemId = strings[4];
                    }
                    Rim4oosCapturePointsPlugin.getData().getConfig().set(key, cordsX + ":" + cordsY + ":" + cordsZ + ":" + itemId );
                    commandSender.sendMessage(ChatColor.AQUA +String.valueOf(cordsX) + ", " + String.valueOf(cordsY) + ", " + String.valueOf(cordsZ) + ", " + itemId);
                    commandSender.sendMessage(Rim4oosCapturePointsPlugin.getData().getKey(key));
                }
            }
            if(!commandSender.hasPermission("rcpp.addPoint")) {
                commandSender.sendMessage(ChatColor.RED + "Недостаточно прав!");
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) return Lists.newArrayList("reload", "add");
        if (args.length == 2 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length == 3 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length == 4 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length >= 5 && "add".equalsIgnoreCase(args[0])) {
            List<String> itemIDs = new ArrayList<>();
            for (Material material : Material.values()) {
                itemIDs.add("minecraft:" + material.name().toLowerCase());
            }
            return itemIDs;
        }
        return Lists.newArrayList();
    }

}
