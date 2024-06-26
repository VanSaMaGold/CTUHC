package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AreaListener implements Listener {
    private final JavaPlugin plugin;
    private boolean gameStarted = false; // 游戏开始状态

    public AreaListener(JavaPlugin plugin) {
        this.plugin = plugin;
        // 注册监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // 游戏开始方法
    public void startGame() {
        gameStarted = true;
    }

    // 防止玩家破坏方块，除非游戏已经开始
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!gameStarted && plugin.getConfig().getBoolean("prevent-block-damage")) {
            event.setCancelled(true);
        }
    }

    // 防止玩家受到伤害
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && plugin.getConfig().getBoolean("prevent-physical-damage")) {
            event.setCancelled(true);
        }
    }

    // 防止玩家饱食度降低
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && plugin.getConfig().getBoolean("prevent-hunger-decrease")) {
            event.setCancelled(true);
        }
    }

    // 防止玩家掉落虚空
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getConfig().getBoolean("prevent-void-fall") && event.getTo().getY() < 0) {
            event.setCancelled(true);
        }
    }

    // 玩家加入事件，用于随机传送
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("random-teleport-on-join")) {
            List<Location> safeLocations = getSafeLocations();
            if (!safeLocations.isEmpty()) {
                Location randomLocation = safeLocations.get(new Random().nextInt(safeLocations.size()));
                event.getPlayer().teleport(randomLocation);
            }
        }
    }

    private List<Location> getSafeLocations() {
        List<Location> safeLocations = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection locationsSection = config.getConfigurationSection("safe-teleport-locations");

        if (locationsSection != null) {
            for (String key : locationsSection.getKeys(false)) {
                ConfigurationSection locationSection = locationsSection.getConfigurationSection(key);
                if (locationSection != null) {
                    String worldName = locationSection.getString("world");
                    double x = locationSection.getDouble("x");
                    double y = locationSection.getDouble("y");
                    double z = locationSection.getDouble("z");
                    float yaw = (float) locationSection.getDouble("yaw");
                    float pitch = (float) locationSection.getDouble("pitch");

                    org.bukkit.World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        Location location = new Location(world, x, y, z, yaw, pitch);
                        safeLocations.add(location);
                    }
                }
            }
        }
        return safeLocations;
    }
}