package me.eggzackley.staffy.commands;

import me.eggzackley.staffy.Inventories.CommandLoggerInv;
import me.eggzackley.staffy.Main;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandLog implements CommandExecutor {

    public static Player target = null;

    public static Plugin plugin;
    public CommandLog(Main main) {
        plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfig().getString("prefix");
        if(prefix == null) {
            prefix = Main.defaultPrefix;
        }
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);

        String noPerms = Main.data.getMessages().getString("no-permissions");
        if(noPerms == null) {
            noPerms = ChatColor.RED + "You do not have permission to use this command.";
        }
        noPerms = ChatColor.translateAlternateColorCodes('&', noPerms);

        String invalidPlayer = Main.data.getMessages().getString("invalid-player");
        if(invalidPlayer == null) {
            invalidPlayer = ChatColor.RED + "That player could not be found. Try using UUID";
        }
        invalidPlayer = ChatColor.translateAlternateColorCodes('&', invalidPlayer);

        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if(!player.hasPermission("staffy.logs.command")) {
            player.sendMessage(prefix + noPerms);
            return true;
        }
        if(args.length >= 1) {
            target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage(prefix + invalidPlayer);
                return true;
            }
        } else {
            player.sendMessage(prefix + invalidPlayer);
            return true;
        }
        player.openInventory(CommandLoggerInv.inventory(player));

        return true;
    }
}
