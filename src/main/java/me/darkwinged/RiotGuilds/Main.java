package me.darkwinged.RiotGuilds;

import me.darkwinged.RiotGuilds.Events.FriendlyFire;
import me.darkwinged.RiotGuilds.Events.GuildKill;
import me.darkwinged.RiotGuilds.Events.menuEvents;
import me.darkwinged.RiotGuilds.commands.cmdGuild;
import me.darkwinged.RiotGuilds.libaries.CustomConfig;
import me.darkwinged.RiotGuilds.libaries.SignFactory;
import me.darkwinged.RiotGuilds.libaries.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main getInstance;
    public SignFactory signFactory;
    private static Economy econ = null;

    public CustomConfig fileMessage;

    public void onEnable() {
        // ---- [ Initializing instance of main class | manager classes ] ----
        getInstance = this;

        // ---- [ Checking dependencies ] ----
        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("no-plugin-dependency")));
            getServer().getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("no-plugin-dependency")));
            getServer().getPluginManager().disablePlugin(this);
        }

        // --- [ Referencing manager classes ] ----
        signFactory = new SignFactory(this);

        // ---- [ Loading Commands | Loading Events | Loading YML Files | Loading Guilds ] ----
        loadCommands();
        loadEvents();
        saveDefaultConfig();
        Utils.loadGuilds();

        // ---- [ Loading lang file ] ----
        fileMessage = new CustomConfig(this, "lang/" + this.getConfig().getString("Storage.Language File"), true);
        fileMessage.saveDefaultConfig();
        // ---- [ Startup message ] ----
        getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("startup")));
    }

    public void onDisable() {
        Utils.saveGuilds();

        // ---- [ shutdown message ] ----
        getServer().getConsoleSender().sendMessage(Utils.chatColor(this.fileMessage.getConfig().getString("shutdown")));
    }

    public void loadCommands() {
        getCommand("guild").setExecutor(new cmdGuild());
    }
    public void loadEvents() {
        getServer().getPluginManager().registerEvents(new menuEvents(), this);
        getServer().getPluginManager().registerEvents(new FriendlyFire(), this);
        getServer().getPluginManager().registerEvents(new GuildKill(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }
}
