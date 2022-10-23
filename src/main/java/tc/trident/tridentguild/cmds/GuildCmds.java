package tc.trident.tridentguild.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.invite.Invite;
import tc.trident.tridentguild.invite.InviteRedisData;
import tc.trident.tridentguild.menus.GeneralGuildMenu;
import tc.trident.tridentguild.menus.UpgradesMenu;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

import java.nio.Buffer;

public class GuildCmds implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (!player.hasPermission("survivaltc.beta")){
                Utils.sendError(player,"soon");
                return true;
            }

            if (args.length == 0) {
                if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                    Utils.sendError(player, "you-not-guild-member");
                    return true;
                }
                GeneralGuildMenu.openMenu(player);
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("upgrade")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    switch (guild.getGuildMember(player.getName()).getPermission()) {
                        case MEMBER:
                            if (!guild.memberPerms.get("guild.upgrade")) {
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                        case OPERATOR:
                            if (!guild.operatorPerms.get("guild.upgrade")) {
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                    UpgradesMenu.openMenu(player);
                } else if (args[0].equalsIgnoreCase("kabul")) {
                    if (TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-already-guild-member");
                        return true;
                    }
                    if(!TridentGuild.getInviteHandler().playerInvites.containsKey(player.getName())){
                        Utils.sendError(player,"invite-none");
                        return true;
                    }
                    Invite invite = TridentGuild.getInviteHandler().getInvite(player.getName());
                    Guild guild = TridentGuild.getSqlHandler().getGuild(invite.getGuildUUID());
                    if(guild.isGuildFull()){
                        Utils.sendError(player,"guild-full");
                        return true;
                    }
                    if(!TridentGuild.getGuildManager().loadedGuilds.containsKey(invite.getGuildUUID())){
                        TridentGuild.getGuildManager().loadGuild(invite.getGuildUUID());
                    }
                    TridentGuild.getGuildManager().loadedGuilds.get(invite.getGuildUUID()).addGuildMember(player.getName(), GuildMember.GuildPermission.MEMBER);
                    TridentGuild.getInviteHandler().playerInvites.remove(player.getName());
                    TridentGuild.getInviteHandler().sendMessage(player.getName(), invite.getGuildUUID().toString(), InviteRedisData.InviteDataType.JOINED_GUILD);

                }else if (args[0].equalsIgnoreCase("red")) {
                    if(TridentGuild.getInviteHandler().playerInvites.containsKey(player.getName())){
                        Utils.sendError(player,"invite-none");
                        return true;
                    }
                    TridentGuild.getInviteHandler().playerInvites.remove(player.getName());
                    player.sendMessage(Utils.addColors(Utils.getMessage("invite-rejected",true)));
                }else if (args[0].equalsIgnoreCase("ayarlar")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if (guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER) {
                        Utils.sendError(player, "not-owner");
                        return true;
                    }
                    UpgradesMenu.openMenu(player);
                } else if (args[0].equalsIgnoreCase("sil")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if (guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER) {
                        Utils.sendError(player, "not-owner");
                        return true;
                    }

                    deleteGuild(player,guild);


                }else if (args[0].equalsIgnoreCase("çık")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if (guild.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.OWNER) {
                        Utils.sendError(player, "cant-quit-from-own");
                        return true;
                    }
                    player.sendMessage(Utils.addColors(Utils.getMessage("quit",true)));
                    guild.removeGuildMember(player.getName());
                } else if (args[0].equalsIgnoreCase("yardım")) {
                    Utils.sendHelpMessages(player);
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("oluştur")) {
                    if (TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-already-guild-member");
                        return true;
                    }
                    if (!TridentGuild.getGuildManager().guildNames.contains(args[1].toLowerCase())) {     // Özel karakter filtresi
                        TridentGuild.getGuildManager().createGuild(player.getName(), Utils.addColors(args[1]));
                        player.sendMessage(Utils.addColors(Utils.getMessage("guild-created", true)));
                        return true;
                    } else {
                        Utils.sendError(player, "guild-name-already-error");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("davet")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }

                    if (!TridentSync.getInstance().getPlayerListManager().getOnlinePlayers().contains(args[1])) {  // Serverlar arası kontrol
                        Utils.sendError(player, "name-not-found");
                        return true;
                    }
                    if (TridentGuild.getGuildManager().hasGuild(args[1])) {
                        Utils.sendError(player, "invite-already-has-guild");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());

                    if(guild.isGuildFull()){
                        Utils.sendError(player,"guild-full");
                        return true;
                    }
                    switch (guild.getGuildMember(player.getName()).getPermission()) {
                        case MEMBER:
                            if (!guild.memberPerms.get("guild.invite")) {
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                            break;
                        case OPERATOR:
                            if (!guild.operatorPerms.get("guild.invite")) {
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }

                    TridentGuild.getInviteHandler().sendInvite(player.getName(), args[1]);
                    player.sendMessage(Utils.addColors(Utils.getMessage("invite-sent", true).replace("%player%", args[1])));
                } else if (args[0].equalsIgnoreCase("at")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    if (!guild.isGuildMember(args[1])) {
                        Utils.sendError(player, "player-is-not-guild-member");
                        return true;
                    }
                    switch (guild.getGuildMember(player.getName()).getPermission()) {
                        case MEMBER:
                            Utils.sendError(player, "no-perm");
                            return true;
                        case OPERATOR:
                            if (!guild.operatorPerms.get("guild.invite")) {
                                Utils.sendError(player, "no-perm");
                                return true;
                            }
                    }
                    guild.removeGuildMember(args[1]);
                } else if (args[0].equalsIgnoreCase("bağış")) {
                    if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                        Utils.sendError(player, "you-not-guild-member");
                        return true;
                    }
                    Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                    int amount;
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        Utils.sendError(player, "number-error");
                        return true;
                    }
                    TridentGuild.getEcon().withdrawPlayer(player, amount);
                    guild.setBalance(guild.getBalance() + (float) amount);
                    GuildMember guildMember = guild.getGuildMember(player.getName());
                    guildMember.setTotalDonate(guildMember.getTotalDonate() + (float) amount);
                    TridentGuild.getGuildManager().syncToSqlGuildMember(guildMember, guild.getGuildUUID(), SyncType.UPDATE);
                    TridentGuild.getSyncManager().syncGuild(guild, SyncType.UPDATE);
                    TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
                    player.sendMessage(Utils.addColors(Utils.getMessage("donated", true).replace("%money%", Utils.nf.format(amount))));
                }
            }
        } else {

        }
        return true;
    }
    public static void deleteGuild(Player player, Guild guild){
        // Silmek için onaylama
        if(!TridentGuild.getInviteHandler().isGuildDeleteCooldownExpired(player.getName())){
            TridentGuild.getInviteHandler().removeGuildDeleteCooldown(player.getName());
            TridentGuild.getGuildManager().removeGuild(guild.getGuildUUID());
            TridentGuild.getSyncManager().syncGuild(guild,SyncType.REMOVE_GUILD);
            TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.REMOVE_GUILD);
        }else{
            TridentGuild.getInviteHandler().addGuildDeleteCooldown(player.getName());
            player.sendMessage(Utils.addColors(Utils.getMessage("guild-remove-again",true)));
        }
    }
}
