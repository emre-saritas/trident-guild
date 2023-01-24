package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class War {
    private World world;
    public Map<UUID, Integer> guildPoints = new LinkedHashMap<>();
    public HashMap<String, UUID> playerGuilds = new HashMap<>();
    private HashMap<String, WarPlayerData> playerWarDatas = new HashMap<>();
    private WarState state = WarState.WAITING;
    private BukkitTask main;
    private Beacon beacon;
    public BossBar bossBar;
    public final int KILL_POINTS = TridentGuild.kingdomwar.getInt("kill-point");
    public final int BEACON_POINTS = TridentGuild.kingdomwar.getInt("beacon-point-per-second");
    private long endTime;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");


    public War(){
        world = Bukkit.getWorld("canavarworld");
        endTime = System.currentTimeMillis() + (long) TridentGuild.kingdomwar.getInt("war-length") *60*1000;
        Utils.debug(TridentGuild.getWarManager()+"");


        bossBar = Bukkit.createBossBar(
                Utils.addColors(
                        TridentGuild.messages.getString("bossbar.intermission").replace("%time%","")
                ),
                BarColor.YELLOW,
                BarStyle.SOLID);

        updateBossBar();
        beacon = new Beacon(this);
        main = mainRun.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }

    /**
     * Game time control
     * Start the finish sequence when time runs out
     */
    private BukkitRunnable mainRun = new BukkitRunnable() {
        @Override
        public void run() {
            updateBossBar();
            switch (state){
                case WAITING:
                    changeState(WarState.PLAYING);
                    break;
                case PLAYING:
                    if(getTimeLeft() <= 0){
                        changeState(WarState.FINISH);
                    }
                    break;
                case FINISH:
                    if((endTime - System.currentTimeMillis())/1000 <= 0){
                        playerGuilds.forEach((playerName, uuid) -> {
                            removePlayer(Bukkit.getPlayerExact(playerName));
                        });
                        main.cancel();
                        this.cancel();
                        TridentGuild.getWarManager().restart();
                    }
                    break;
            }

        }
    };

    public void changeState(WarState state){
        switch (state){
            case PLAYING:
                this.state = WarState.PLAYING;
                bossBar.setColor(BarColor.RED);
                Utils.debug("[TridentGuild] Savaş Başladı!");
                break;
            case FINISH:
                this.state = WarState.FINISH;

                UUID winnerGuild = getGuildByIndex(0);

                Utils.debug("[TridentGuild] Savaş Sona Erdi. Kazanan Lonca: "+winnerGuild);
                Utils.debug("[TridentGuild] Puan Durumu:");
                for(UUID guildUUID : guildPoints.keySet()){
                    Utils.debug("[TridentGuild] "+guildUUID+" - "+guildPoints.get(guildUUID));
                }

                endTime = System.currentTimeMillis()+30*1000;

                bossBar.setTitle(Utils.addColors(TridentGuild.messages.getString("bossbar.finish").replace("%time%",(int)(endTime - System.currentTimeMillis())/1000+" saniye")));
                bossBar.setColor(BarColor.YELLOW);
        }
    }
    public void updateBossBar(){
        switch (state){
            case WAITING:
                break;
            case PLAYING:
                Date date = new Date(getTimeLeft());
                bossBar.setTitle(
                        Utils.addColors(
                                TridentGuild.messages.getString("bossbar.war-time").replace("%time%",format.format(date))
                        ));
                break;
        }
    }
    public UUID getGuildByIndex(int index){
        sortGuildsByPoints();

        UUID guild = null;
        int i = 0;
        for(UUID keyGuild : guildPoints.keySet()){
            if(i==index)
                guild = keyGuild;
        }

        return guild;
    }
    public boolean isGuildLimitReached(UUID guildUUID){
        int count = 0;
        for(String playerName : playerGuilds.keySet()){
            if(playerGuilds.get(playerName).equals(guildUUID))
                count+=1;
        }
        return count >= TridentGuild.kingdomwar.getInt("player-limit");
    }

    public void kill(Player killer, Player dead){
        playerWarDatas.get(killer.getName()).incKills();
        playerWarDatas.get(dead.getName()).incDeaths();
        addPoints(playerGuilds.get(killer.getName()),KILL_POINTS);
        removePlayer(dead);
    }
    public void addPlayerToWar(Player player, String spawnID){
        if(!TridentGuild.getGuildManager().hasGuild(player.getName())) return;
        UUID guildUUID = TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildUUID();
        if(!guildPoints.containsKey(TridentGuild.getGuildManager().getPlayerGuild(player.getName())))
            guildPoints.put(guildUUID,0);
        if(!playerWarDatas.containsKey(player.getName()))
            playerWarDatas.put(player.getName(),new WarPlayerData());
        playerGuilds.put(player.getName(), guildUUID);

        player.teleport(Utils.getLocationFromString(TridentGuild.kingdomwar.getString("spawn-locations."+spawnID),
                TridentGuild.getWarManager().getWar().getWorld()));
    }
    public void removePlayer(Player player){
        playerGuilds.remove(player);
        ((LivingEntity) player).setHealth(20);
        player.teleport(Utils.getLocationFromString("lobby-spawn",world));
    }

    public WarState getState() {
        return state;
    }

    public int getTimeLeft(){
        return (int) (endTime - System.currentTimeMillis());
    }
    public void sortGuildsByPoints(){
        guildPoints = Utils.sortByValue(guildPoints,false);
    }
    public void addPoints(UUID guildUUID, int points){
        Utils.debug("[TridentGuild] "+guildUUID+" earned "+points+" points!");
        guildPoints.replace(guildUUID,points);
    }
    public World getWorld() {
        return world;
    }
    enum WarState{
        WAITING,
        PLAYING,
        FINISH
    }
}
