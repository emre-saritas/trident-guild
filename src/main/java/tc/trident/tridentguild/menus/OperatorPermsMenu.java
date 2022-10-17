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
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class OperatorPermsMenu implements InventoryProvider {
    private final Guild guild;

    private String active = "&aAktif";
    private String deactive = "&cDeaktif";
    private String playerName;


    public OperatorPermsMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
        this.playerName=playerName;
    }


    public void init(Player player, InventoryContents contents) {
        contents.set(0,0, ClickableItem.of(getItem("invite"),inventoryClickEvent -> {
            onClick(player,"invite");
        }));
        contents.set(0,1, ClickableItem.of(getItem("kick"),inventoryClickEvent -> {
            onClick(player,"kick");
        }));
        contents.set(0,2, ClickableItem.of(getItem("bannerchange"),inventoryClickEvent -> {
            onClick(player,"bannerchange");
        }));
        contents.set(0,3, ClickableItem.of(getItem("levelup"),inventoryClickEvent -> {
            onClick(player,"levelup");
        }));
        contents.set(0,4, ClickableItem.of(getItem("upgrade"),inventoryClickEvent -> {
            onClick(player,"upgrade");
        }));
    }


    public void update(Player player, InventoryContents contents) {

    }

    public void onClick(Player player,String id){
        if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
            Utils.sendError(player, "you-not-guild-member");
            return;
        }
        if(guild.getGuildMember(playerName).getPermission() == GuildMember.GuildPermission.OWNER){
            if(guild.operatorPerms.get("guild."+id)){
                guild.operatorPerms.replace("guild."+id,false);
            }else{
                guild.operatorPerms.replace("guild."+id,true);
            }
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
        OperatorPermsMenu.openMenu(player);
    }
    public ItemStack getItem(String id){
        YamlItem item = new YamlItem("operator-perms."+id, TridentGuild.menus);
        if(guild.operatorPerms.get("guild."+id)){
            item.replaceLore("%state%",active);
        }else{
            item.replaceLore("%state%",deactive);
        }
        return item.complete();
    }

    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-op-perms")
                .provider(new OperatorPermsMenu(player.getName()))
                .size(3, 9)
                .title(ChatColor.BLACK + "Operatör Yetkileri")
                .build();
        INVENTORY.open(player.getPlayer()); //    Opens the menu
    }
}
