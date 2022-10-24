package tc.trident.tridentguild.listeners;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildListeners implements Listener {

    List<String> cancel = new ArrayList<>();



    @EventHandler
    public void onProjPlayerDamage(ProjectileHitEvent e){
        if(e.getHitEntity() instanceof Player){
            Player hitP = (Player) e.getHitEntity();
            if(!TridentGuild.getGuildManager().hasGuild(hitP.getName())) return;
            if(e.getEntity().getShooter() instanceof Player){
                Player shooterP = (Player) e.getEntity().getShooter();
                if(!TridentGuild.getGuildManager().hasGuild(shooterP.getName())) return;
                if(TridentGuild.getGuildManager().getPlayerGuild(hitP.getName()).getGuildUUID().equals(TridentGuild.getGuildManager().getPlayerGuild(shooterP.getName()).getGuildUUID())){
                    if(TridentGuild.getGuildManager().getPlayerGuild(hitP.getName()).isPvp()) return;
                    if(!cancel.contains(hitP.getName())){
                        cancel.add(hitP.getName());
                        e.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(!TridentGuild.getGuildManager().hasGuild(e.getEntity().getName())) return;
        if(e.getDamager().getType() == EntityType.ARROW || e.getDamager().getType() == EntityType.SPECTRAL_ARROW || e.getDamager().getType() == EntityType.SNOWBALL){
            if(cancel.contains(e.getEntity().getName())){
                cancel.remove(e.getEntity().getName());
                e.setCancelled(true);
            }
        }else if(e.getDamager().getType() != EntityType.PLAYER) return;
        if(!TridentGuild.getGuildManager().hasGuild(e.getDamager().getName())) return;
        Guild guild = TridentGuild.getGuildManager().getPlayerGuild(e.getDamager().getName());
        if(TridentGuild.getGuildManager().getPlayerGuild(e.getEntity().getName()).getGuildUUID().equals(guild.getGuildUUID()) && !guild.isPvp()){
            e.getDamager().sendMessage(Utils.addColors(Utils.getMessage("cant-hit-guild-member",true)));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent e){
        if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) return;
        net.minecraft.server.v1_16_R3.ItemStack itemNMS = CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand());
        if(!itemNMS.hasTag()) return;
        NBTTagCompound itemTags = itemNMS.getTag();
        if(itemTags.hasKey("guild-scroll")) e.setCancelled(true);
    }
}
