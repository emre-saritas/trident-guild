package tc.trident.tridentguild.kingdomwars;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tc.trident.sync.player.events.TridentEntityDamageEvent;
import tc.trident.tridentguild.TridentGuild;

public class WarListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        TridentGuild.getWarManager().getWar().bossBar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLastDamage(EntityDamageByEntityEvent e){
        if(TridentGuild.getWarManager().getWar().getState() == War.WarState.PLAYING){

            if(e.getEntity().getType() != EntityType.PLAYER) return;
            if(TridentGuild.getWarManager().getWar() == null) return;
            if(e.getDamage() < ((LivingEntity) e.getEntity()).getHealth()) return;
            e.setCancelled(true);

            War war = TridentGuild.getWarManager().getWar();

            if(e.getDamager().getType() != EntityType.PLAYER) {
                TridentGuild.getWarManager().getWar().removePlayer((Player) e.getEntity());
            }else
                war.kill((Player) e.getDamager(), (Player) e.getEntity());
        }
    }
}
