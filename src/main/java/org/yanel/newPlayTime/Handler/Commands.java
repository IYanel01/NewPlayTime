package org.yanel.newPlayTime.Handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.yanel.newPlayTime.NewPlayTime;

public class Commands implements CommandExecutor {

    private final NewPlayTime plugin;

    public Commands(NewPlayTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        // Check for both "npt" and "playtime" commands to open the GUI
        if (command.getName().equalsIgnoreCase("npt") || command.getName().equalsIgnoreCase("playtime")) {
            // Check if the "debug" subcommand is provided
            if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
                // Handle /npt debug or /playtime debug
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
            } else {
                // Default behavior: open the GUI for the player when using /npt or /playtime
                new PlayerTimerGUI(plugin, player).open();
                return true;
            }
        }

        return false;
    }
}
