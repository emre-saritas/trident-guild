package tc.trident.tridentguild.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

import java.util.UUID;

public class AdminCmds implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (!player.hasPermission("survivaltc.admin")) return true;

            if(args.length == 2){
                if(args[0].equalsIgnoreCase("remove")){
                    if(!TridentGuild.getGuildManager().hasGuild(args[1])){
                        Utils.sendError(player,"player-is-not-guild-member");
                        return true;
                    }
                    UUID uuid = TridentGuild.getGuildManager().onlinePlayerGuilds.get(args[1]);
                    Guild guild = TridentGuild.getGuildManager().loadedGuilds.get(uuid);
                    TridentGuild.getGuildManager().removeGuild(uuid);
                    TridentGuild.getSyncManager().syncGuild(guild,SyncType.REMOVE_GUILD);
                    TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.REMOVE_GUILD);
                    player.sendMessage(Utils.addColors(Utils.getMessage("guild-removed",true)));
                }

            }
        }


        return true;
    }
}
