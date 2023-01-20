package tc.trident.tridentguild.kingdomwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class WarCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("tp0")){
                    tryEnterWar(player,"0");
                }
            }
        }

        return true;
    }

    private void tryEnterWar(Player player, String spawnID){
        if(TridentGuild.getWarManager().isWarStarted()){
            if(TridentGuild.getWarManager().getWar().isGuildLimitReached(TridentGuild.getGuildManager().onlinePlayerGuilds.get(player.getName()))){
                Utils.sendError(player,"error.war-is-full");
            }else{
                TridentGuild.getWarManager().getWar().addPlayerToWar(player,spawnID);
            }
        }else{
            Utils.sendError(player,"error.not-started");
        }
    }
}
