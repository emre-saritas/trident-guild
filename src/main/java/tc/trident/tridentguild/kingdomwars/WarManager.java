package tc.trident.tridentguild.kingdomwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.GuildChatRedisData;
import tc.trident.tridentguild.utils.Utils;

import java.util.*;

public class WarManager {
    private List<UUID> queueWarGuilds = new ArrayList<>();
    private TimerTask mainTask;
    private War war;
    private ServerType serverType;
    private WarRedisListener warRedisListener;
    private WarState state;



    public WarManager(ServerType type){
        warRedisListener = new WarRedisListener();
        Bukkit.getPluginManager().registerEvents(warRedisListener,TridentGuild.getInstance());

        this.serverType=type;
        start();
        /**
         * değişecek
         * sadece hub sunucusunda gerçekleşecek
         * data savaş sunucularına gönderilecek
         */
    }

    public void start(){
        Timer timer = new Timer ();
        this.mainTask = new TimerTask() {
            @Override
            public void run() {
                // is war time check
                startNewWar();
            }
        };
        timer.schedule (mainTask, 0l, 1000);
    }

    public void reset(){    // redis finish
        Utils.debug("[TridentGuild] War Manager Reset");
        queueWarGuilds = new ArrayList<>();
        start();
    }
    public void startNewWar(){
        this.mainTask.cancel();
        state = WarState.STARTED;
        Utils.debug("[TridentGuild] War is starting... Guilds:");
        queueWarGuilds.forEach(UUID -> {
            Utils.debug(UUID.toString());
        });
        WarRedisData data = new WarRedisData(WarRedisData.WarRedisDataType.WAR_DATA, null,queueWarGuilds);
        TridentSync.getInstance().getRedis().getChannel("sWar", WarRedisData.class).sendMessage(data);
    }
    public void setupWar(List<UUID> queueWarGuilds){
        this.war = new War(queueWarGuilds);
    }
    public void addGuildToWarQueue(UUID guildUUID){     // After owner purchase war
        queueWarGuilds.add(guildUUID);
    }
    public void sendPlayerToWar(Player player){
        if(!TridentGuild.getGuildManager().hasGuild(player.getName())){
            Utils.sendError(player,"you-not-guild-member");
            return;
        }
        if(state == WarState.WAITING){
            Utils.sendError(player,"error.not-started");
            return;
        }
        UUID playerGuild = TridentGuild.getGuildManager().onlinePlayerGuilds.get(player.getName());
        if(!queueWarGuilds.contains(playerGuild)){
            Utils.sendError(player,"error.not-war-guild-member");
            return;
        }
        TridentSync.sendPlayerToServer(player,"war");
    }
    public War getWar() {
        return war;
    }

    public void close(){
        warRedisListener.close();
    }
    public enum ServerType{
        WAR,
        HUB
    }
    public enum WarState{
        WAITING,
        STARTED
    }
}
