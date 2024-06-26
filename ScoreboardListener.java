package org.vansama.ctuhc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class ScoreboardListener implements Listener {
    private final JavaPlugin plugin;
    private final Scoreboard scoreboard;
    private final Map<String, List<String>> stateLines;

    public ScoreboardListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.stateLines = new HashMap<>();
        loadScoreboardSettings();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void loadScoreboardSettings() {
        ConfigurationSection states = plugin.getConfig().getConfigurationSection("Scoreboard.States");
        if (states != null) {
            for (String state : states.getKeys(false)) {
                List<String> lines = new ArrayList<>();
                for (String line : states.getStringList(state + ".Lines")) {
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                stateLines.put(state, lines);
            }
        }
    }

    private void updateScoreboard(String state) {
        if (!plugin.getConfig().getBoolean("Scoreboard.Enabled")) return;

        List<String> lines = stateLines.get(state);
        if (lines == null || lines.isEmpty()) return;

        Objective objective = scoreboard.getObjective("gameState");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("gameState", "gameState", plugin.getConfig().getString("Scoreboard.Title"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // Clear existing scores
        for (String line : lines) {
            objective.getScore(ChatColor.stripColor(line)).setScore(objective.getScoreboard().getEntries().size());
        }

        // Set new scores
        int scoreIndex = 0;
        for (String line : lines) {
            Score score = objective.getScore(ChatColor.stripColor(line));
            score.setScore(--scoreIndex);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        updateScoreboard("started");
    }

    @EventHandler
    public void onServerEnd(ServerEndEvent event) {
        updateScoreboard("ended");
        // Update winner's name in the scoreboard
        String winnerName = plugin.getConfig().getString("Commning-Soon"); // Assuming this method exists and returns the winner's name
        List<String> endedLines = stateLines.get("ended");
        if (endedLines != null) {
            for (int i = 0; i < endedLines.size(); i++) {
                String line = endedLines.get(i);
                if (line.contains("%winner%")) {
                    endedLines.set(i, line.replace("%winner%", winnerName));
                }
            }
            stateLines.put("ended", endedLines); // Update the lines in the map
            updateScoreboard("ended"); // Re-update the scoreboard to reflect changes
        }
    }
}