package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class MembersMenu implements InventoryProvider {

    private final Guild guild;

    public MembersMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
    }

    public void init(Player player, InventoryContents contents){

    }



    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-menu")
                .provider(new MembersMenu(player.getName()))
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca Üyeleri")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
