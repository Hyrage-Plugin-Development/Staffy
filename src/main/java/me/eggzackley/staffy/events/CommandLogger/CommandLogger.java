package me.eggzackley.staffy.events.CommandLogger;

import me.eggzackley.staffy.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommandLogger implements Listener {
    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent e) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Player player = e.getPlayer();
        Location location = player.getLocation();
        String loc = location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
                Document document = new Document("uuid", player.getUniqueId().toString())
                .append("player", player.getName())
                .append("gamemode", player.getGameMode())
                .append("command", e.getMessage())
                .append("location", loc)
                .append("timestamp", format.format(now));
        Main.commandLog.insertOne(document);
    }
}
