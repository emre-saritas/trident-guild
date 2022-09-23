package tc.trident.tridentguild.menus;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MemberPermsMenu implements InventoryProvider {

    public MemberPermsMenu(String playerName){

    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

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
