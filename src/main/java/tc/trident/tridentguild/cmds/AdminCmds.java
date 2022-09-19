package tc.trident.tridentguild.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class AdminCmds implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();
            if(!player.hasPermission("survivaltc.admin")) return true;
            if(args.length==0){
                Utils.sendHelpMessages(player);
            }if(args.length==1){
                if(args[0].equalsIgnoreCase("upgrade")){

                }
            }if(args.length==2){
                if(args[0].equalsIgnoreCase("oluştur")){
                    if(TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())){
                        TridentGuild.getGuildManager().createGuild(player.getName(), Utils.addColors(args[1]));
                    }else{
                        player.sendMessage(Utils.addColors(Utils.getMessage("guild-name-unavailable",true)));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("davet")){
                    if(TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())){  // Değişecek
                        // Davet ile ilgili kodlar eklenecek
                        TridentGuild.getGuildManager().getPlayerGuild(player.getName()).addGuildMember(args[1]);
                    }else{
                        player.sendMessage(Utils.addColors(Utils.getMessage("name-not-found",true)));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("at")){
                    if(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.MEMBER){
                        if(!TridentGuild.getGuildManager().getPlayerGuild(player.getName()).memberPerms.get("guild.kick")) {
                            player.sendMessage(Utils.addColors(Utils.getMessage("no-perm",true)));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                            return true;
                        }
                        if(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(args[1]).getPermission() == GuildMember.GuildPermission.OPERATOR || TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(args[1]).getPermission() == GuildMember.GuildPermission.OWNER){
                            player.sendMessage(Utils.addColors(Utils.getMessage("no-perm",true)));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                            return true;
                        }
                    }else if(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.OPERATOR){
                        if(!TridentGuild.getGuildManager().getPlayerGuild(player.getName()).operatorPerms.get("guild.kick")) {
                            player.sendMessage(Utils.addColors(Utils.getMessage("no-perm",true)));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                            return true;
                        }
                        if(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(args[1]).getPermission() == GuildMember.GuildPermission.OWNER){
                            player.sendMessage(Utils.addColors(Utils.getMessage("no-perm",true)));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                            return true;
                        }
                    }
                    if(TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())){  // Değişecek
                        // Davet ile ilgili kodlar eklenecek
                        TridentGuild.getGuildManager().getPlayerGuild(player.getName()).removeGuildMember(args[1]);
                    }else{
                        player.sendMessage(Utils.addColors(Utils.getMessage("name-not-found",true)));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                        return true;
                    }
                }
            }
        }else{

        }

        return true;
    }
}
