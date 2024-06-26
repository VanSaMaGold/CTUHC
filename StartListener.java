package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StartListener implements Listener {
    private final JavaPlugin plugin;
    private boolean isGameStarted;
    private Set<Player> players;
    private int boundarySize;
    private int actionBarInterval;
    private int boundaryShrinkInterval;
    private int boundaryShrinkSize;
    private Random random;

    public StartListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.players = new HashSet<>();
        this.isGameStarted = false;
        this.random = new Random();
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        actionBarInterval = config.getInt("Start.actionbar-interval") * 20; // Convert to ticks
        boundaryShrinkInterval = config.getInt("Start.boundary-shrink-interval") * 20;
        boundarySize = config.getInt("Start.initial-boundary-size");
        boundaryShrinkSize = config.getInt("Start.boundary-shrink-size");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isGameStarted) {
            players.add(event.getPlayer());
            event.getPlayer().setGameMode(GameMode.SPECTATOR); // Set to spectator if game hasn't started
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    public void startGame() {
        if (!isGameStarted && !players.isEmpty()) {
            isGameStarted = true;
            for (Player player : players) {
                Location randomLocation = getInitialRandomLocation();
                player.teleport(randomLocation);
            }
            startBoundaryShrinkTask();
        }
    }

    private void startBoundaryShrinkTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boundarySize > 0) {
                    boundarySize -= boundaryShrinkSize;
                    updateActionbar();
                } else {
                    announceVictor();
                }
            }
        }.runTaskTimer(plugin, 0, boundaryShrinkInterval);
    }

    private void updateActionbar() {
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                String message = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("Messages.alive-players").replace("%alive%", String.valueOf(players.size())));
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(message));
            }
        }
    }
    private void announceVictor() {
        if (players.size() == 1) {
            Player victor = players.iterator().next();
            executeVictorCommands(victor);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getServer().getPluginManager().callEvent(new ServerEndEvent());
            }, 20 * 60); // Delay server end event by 1 minute
        }
    }

    private void executeVictorCommands(Player victor) {
        for (String command : plugin.getConfig().getStringList("VictorCommands")) {
            String formattedCommand = command.replaceAll("%player%", victor.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
        }
        victor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("Messages.game-victory").replace("%player%", victor.getName())));
    }

    private Location getInitialRandomLocation() {
        double x = random.nextDouble() * boundarySize;
        double z = random.nextDouble() * boundarySize;
        return new Location(Bukkit.getWorlds().get(0), x + boundarySize / 2, 64, z + boundarySize / 2);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (!event.isCancelled()) {
            startGame();
        }



        }

}