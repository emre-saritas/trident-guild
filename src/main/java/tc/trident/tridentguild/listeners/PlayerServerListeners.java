package tc.trident.tridentguild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import tc.trident.sync.TridentSync;
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
    @EventHandler (priority = EventPriority.LOW)
    public void playerSyncData(PlayerJoinEvent e){
        if(!TridentGuild.getSqlHandler().hasGuild(e.getPlayer().getName())){
            return;
        }
        UUID uuid = TridentGuild.getSqlHandler().getGuildUUID(e.getPlayer().getName());
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!TridentSync.getInstance().getPlayerManager().hasPlayerData(e.getPlayer().getName())) return;
                if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(uuid)) return;
                TridentGuild.getGuildManager().loadedGuilds.get(uuid).getGuildMember(e.getPlayer().getName()).syncPoints();
            }
        }.runTaskLaterAsynchronously(TridentGuild.getInstance(),40);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(!TridentGuild.getGuildManager().hasGuild(e.getPlayer().getName())) return;
        UUID uuid = TridentGuild.getGuildManager().onlinePlayerGuilds.get(e.getPlayer().getName());
        TridentGuild.getGuildManager().unloadGuildMember(TridentGuild.getGuildManager().getPlayerGuild(e.getPlayer().getName()).getGuildMember(e.getPlayer().getName()),uuid);
        TridentGuild.getGuildManager().onlinePlayerGuilds.remove(e.getPlayer().getName());

        if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsValue(uuid)){
            TridentGuild.getGuildManager().unloadGuild(uuid);
        }
    }
}
