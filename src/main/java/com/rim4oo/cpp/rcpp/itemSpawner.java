package com.rim4oo.cpp.rcpp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class itemSpawner extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final World world;
    private final List<ItemStack> materials;
    private final Location location;

    private final Player player;
    private  final String key;

    public itemSpawner(JavaPlugin plugin, World world, List<ItemStack> materials, Location location, Player player, String key) {
        this.plugin = plugin;
        this.world = world;
        this.materials = materials;
        this.location = location;
        this.player = player;
        this.key = key;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            double currentDistance = player.getLocation().distance(location);
            if ((currentDistance < Rim4oosCapturePointsPlugin.getDataOp().getKey("Distance2MaintainPointWork"))) {
                for (ItemStack material : materials) {
                    Item item = world.dropItem(location, new ItemStack(material));
                    item.setPickupDelay(20); // Предмет не может быть поднят игроком
                    plugin.getServer().getScheduler().runTaskLater(plugin, item::remove, 20 * 50); // Удаление через 10 секунд
                }
            }
            else {
                stop();
                if(Rim4oosCapturePointsPlugin.getDataOp().getLang().equalsIgnoreCase("ru")){
                        player.sendMessage(ChatColor.RED + "Вы слишком далеко от точки!");
                        player.sendMessage(ChatColor.RED + "Ресурсы больше не появляются");
                    } else{
                    player.sendMessage(ChatColor.RED + "You are too far from the point!");
                    player.sendMessage(ChatColor.RED + "Resources no longer appear");
                    }
                Rim4oosCapturePointsPlugin.getData().setWork(key, false);
            }
        }else {
            stop();
            Rim4oosCapturePointsPlugin.getData().setWork(key, false);
        }

    }

    public void start(int intervalTicks) {
        Rim4oosCapturePointsPlugin.getData().setWork(key, true);
        this.runTaskTimer(plugin, 0, intervalTicks);
    }

    public void stop() {
        if (Rim4oosCapturePointsPlugin.getData().getWork(key)) {
            cancel();
        }
    }
}
