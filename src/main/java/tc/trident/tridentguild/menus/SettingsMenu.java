package tc.trident.tridentguild.menus;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.TridentGuild;

public class SettingsMenu implements InventoryProvider {

    private final Guild guild;

    public SettingsMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
    }

    public void init(Player player, InventoryContents contents){

    }



    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-settings")
                .provider(new SettingsMenu(player.getName()))
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca Ayarları")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
