package tc.trident.tridentguild.kingdomwars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarRedisData {


    private final WarRedisDataType type;
    private UUID guildUUID;
    private boolean isFull;
    private List<UUID> queueWarGuilds = new ArrayList<>();
    private String playerName;


    public WarRedisData(WarRedisDataType type){
        this.type=type;
    }
    public WarRedisData(WarRedisDataType type, UUID guildUUID){
        this.type=type;
        this.guildUUID=guildUUID;
    }
    public WarRedisData(WarRedisDataType type, UUID guildUUID, List<UUID> queueWarGuilds){
        this.type=type;
        this.guildUUID=guildUUID;
        this.queueWarGuilds = new ArrayList<>(queueWarGuilds);
    }
    public WarRedisData(WarRedisDataType type, UUID guildUUID, List<UUID> queueWarGuilds, boolean isFull, String playerName){
        this.type=type;
        this.guildUUID=guildUUID;
        this.queueWarGuilds = new ArrayList<>(queueWarGuilds);
        this.isFull=isFull;
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
    public boolean isFull() {
        return isFull;
    }
    public List<UUID> getQueueWarGuilds() {
        return queueWarGuilds;
    }
    public WarRedisDataType getType(){
        return type;
    }
    public UUID getGuildUUID() {
        return guildUUID;
    }
    public enum WarRedisDataType{
        WAR_DATA,
        IS_FULL,
        IS_FULL_ANSWER
    }
}
