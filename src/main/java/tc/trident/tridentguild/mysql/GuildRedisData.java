package tc.trident.tridentguild.mysql;

import tc.trident.tridentguild.Guild;

public class GuildRedisData {

    private Guild newGuild;
    private SyncType type;
    private String playerName;

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
