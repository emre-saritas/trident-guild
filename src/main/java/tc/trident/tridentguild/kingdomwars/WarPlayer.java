package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.CustomBannerStand;

import java.util.UUID;

public class WarPlayer {

    String playerName;
    CustomBannerStand bannerStand;
    UUID guildUUID;
    BukkitTask task;
    boolean spawnProtect = true;
    long spawnProtectEnd = System.currentTimeMillis()+1000*3;

    public WarPlayer(String playerName, CustomBannerStand bannerStand, UUID guildUUID) {
        this.playerName = playerName;
        this.bannerStand = bannerStand;
        this.guildUUID = guildUUID;

        task = new BukkitRunnable(){
            @Override
            public void run() {
                if(spawnProtectEnd <= System.currentTimeMillis())
                    spawnProtect = false;
                bannerStand.getStand().teleport(Bukkit.getPlayerExact(playerName).getLocation().add(new Vector(0,2,0)));
            }
        }.runTaskTimer(TridentGuild.getInstance(), 10, 2);
    }

    public boolean isSpawnProtect() {
        return spawnProtect;
    }

    public void stop(){
        bannerStand.kill();
        task.cancel();
    }

    public String getPlayerName() {
        return playerName;
    }

    public CustomBannerStand getBannerStand() {
        return bannerStand;
    }

    public UUID getGuildUUID() {
        return guildUUID;
    }
}
