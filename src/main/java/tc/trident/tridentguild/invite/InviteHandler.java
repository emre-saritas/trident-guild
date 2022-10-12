package tc.trident.tridentguild.invite;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.HashMap;
import java.util.UUID;

public class InviteHandler {

    private HashMap<String, Invite> playerInvites = new HashMap<>();
    private HashMap<String, Long> playerGuildDeletes = new HashMap<>();
    private BukkitTask inviteController;

    public InviteHandler(){
        setupInviteListener();
        inviteController = inviteControllerRun.runTaskTimerAsynchronously(TridentGuild.getInstance(),10,10);
    }


    private BukkitRunnable inviteControllerRun = new BukkitRunnable() {
        @Override
        public void run() {
            if(playerInvites.size() != 0){
                for(Invite invite : playerInvites.values()){
                    if(invite.isExpired()){
                        playerInvites.remove(invite.getPlayerName());
                    }
                }
            }
        }
    };

    public void removeGuildDeleteCooldown(String playerName){
        playerGuildDeletes.remove(playerName);
    }
    public void addGuildDeleteCooldown(String playerName){
        playerGuildDeletes.replace(playerName, System.currentTimeMillis());
    }
    public boolean isGuildDeleteCooldownExpired(String playerName){
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
    public void setupInviteListener(){
        TridentSync.getInstance().getRedis().getChannel("sGuildInvite",InviteRedisData.class).newAgent().addListener(((channelAgent, inviteRedisData) -> {
            if(!Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()))) return;
            switch (inviteRedisData.getDataType()){
                case INVITE:
                    if(playerInvites.containsKey(inviteRedisData.getTargetPlayerName())){
                        sendMessage(inviteRedisData.getTargetPlayerName(),inviteRedisData.getSendingPlayer(), InviteRedisData.InviteDataType.ERROR_HAS_INVITE);
                        break;
                    }
                    if(TridentGuild.getGuildManager().hasGuild(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()))){
                        sendMessage(inviteRedisData.getTargetPlayerName(),inviteRedisData.getSendingPlayer(), InviteRedisData.InviteDataType.ERROR_HAS_GUILD);
                        break;
                    }
                    playerInvites.put(inviteRedisData.getTargetPlayerName(),inviteRedisData.getInvite());
                    Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()).sendMessage(Utils.addColors(Utils.getMessage("invite-received",true)));
                    break;
                case JOINED_GUILD:
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(inviteRedisData.getInvite().getGuildUUID())) break;
                    TridentGuild.getGuildManager().loadedGuilds.get(inviteRedisData.getInvite().getGuildUUID()).memberList.forEach(member -> {
                        if(member.getPlayer().isOnline()){
                            member.getPlayer().getPlayer().sendMessage(Utils.addColors(Utils.getMessage("player-joined-guild",true).replace("%player%",inviteRedisData.getSendingPlayer())));
                        }
                    });
                    break;
                case ERROR_HAS_GUILD:
                    Utils.sendError(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()),"invite-already-has-guild");
                    break;
                case ERROR_HAS_INVITE:
                    Utils.sendError(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()),"invite-already-has");
                    break;
            }
        }));
    }
}
