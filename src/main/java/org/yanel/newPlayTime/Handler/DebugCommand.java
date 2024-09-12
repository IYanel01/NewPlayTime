package org.yanel.newPlayTime.Handler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yanel.newPlayTime.NewPlayTime;

public class DebugCommand implements CommandExecutor {

    private final NewPlayTime plugin;

    public DebugCommand(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Toggle debug mode
        if (plugin.isDebugMode()) {
            plugin.setDebugMode(false);
            player.sendMessage(ChatColor.GREEN + "Debug mode disabled.");
            plugin.getLogger().info("Debug mode disabled by " + player.getName());
        } else {
            plugin.setDebugMode(true);
            player.sendMessage(ChatColor.GREEN + "Debug mode enabled.");
            plugin.getLogger().info("Debug mode enabled by " + player.getName());
        }

        return true;
    }
}
