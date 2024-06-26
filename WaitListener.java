package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WaitListener implements Listener {
    private final JavaPlugin plugin;
    private final Set<Player> players = new HashSet<>();
    private BukkitTask countdownTask;
    private int minPlayers;
    private int maxPlayers;
    private int countdownInterval; // 倒计时提醒间隔，以 ticks 为单位
    private int countDownTime;
    private Sound startSound;
    private String countdownMessage;
    private String startMessage;

    public WaitListener(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        this.minPlayers = config.getInt("Settings.min-players");
        this.maxPlayers = config.getInt("Settings.max-players", -1);
        this.countDownTime = config.getInt("Settings.countdown-time") * 20; // 转换成 ticks
        this.countdownInterval = config.getInt("Settings.countdown-interval") * 20; // 从秒转换为 ticks
        this.startSound = Sound.valueOf(config.getString("Settings.start-sound").toUpperCase().replace(" ", "_"));
        this.countdownMessage = config.getString("Settings.countdown-message");
        this.startMessage = config.getString("Settings.start-message");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        synchronized (players) {
            if (players.size() < (maxPlayers == -1 ? Integer.MAX_VALUE : maxPlayers)) {
                players.add(event.getPlayer());
                if (players.size() >= minPlayers && countdownTask == null) {
                    startCountdown();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        synchronized (players) {
            players.remove(event.getPlayer());
            if (players.size() < minPlayers && countdownTask != null) {
                cancelCountdown();
            }
        }
    }

    private void startCountdown() {
        if (countdownTask == null || !Bukkit.getScheduler().isCurrentlyRunning(countdownTask.getTaskId())) {
            countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                private int time = countDownTime;

                @Override
                public void run() {
                    if (time <= 0) {
                        cancelCountdown();
                        notifyPlayers(startMessage);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), startSound, 1.0f, 1.0f);
                        }
                        // 游戏开始后的逻辑代码
                    } else {
                        notifyPlayers(ChatColor.translateAlternateColorCodes('&', String.format(countdownMessage, (time / 20))));
                        time -= countdownInterval;
                    }
                }
            }, 20, countdownInterval); // 立即执行一次，之后每隔countdownInterval ticks执行
        }
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            Bukkit.getScheduler().cancelTask(countdownTask.getTaskId());
            countdownTask = null;
            notifyPlayers("Countdown cancelled!");
        }
    }

    private void notifyPlayers(String message) {
        for (Player player : new ArrayList<>(players)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}