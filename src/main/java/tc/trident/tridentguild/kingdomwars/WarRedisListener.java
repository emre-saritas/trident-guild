package tc.trident.tridentguild.kingdomwars;

import me.lucko.helper.messaging.ChannelAgent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class WarRedisListener implements Listener {

    private ChannelAgent<WarRedisData> agent;

    public WarRedisListener(){
        setupRedisListener();
    }

    public void setupRedisListener(){
        agent = TridentSync.getInstance().getRedis().getChannel("sWar", WarRedisData.class).newAgent();
        agent.addListener(((channelAgent, redisData) -> {
            try{
                Utils.debug("[TridentGuild] War redis data received - "+redisData.getType());

                switch (redisData.getType()){
                    case WAR_DATA:
                        TridentGuild.getWarManager().setupWar(redisData.getQueueWarGuilds());
                        break;
                    case IS_FULL:
                        WarRedisData data = new WarRedisData(WarRedisData.WarRedisDataType.IS_FULL_ANSWER, null,null, TridentGuild.getWarManager().getWar().isGuildFull(redisData.getGuildUUID()), redisData.getPlayerName());
                        TridentSync.getInstance().getRedis().getChannel("sWar", WarRedisData.class).sendMessage(data);
                        break;
                    case IS_FULL_ANSWER:
                        if(Bukkit.getOfflinePlayer(redisData.getPlayerName()).isOnline()){
                            if(redisData.isFull()){
                                Utils.sendError(Bukkit.getPlayerExact(redisData.getPlayerName()),"error.war-is-full");
                            }else{
                                // countdown and send to war
                            }
                        }
                        break;

                }
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }));
    }

    public void close(){
        agent.close();
    }
}
