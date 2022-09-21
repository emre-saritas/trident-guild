package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {


    public HashMap<UUID, Guild> loadedGuilds = new HashMap<>();
    public HashMap<String, UUID> onlinePlayerGuilds = new HashMap<>();
    public List<String> guildNames = new ArrayList<>();

    public GuildManager(){
        //loadOnlinePlayerGuildUUIDs();
        //loadOnlinePlayerGuilds();
    }


    public void loadOnlinePlayerGuildUUIDs(){

    }
    public void loadOnlineGuilds(){
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(!isGuildLoaded(player.getName())){
                loadGuild(onlinePlayerGuilds.get(player.getName()));
            }
        });
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
    }
    public void loadGuild(UUID guildUUID){
        loadedGuilds.put(guildUUID,null);
    }
    public void unloadGuild(UUID guildUUID){
        loadedGuilds.remove(guildUUID);
    }
}
