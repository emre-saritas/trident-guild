package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import tc.trident.sync.TridentSync;
import tc.trident.sync.server.ServerType;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WarManager {

    public War war = null;
    public World world = Bukkit.getWorld("canavarworld");
    private ServerType type;
    public long nextWar = TridentGuild.kingdomwar.getLong("next-war");
    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public WarManager(){
        type = TridentSync.SERVER_TYPE;

        updateNextWar();

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
        if(war == null) return false;
        return war.getState() == War.WarState.PLAYING;
    }

    public War getWar() {
        return war;
    }
    public void updateNextWar(){
        if(nextWar <= System.currentTimeMillis()){
            nextWar = nextWar+604800000;
        }
        Utils.debug("[TridentGuild] Next War Date: "+getNextWarDateString());
        TridentGuild.kingdomwar.set("next-war",nextWar);
        TridentGuild.kingdomwar.save();
    }
    public String getNextWarDateString(){
        Date date = new Date(nextWar);
        return format.format(date);
    }
}
