package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerEventListener implements Listener {
    // 静态变量，以便所有实例共享游戏状态
    private static boolean isGameStarted = false;

    public PlayerEventListener(JavaPlugin plugin) {
        // 注册PlayerEventListener类的监听器
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("CTUHC"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isGameStarted) {
            player.sendMessage(ChatColor.GREEN + "Game has started! You are now a spectator.");
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Welcome to the server! Game has not started yet.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isGameStarted) {
            player.sendMessage(ChatColor.RED + "You have left the game!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Bye! The game has not started yet.");
        }
    }

    // 处理游戏开始事件
    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (!event.isCancelled()) { // 使用事件实例来调用isCancelled()
            // 当游戏开始事件被触发且未被取消时，更新游戏状态
            isGameStarted = true;
            // 这里可以添加更多游戏开始时的逻辑
            // 例如，通知所有玩家游戏即将开始
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + "The game is about to start!");
            }
        }
    }
}