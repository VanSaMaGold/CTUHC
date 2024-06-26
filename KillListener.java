package org.vansama.ctuhc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KillListener implements Listener {
    private final List<String> killMessages;
    private final boolean exposeCoordinates;

    public KillListener(FileConfiguration config) {
        this.killMessages = config.getStringList("kill-messages");
        this.exposeCoordinates = config.getBoolean("expose-coordinates", false);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player player = event.getEntity();
        Player killer = player.getKiller();
        String messageTemplate = killMessages.get(new Random().nextInt(killMessages.size()));
        String message = messageTemplate.replaceAll("%player%", player.getName())
                .replaceAll("%killer%", killer.getName());

        if (exposeCoordinates) {
            String coordinates = String.format("(%.0f, %.0f, %.0f)", killer.getLocation().getX(), killer.getLocation().getY(), killer.getLocation().getZ());
            message += " at coordinates: " + coordinates;
        }

        Bukkit.broadcastMessage(message);
    }
}