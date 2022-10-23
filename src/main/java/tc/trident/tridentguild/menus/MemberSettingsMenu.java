package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class MemberSettingsMenu implements InventoryProvider {

    private final Guild guild;
    private final GuildMember member;
    public MemberSettingsMenu(String playerName,String targetPlayerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
        this.member = guild.getGuildMember(targetPlayerName);
    }

    public void init(Player player, InventoryContents contents){
        YamlItem bilgi = new YamlItem("member-settings.0",TridentGuild.menus);
        bilgi.replaceLore("%perm%",member.getPermission().getName());
        bilgi.replaceLore("%donated%",Utils.nf.format(member.getTotalDonate()));
        contents.set(1,2,ClickableItem.empty(bilgi.complete()));
        contents.set(1,4,ClickableItem.of(new YamlItem("member-settings.1",TridentGuild.menus).complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(!TridentGuild.getGuildManager().hasGuild(member.getPlayer().getName())){
                Utils.sendError(player,"cant-do");
                player.closeInventory();
                return;
            }
           if(inventoryClickEvent.isLeftClick()){
               if(member.getPermission()!= GuildMember.GuildPermission.MEMBER){
                   Utils.sendError(player,"cant-do");
                   player.closeInventory();
                   return;
               }
               member.setPermission(GuildMember.GuildPermission.OPERATOR);
               TridentGuild.getGuildManager().syncToSqlGuildMember(member,guild.getGuildUUID(), SyncType.UPDATE);
               TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
               TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
               player.sendMessage(Utils.addColors(Utils.getMessage("member-rank-up",true).replace("%player%", member.getPlayer().getName())));
               player.closeInventory();
           } else if (inventoryClickEvent.isRightClick()) {
               if(member.getPermission()!= GuildMember.GuildPermission.OPERATOR){
                   Utils.sendError(player,"cant-do");
                   player.closeInventory();
                   return;
               }
               member.setPermission(GuildMember.GuildPermission.MEMBER);
               TridentGuild.getGuildManager().syncToSqlGuildMember(member,guild.getGuildUUID(), SyncType.UPDATE);
               TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
               TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
               player.sendMessage(Utils.addColors(Utils.getMessage("member-rank-down",true).replace("%player%", member.getPlayer().getName())));
               player.closeInventory();
           }
        }));
        contents.set(1,6,ClickableItem.of(new YamlItem("member-settings.2",TridentGuild.menus).complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(!TridentGuild.getGuildManager().hasGuild(member.getPlayer().getName())){
                Utils.sendError(player,"cant-do");
                player.closeInventory();
                return;
            }
            if(member.getPermission() == GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"no-perm");
                player.closeInventory();
                return;
            }
            if(guild.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.OPERATOR){
                if(!guild.operatorPerms.get("guild.kick")){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
                if(member.getPermission() != GuildMember.GuildPermission.MEMBER){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
            }
            guild.removeGuildMember(member.getPlayer().getName());
            player.sendMessage(Utils.addColors(Utils.getMessage("member-kicked",true)));
            player.closeInventory();
        }));
    }



    public static void openMenu(Player player, String targetPlayerName){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-member")
                .provider(new MemberSettingsMenu(player.getName(),targetPlayerName))
                .size(3, 9)
                .title(ChatColor.BLACK + targetPlayerName)
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
