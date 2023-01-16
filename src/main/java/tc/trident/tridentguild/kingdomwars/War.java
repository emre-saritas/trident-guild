package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class War {
    private World world;
    private HashMap<UUID, Integer> guildPoints = new HashMap<>();
    private BukkitTask main;
    private long endTime;
    private Location tempSpawnPoint;


    public War(){
        world = null;
        endTime = System.currentTimeMillis() + (long) TridentGuild.kingdomwar.getInt("war-length") *60*1000;
        tempSpawnPoint = Utils.getLocationFromString(TridentGuild.kingdomwar.getString("temp-spawn-point"),world);

        main = mainRun.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }

    private BukkitRunnable mainRun = new BukkitRunnable() {
        @Override
        public void run() {

        }
    };


    public void addPlayerToWar(Player player){
        if(!TridentGuild.getGuildManager().hasGuild(player.getName())) return;
        if(!guildPoints.containsKey(TridentGuild.getGuildManager().getPlayerGuild(player.getName())))
            guildPoints.put(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildUUID(),0);

        player.teleport(tempSpawnPoint);
    }
    public int getSecondsLeft(){
        return (int) ((endTime - System.currentTimeMillis())/1000);
    }
    public List<UUID> sortGuildsByPoints(){
        return null;
    }
    public void addPoints(UUID guildUUID, int points){
        guildPoints.replace(guildUUID,points);
    }
    public World getWorld() {
        return world;
    }
}
