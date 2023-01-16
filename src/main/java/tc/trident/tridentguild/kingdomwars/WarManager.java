package tc.trident.tridentguild.kingdomwars;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.sync.TridentSync;
import tc.trident.sync.server.Server;
import tc.trident.sync.server.ServerType;

public class WarManager {

    private War war = null;
    private ServerType type;
    private BukkitTask main;

    public WarManager(){
        type = TridentSync.SERVER_TYPE;


        // if server is war server then it checks
        if(type == ServerType.DUNGEON){
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
    public void startWar(){
        war = new War();
    }
    public int getTimeLeft(){
        return 0;
    }
}
