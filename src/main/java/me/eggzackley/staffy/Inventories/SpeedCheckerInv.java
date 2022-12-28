package me.eggzackley.staffy.Inventories;

import me.eggzackley.staffy.Main;
import me.eggzackley.staffy.commands.CommandLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class SpeedCheckerInv {
    public static Inventory inventory(Player player, int ping) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&e" + player.getName() + " &aSpeed Logs"));
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(CommandLog.target.getName());
        ArrayList<String> lore = new ArrayList<>();
        Main.commandLog.find().forEach(document -> {
            skullMeta.setDisplayName(ChatColor.RED  + "Player: " + ChatColor.GREEN + document.getString("player"));
            lore.clear();
            String location = document.getString("location");
            String[] split = location.split(":");
            World world = Bukkit.getWorld(split[0]);
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int z = Integer.parseInt(split[3]);
            lore.add(ChatColor.GRAY + "-----------------------------------------");
            lore.add(ChatColor.GOLD + "UUID: " + ChatColor.AQUA + document.getString("uuid"));
            lore.add(ChatColor.GOLD+ "Gamemode: " + ChatColor.AQUA + document.getString("gamemode"));
            lore.add(ChatColor.GOLD + "Ping: " + ChatColor.AQUA + document.getString("ping"));
            lore.add(ChatColor.GOLD + "World: " + ChatColor.AQUA + world.getName());
            lore.add(ChatColor.GOLD + "Start-Loc: " + ChatColor.AQUA + document.getString("startLoc"));
            lore.add(ChatColor.GOLD + "End-Loc: " + ChatColor.AQUA + document.getString("endLoc"));
            lore.add(ChatColor.GOLD + "MPS: " + ChatColor.AQUA + document.getString("mps"));
            lore.add(ChatColor.GOLD + "Timestamp: " + ChatColor.AQUA + document.getString("timestamp"));
            skullMeta.setLore(lore);
            item.setItemMeta(skullMeta);
            inventory.addItem(item);
            if(inventory.firstEmpty() == -1) {
                ItemStack arrow = new ItemStack(Material.ARROW, 1);
                ItemMeta arrowMeta = arrow.getItemMeta();
                String nextPage = ChatColor.translateAlternateColorCodes('&', "&aNext Page >");
                arrowMeta.setDisplayName(nextPage);
                arrow.setItemMeta(arrowMeta);
                inventory.setItem(53, arrow);
            }
        });
        return inventory;
    }
}
