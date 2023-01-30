package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Cuboid;
import tc.trident.tridentguild.utils.Utils;

import java.util.*;

public class Beacon {
    private BukkitTask task;
    private Cuboid beaconCube;
    private UUID owningGuild = null;
    public World world = Bukkit.getWorld("canavarworld");

    public Beacon(){
        beaconCube = new Cuboid(Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-1"),world),
                Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-2"),world));

        task = runnable.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,60);
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
                owningGuild = getNewOwningGuild();
                if(owningGuild != null){
                    TridentGuild.getWarManager().getWar().addPoints(owningGuild, TridentGuild.getWarManager().getWar().BEACON_POINTS);
                    for(WarPlayer warPlayer : TridentGuild.getWarManager().getWar().players.values()){
                        if(warPlayer.getGuildUUID().equals(owningGuild)){
                            Player target = Bukkit.getPlayerExact(warPlayer.getPlayerName());
                            target.sendMessage(Utils.addColors(Utils.getMessage("general.beacon-points",true)));
                            target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                        }
                    }
                }
            }
        }
    };

    public UUID getOwningGuild() {
        return owningGuild;
    }
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
