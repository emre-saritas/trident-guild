package tc.trident.tridentguild.kingdomwars;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class WarPlayer {
    private final UUID guildUUID;
    private final OfflinePlayer player;
    private int killCount;
    private int deathCount;
    private boolean protection;

    public WarPlayer(OfflinePlayer player, UUID guildUUID) {
        this.player=player;
        this.guildUUID = guildUUID;
    }
    public OfflinePlayer getPlayer() {
        return player;
    }
    public UUID getGuildUUID() {
        return guildUUID;
    }
    public int getDeathCount() {
        return deathCount;
    }
    public void addKill(){
        killCount+=1;
    }
    public void addDeath(){
        deathCount+=1;
    }
    public int getKillCount() {
        return killCount;
    }
    public boolean isProtected() {
        return protection;
    }
    public void setProtection(boolean protection) {
        this.protection = protection;
    }
}
