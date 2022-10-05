package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.mysql.SyncType;

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




    public void syncGuild(Guild guild, SyncType updateType){
        if(updateType == SyncType.UPDATE){
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

        }else if(updateType == SyncType.REMOVE_GUILD){
            TridentGuild.getSqlHandler().deleteGuild(guild.getGuildUUID().toString());
        }
    }
    public void syncGuildMember(GuildMember member, UUID guildUUID, SyncType updateType){
        if(updateType == SyncType.UPDATE){
            TridentGuild.getSqlHandler().updateGuildMember(member.getPlayer().getName(),
                    guildUUID.toString(),
                    member.getPermission().toString(),
                    member.getTotalDonate());
        }else if(updateType == SyncType.REMOVE_PLAYER){
            TridentGuild.getSqlHandler().deleteGuildMember(member.getPlayer().getName());
        }
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

    public void updateGuild(Guild guild){
        loadedGuilds.replace(guild.getGuildUUID(),guild);
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
        TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
        TridentGuild.getGuildManager().syncGuild(guild, SyncType.UPDATE);
    }
    public void removeGuild(UUID uuid){
        Guild guild = loadedGuilds.get(uuid);
        guildNames.remove(guild.getGuildName());
        guild.guildMembers.forEach((name,gMember)->{
            onlinePlayerGuilds.remove(name);
        });
        unloadGuild(uuid);
        TridentGuild.getSyncManager().syncGuild(guild,SyncType.REMOVE_GUILD);
        TridentGuild.getGuildManager().syncGuild(guild, SyncType.REMOVE_GUILD);
    }

    public void unloadGuild(UUID guildUUID){
        loadedGuilds.remove(guildUUID);
    }
}
