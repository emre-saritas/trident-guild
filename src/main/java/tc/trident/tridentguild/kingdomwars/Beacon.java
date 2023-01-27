package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Cuboid;
import tc.trident.tridentguild.utils.Utils;

import java.util.*;

public class Beacon {
    private BukkitTask task;
    private Cuboid beaconCube;

    public Beacon(War warInstance){
        beaconCube = new Cuboid(Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-1"),warInstance.getWorld()),
                Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-2"),warInstance.getWorld()));

        task = runnable.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,20);
    }

    /**
     * Controls the beacon
     * Gives points to the owning guild
     */
    private BukkitRunnable runnable = new BukkitRunnable(){
        @Override
        public void run() {
            if(TridentGuild.getWarManager().getWar().getState() == War.WarState.FINISH){
                this.cancel();
                task.cancel();
            }else{
                UUID owningGuild = getNewOwningGuild();
                if(owningGuild != null){
                    TridentGuild.getWarManager().getWar().addPoints(owningGuild, TridentGuild.getWarManager().getWar().BEACON_POINTS);
                }
            }
        }
    };


    /**
     * Lists and iterates over players in the beacon area
     * If a guild has more members than any others
     * retruns new owning guild of the beacon
     * @return
     */
    public UUID getNewOwningGuild() {
        UUID newOwningGuild = null;
        HashMap<UUID, Integer> guildsInBeacon = new HashMap<>();


        TridentGuild.getWarManager().getWar().players.forEach((name,warPlayer) -> {
            if(beaconCube.containsLocation(Bukkit.getPlayerExact(name).getLocation())){
                if(!guildsInBeacon.containsKey(warPlayer.getGuildUUID()))
                    guildsInBeacon.put(warPlayer.guildUUID, 1);
                else
                    guildsInBeacon.replace(warPlayer.getGuildUUID(), guildsInBeacon.get(warPlayer.getGuildUUID())+1);
            }
        });

        int most = 0;
        for(UUID uuid : guildsInBeacon.keySet()){
            if(guildsInBeacon.get(uuid) > most){
                newOwningGuild = uuid;
            }
        }
        
        for(UUID uuid : guildsInBeacon.keySet()){
            if(uuid == newOwningGuild) continue;
            if(Objects.equals(guildsInBeacon.get(uuid), guildsInBeacon.get(newOwningGuild)))
                newOwningGuild = null;
        }
        
        return newOwningGuild;
    }

}
