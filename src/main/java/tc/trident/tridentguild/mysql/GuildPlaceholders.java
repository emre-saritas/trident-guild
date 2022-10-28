package tc.trident.tridentguild.mysql;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class GuildPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "guild";
    }

    @Override
    public @NotNull String getAuthor() {
        return "trident";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {

        if(player == null ) return null;
        if(identifier.equalsIgnoreCase("name")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildName();
        }if(identifier.equalsIgnoreCase("name_colored")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getColoredGuildName();
        }else if(identifier.equalsIgnoreCase("level")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildLevel()+"";
        }else if(identifier.equalsIgnoreCase("member_count")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return TridentGuild.getGuildManager().getPlayerGuild(player.getName()).guildMembers.size()+"";
        }else if(identifier.equalsIgnoreCase("permission")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(player.getName()).getPermission().getName()+"";
        }else if(identifier.equalsIgnoreCase("balance")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return Utils.nf.format(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getBalance());
        }else if(identifier.equalsIgnoreCase("points")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return "---";
            }
            return Utils.nf.format(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getTotalGuildPoints());
        }else if(identifier.equalsIgnoreCase("points_flat")){
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                return 0+"";
            }
            if(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                return 0+"";
            }
            return Utils.nf.format(TridentGuild.getGuildManager().getPlayerGuild(player.getName()).getTotalGuildPoints());
        }
        return null;
    }
}