package tc.trident.tridentguild;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.cmds.AdminCmds;
import tc.trident.tridentguild.utils.Yaml;

public class TridentGuild extends JavaPlugin {
    public static Yaml config,messages,menus;
    private static TridentGuild instance;
    private static Economy econ;
    private static GuildManager guildManager;

    public void onEnable() {
        instance = this;
        config = new Yaml(getDataFolder() + "/config.yml", "config.yml");
        messages = new Yaml(getDataFolder() + "/messages.yml", "messages.yml");
        menus = new Yaml(getDataFolder() + "/menus.yml", "menus.yml");
        this.getCommand("tridentguild").setExecutor((CommandExecutor) new AdminCmds());
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
        }
        guildManager = new GuildManager();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static GuildManager getGuildManager() {
        return guildManager;
    }
    public static TridentGuild getInstance() {
        return instance;
    }
    public static Economy getEcon() {
        return econ;
    }
}
