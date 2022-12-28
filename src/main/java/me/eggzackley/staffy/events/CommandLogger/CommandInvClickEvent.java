package me.eggzackley.staffy.events.CommandLogger;

import me.eggzackley.staffy.Inventories.CommandLoggerInv;
import me.eggzackley.staffy.Main;
import me.eggzackley.staffy.commands.CommandLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CommandInvClickEvent implements Listener {

    Plugin plugin;

    public CommandInvClickEvent(Main main) {
        plugin = main;
    }

    public static int count = 0;

    @EventHandler
    public void onInvClickEvent(InventoryClickEvent e) {

        String prefix = plugin.getConfig().getString("prefix");
        if (prefix == null) {
            prefix = Main.defaultPrefix;
        }
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);

        String invalidInt = Main.data.getMessages().getString("invalid-number");
        if (invalidInt == null) {
            invalidInt = "&cCould not find valid page number.";
        }
        invalidInt = ChatColor.translateAlternateColorCodes('&', invalidInt);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        int page = 1;

        String invName;
        String[] remName;
        if (inventory.getName().contains("(Page ")) {
            invName = inventory.getName().replace("(", "");
            invName = invName.replace(")", "");
            remName = invName.split(" ");
            try {
                page = Integer.parseInt(remName[4]);
            } catch (NumberFormatException err) {
                player.sendMessage(prefix + invalidInt);
                return;
            }
        }
        int addPage = page + 1;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack countItem = inventory.getItem(i);
            if(countItem != null && countItem.getType() != Material.AIR) {
                if(countItem.getType() != Material.ARROW) {
                    count = count + countItem.getAmount();
                }
            }
        }

        if (e.getView().getTopInventory().getType() == InventoryType.CHEST && e.getView().getBottomInventory().getType() == InventoryType.PLAYER) {
            if (e.getView().getTitle().contains(CommandLoggerInv.inventory(player).getName())) {
                e.setCancelled(true);
                ItemStack item = e.getCurrentItem();
                if (item != null && item.getType() == Material.ARROW) {
                    ItemMeta itemMeta = item.getItemMeta();
                    String nextArrow = ChatColor.translateAlternateColorCodes('&', "&aNext Page >");
                    String backArrow = ChatColor.translateAlternateColorCodes('&', "&c< Previous Page");
                    if (itemMeta.getDisplayName().equals(nextArrow)) {
                        Inventory newInv = Bukkit.createInventory(null, 54, CommandLoggerInv.inventory(player).getName() + ChatColor.GREEN + " (Page " + addPage + ")");
                        itemMeta.setDisplayName(backArrow);
                        item.setItemMeta(itemMeta);
                        newInv.setItem(45, item);
                        AtomicInteger countPage = new AtomicInteger();
                        Main.commandLog.find().forEach(document -> {
                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                            skullMeta.setOwner(CommandLog.target.getName());
                            ArrayList<String> lore = new ArrayList<>();
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
                            lore.add(ChatColor.GOLD + "Command: " + ChatColor.AQUA + document.getString("command"));
                            lore.add(ChatColor.GOLD + "World: " + ChatColor.AQUA + world.getName());
                            lore.add(ChatColor.GOLD + "Coordinates: " + ChatColor.AQUA + x + ", " + y + ", " + z);
                            lore.add(ChatColor.GOLD + "Timestamp: " + ChatColor.AQUA + document.getString("timestamp"));
                            skullMeta.setLore(lore);
                            skull.setItemMeta(skullMeta);
                            newInv.addItem(skull);
                            if(newInv.firstEmpty() == -1) {
                                ItemStack arrow = new ItemStack(Material.ARROW, 1);
                                ItemMeta arrowMeta = arrow.getItemMeta();
                                String nextPage = ChatColor.translateAlternateColorCodes('&', "&aNext Page >");
                                arrowMeta.setDisplayName(nextPage);
                                arrow.setItemMeta(arrowMeta);
                                newInv.setItem(53, arrow);
                                countPage.set(countPage.get() + 1);
                                if(countPage.get() < addPage) {
                                    newInv.clear();
                                    newInv.setItem(45, item);
                                }
                            }
                        });
                        if(newInv.getItem(0).getType() == Material.AIR || newInv.getItem(0) == null) return;
                        player.openInventory(newInv);
                    } else if(itemMeta.getDisplayName().equals(backArrow)) {
                        Inventory newInv = Bukkit.createInventory(null, 54, CommandLoggerInv.inventory(player).getName() + ChatColor.GREEN + " (Page " + (page - 1) + ")");
                        if(page - 1 == 0 || page - 1 == 1 ) {
                            player.openInventory(CommandLoggerInv.inventory(player));
                            return;
                        }
                        if(inventory.getName().contains(ChatColor.GREEN + "(Page ")) {
                            newInv.setItem(45, item);
                        }
                        AtomicInteger countPage = new AtomicInteger();
                        Main.commandLog.find().forEach(document -> {
                            if(inventory.getName().contains("(Page ")) {
                                itemMeta.setDisplayName(backArrow);
                                item.setItemMeta(itemMeta);
                                newInv.setItem(45, item);
                            }
                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                            skullMeta.setOwner(CommandLog.target.getName());
                            ArrayList<String> lore = new ArrayList<>();
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
                            lore.add(ChatColor.GOLD + "Command: " + ChatColor.AQUA + document.getString("command"));
                            lore.add(ChatColor.GOLD + "World: " + ChatColor.AQUA + world.getName());
                            lore.add(ChatColor.GOLD + "Coordinates: " + ChatColor.AQUA + x + ", " + y + ", " + z);
                            lore.add(ChatColor.GOLD + "Timestamp: " + ChatColor.AQUA + document.getString("timestamp"));
                            skullMeta.setLore(lore);
                            skull.setItemMeta(skullMeta);
                            newInv.addItem(skull);
                            if(newInv.firstEmpty() == -1) {
                                ItemStack arrow = new ItemStack(Material.ARROW, 1);
                                ItemMeta arrowMeta = arrow.getItemMeta();
                                String nextPage = ChatColor.translateAlternateColorCodes('&', "&aNext Page >");
                                arrowMeta.setDisplayName(nextPage);
                                arrow.setItemMeta(arrowMeta);
                                newInv.setItem(53, arrow);
                                countPage.set(countPage.get() + 1);
                                if(countPage.get() < (addPage - 2)) {
                                    newInv.clear();
                                }
                            }
                        });
                        if(newInv.getItem(0).getType() == Material.AIR) return;
                        player.openInventory(newInv);
                    }
                }
            }
        }
    }
}
