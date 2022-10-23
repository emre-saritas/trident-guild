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
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class MembersMenu implements InventoryProvider {

    private int col=0;
    private int row=0;
    private int page=0;

    public MembersMenu(){
    }

    public void init(Player player, InventoryContents contents){
        Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
        for(int i = page*45; i<guild.guildMembers.size(); i++){
            GuildMember guildMember = guild.memberList.get(i);
            contents.set(row,col,ClickableItem.of(guildMember.getMemberShowItem(),inventoryClickEvent -> {
                Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
                if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                    Utils.sendError(player, "you-not-guild-member");
                    return;
                }
                if(guildMember.getPlayer().getName().equalsIgnoreCase(player.getName())){
                    Utils.sendError(player,"cant-do");
                    player.closeInventory();
                    return;
                }
                if(guildClick.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.MEMBER){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                }
                MemberSettingsMenu.openMenu(player,guildMember.getPlayer().getName());
            }));
            col++;
            if(col%9 == 0){
                col = 0;
                row++;
            }
        }
    }



    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-members")
                .provider(new MembersMenu())
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca Üyeleri")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
