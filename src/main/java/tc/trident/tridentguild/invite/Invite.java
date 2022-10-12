package tc.trident.tridentguild.invite;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.tridentguild.TridentGuild;

import java.util.UUID;

public class Invite {
    private final String playerName;
    private final UUID guildUUID;
    private final long inviteDate;

    public Invite(String playerName, UUID guildUUID, long inviteDate) {
        this.playerName = playerName;
        this.guildUUID = guildUUID;
        this.inviteDate = inviteDate;
    }

    public boolean isExpired(){
        return (System.currentTimeMillis()-inviteDate) < TridentGuild.config.getInt("invite-time")* 1000L;
    }
    public int getSecondsLeft(){
        return (int) (((System.currentTimeMillis()+(TridentGuild.config.getInt("invite-time")*1000L))-inviteDate)/1000L);
    }
    public String getPlayerName() {
        return playerName;
    }
    public UUID getGuildUUID() {
        return guildUUID;
    }
    public long getInviteDate() {
        return inviteDate;
    }
}
