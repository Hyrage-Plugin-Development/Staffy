package me.eggzackley.staffy;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.eggzackley.staffy.commands.CommandLog;
import me.eggzackley.staffy.events.CommandLogger.CommandInvClickEvent;
import me.eggzackley.staffy.events.CommandLogger.CommandLogger;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static ConfigManager data;
    public static String defaultPrefix = ChatColor.translateAlternateColorCodes('&', "&bStaffy &7> ");

    public static MongoClient mongoClient = null;
    public static MongoDatabase database = null;
    public static MongoCollection<Document> commandLog = null;
    public static MongoCollection<Document> reportLog = null;
    public static MongoCollection<Document> punishLog = null;
    public static MongoCollection<Document> inventoryLog = null;
    public String prefix = this.getConfig().getString("prefix");

    @Override
    public void onEnable() {
        getConfigManager();
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "-----------------" + " Staffy " + "-----------------");
        if (prefix == null) {
            prefix = defaultPrefix;
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Prefix is null, using default prefix.");
        } else {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Prefix was found.");
        }

        this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "THIS IS NOT AN ERROR: If you reloaded this plugin/server, an error might occur, disabling the connection to MongoDB. Please restart your server if an error does occur.");

        String getClient = this.getConfig().getString("mongo-db.client");
        String getDB = this.getConfig().getString("mongo-db.database");
        if (getClient == null) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "MongoDB client login is null.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (getDB == null) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "MongoDB database is null.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            mongoClient = MongoClients.create(getClient);
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "MongoDB client was found.");
        } catch (NullPointerException npe) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Null MongoDB client.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (IllegalArgumentException iae) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Invalid MongoDB client string.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            database = mongoClient.getDatabase(getDB);
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "MongoDB database was found.");
        } catch (NullPointerException npe) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Null MongoDB database.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (IllegalArgumentException iae) {
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Invalid MongoDB database name.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }


        try {
            database.createCollection("commands");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Command log collection not found. We have created the collection for you.");
            commandLog = database.getCollection("commands");
        } catch(MongoCommandException e) {
            commandLog = database.getCollection("commands");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Command log collection found.");
        }

        try {
            database.createCollection("reports");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Report log collection not found. We have created the collection for you.");
            reportLog = database.getCollection("reports");
        } catch(MongoCommandException e) {
            reportLog = database.getCollection("reports");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Report log collection found.");
        }

        try {
            database.createCollection("punishments");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Punishment log collection not found. We have created the collection for you.");
            punishLog = database.getCollection("punishments");
        } catch(MongoCommandException e) {
            punishLog = database.getCollection("punishments");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Punishment log collection found.");
        }

        try {
            database.createCollection("inventories");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Inventory log collection not found. We have created the collection for you.");
            inventoryLog = database.getCollection("inventories");
        } catch(MongoCommandException e) {
            inventoryLog = database.getCollection("inventories");
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Inventory log collection found.");
        }

        this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.GREEN + "Successfully logged into MongoDB database.");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "------------------------------------------------");

        this.getServer().getPluginManager().registerEvents(new CommandLogger(), this);
        this.getServer().getPluginManager().registerEvents(new CommandInvClickEvent(this), this);

        this.getCommand("commands").setExecutor(new CommandLog(this));

        PluginDescriptionFile pdf = this.getDescription();
        String version = pdf.getVersion();

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "-----------------" + " Staffy " + "-----------------");
        if (prefix == null) {
            prefix = defaultPrefix;
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Prefix is null, using default prefix.");

        }
        this.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Plugin Disabled.");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "------------------------------------------------");
    }

    public void getConfigManager() {
        data = new ConfigManager(this);
        data.saveDefaultConfig();
        data.reloadConfig();
        data.saveConfig();
        data.saveDefaultMessages();
        data.saveMessages();
        data.reloadMessages();
    }
}
