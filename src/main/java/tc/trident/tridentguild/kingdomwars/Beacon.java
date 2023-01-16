package tc.trident.tridentguild.kingdomwars;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Cuboid;
import tc.trident.tridentguild.utils.Utils;

import java.util.UUID;

public class Beacon {
    private UUID owningGuild;
    private BukkitTask task;
    private Cuboid beaconCube;
    private War warInstance;

    public Beacon(War warInstance){
        this.warInstance=warInstance;
        beaconCube = new Cuboid(Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-1"),warInstance.getWorld()),
                Utils.getLocationFromString(TridentGuild.kingdomwar.getString("beacon.point-2"),warInstance.getWorld()));

        task = runnable.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }

    /**
     * Controls the beacon
     * Changes it's ownership with slow animation
     */
    private BukkitRunnable runnable = new BukkitRunnable(){
        @Override
        public void run() {

        }
    };
    public UUID getOwningGuild() {
        return owningGuild;
    }

}
