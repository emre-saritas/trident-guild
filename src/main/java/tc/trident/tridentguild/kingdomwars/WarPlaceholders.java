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
            getGuild(war, 0);
        }else if(params.equalsIgnoreCase("2nd")){
            getGuild(war, 1);
        }else if(params.equalsIgnoreCase("3rd")){
            getGuild(war, 2);
        }else if(params.equalsIgnoreCase("4th")){
            getGuild(war, 3);
        }else if(params.equalsIgnoreCase("5th")){
            getGuild(war, 4);
        }else if(params.equalsIgnoreCase("1st_points")){
            return getGuildPoints(war,0);
        }else if(params.equalsIgnoreCase("2nd_points")){
            return getGuildPoints(war,1);
        }else if(params.equalsIgnoreCase("3rd_points")){
            return getGuildPoints(war,2);
        }else if(params.equalsIgnoreCase("4th_points")){
            return getGuildPoints(war,3);
        }else if(params.equalsIgnoreCase("5th_points")){
            return getGuildPoints(war,4);
        }

        return null;
    }

    public static String getGuild(War war, int i){
        UUID guildUUID = war.getGuildByIndex(i, true);
        if(guildUUID != null){
            return TridentGuild.getGuildManager().loadedGuilds.get(guildUUID).getGuildName();
        }else{
            return "---";
        }
    }
    public static String getGuildPoints(War war, int i){
        UUID guildUUID = war.getGuildByIndex(i, true);
        if(guildUUID != null){
            return war.guildPoints.get(guildUUID)+"";
        }else{
            return "---";
        }
    }
}
