package tc.trident.tridentguild.mysql;

import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.utils.Utils;

public class GuildRedisData {

    private final Guild newGuild;
    private final SyncType type;
    private final String playerName;


    public GuildRedisData(Guild newGuild, SyncType type, String playerName) {
        this.newGuild = newGuild;
        this.type = type;
        this.playerName = playerName;
    }

    public Guild getNewGuild() {
        return newGuild;
    }

    public String getPlayerName() {
        return playerName;
    }

    public SyncType getType() {
        return type;
    }
}
