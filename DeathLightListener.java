package org.vansama.ctuhc;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathLightListener implements Listener {
    private final FileConfiguration config;

    public DeathLightListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // 如果配置启用，玩家死亡时释放雷电
        if (config.getBoolean("enable-lightning", true)) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }

        // 根据配置决定是否取消掉落物品
        if (config.getBoolean("cancel-drops", true)) {
            event.getDrops().clear();
        }

        // 将玩家设置为旁观者模式
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(ChatColor.RED + config.getString("specmode"));
    }
}