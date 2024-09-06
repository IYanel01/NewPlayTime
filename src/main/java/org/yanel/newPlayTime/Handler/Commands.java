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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("npt") || command.getName().equalsIgnoreCase("playtime")) {
                // Open the GUI for the player
                new PlayerTimerGUI(plugin, player).open();
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
        }
        return false;
    }
}
