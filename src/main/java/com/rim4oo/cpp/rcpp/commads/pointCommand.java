package com.rim4oo.cpp.rcpp.commads;

import com.google.common.collect.Lists;
import com.rim4oo.cpp.rcpp.Rim4oosCapturePointsPlugin;
import com.rim4oo.cpp.rcpp.itemSpawner;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import java.util.*;


public class pointCommand implements CommandExecutor, TabCompleter {

    public pointCommand() {
        Objects.requireNonNull(Rim4oosCapturePointsPlugin.getInstance().getCommand("point")).setExecutor(this);
        Objects.requireNonNull(Rim4oosCapturePointsPlugin.getInstance().getCommand("point")).setTabCompleter(this);
    }
    int MinPLayersOnlineOnServer = Rim4oosCapturePointsPlugin.getDataOp().getKey("MinOnline");
    int MinPLayersOnlineInCommand = Rim4oosCapturePointsPlugin.getDataOp().getKey("MinimalOnlineInOpposingTeams");

    int time = Rim4oosCapturePointsPlugin.getDataOp().getKey("DelayBetweenResourceSpawns");

    public String langPrint(String en, String ru){
        if(Rim4oosCapturePointsPlugin.getDataOp().getLang().equalsIgnoreCase("ru")){
            return ru;
        } else{
            return  en;
        }
    }

