package me.eggzackley.staffy.events.SpeedChecker;

import me.eggzackley.staffy.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class SpeedChecker implements Listener {
    private Location endLoc = null;
    Plugin plugin;
    public SpeedChecker(Main main) {
        plugin = main;
    }

    @EventHandler
    public void playerWalkSpeedTester(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int ping = 0;
        try {
            Object playerOb = p.getClass().getMethod("getHandle").invoke(p);
            ping = (int) playerOb.getClass().getField("ping").get(playerOb);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException err) {
            err.printStackTrace();
        }
        Location startLoc = p.getLocation();
        p.getVelocity();
        e.getTo();
        Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
            endLoc = p.getLocation();
        }, 20);

        double finalLoc = startLoc.distance(endLoc) * 20;

        if(ping >= 300) {

        }
    }

    @EventHandler
    public void playerFlySpeedTester(PlayerMoveEvent e) {

    }
}
