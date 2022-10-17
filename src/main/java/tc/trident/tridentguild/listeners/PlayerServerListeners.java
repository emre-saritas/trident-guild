package tc.trident.tridentguild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.UUID;

public class PlayerServerListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(!TridentGuild.getSqlHandler().hasGuild(e.getPlayer().getName())){
            return;
        }
        UUID uuid = TridentGuild.getSqlHandler().getGuildUUID(e.getPlayer().getName());

        TridentGuild.getGuildManager().onlinePlayerGuilds.put(e.getPlayer().getName(),uuid);

        // check if guild loaded, if not load it
        if(!TridentGuild.getGuildManager().isGuildLoaded(e.getPlayer().getName())){
            TridentGuild.getGuildManager().loadGuild(uuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(!TridentGuild.getGuildManager().hasGuild(e.getPlayer().getName())) return;
        UUID uuid = TridentGuild.getGuildManager().onlinePlayerGuilds.get(e.getPlayer().getName());
        TridentGuild.getGuildManager().onlinePlayerGuilds.remove(e.getPlayer().getName());

        if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsValue(uuid)){
            TridentGuild.getGuildManager().unloadGuild(uuid);
        }
    }
}
