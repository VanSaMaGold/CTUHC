package org.vansama.ctuhc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandListener implements CommandExecutor {
    private final JavaPlugin plugin;

    // 使用 @NotNull 注解来标记构造函数参数，确保它不被忽略
    public CommandListener(@NotNull("JavaPlugin instance cannot be null") JavaPlugin plugin) {
        this.plugin = plugin;
        NotNullProcessor.validate(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // 当没有提供子命令时，显示帮助信息
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                if (sender.hasPermission("ctclean.reload")) { // 确保权限节点是正确的
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "插件配置已重新加载。");
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此操作。");
                }
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "未知命令 '" + subCommand + "'.");
                showHelp(sender);
                break;
        }
        return true;
    }


    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("help.MenuTitle"));
        sender.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("help.MenuLine1"));
        sender.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("help.MenuLine2"));
        sender.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("help.MenuLine3"));
    }
}