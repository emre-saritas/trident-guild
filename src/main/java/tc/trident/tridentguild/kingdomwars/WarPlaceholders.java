package tc.trident.tridentguild.kingdomwars;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.trident.tridentguild.TridentGuild;

import java.util.UUID;

public class WarPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "war";
    }

    @Override
    public @NotNull String getAuthor() {
        return "trident";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) return null;
        if(!TridentGuild.getWarManager().isWarStarted())
            return "---";
        War war = TridentGuild.getWarManager().getWar();

        if(params.equalsIgnoreCase("1st")){
            UUID guildUUID = war.getGuildByIndex(0);
            if(guildUUID != null){
                return TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).getGuildName();
            }else{
                return "---";
            }
        }else if(params.equalsIgnoreCase("2nd")){
            UUID guildUUID = war.getGuildByIndex(1);
            if(guildUUID != null){
                return TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).getGuildName();
            }else{
                return "---";
            }
        }else if(params.equalsIgnoreCase("3rd")){
            UUID guildUUID = war.getGuildByIndex(2);
            if(guildUUID != null){
                return TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).getGuildName();
            }else{
                return "---";
            }
        }else if(params.equalsIgnoreCase("1st_points")){
            UUID guildUUID = war.getGuildByIndex(0);
            if(guildUUID != null){
                return war.guildPoints.get(guildUUID)+"";
            }else{
                return "---";
            }
        }else if(params.equalsIgnoreCase("2nd_points")){
            UUID guildUUID = war.getGuildByIndex(0);
            if(guildUUID != null){
                return war.guildPoints.get(guildUUID)+"";
            }else{
                return "---";
            }
        }else if(params.equalsIgnoreCase("3rd_points")){
            UUID guildUUID = war.getGuildByIndex(0);
            if(guildUUID != null){
                return war.guildPoints.get(guildUUID)+"";
            }else{
                return "---";
            }
        }

        return null;
    }
}
