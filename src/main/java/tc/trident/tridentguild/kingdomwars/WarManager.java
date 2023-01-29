package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.sync.TridentSync;
import tc.trident.sync.server.Server;
import tc.trident.sync.server.ServerType;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.cmds.AdminCmds;
import tc.trident.tridentguild.listeners.PlayerServerListeners;
import tc.trident.tridentguild.utils.Utils;

public class WarManager {

    public War war = null;
    private ServerType type;

    public WarManager(){
        type = TridentSync.SERVER_TYPE;

        // if server is war server then it checks
        if(type == ServerType.ARENA){
            war = new War();
            Bukkit.getPluginManager().registerEvents(new WarListeners(),TridentGuild.getInstance());
        }

    }

    public void stop(){
        war.stop();
        war = null;
    }
    public void restart(){
        stop();
    }
    public boolean isWarStarted(){
        return war != null;
    }

    public War getWar() {
        return war;
    }
}
