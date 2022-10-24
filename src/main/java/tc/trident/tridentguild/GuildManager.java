package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import tc.trident.tridentguild.listeners.GuildListeners;
import tc.trident.tridentguild.listeners.PlayerServerListeners;
import tc.trident.tridentguild.mysql.SyncType;
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
        guildNames.addAll(TridentGuild.getSqlHandler().getGuildNames());
        Bukkit.getPluginManager().registerEvents(new GuildListeners(),TridentGuild.getInstance());
    }




    public void syncToSqlGuild(Guild guild, SyncType updateType){
        if(updateType == SyncType.UPDATE){
            TridentGuild.getSqlHandler().updateGuild(guild.getGuildUUID().toString(),
                    guild.getGuildName(),
                    guild.getGuildLevel(),
                    guild.getMinerLevel(),
                    guild.getLumberLevel(),
                    guild.getHunterLevel(),
                    guild.getFarmerLevel(),
                    guild.getBalance(),
                    guild.serializePatterns(),
                    Guild.serializeMemberPerms(guild.getMemberPerms()),
                    Guild.serializeOpPerms(guild.getOperatorPerms()),
                    guild.getBannerMaterial().toString(),
                    guild.getCreateDate(),
                    guild.isPvp());
        }else if(updateType == SyncType.REMOVE_GUILD){
            TridentGuild.getSqlHandler().deleteGuild(guild.getGuildUUID().toString());
        }
    }
    public void syncToSqlGuildMember(GuildMember member, UUID guildUUID, SyncType updateType){
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
                if(TridentGuild.getSqlHandler().hasGuild(player.getName()))
                    loadGuild(onlinePlayerGuilds.get(player.getName()));
            }
        });
    }

    public void updateGuild(Guild guild){
        loadedGuilds.replace(guild.getGuildUUID(),guild);
    }
    public void loadGuild(UUID guildUUID){
        Guild guild = TridentGuild.getSqlHandler().getGuild(guildUUID);
        if(guild == null) return;
        loadedGuilds.put(guildUUID,guild);
        guildNames.add(guild.getGuildName().toLowerCase());
    }
    public boolean hasGuild(String playerName){
        if(Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayerExact(playerName))){
            return onlinePlayerGuilds.containsKey(playerName);
        }else{
            return TridentGuild.getSqlHandler().getGuildUUID(playerName) != null;
        }
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
        guild.addGuildMember(playerName, GuildMember.GuildPermission.OWNER);
        guild.makeOwner(playerName);
        loadedGuilds.put(uuid,guild);
        onlinePlayerGuilds.put(playerName,uuid);
        guildNames.add(guildName.toLowerCase());
        TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
        TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
    }
    public void removeGuild(UUID uuid){
        Guild guild = loadedGuilds.get(uuid);
        guildNames.remove(guild.getGuildName().toLowerCase());
        guild.guildMembers.forEach((name,gMember)->{
            onlinePlayerGuilds.remove(name);
        });
        loadedGuilds.remove(uuid);
    }


    public void unloadGuild(UUID guildUUID){
        Guild guild = loadedGuilds.get(guildUUID);
        TridentGuild.getSqlHandler().updateGuild(guild.getGuildUUID().toString(),
                guild.getGuildName(),
                guild.getGuildLevel(),
                guild.getMinerLevel(),
                guild.getLumberLevel(),
                guild.getHunterLevel(),
                guild.getFarmerLevel(),
                guild.getBalance(),
                guild.serializePatterns(),
                Guild.serializeMemberPerms(guild.getMemberPerms()),
                Guild.serializeOpPerms(guild.getOperatorPerms()),
                guild.getBannerMaterial().toString(),
                guild.getCreateDate(),
                guild.isPvp());
        loadedGuilds.remove(guildUUID);
    }
    public void saveGuild(UUID guildUUID){
        Guild guild = loadedGuilds.get(guildUUID);
        TridentGuild.getSqlHandler().updateGuild(guild.getGuildUUID().toString(),
                guild.getGuildName(),
                guild.getGuildLevel(),
                guild.getMinerLevel(),
                guild.getLumberLevel(),
                guild.getHunterLevel(),
                guild.getFarmerLevel(),
                guild.getBalance(),
                guild.serializePatterns(),
                Guild.serializeMemberPerms(guild.getMemberPerms()),
                Guild.serializeOpPerms(guild.getOperatorPerms()),
                guild.getBannerMaterial().toString(),
                guild.getCreateDate(),
                guild.isPvp());
    }
    public void unloadAllGuilds(){
        loadedGuilds.forEach((uuid, guild) -> {
            saveGuild(uuid);
        });
    }
}
