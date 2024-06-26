package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerEndListener implements Listener {
    private final JavaPlugin plugin;

    public ServerEndListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerEnd(ServerEndEvent event) {
        int restartCooldown = plugin.getConfig().getInt("ServerRestart.restart-cooldown") * 20; // Convert to ticks
        String restartMessage = plugin.getConfig().getString("ServerRestart.restart-message");

        for (int i = restartCooldown / 20; i > 0; i--) {
            final int finalI = i; // Use a final variable to capture the loop value
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    restartMessage.replace("%seconds%", String.valueOf(finalI)))), finalI * 20L);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("ServerRestart.restart-command")), restartCooldown);
    }
}