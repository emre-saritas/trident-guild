package tc.trident.tridentguild.mysql;

import java.util.UUID;

public class GuildChatRedisData {


    private final UUID guildUUID;
    private final String message;
    private final String sendingPlayerName;

    public GuildChatRedisData(UUID guildUUID, String message, String sendingPlayerName) {
        this.guildUUID = guildUUID;
        this.message = message;
        this.sendingPlayerName = sendingPlayerName;
    }

    public UUID getGuildUUID() {
        return guildUUID;
    }
    public String getMessage() {
        return message;
    }
    public String getSendingPlayerName() {
        return sendingPlayerName;
    }
}