    public @NotNull String getTeamForPlayer(Player player) {
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        return (team != null) ? team.getName() : "";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, String[] strings) {
        if (strings[0].equalsIgnoreCase("capture") || strings[0].isEmpty())  {
            String closestPoint = "";
            double closestX = 0;
            double closestY = 0;
            double closestZ = 0;
            double minDistance = Double.MAX_VALUE;
            Player player = (Player) commandSender;
            Location playerLocation = player.getLocation();
            double xP = playerLocation.getX();
            double yP = playerLocation.getY();
            double zP = playerLocation.getZ();
            List<String> allPoints = new ArrayList<>(Rim4oosCapturePointsPlugin.getData().getConfig().getKeys(false));
            if (!allPoints.isEmpty()) {
                String[] values;
                for (String point : allPoints) {
                    String key = Rim4oosCapturePointsPlugin.getData().getKey(point);
                    if (key != null) {
                        values = key.split(",");
                            double x = Double.parseDouble(values[0]);
                            double y = Double.parseDouble(values[1]);
                            double z = Double.parseDouble(values[2]);
                            double distance = Math.sqrt(Math.pow(xP - x, 2) + Math.pow(yP - y, 2) + Math.pow(zP - z, 2));
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestPoint = point;
                                closestX = x;
                                closestY = y;
                                closestZ = z;
                            }
                    }
                }



                String keys = Rim4oosCapturePointsPlugin.getData().getKey(closestPoint);
                String[] values3 = keys.split(",");
                List<ItemStack> materialList = new ArrayList<>();

                for (int i = 5; i < values3.length; i++) {
                    Material material = Material.getMaterial(values3[i].trim());
                    if (material != null) {
                        ItemStack item = new ItemStack(material);
                        materialList.add(item);
                    } else {
                        System.out.println(langPrint("Material ","Материал ") + values3[i] + langPrint(" not found"," не найден"));
                    }
                }

                World world = player.getWorld();
                Location location = new Location(world, closestX, closestY, closestZ);
                commandSender.sendMessage(ChatColor.AQUA + langPrint("Nearest point: ","Ближайшая точка: ") + closestPoint);
                commandSender.sendMessage(ChatColor.AQUA + langPrint("Distance to a point: ","Расстояние до точки: ") + minDistance);
                if (minDistance < Rim4oosCapturePointsPlugin.getDataOp().getKey("Distance2StartPointWork")) {
                    itemSpawner itemSpawner = new itemSpawner(JavaPlugin.getPlugin(Rim4oosCapturePointsPlugin.class), world, materialList, location, player, closestPoint);
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if ((MinPLayersOnlineOnServer <= onlinePlayers.size())) {
                        Map<String, Integer> teamCounts = new HashMap<>();
                        for (Player onlinePlayer : onlinePlayers) {
                            String team = getTeamForPlayer(onlinePlayer);
                            if (!team.equals(getTeamForPlayer(player))) {
                                teamCounts.put(team, teamCounts.getOrDefault(team, 0) + 1);
                            }
                        }

                        int CountInSideCommand = 0;
                        for (Map.Entry<String, Integer> entry : teamCounts.entrySet()) {
                            CountInSideCommand = entry.getValue();
                        }

                        if (CountInSideCommand >= MinPLayersOnlineInCommand ){
                            if(!Rim4oosCapturePointsPlugin.getData().getTeamVl(closestPoint).equalsIgnoreCase(getTeamForPlayer(player))) {
                                if (Rim4oosCapturePointsPlugin.getData().getWork(closestPoint)) {
                                    commandSender.sendMessage(ChatColor.RED + langPrint("Failed to capture the point","Не удалось захватить точку"));
                                    commandSender.sendMessage(ChatColor.RED + langPrint("The point is still being launched by the enemy", "Точка ещё запущена врагом"));
                                } else{
                                    Rim4oosCapturePointsPlugin.getData().setTeamVl(closestPoint, getTeamForPlayer(player));
                                    String key = Rim4oosCapturePointsPlugin.getData().getKey(closestPoint);
                                    String[] values2 = key.split(",");
                                    String output = closestPoint + "  " + values2[0] + "," + values2[1] + "," + values2[2];
                                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + langPrint("Team ","Команда ") + getTeamForPlayer(player) + langPrint(" has captured point "," захватила точку ") + output);
                                    itemSpawner.start(time);
                                }
                            }else if(Rim4oosCapturePointsPlugin.getData().getTeamVl(closestPoint).equalsIgnoreCase(getTeamForPlayer(player))) {
                                commandSender.sendMessage(ChatColor.RED + langPrint("The point is already yours","Точка уже ваша"));
                                if(Rim4oosCapturePointsPlugin.getData().getWork(closestPoint)){
                                    commandSender.sendMessage(ChatColor.RED + langPrint("The point's already working","Точка уже работает"));
                                }else{
                                    itemSpawner.stop();
                                    itemSpawner.start(time);
                                    commandSender.sendMessage(ChatColor.GREEN +"Point launched");
                                }
                            }
                        }else if(Rim4oosCapturePointsPlugin.getData().getTeamVl(closestPoint).equalsIgnoreCase(getTeamForPlayer(player))) {
                            commandSender.sendMessage(ChatColor.RED + langPrint("The point's already working","Точка уже ваша"));
                            if(Rim4oosCapturePointsPlugin.getData().getWork(closestPoint)){
                                commandSender.sendMessage(ChatColor.RED + langPrint("","Точка уже работает"));
                            }else{
                                itemSpawner.stop();
                                itemSpawner.start(time);
                                commandSender.sendMessage(ChatColor.GREEN +langPrint("Point launched","Точка запущена"));
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColor.RED + langPrint("Too few players on enemy teams","Слишком мало игроков во вражеских команд"));
                        }


                    }else if(Rim4oosCapturePointsPlugin.getData().getTeamVl(closestPoint).equalsIgnoreCase(getTeamForPlayer(player))){
                        if(Rim4oosCapturePointsPlugin.getData().getWork(closestPoint)){
                            commandSender.sendMessage(ChatColor.RED + langPrint("The point's already working","Точка уже работает"));
                        }else {
                            itemSpawner.stop();
                            itemSpawner.start(time);
                            commandSender.sendMessage(ChatColor.GREEN +langPrint("Point launched","Точка запущена"));
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + langPrint("Too few players on the server","Слишком мало игроков на сервере"));
                    }

                } else {
                    commandSender.sendMessage(ChatColor.RED + langPrint("Too far away!","Слишком далеко!"));
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + langPrint("Points not found!","Точки не найдены!"));
            }
            Rim4oosCapturePointsPlugin.getData().save();

        }


        if(strings[0].equalsIgnoreCase("list")) {
            List<String> allPoints = new ArrayList<>(Rim4oosCapturePointsPlugin.getData().getConfig().getKeys(false));
            for (String p: allPoints){
                String keys = Rim4oosCapturePointsPlugin.getData().getKey(p);
                String[] values3 = keys.split(",");
                List<String> materiaGlList = new ArrayList<>(Arrays.asList(values3).subList(5, values3.length));
                commandSender.sendMessage(langPrint("Point ","Точка ") + ChatColor.AQUA + p + ChatColor.YELLOW + " " + Rim4oosCapturePointsPlugin.getData().getTeamVl(p)  + " \n" + ChatColor.WHITE + materiaGlList);
            }
        }



        if (strings[0].equalsIgnoreCase("clear")) {
            if (commandSender.hasPermission("rcpp.clearPoint")) {
                if (strings[1].equalsIgnoreCase("all")) {
                    for (String key : Rim4oosCapturePointsPlugin.getData().getConfig().getKeys(false)) {
                        Rim4oosCapturePointsPlugin.getData().getConfig().set(key, null);
                    }
                    commandSender.sendMessage(ChatColor.AQUA + langPrint("All points were successfully removed.","Все точки были успешно удалены."));
                } else {
                    String key = strings[1];
                    if (Rim4oosCapturePointsPlugin.getData().getConfig().contains(key)) {
                        Rim4oosCapturePointsPlugin.getData().getConfig().set(key, null);
                        commandSender.sendMessage(ChatColor.AQUA + langPrint("Point ","Точка ") + key + langPrint(" has been successfully deleted."," была успешно удалена."));
                    } else {
                        commandSender.sendMessage(ChatColor.RED + langPrint("Point ","Точка ") + key + langPrint(" not found."," не найдена."));
                    }
                }
                Rim4oosCapturePointsPlugin.getData().save();
            } else {
                commandSender.sendMessage(ChatColor.RED + langPrint("Not enough rights!","Недостаточно прав!"));
            }
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            Rim4oosCapturePointsPlugin.getInstance().reloadConfig();
            if (commandSender.hasPermission("rcpp.reload")) {
                commandSender.sendMessage(ChatColor.AQUA + langPrint("Successful!","Успешно!"));
            } else {
                commandSender.sendMessage(ChatColor.RED + langPrint("Not enough rights!","Недостаточно прав!"));
            }
        }

        if (strings[0].equalsIgnoreCase("add")) {
            if (commandSender.hasPermission("rcpp.addPoint")) {
                    Player player = (Player) commandSender;
                    if(!strings[4].equalsIgnoreCase("all")) {
                        int cordsX = strings[1].equals("~") ? player.getLocation().getBlockX() : Integer.parseInt(strings[1]);
                        int cordsY = strings[2].equals("~") ? player.getLocation().getBlockY() : Integer.parseInt(strings[2]);
                        int cordsZ = strings[3].equals("~") ? player.getLocation().getBlockZ() : Integer.parseInt(strings[3]);
                        String key = strings[4];
                        StringBuilder itemIds = new StringBuilder();
                        for (int i = 5; i < strings.length; i++) {
                            itemIds.append(strings[i]).append(", ");
                        }
                        if (itemIds.length() > 0) {
                            itemIds.setLength(itemIds.length() - 2);  // Удалить последнюю запятую и пробел
                        }
                        String value = cordsX + "," + cordsY + "," + cordsZ + "," + false + "," + null + "," + itemIds;
                        Rim4oosCapturePointsPlugin.getData().getConfig().set(key, value);
                        commandSender.sendMessage(Rim4oosCapturePointsPlugin.getData().getKey(key));
                        Rim4oosCapturePointsPlugin.getData().save();
                    } else {
                        commandSender.sendMessage(ChatColor.RED + langPrint("Well, that was genius.","Ну это было гениально"));
                    }
            }
            if (!commandSender.hasPermission("rcpp.addPoint")) {
                commandSender.sendMessage(ChatColor.RED + langPrint("Not enough rights!","Недостаточно прав!"));
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (sender.hasPermission("rcpp.addPoint")) {
                suggestions.add("add");
            }
            if (sender.hasPermission("rcpp.clearPoint")) {
                suggestions.add("clear");
            }
            if (sender.hasPermission("rcpp.reload")) {
                suggestions.add("reload");
            }
            suggestions.add("capture");
            suggestions.add("list");

            return suggestions;
        }
        if (args.length == 2 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length == 3 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length == 4 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("~");
        if (args.length == 5 && "add".equalsIgnoreCase(args[0])) return Lists.newArrayList("name");
        if (args.length >= 6 && "add".equalsIgnoreCase(args[0])) {
            List<String> itemIDs = new ArrayList<>();
            for (Material material : Material.values()) {
                itemIDs.add(material.name());
            }
            List<String> filteredIDs = new ArrayList<>();
            for (String id : itemIDs) {
                for(int i = 5; i < args.length; i++) {
                    if(id.startsWith(args[i].toUpperCase())) {
                        filteredIDs.add(id);
                    }
                }
            }
            itemIDs = filteredIDs;

            Collections.sort(itemIDs);
            return itemIDs;
        }


        if (args.length == 2) {
            if ("clear".equalsIgnoreCase(args[0])) {
                List<String> options = new ArrayList<>(Rim4oosCapturePointsPlugin.getData().getConfig().getKeys(false));
                options.add("all");
                return options;
            }
        }

        return Lists.newArrayList();
    }
}

