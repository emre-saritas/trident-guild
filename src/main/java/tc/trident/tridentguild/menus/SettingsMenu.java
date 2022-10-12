package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class SettingsMenu implements InventoryProvider {

    private final Guild guild;

    public SettingsMenu(String playerName){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(playerName);
    }

    public void init(Player player, InventoryContents contents){
        YamlItem item = new YamlItem("settings.0",TridentGuild.menus);
        ItemStack banner = item.complete();
        //banner.setItemMeta(guild.getBannerMeta());
        ItemMeta meta = banner.getItemMeta();
        meta.setLore(item.getLore());
        meta.setDisplayName(item.getName());
        banner.setItemMeta(meta);
        contents.set(1,1, ClickableItem.of(banner,inventoryClickEvent -> {
            ItemStack newBanner = player.getInventory().getItemInMainHand();
            if(newBanner.getType().toString().contains("BANNER") && !newBanner.getType().toString().contains("BANNER_PATTERN")){
                BannerMeta newMeta = (BannerMeta) newBanner.getItemMeta();
                //guild.setBannerMeta(newMeta);
                TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
                TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
                player.sendMessage(Utils.addColors(Utils.getMessage("banner-set",true)));
                player.closeInventory();
            }else{
                Utils.sendError(player,"banner-error");
                player.closeInventory();
            }
        }));
        item = new YamlItem("settings.1",TridentGuild.menus);
        contents.set(1,3,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            OperatorPermsMenu.openMenu(player);
        }));
        item = new YamlItem("settings.2",TridentGuild.menus);
        contents.set(1,3,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            MemberPermsMenu.openMenu(player);
        }));
        item = new YamlItem("settings.3",TridentGuild.menus);
        contents.set(1,3,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            TridentGuild.getGuildManager().removeGuild(guild.getGuildUUID());
            TridentGuild.getSyncManager().syncGuild(guild,SyncType.REMOVE_GUILD);
            TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.REMOVE_GUILD);
        }));
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
