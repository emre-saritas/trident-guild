package tc.trident.tridentguild.kingdomwars;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import tc.trident.sync.player.events.TridentEntityDamageEvent;
import tc.trident.sync.player.events.TridentPlayerDeathEvent;
import tc.trident.tridentguild.TridentGuild;

public class WarListeners implements Listener {

    @EventHandler
    public void onPlayerLastDamage(TridentEntityDamageEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(e.getAttacker().getType() != EntityType.PLAYER) return;
        if(TridentGuild.getWarManager().getWar() == null) return;
        if(e.getDamage() < ((LivingEntity) e.getEntity()).getHealth()) return;
        e.setCancelled(true);

        War war = TridentGuild.getWarManager().getWar();

        war.kill((Player) e.getAttacker(), (Player) e.getEntity());

    }

}
