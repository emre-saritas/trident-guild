package tc.trident.tridentguild.kingdomwars;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.scoreboard.Scoreboard;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import me.lucko.helper.scoreboard.ScoreboardProvider;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.CustomBannerStand;
import tc.trident.tridentguild.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class War {
    private World world;
    public Map<UUID, Integer> guildPoints = new LinkedHashMap<>();
    private HashMap<String, WarPlayerData> playerWarDatas = new HashMap<>();
    public HashMap<String, WarPlayer> players = new HashMap<>();
    private WarState state = WarState.WAITING;
    private BukkitTask main;
    private Beacon beacon;
    public BossBar bossBar;
    public final int KILL_POINTS = TridentGuild.kingdomwar.getInt("kill-point");
    public final int BEACON_POINTS = TridentGuild.kingdomwar.getInt("beacon-point-per-second");
    private Scoreboard sb = Services.load(ScoreboardProvider.class).getScoreboard();
    private MetadataKey<ScoreboardObjective> SCOREBOARD_KEY;
    private BukkitTask scoreboardUpdateTask;
    private long endTime;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    private BiConsumer<Player, ScoreboardObjective> updater = (p, obj) -> {
        obj.setDisplayName("&9&lLonca Savaşı");
        obj.applyLines(
                " ",
                "&6| &fAd: &6"+p.getName(),
                "&6| &fPing: &6"+((CraftPlayer) p).getHandle().ping+"ms",
                "&6| &fLonca: &e"+TridentGuild.getGuildManager().loadedGuilds.get(players.get(p.getName()).getGuildUUID()).getGuildName(),
                "&6| &fLonca Puanı: &e"+guildPoints.get(players.get(p.getName()).getGuildUUID()),
                "&6| &fÖldürme: &e"+playerWarDatas.get(p.getName()).getKills(),
                "&6| &fÖlme: &e"+playerWarDatas.get(p.getName()).getDeaths(),
                "  ",
                "&f&lPuanlar",
                "&9| &f1. "+WarPlaceholders.getGuild(this,0)+" - &6"+WarPlaceholders.getGuildPoints(this,0),
                "&9| &f2. "+WarPlaceholders.getGuild(this,1)+" - &6"+WarPlaceholders.getGuildPoints(this,1),
                "&9| &f3. "+WarPlaceholders.getGuild(this,2)+" - &6"+WarPlaceholders.getGuildPoints(this,2),
                "&9| &f4. "+WarPlaceholders.getGuild(this,3)+" - &6"+WarPlaceholders.getGuildPoints(this,3),
                "&9| &f5. "+WarPlaceholders.getGuild(this,4)+" - &6"+WarPlaceholders.getGuildPoints(this,4),
                "   "
        );
    };


    public War(){
        world = Bukkit.getWorld("canavarworld");
        endTime = System.currentTimeMillis() + (long) TridentGuild.kingdomwar.getInt("war-length") *60*1000;
        SCOREBOARD_KEY= MetadataKey.create("guildwar", ScoreboardObjective.class);


        bossBar = Bukkit.createBossBar(
                Utils.addColors(
                        TridentGuild.messages.getString("bossbar.intermission").replace("%time%","")
                ),
                BarColor.YELLOW,
                BarStyle.SOLID);

        // Scoreboard güncelleme
        scoreboardUpdateTask=scoreboardUpdater();

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
                        players.forEach((playerName, warPlayer) -> {
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

                UUID winnerGuild = getGuildByIndex(0, true);

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
    public UUID getGuildByIndex(int index, boolean sort){
        if(sort)
            sortGuildsByPoints();
        return getGuildByIndex(index);
    }
    public UUID getGuildByIndex(int index){
        UUID guild = null;
        int i = 0;
        for(UUID keyGuild : guildPoints.keySet()){
            if(i==index)
                guild = keyGuild;
        }
        return guild;
    }
    public void finishSound(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);
        Schedulers.async().runLater(() -> {
            player.playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);
        }, 500, TimeUnit.MILLISECONDS);
        Schedulers.async().runLater(() -> {
            player.playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);
        }, 1000, TimeUnit.MILLISECONDS);
        Schedulers.async().runLater(() -> {
            player.playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,1,1);
        }, 1500, TimeUnit.MILLISECONDS);
        Schedulers.async().runLater(() -> {
            player.playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,1,1);
        }, 1800, TimeUnit.MILLISECONDS);
        Schedulers.async().runLater(() -> {
            player.playSound(player.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,1,1);
        }, 2100, TimeUnit.MILLISECONDS);
    }
    public boolean isGuildLimitReached(UUID guildUUID){
        int count = 0;
        for(String playerName : players.keySet()){
            if(players.get(playerName).equals(guildUUID))
                count+=1;
        }
        return count >= TridentGuild.kingdomwar.getInt("player-limit");
    }

    public void kill(Player killer, Player dead){
        playerWarDatas.get(killer.getName()).incKills();
        playerWarDatas.get(dead.getName()).incDeaths();
        addPoints(players.get(killer.getName()).getGuildUUID(),KILL_POINTS);
        removePlayer(dead);
    }
    public void addPlayerToWar(Player player, String spawnID){
        if(!TridentGuild.getGuildManager().hasGuild(player.getName())) return;
        UUID guildUUID = TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildUUID();
        if(!guildPoints.containsKey(TridentGuild.getGuildManager().getPlayerGuild(player.getName())))
            guildPoints.put(guildUUID,0);
        if(!playerWarDatas.containsKey(player.getName()))
            playerWarDatas.put(player.getName(),new WarPlayerData());
        CustomBannerStand bannerStand = new CustomBannerStand(player.getLocation(),
                TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).patterns,guildUUID,
                TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).bannerMaterial);

        WarPlayer wP = new WarPlayer(player.getName(), bannerStand, guildUUID);
        players.put(player.getName(), wP);

        registerScoreboardToPlayer(player);

        player.teleport(Utils.getLocationFromString(TridentGuild.kingdomwar.getString("spawn-locations."+spawnID),
                TridentGuild.getWarManager().getWar().getWorld()));
    }

    public void removePlayer(Player player){
        players.get(player.getName()).stop();
        players.remove(player.getName());
        ((LivingEntity) player).setHealth(20);
        player.teleport(Utils.getLocationFromString("lobby-spawn",world));
    }
    private void registerScoreboardToPlayer(Player player){
        ScoreboardObjective obj = sb.createPlayerObjective(player, "null", DisplaySlot.SIDEBAR);
        Metadata.provideForPlayer(player).put(SCOREBOARD_KEY, obj);

        updater.accept(player, obj);
    }
    public WarState getState() {
        return state;
    }
    public void stop(){
        main.cancel();
        players.forEach((s, warPlayer) -> {
            if(Bukkit.getOfflinePlayer(s).isOnline())
                removePlayer(Bukkit.getPlayerExact(s));
        });
    }
    public int getTimeLeft(){
        return (int) (endTime - System.currentTimeMillis());
    }
    public void sortGuildsByPoints(){
        guildPoints = Utils.sortByValue(guildPoints,false);
    }
    public void addPoints(UUID guildUUID, int points){
        Utils.debug("[TridentGuild] "+guildUUID+" earned "+points+" points!");
        guildPoints.replace(guildUUID,points+guildPoints.get(guildUUID));
    }
    public World getWorld() {
        return world;
    }
    private BukkitTask scoreboardUpdater(){
        return new BukkitRunnable(){
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(!players.containsKey(player.getName())) continue;
                    MetadataMap metadata = Metadata.provideForPlayer(player);
                    ScoreboardObjective obj = metadata.getOrNull(SCOREBOARD_KEY);
                    if (obj != null) {
                        updater.accept(player, obj);
                    }
                }
            }
        }.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }
    enum WarState{
        WAITING,
        PLAYING,
        FINISH
    }
}
