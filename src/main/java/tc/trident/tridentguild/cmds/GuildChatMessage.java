package tc.trident.tridentguild.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class GuildChatMessage implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        if(TridentGuild.getGuildManager().hasGuild(player)){
            // Redis command sending
        }else{
            Utils.sendError(player,"you-not-guild-member");
            return true;
        }

        return true;
    }
}
