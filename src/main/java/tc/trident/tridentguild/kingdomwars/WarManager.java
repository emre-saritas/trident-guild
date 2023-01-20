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
import tc.trident.tridentguild.utils.Utils;

public class WarManager {

    private War war = null;
    private ServerType type;
    private BukkitTask main;
    private BossBar bossBar;

    public WarManager(){
        type = TridentSync.SERVER_TYPE;

        bossBar = Bukkit.createBossBar(
                Utils.addColors(TridentGuild.messages.getString("bossbar.intermission")),
                BarColor.YELLOW,
                BarStyle.SOLID);

        // if server is war server then it checks
        if(type == ServerType.DUNGEON){
            TridentGuild.getInstance().getCommand("tridentwar").setExecutor((CommandExecutor) new WarCommands());
            main = mainRun.runTaskTimerAsynchronously(TridentSync.getInstance(),10,10);
        }
    }


    private BukkitRunnable mainRun = new BukkitRunnable() {
        @Override
        public void run() {
            if(getTimeLeft() <= 0){
                startWar();
            }
        }
    };


    public void restart(){
        war = null;
        // restart of the server
    }
    public BossBar getBossBar() {
        return bossBar;
    }
    public boolean isWarStarted(){
        return war != null;
    }

    public War getWar() {
        return war;
    }

    public void startWar(){
        war = new War();
    }
    public int getTimeLeft(){
        return 0;
    }
}
