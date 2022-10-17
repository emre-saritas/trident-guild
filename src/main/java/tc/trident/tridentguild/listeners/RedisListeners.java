package tc.trident.tridentguild.listeners;

import me.lucko.helper.messaging.ChannelAgent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.invite.InviteRedisData;
import tc.trident.tridentguild.mysql.GuildRedisData;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

import java.util.UUID;

public class RedisListeners implements Listener {

    private ChannelAgent<GuildRedisData> agent1;
    private ChannelAgent<InviteRedisData> agent2;

    public RedisListeners(){
        setupInviteListener();
        setupChannelListener();
    }

    public void setupChannelListener(){
        agent1 = TridentSync.getInstance().getRedis().getChannel("sGuild", GuildRedisData.class).newAgent();
        agent1.addListener(((channelAgent, redisData) -> {
            try{
                Utils.debug("[TridentGuild] Redis data received - "+redisData.getType());

                if(redisData.getType() == SyncType.UPDATE){
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(redisData.getNewGuild().getGuildUUID())) return;
                    TridentGuild.getSyncManager().updateGuild(redisData.getNewGuild());

                }else if(redisData.getType() == SyncType.REMOVE_GUILD){
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(redisData.getNewGuild().getGuildUUID())) return;
                    TridentGuild.getGuildManager().removeGuild(redisData.getNewGuild().getGuildUUID());
                    Utils.debug("[TridentGuild] Guild removed - "+redisData.getNewGuild().getGuildUUID());

                }else if(redisData.getType() == SyncType.REMOVE_PLAYER){
                    TridentGuild.getGuildManager().onlinePlayerGuilds.remove(redisData.getPlayerName());
                    Utils.debug("[TridentGuild] Guild member kicked - "+redisData.getPlayerName());

                    if(TridentGuild.getGuildManager().onlinePlayerGuilds.containsValue(redisData.getNewGuild().getGuildUUID())){
                        TridentGuild.getSyncManager().updateGuild(redisData.getNewGuild());
                    }else{
                        if(TridentGuild.getGuildManager().loadedGuilds.containsKey(redisData.getNewGuild().getGuildUUID())){
                            TridentGuild.getGuildManager().unloadGuild(redisData.getNewGuild().getGuildUUID());
                        }
                    }
                }
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }));
    }

    public void setupInviteListener(){
        agent2 = TridentSync.getInstance().getRedis().getChannel("sGuildInvite", InviteRedisData.class).newAgent();
        agent2.addListener(((channelAgent, inviteRedisData) -> {
            Utils.debug("[TridentGuild] Redis data received - "+inviteRedisData.getDataType());
            switch (inviteRedisData.getDataType()){
                case INVITE:
                    if(!Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()))) return;
                    if(TridentGuild.getInviteHandler().playerInvites.containsKey(inviteRedisData.getTargetPlayerName())){
                        TridentGuild.getInviteHandler().sendMessage(inviteRedisData.getTargetPlayerName(),inviteRedisData.getSendingPlayer(), InviteRedisData.InviteDataType.ERROR_HAS_INVITE);
                        break;
                    }
                    if(TridentGuild.getGuildManager().hasGuild(inviteRedisData.getTargetPlayerName())){
                        TridentGuild.getInviteHandler().sendMessage(inviteRedisData.getTargetPlayerName(),inviteRedisData.getSendingPlayer(), InviteRedisData.InviteDataType.ERROR_HAS_GUILD);
                        break;
                    }
                    TridentGuild.getInviteHandler().playerInvites.put(inviteRedisData.getTargetPlayerName(),inviteRedisData.getInvite());
                    Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()).sendMessage(Utils.addColors(Utils.getMessage("invite-received",true).replace("%player%",inviteRedisData.getSendingPlayer()).replace("%guild%",inviteRedisData.getInvite().getGuildName())));
                    Utils.debug("[TridentGuild] Guild invite sent - "+inviteRedisData.getTargetPlayerName());
                    break;
                case JOINED_GUILD:
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(UUID.fromString(inviteRedisData.getTargetPlayerName()))) break;
                    Utils.debug(UUID.fromString(inviteRedisData.getTargetPlayerName()) + " var");
                    TridentGuild.getGuildManager().loadedGuilds.get(UUID.fromString(inviteRedisData.getTargetPlayerName())).memberList.forEach(member -> {
                        Utils.debug(member.getPlayer().getName());
                        if(member.getPlayer().isOnline()){
                            Utils.debug(member.getPlayer().getName() + " online");
                            member.getPlayer().getPlayer().sendMessage(Utils.addColors(Utils.getMessage("player-joined-guild",true).replace("%player%",inviteRedisData.getSendingPlayer())));
                        }
                    });
                    Utils.debug("[TridentGuild] New member - "+inviteRedisData.getSendingPlayer());
                    break;
                case ERROR_HAS_GUILD:
                    if(!Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()))) return;
                    Utils.sendError(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()),"invite-already-has-guild");
                    break;
                case ERROR_HAS_INVITE:
                    if(!Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()))) return;
                    Utils.sendError(Bukkit.getPlayerExact(inviteRedisData.getTargetPlayerName()),"invite-already-has-invite");
                    break;
            }
        }));
    }

    public void close(){
        agent1.close();
        agent2.close();
    }
}
