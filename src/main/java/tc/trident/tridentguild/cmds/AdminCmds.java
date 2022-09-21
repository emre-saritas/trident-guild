package tc.trident.tridentguild.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tc.trident.tridentguild.Guild;
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
                    if(!TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())){     // Özel karakter filtresi
                        TridentGuild.getGuildManager().createGuild(player.getName(), Utils.addColors(args[1]));
                    }else{
                        Utils.sendError(player,"guild-name-unavailable");
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("davet")){
                    Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                    if(!Bukkit.getServer().getOnlinePlayers().contains(targetPlayer)){  // Serverlar arası yapılacak
                        Utils.sendError(player, "name-not-found");
                        return true;
                    }
                    if(TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(args[1])){
                        Utils.sendError(player, "invite-already-has-guild");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    switch (guild.getGuildMember(player.getName()).getPermission()){
                        case MEMBER:
                            if(!guild.memberPerms.get("guild.invite")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                        case OPERATOR:
                            if(!guild.operatorPerms.get("guild.invite")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                    // Davet istek vs vs
                    TridentGuild.getGuildManager().getPlayerGuild(player.getName()).addGuildMember(targetPlayer.getName());
                    player.sendMessage(Utils.addColors(Utils.getMessage("invite-sent",true)));
                }else if(args[0].equalsIgnoreCase("at")){
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                    if(!Bukkit.getServer().getOnlinePlayers().contains(targetPlayer)){  // Serverlar arası yapılacak
                        Utils.sendError(player, "name-not-found");
                        return true;
                    }
                    switch (guild.getGuildMember(player.getName()).getPermission()){
                        case MEMBER:            // memberda kick yetkisi olmayacak, perm atanırken de ayarla
                            if(!guild.memberPerms.get("guild.invite")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                            GuildMember.GuildPermission targetPerm = guild.getGuildMember(targetPlayer.getName()).getPermission();
                            if(targetPerm.getPower() >= GuildMember.GuildPermission.MEMBER.getPower()){
                                Utils.sendError(player,"member-kick-more-power");
                                return true;
                            }
                        case OPERATOR:
                            if(!guild.operatorPerms.get("guild.invite")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                }
            }
        }else{

        }

        return true;
    }
}
