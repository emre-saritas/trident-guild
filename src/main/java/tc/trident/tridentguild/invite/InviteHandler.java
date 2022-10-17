package tc.trident.tridentguild.invite;

import me.lucko.helper.messaging.ChannelAgent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.HashMap;
import java.util.UUID;

public class InviteHandler {

    public HashMap<String, Invite> playerInvites = new HashMap<>();
    public HashMap<String, Long> playerGuildDeletes = new HashMap<>();
    private BukkitTask inviteController;

    public InviteHandler(){
        inviteController = inviteControllerRun.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }


    private BukkitRunnable inviteControllerRun = new BukkitRunnable() {
        @Override
        public void run() {
            if(playerInvites.size() != 0){
                for(Invite invite : playerInvites.values()){
                    if(invite.isExpired()){
                        if(Bukkit.getOfflinePlayer(invite.getPlayerName()).isOnline())
                            Bukkit.getPlayerExact(invite.getPlayerName()).sendMessage(Utils.addColors(Utils.getMessage("invite-expired",true)));
                        playerInvites.remove(invite.getPlayerName());
                    }
                }
            }
        }
    };
    public Invite getInvite(String playerName){
        return playerInvites.get(playerName);
    }
    public void removeGuildDeleteCooldown(String playerName){
        if(playerGuildDeletes.containsKey(playerName))
            playerGuildDeletes.remove(playerName);
    }
    public void addGuildDeleteCooldown(String playerName){
        if(playerGuildDeletes.containsKey(playerName))
            playerGuildDeletes.replace(playerName, System.currentTimeMillis());
        else
            playerGuildDeletes.put(playerName, System.currentTimeMillis());
    }
    public boolean isGuildDeleteCooldownExpired(String playerName){
        if(!playerGuildDeletes.containsKey(playerName)) return true;
        return System.currentTimeMillis() >= playerGuildDeletes.get(playerName)+5000L;
    }
    public void sendInvite(String sendingPlayer, String targetPlayer){
        Invite invite = new Invite(targetPlayer,TridentGuild.getGuildManager().onlinePlayerGuilds.get(sendingPlayer),System.currentTimeMillis());
        sendMessage(sendingPlayer,targetPlayer, InviteRedisData.InviteDataType.INVITE,invite);
    }

    public void sendMessage(String sendingPlayer, String targetPlayer, InviteRedisData.InviteDataType dataType){
        sendMessage(sendingPlayer, targetPlayer,dataType,null);
    }
    public void sendMessage(String sendingPlayer, String targetPlayer, InviteRedisData.InviteDataType dataType, Invite invite){
        TridentSync.getInstance().getRedis().getChannel("sGuildInvite",InviteRedisData.class).sendMessage(new InviteRedisData(sendingPlayer,targetPlayer,invite,dataType));
    }
}
