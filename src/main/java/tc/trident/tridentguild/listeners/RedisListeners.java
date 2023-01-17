package tc.trident.tridentguild.listeners;

import me.lucko.helper.messaging.ChannelAgent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;
import tc.trident.sync.TridentSync;
import tc.trident.sync.server.ServerType;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.invite.InviteRedisData;
import tc.trident.tridentguild.mysql.GuildChatRedisData;
import tc.trident.tridentguild.mysql.GuildRedisData;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

import java.util.UUID;

public class RedisListeners implements Listener {

    private ChannelAgent<GuildRedisData> agent1;
    private ChannelAgent<InviteRedisData> agent2;
    private ChannelAgent<GuildChatRedisData> agent3;

    public RedisListeners(){
        setupInviteListener();
        setupChannelListener();
        guildChatListener();
    }

    public void setupChannelListener(){
        agent1 = TridentSync.getInstance().getRedis().getChannel("sGuild", GuildRedisData.class).newAgent();
        agent1.addListener(((channelAgent, redisData) -> {
            try{
                Utils.debug("[TridentGuild] Redis data received - "+redisData.getType());

                if(redisData.getType() == SyncType.UPDATE){
                    if(!TridentGuild.getGuildManager().guildNames.contains(redisData.getNewGuild().getGuildName().toLowerCase())) TridentGuild.getGuildManager().guildNames.add(redisData.getNewGuild().getGuildName().toLowerCase());
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

    public void guildChatListener(){
        agent3 = TridentSync.getInstance().getRedis().getChannel("sGuildChat", GuildChatRedisData.class).newAgent();
        agent3.addListener(((channelAgent, chatRedisData) -> {
            TridentGuild.getGuildManager().onlinePlayerGuilds.forEach((playerName,guildUUID) -> {
                if(guildUUID.equals(chatRedisData.getGuildUUID())){
                    Bukkit.getPlayerExact(playerName).sendMessage(chatRedisData.getMessage());
                }
            });
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
                    TridentGuild.getGuildManager().loadedGuilds.get(UUID.fromString(inviteRedisData.getTargetPlayerName())).guildMembers.forEach((playerName, member) -> {
                        if(member.getPlayer().isOnline()){
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
        agent3.close();
    }
}
