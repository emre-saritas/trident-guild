package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.mysql.SqlUpdateType;
import tc.trident.tridentguild.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {


    public HashMap<UUID, Guild> loadedGuilds = new HashMap<>();
    public HashMap<String, UUID> onlinePlayerGuilds = new HashMap<>();
    public List<String> guildNames = new ArrayList<>();

    public GuildManager(){
        loadOnlinePlayerGuildUUIDs();
        loadOnlineGuilds();
    }




    public void syncGuild(Guild guild, SqlUpdateType updateType){
        if(updateType == SqlUpdateType.UPDATE){
            TridentGuild.getSqlHandler().updateGuild(guild.getGuildUUID().toString(),
                    guild.getGuildName(),
                    guild.getGuildLevel(),
                    guild.getMinerLevel(),
                    guild.getLumberLevel(),
                    guild.getHunterLevel(),
                    guild.getFarmerLevel(),
                    guild.getBalance(),
                    null,
                    Guild.serializePerms(guild.getMemberPerms()),
                    Guild.serializePerms(guild.getOperatorPerms()));

        }else if(updateType == SqlUpdateType.REMOVE){
            TridentGuild.getSqlHandler().deleteGuild(guild.getGuildUUID().toString());
        }

        // message to other servers
    }
    public void syncGuildMember(GuildMember member, UUID guildUUID,SqlUpdateType updateType){

        if(updateType == SqlUpdateType.UPDATE){
            TridentGuild.getSqlHandler().updateGuildMember(member.getPlayer().getName(),
                    guildUUID.toString(),
                    member.getPermission().toString(),
                    member.getTotalDonate());
        }else if(updateType == SqlUpdateType.REMOVE){
            TridentGuild.getSqlHandler().deleteGuildMember(member.getPlayer().getName());
        }

        // message to other servers
    }
    public void loadOnlinePlayerGuildUUIDs(){
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UUID uuid = TridentGuild.getSqlHandler().getGuildUUID(player.getName());
            if(uuid != null)
                onlinePlayerGuilds.put(player.getName(),uuid);
        });
    }
    public void loadOnlineGuilds(){
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(!isGuildLoaded(player.getName())){
                loadGuild(onlinePlayerGuilds.get(player.getName()));
            }
        });
    }


    public void loadGuild(UUID guildUUID){
        Guild guild = TridentGuild.getSqlHandler().getGuild(guildUUID.toString());
        loadedGuilds.put(guildUUID,guild);
        guildNames.add(guild.getGuildName());
    }
    public boolean hasGuild(Player player){
        return onlinePlayerGuilds.containsKey(player.getName());
    }
    public boolean isGuildLoaded(String playerName){
        return loadedGuilds.containsKey(onlinePlayerGuilds.get(playerName));
    }
    public Guild getPlayerGuild(String playerName){
        return loadedGuilds.get(onlinePlayerGuilds.get(playerName));
    }
    public void createGuild(String playerName, String guildName){
        UUID uuid = UUID.randomUUID();
        Guild guild = new Guild(uuid,guildName);
        guild.addGuildMember(playerName);
        guild.makeOwner(playerName);
        loadedGuilds.put(uuid,guild);
        onlinePlayerGuilds.put(playerName,uuid);
        guildNames.add(guildName);
        TridentGuild.getGuildManager().syncGuild(guild, SqlUpdateType.UPDATE);
    }
    public void removeGuild(Guild guild){
        guildNames.remove(guild.getGuildName());
        UUID uuid = guild.getGuildUUID();
        guild.guildMembers.forEach((name,gMember)->{
            onlinePlayerGuilds.remove(name);
        });
        unloadGuild(uuid);
        TridentGuild.getGuildManager().syncGuild(guild, SqlUpdateType.REMOVE);
    }

    public void unloadGuild(UUID guildUUID){
        loadedGuilds.remove(guildUUID);
    }
}
