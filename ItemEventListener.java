package org.vansama.ctuhc;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class ItemEventListener implements Listener {
    // 注意：这些常量应该从配置文件中加载
    private static final int COMPASS_SLOT = 0; // 指南针在玩家背包的槽位
    private static final int CHEST_SLOT = 1;   // 箱子在玩家背包的槽位
    private static final String COMPASS_NAME = "&6指南针"; // 指南针的自定义名称
    private static final String CHEST_NAME = "&c箱子";     // 箱子的自定义名称
    private static boolean isGameStarted = false;

    public ItemEventListener(JavaPlugin plugin) {
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 检查游戏是否已经开始，如果已经开始，则不执行任何操作
        if (isGameStarted) {
            return;
        }

        if (event.getAction().name().contains("RIGHT")) { // 检查是否是右键操作
            Player player = event.getPlayer();
            ItemStack item = event.getItem(); // 获取玩家手中的物品

            // 检查玩家手中的物品是否是我们自定义的指南针或箱子
            if (isItemWithName(item, COMPASS_NAME)) {
                // 触发指南针GUI
                player.sendMessage("Comming soon");
                event.setCancelled(true);
            } else if (isItemWithName(item, CHEST_NAME)) {
                // 触发箱子GUI
                player.sendMessage("Comming soon");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // 检查游戏是否已经开始，如果已经开始，则允许丢弃物品
        if (!isGameStarted) {
            ItemStack item = event.getItemDrop().getItemStack();
            // 如果玩家尝试丢弃的是指南针或箱子，则取消这个操作
            if (isItemWithName(item, COMPASS_NAME) || isItemWithName(item, CHEST_NAME)) {
                event.setCancelled(true);
            }
        }
    }

    // 检查物品的显示名称是否与给定名称匹配
    private boolean isItemWithName(ItemStack item, String name) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        return item.getItemMeta().getDisplayName().equals(name);
    }

    // 这个方法将由GameStartEvent的处理程序调用
    public static void onGameStart() {
        isGameStarted = true;
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            // 清除玩家背包中的所有物品
            player.getInventory().clear();
            // 可以在这里添加代码，以确保玩家没有持有特定的物品
        }
    }
}