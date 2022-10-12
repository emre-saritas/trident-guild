package tc.trident.tridentguild.cmds;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class GuildChatMessage implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        if(TridentGuild.getGuildManager().hasGuild(player.getName())){
            Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            if(args.length < 1){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&2SurvivalTC &8» &cLütfen bir mesaj giriniz."));
                return false;
            }
            String text = StringUtils.join(args," ");
            String message = ChatColor.translateAlternateColorCodes('&',"&f[&dLonca&f] &e"+player.getName()+": &f");
            Guild.GuildChatMessage gchat = new Guild.GuildChatMessage(message+text);
            TridentSync.getInstance().getRedis().getChannel("s"+guild.getGuildUUID()+"-chat", Guild.GuildChatMessage.class).sendMessage(gchat);
        }else{
            Utils.sendError(player,"you-not-guild-member");
            return true;
        }

        return true;
    }
}
