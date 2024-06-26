package org.vansama.ctuhc;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CTUHC extends JavaPlugin {

    // 定义KillListener实例
    public KillListener killListener;

    @Override
    public void onEnable() {
        // 获取日志记录器
        Logger logger = getLogger();

        // 加载默认配置并重载
        saveDefaultConfig();
        reloadConfig();

        // 获取当前插件的配置
        FileConfiguration config = getConfig();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new AreaListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getServer().getPluginManager().registerEvents(new GameStartListener(this), this);
        getServer().getPluginManager().registerEvents(new StartListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemEventListener(this), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerEndListener(this), this);

        // 正确创建并注册KillListener
        killListener = new KillListener(config);
        getServer().getPluginManager().registerEvents(killListener, this);

        // 注册命令监听器
        getCommand("cuhc").setExecutor(new CommandListener(this));
        getCommand("ctuhc").setExecutor(new CommandListener(this));

        // 打印插件启用信息
        logger.info(ChatColor.GREEN + "----------    -----------");
        logger.info(ChatColor.GREEN + "- CTUHC " + getDescription().getVersion() + " 版本 成功开启 by VanSaMa");
        logger.info(ChatColor.GREEN + "----------    -----------");
    }

    @Override
    public void onDisable() {
        // 插件禁用时的日志信息
        getLogger().info(ChatColor.GREEN + "----------    -----------");
        getLogger().info(ChatColor.RED + "CTUHC " + getDescription().getVersion() + " 版本 关闭成功");
        getLogger().info(ChatColor.GREEN + "----------    -----------");
    }
}
