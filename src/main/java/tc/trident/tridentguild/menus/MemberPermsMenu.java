package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class MemberPermsMenu implements InventoryProvider {

    private final Guild guild;

    private String active = "&aAktif";
    private String deactive = "&cDeaktif";
    private String playerName;


    public MemberPermsMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
        this.playerName=playerName;
    }


    public void init(Player player, InventoryContents contents) {
        contents.set(0,0, ClickableItem.of(getItem("invite"), inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            onClick(player,"invite");
        }));
    }


    public void update(Player player, InventoryContents contents) {

    }

    public void onClick(Player player,String id){
        if(guild.getGuildMember(playerName).getPermission() == GuildMember.GuildPermission.OWNER){
            if(guild.memberPerms.get("guild."+id)){
                guild.memberPerms.replace("guild."+id,false);
            }else{
                guild.memberPerms.replace("guild."+id,true);
            }
        }
        TridentGuild.getSyncManager().syncGuild(guild, SyncType.UPDATE);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
        OperatorPermsMenu.openMenu(player);
    }
    public ItemStack getItem(String id){
        YamlItem item = new YamlItem("member-perms."+id, TridentGuild.menus);
        if(guild.memberPerms.get("guild."+id)){
            item.replaceLore("%state%",active);
        }else{
            item.replaceLore("%state%",deactive);
        }
        return item.complete();
    }
    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-member-perms")
                .provider(new OperatorPermsMenu(player.getName()))
                .size(3, 9)
                .title(ChatColor.BLACK + "Üye Yetkileri")
                .build();
        INVENTORY.open(player.getPlayer()); //    Opens the menu
    }
}
