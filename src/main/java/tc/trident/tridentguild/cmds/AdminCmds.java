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
import tc.trident.tridentguild.menus.UpgradesMenu;
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
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    switch (guild.getGuildMember(player.getName()).getPermission()){
                        case MEMBER:
                            if(!guild.memberPerms.get("guild.upgrade")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                        case OPERATOR:
                            if(!guild.operatorPerms.get("guild.upgrade")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                    UpgradesMenu.openMenu(player);
                }else if(args[0].equalsIgnoreCase("ayarlar")){
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                        Utils.sendError(player,"not-owner");
                        return true;
                    }
                    UpgradesMenu.openMenu(player);
                }
            }if(args.length==2){
                if(args[0].equalsIgnoreCase("oluştur")){
                    if(TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-already-guild-member");
                        return true;
                    }
                    if(!TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())){     // Özel karakter filtresi
                        TridentGuild.getGuildManager().createGuild(player.getName(), Utils.addColors(args[1]));
                        player.sendMessage(Utils.addColors(Utils.getMessage("guild-created",true)));
                        return true;
                    }else{
                        Utils.sendError(player,"guild-name-unavailable");
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("sil")){
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                        Utils.sendError(player,"not-owner");
                        return true;
                    }
                    // Tekrar onay eklenecek
                    TridentGuild.getGuildManager().removeGuild(guild);
                }else if(args[0].equalsIgnoreCase("davet")){
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                    if(!Bukkit.getServer().getOnlinePlayers().contains(targetPlayer)){  // Serverlar arası kontrol
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
                            break;
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
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if(!guild.isGuildMember(args[1])){
                        Utils.sendError(player,"player-is-not-guild-member");
                        return true;
                    }
                    switch (guild.getGuildMember(player.getName()).getPermission()){
                        case MEMBER:
                            Utils.sendError(player, "no-perm");
                            return true;
                        case OPERATOR:
                            if(!guild.operatorPerms.get("guild.invite")){
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                    guild.removeGuildMember(args[1]);
                }else if(args[0].equalsIgnoreCase("bağış")){
                    if(!TridentGuild.getGuildManager().onlinePlayerGuilds.containsKey(player.getName())){
                        Utils.sendError(player,"you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    int amount;
                    try{
                        amount = Integer.parseInt(args[1]);
                    }catch (NumberFormatException e){
                        Utils.sendError(player, "number-error");
                        return true;
                    }
                    guild.setBalance(guild.getBalance()+amount);
                    GuildMember guildMember = guild.getGuildMember(player.getName());
                    guildMember.setTotalDonate(guildMember.getTotalDonate()+amount);
                    player.sendMessage(Utils.addColors(Utils.getMessage("number-error",true)));
                }
            }
        }else{

        }

        return true;
    }
}
