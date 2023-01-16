package tc.trident.tridentguild;

import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.RegisteredServiceProvider;
import tc.trident.tridentguild.cmds.AdminCmds;
import tc.trident.tridentguild.cmds.GuildChatMessage;
import tc.trident.tridentguild.cmds.GuildCmds;
import tc.trident.tridentguild.invite.InviteHandler;
import tc.trident.tridentguild.kingdomwars.WarManager;
import tc.trident.tridentguild.listeners.PlayerServerListeners;
import tc.trident.tridentguild.listeners.RedisListeners;
import tc.trident.tridentguild.mysql.*;
import tc.trident.tridentguild.utils.Yaml;

public class TridentGuild extends ExtendedJavaPlugin {
    public static Yaml config,messages,menus,upgrades,redis,kingdomwar;
    private static TridentGuild instance;
    private static MySQLManager sqlManager;
    private static Economy econ;
    private static GuildManager guildManager;
    private static WarManager warManager;
    private static SyncManager syncManager;
    private static InviteHandler inviteHandler;
    private RedisListeners redisListeners;

    @Override
    public void enable() {
        instance = this;
        config = new Yaml(getDataFolder() + "/config.yml", "config.yml");
        messages = new Yaml(getDataFolder() + "/messages.yml", "messages.yml");
        menus = new Yaml(getDataFolder() + "/menus.yml", "menus.yml");
        upgrades = new Yaml(getDataFolder() + "/upgrades.yml", "upgrades.yml");
        redis = new Yaml(getDataFolder() + "/redis.yml", "redis.yml");
        kingdomwar = new Yaml(getDataFolder() + "/kingdomwar.yml", "kingdomwar.yml");
        try{
            syncManager = new SyncManager();
            sqlManager = new MySQLManager(this);
            guildManager = new GuildManager();
            inviteHandler = new InviteHandler();
            warManager = new WarManager();
            Bukkit.getPluginManager().registerEvents(new PlayerServerListeners(),this);
            redisListeners = new RedisListeners();
            Bukkit.getPluginManager().registerEvents(redisListeners,this);
            this.getCommand("tridentguild").setExecutor((CommandExecutor) new AdminCmds());
            this.getCommand("lonca").setExecutor((CommandExecutor) new GuildCmds());
            this.getCommand("lmsg").setExecutor((CommandExecutor) new GuildChatMessage());
            if (!setupEconomy()) {
                getServer().getPluginManager().disablePlugin(this);
            }
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new GuildPlaceholders().register();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void disable() {
        if(redisListeners != null)
            redisListeners.close();
        guildManager.unloadAllGuilds();
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


    public static WarManager getWarManager() {
        return warManager;
    }
    public static InviteHandler getInviteHandler() {
        return inviteHandler;
    }
    public static MySQLManager getSqlManager() {
        return sqlManager;
    }
    public static MySQLHandler getSqlHandler(){
        return MySQLManager.mysqlHandler;
    }
    public static SyncManager getSyncManager() {
        return syncManager;
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
