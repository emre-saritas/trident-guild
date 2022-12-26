package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import tc.trident.sync.TridentSync;
import tc.trident.sync.sync.SyncManager;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.listeners.RedisListeners;
import tc.trident.tridentguild.utils.Utils;

import java.util.*;

public class War {

    private HashMap<UUID, Integer> guildPoints = new HashMap<>();
    private HashMap<String, WarPlayer> players = new HashMap<>();

    private UUID beaconGuild0 = null;
    private BossBar bossBar;
    private TimerTask mainTask, finishTask;
    private World world = Bukkit.getWorld(TridentGuild.kingdomwar.getString("world"));

    public War(List<UUID> queueWarGuilds){
        queueWarGuilds.forEach(uuid -> {
            guildPoints.put(uuid,0);
        });

        WarListeners warListeners = new WarListeners(this);
        Bukkit.getPluginManager().registerEvents(warListeners,TridentGuild.getInstance());

        // setup bossbar
        // setup right tab
        // setup playerlist
        start();
    }

    public void start(){
        Timer timer = new Timer ();
        this.mainTask = new TimerTask() {
            @Override
            public void run() {
                /**
                 * beacon kodları baştan yaz
                 * beaconı kontrol etsin
                 * oyuncu çokluğunda animasyonla birlikte capture edilsin
                 */
                if(beaconGuild0 != calculateOwnershipOfBeacon(0)){
                    beaconGuild0 = calculateOwnershipOfBeacon(0);
                    // start animation
                }
                if(false) // if time finish
                    finish();
            }
        };
        timer.schedule (mainTask, 0l, 1000);
    }

    public void finish(){
        stop();
        Timer timer = new Timer ();
        /**
         * listenerlar durdurulmalı
         */
        this.finishTask = new TimerTask() {
            @Override
            public void run() {
                if(true){   // countdown to close
                    // finishing animations
                    // send players back
                    SyncManager.globalBroadcast(Utils.getMessage("general.winner-guild",true).replace("%guild-name%","KAZANAN_LONCA"));
                    // sql update war data
                }
            }
        };
        timer.schedule (finishTask, 0l, 1000);
    }

    public void stop(){
        mainTask.cancel();
    }
    public void addPoint(Player player, int point){
        UUID guildUUID = players.get(player.getName()).getGuildUUID();
        addPoint(guildUUID,point);
    }
    public boolean isGuildFull(UUID guildUUID){
        int count = 0;
        for(WarPlayer player : players.values()){
            if(player.getGuildUUID().equals(guildUUID)) count+=1;
        }
        return count>=TridentGuild.kingdomwar.getInt("player-limit");
    }
    public void addPoint(UUID guildUUID, int point){
        guildPoints.replace(guildUUID,guildPoints.get(guildUUID)+point);
    }
    public void addPlayerToWar(Player player){
        if(!TridentGuild.getGuildManager().hasGuild(player.getName())) return;
        players.put(player.getName(), new WarPlayer(player,TridentGuild.getGuildManager().onlinePlayerGuilds.get(player.getName())));
        // teleport player
    }
    public void playerKilled(Player killer, Player dead){
        players.get(killer.getName()).addKill();;
        players.get(dead.getName()).addKill();;
        addPoint(killer,TridentGuild.kingdomwar.getInt("kill-point"));
        // UI/UX
    }
    public UUID calculateOwnershipOfBeacon(int beaconID){
        // loop players
        //      cuboid controls

        // return guild owning that beacon
        return null;
    }
}
