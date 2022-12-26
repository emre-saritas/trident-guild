package tc.trident.tridentguild.kingdomwars;

import me.lucko.helper.messaging.ChannelAgent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.GuildChatRedisData;
import tc.trident.tridentguild.mysql.GuildRedisData;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

public class WarListeners implements Listener {
    private War war = null;

    public WarListeners(War war){
        this.war = war;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(!TridentGuild.getGuildManager().hasGuild(e.getPlayer().getName())){
            e.getPlayer().kickPlayer("Lonca üyesi değilsiniz.");
            return;
        }
        war.addPlayerToWar(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKilled(EntityDeathEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(e.getEntity().getKiller().getType() != EntityType.PLAYER) return;
        war.playerKilled(e.getEntity().getKiller(), (Player) e.getEntity());
    }


}
