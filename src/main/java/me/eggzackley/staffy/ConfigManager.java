package me.eggzackley.staffy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;
    private File configFile;

    private FileConfiguration messages;
    private File messagesFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return (YamlConfiguration) config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save config to " + configFile + e);
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messages.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return (YamlConfiguration) messages;
    }

    public void saveMessages() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save messages data to " + messagesFile + e);
        }
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }
}
