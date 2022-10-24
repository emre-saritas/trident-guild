package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildManager;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class GeneralGuildMenu implements InventoryProvider {

    private final Guild guild;

    public GeneralGuildMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
    }

    public void init(Player player, InventoryContents contents){
        YamlItem item = new YamlItem("general-guild.0", TridentGuild.menus);
        item.replaceLore("%name%",guild.getGuildName());
        item.replaceLore("%count%",guild.guildMembers.size()+"");
        item.replaceLore("%level%",guild.getGuildLevel()+"");
        item.replaceLore("%limit%",TridentGuild.upgrades.getInt("guild.levels."+guild.getGuildLevel()+".limit")+"");
        item.replaceLore("%money%", Utils.nf.format(guild.getBalance()));
        item.replaceLore("%since%", guild.getCreateDate());

        contents.set(1,1, ClickableItem.empty(item.complete()));
        item = new YamlItem("general-guild.1", TridentGuild.menus);
        contents.set(1,3, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            MembersMenu.openMenu(player, null);
        }));
        item = new YamlItem("general-guild.2", TridentGuild.menus);
        contents.set(1,5, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            UpgradesMenu.openMenu(player);
        }));
        item = new YamlItem("general-guild.3", TridentGuild.menus);
        contents.set(1,7, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            SettingsMenu.openMenu(player);
        }));
    }



    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-menu")
                .provider(new GeneralGuildMenu(player.getName()))
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
