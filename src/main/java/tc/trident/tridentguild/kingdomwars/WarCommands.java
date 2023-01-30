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
                if(player.hasPermission("skyblocktc.admin")){
                    if(args[0].equalsIgnoreCase("start")){
                        if(TridentGuild.getWarManager().getWar().getState() != War.WarState.WAITING) return true;
                        TridentGuild.getWarManager().getWar().changeState(War.WarState.PLAYING);
                    }else if(args[0].equalsIgnoreCase("finish")){
                        if(TridentGuild.getWarManager().getWar().getState() != War.WarState.PLAYING) return true;
                        TridentGuild.getWarManager().getWar().changeState(War.WarState.FINISH);
                    }
                }

                if(args[0].equalsIgnoreCase("tp0")){
                    tryEnterWar(player,"0");
                }else if(args[0].equalsIgnoreCase("tp1")){
                    tryEnterWar(player,"1");
                }else if(args[0].equalsIgnoreCase("tp2")){
                    tryEnterWar(player,"2");
                }else if(args[0].equalsIgnoreCase("tp3")){
                    tryEnterWar(player,"3");
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
