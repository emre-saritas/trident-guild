package tc.trident.tridentguild.mysql;

import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.redis.Redis;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncManager {

    private ChannelAgent<JSONObject> agent7;

    public SyncManager(){
        setupChannelListener();
    }


    public void setupChannelListener(){
        agent7 = TridentSync.getInstance().getRedis().getChannel("sGuild", JSONObject.class).newAgent();

        agent7.addListener(((channelAgent, guildJSON) -> {
            try{
                Utils.debug("[TridentGuild] Redis data received - "+guildJSON.get("syncType"));

                if(SyncType.valueOf(guildJSON.get("syncType").toString()) == SyncType.UPDATE){
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(UUID.fromString(guildJSON.get("guildUUID").toString()))) return;

                    updateGuild(guildJSON);
                }else if(SyncType.valueOf(guildJSON.get("syncType").toString()) == SyncType.REMOVE_GUILD){
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(UUID.fromString(guildJSON.get("guildUUID").toString()))) return;
                    TridentGuild.getGuildManager().removeGuild(UUID.fromString(guildJSON.get("guildUUID").toString()));
                    Utils.debug("[TridentGuild] Guild removed - "+guildJSON.get("guildUUID").toString());

                }else if(SyncType.valueOf(guildJSON.get("syncType").toString()) == SyncType.REMOVE_PLAYER){
                    TridentGuild.getGuildManager().onlinePlayerGuilds.remove(guildJSON.get("playerName").toString());
                    Utils.debug("[TridentGuild] Guild member kicked - "+guildJSON.get("playerName").toString());
                    updateGuild(guildJSON);
                }
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }));


    }

    public void updateGuild(JSONObject guildJSON){
        List<GuildMember> members = new ArrayList<>();

        int i = 0;
        while(guildJSON.containsKey("members."+i+".name")){
            members.add(new GuildMember(guildJSON.get("members."+i+".name").toString(),
                    GuildMember.GuildPermission.valueOf(guildJSON.get("members."+i+".permission").toString()),
                    Integer.parseInt(guildJSON.get("members."+i+".donated").toString())));
            i++;
        }

        Guild newGuildData = new Guild(UUID.fromString(guildJSON.get("guildUUID").toString()),
                guildJSON.get("name").toString(),
                null,
                Integer.parseInt(guildJSON.get("level").toString()),
                Float.parseFloat(guildJSON.get("balance").toString()),
                Integer.parseInt(guildJSON.get("minerLevel").toString()),
                Integer.parseInt(guildJSON.get("lumberLevel").toString()),
                Integer.parseInt(guildJSON.get("hunterLevel").toString()),
                Integer.parseInt(guildJSON.get("farmerLevel").toString()),
                members,
                Guild.deserializeMemberPerms(guildJSON.get("memberPerms").toString()),
                Guild.deserializeMemberPerms(guildJSON.get("opPerms").toString()));

        TridentGuild.getGuildManager().updateGuild(newGuildData);
        Utils.debug("[TridentGuild] Guild updated - "+guildJSON.get("guildUUID").toString());
    }
    public void syncGuild(Guild guild, SyncType syncType){
        syncGuild(guild,syncType,null);
    }
    public void syncGuild(Guild guild, SyncType syncType, String playerName){
        JSONObject json = new JSONObject();
        json.put("syncType",syncType.toString());
        json.put("guildUUID",guild.getGuildUUID());
        if(syncType == SyncType.UPDATE || syncType == SyncType.REMOVE_PLAYER){
            if(syncType == SyncType.REMOVE_PLAYER){
                json.put("playerName",playerName);
            }
            json.put("name",guild.getGuildName());
            json.put("bannerMeta",null);
            json.put("level",guild.getGuildLevel());
            json.put("balance",guild.getBalance());
            json.put("minerLevel",guild.getMinerLevel());
            json.put("lumberLevel",guild.getLumberLevel());
            json.put("hunterLevel",guild.getHunterLevel());
            json.put("farmerLevel",guild.getFarmerLevel());
            json.put("memberPerms",Guild.serializePerms(guild.getMemberPerms()));
            json.put("opPerms",Guild.serializePerms(guild.getOperatorPerms()));
            for(int i=0; i<guild.memberList.size(); i++){
                json.put("members."+i+".name",guild.memberList.get(i).getPlayer().getName());
                json.put("members."+i+".permission",guild.memberList.get(i).getPermission().toString());
                json.put("members."+i+".donated",guild.memberList.get(i).getTotalDonate());
            }
        }
        TridentSync.getInstance().getRedis().getChannel("sGuild", JSONObject.class).sendMessage(json);
    }

    public void close(){
        agent7.close();
    }
}
