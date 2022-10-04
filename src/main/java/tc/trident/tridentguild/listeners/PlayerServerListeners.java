package tc.trident.tridentguild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.trident.tridentguild.TridentGuild;

import java.util.UUID;

public class PlayerServerListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        UUID uuid = TridentGuild.getSqlHandler().getGuildUUID(e.getPlayer().getName());
        if(uuid == null) return;    // has guild check
        // Save player guild uuid
        TridentGuild.getGuildManager().onlinePlayerGuilds.put(e.getPlayer().getName(),uuid);

        // check if guild loaded, if not load it
        if(!TridentGuild.getGuildManager().isGuildLoaded(e.getPlayer().getName())){
            TridentGuild.getGuildManager().loadGuild(uuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(!TridentGuild.getGuildManager().hasGuild(e.getPlayer())) return;
        UUID uuid = TridentGuild.getGuildManager().onlinePlayerGuilds.get(e.getPlayer().getName());
        TridentGuild.getGuildManager().onlinePlayerGuilds.remove(e.getPlayer().getName());

        // check if guild still has online player then unload it
        if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsValue(uuid)){
            TridentGuild.getGuildManager().unloadGuild(uuid);
        }
    }
}
