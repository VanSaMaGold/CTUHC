package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vansama.ctuhc.CTUHC;
import org.vansama.ctuhc.GameStartEvent;

import java.util.*;
import java.util.stream.Collectors;

public class AirdropListener implements Listener {
    private final CTUHC plugin;
    private boolean isGameStarted;
    private final Random random = new Random();
    private int minDelay;
    private int maxDelay;
    private List<Material> items;
    private int offsetRange;
    private boolean lightningOnLand;
    private boolean broadcastCoordinates;

    public AirdropListener(CTUHC plugin) {
        this.plugin = plugin;
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void reloadConfig() {
        // 从配置文件中读取设置
        isGameStarted = plugin.getConfig().getBoolean("airdrop.enabled");
        minDelay = plugin.getConfig().getInt("airdrop.min-delay") * 20; // 转换为tick
        maxDelay = plugin.getConfig().getInt("airdrop.max-delay") * 20; // 转换为tick

        List<String> itemStrings = plugin.getConfig().getStringList("airdrop.items");
        // 使用 map 配合 filter 的链式调用，减少 lambda 表达式的使用
        items = itemStrings.stream()
                .map(Material::matchMaterial) // 直接使用 matchMaterial 方法引用
                .filter(Objects::nonNull) // 使用 Objects::nonNull 方法引用过滤非null值
                .distinct() // 去除重复的Material
                .collect(Collectors.toList());
        offsetRange = plugin.getConfig().getInt("airdrop.offset-range");
        lightningOnLand = plugin.getConfig().getBoolean("airdrop.lightning-on-land");
        broadcastCoordinates = plugin.getConfig().getBoolean("airdrop.broadcast-coordinates");
    }

    public void setGameStarted(boolean started) {
        isGameStarted = started;
        if (started) {
            startAirdropSequence();
        }
    }

    private void startAirdropSequence() {
        if (isGameStarted && !items.isEmpty()) {
            int delay = random.nextInt(maxDelay - minDelay) + minDelay;
            Bukkit.getScheduler().runTaskLater(plugin, this::triggerAirdrop, delay);
        }
    }

    private void triggerAirdrop() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (!onlinePlayers.isEmpty()) {
            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
            Location playerLocation = randomPlayer.getLocation();

            // 计算随机偏移量
            int offsetX = random.nextInt(offsetRange * 2) - offsetRange;
            int offsetZ = random.nextInt(offsetRange * 2) - offsetRange;

            // 获取玩家位置的区块坐标并加上偏移量
            int blockX = playerLocation.getBlockX() + offsetX;
            int blockZ = playerLocation.getBlockZ() + offsetZ;

            // 获取区块内最高的高度
            int highestY = playerLocation.getWorld().getHighestBlockYAt(blockX, blockZ);

            // 为箱子设置初始位置，稍微高于最高点以确保落下
            Location dropLocation = new Location(playerLocation.getWorld(), blockX + 0.5, highestY + 5, blockZ + 0.5);

            // 创建掉落的箱子
            Item chest = dropLocation.getWorld().dropItem(dropLocation, new ItemStack(Material.CHEST));
            chest.setVelocity(new Vector(0, 0.1, 0)); // 使用正确的Vector构造函数

            // 等待箱子落地
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (chest.isOnGround()) {
                    // 根据配置文件中的设置来决定是否召唤闪电
                    if (lightningOnLand) {
                        dropLocation.getWorld().strikeLightningEffect(dropLocation.add(0, 3, 0));
                    }
                    if (broadcastCoordinates) {
                        Bukkit.broadcastMessage("Airdrop landed at " + chest.getLocation().getBlockX() + ", " + chest.getLocation().getBlockY() + ", " + chest.getLocation().getBlockZ());
                    }
                    dropItems(chest.getLocation(), items);
                    chest.remove();
                }
            }, 80); // 大约等待箱子落下的ticks数
        }
    }


    private void dropItems(Location location, List<Material> items) {
        for (Material item : items) {
            ItemStack stack = new ItemStack(item, 1);
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Airdrop Item");
                stack.setItemMeta(meta);
            }
            location.getWorld().dropItemNaturally(location, stack);
        }
    }

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        setGameStarted(true);
    }
}