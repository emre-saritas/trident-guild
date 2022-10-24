package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.cmds.GuildCmds;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class SettingsMenu implements InventoryProvider {


    public SettingsMenu(){

    }

    public void init(Player player, InventoryContents contents){

        Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());

        YamlItem item = new YamlItem("settings.0",TridentGuild.menus);
        ItemMeta yamlMeta = item.complete().getItemMeta();
        ItemStack banner = guild.getGuildBanner().clone();
        ItemMeta meta = banner.getItemMeta();
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS });
        meta.setLore(yamlMeta.getLore());
        meta.setDisplayName(yamlMeta.getDisplayName());
        banner.setItemMeta(meta);

        contents.set(1,1, ClickableItem.of(banner,inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            ItemStack newBanner = player.getInventory().getItemInMainHand();
            if(newBanner.getType().toString().contains("BANNER") && !newBanner.getType().toString().contains("BANNER_PATTERN")){
                if(guild.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.MEMBER){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
                if(!hasPermission("guild.bannerchange",guild.getGuildMember(player.getName()).getPermission(),guild)){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
                if(guildClick.getBalance()<TridentGuild.config.getInt("banner-change-price")){
                    Utils.sendError(player,"banner-error-money");
                    player.closeInventory();
                    return;
                }
                BannerMeta newMeta = (BannerMeta) newBanner.getItemMeta().clone();
                guildClick.setBalance(guildClick.getBalance()-TridentGuild.config.getInt("banner-change-price"));
                guildClick.setBannerPatterns(newMeta);
                guildClick.setBannerMaterial(newBanner.getType());
                TridentGuild.getSyncManager().syncGuild(guildClick,SyncType.UPDATE);
                TridentGuild.getGuildManager().syncToSqlGuild(guildClick, SyncType.UPDATE);
                player.sendMessage(Utils.addColors(Utils.getMessage("banner-set",true)));
                player.closeInventory();
            }else{
                Utils.sendError(player,"banner-error");
                player.closeInventory();
            }
        }));
        item = new YamlItem("settings.1",TridentGuild.menus);
        contents.set(1,3,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(guildClick.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            OperatorPermsMenu.openMenu(player);
        }));
        item = new YamlItem("settings.2",TridentGuild.menus);
        contents.set(1,5,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(guildClick.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            MemberPermsMenu.openMenu(player);
        }));
        item = new YamlItem("settings.3",TridentGuild.menus);
        contents.set(1,7,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(guildClick.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            GuildCmds.deleteGuild(player,guildClick);
            player.closeInventory();
        }));
        if(guild.isPvp()){
            item = new YamlItem("settings.4.acik",TridentGuild.menus);
        }else{
            item = new YamlItem("settings.4.kapali",TridentGuild.menus);
        }
        contents.set(2,4,ClickableItem.of(item.complete(), inventoryClickEvent -> {
            if(guild.getGuildMember(player.getName()).getPermission() != GuildMember.GuildPermission.OWNER){
                Utils.sendError(player,"not-owner");
                player.closeInventory();
                return;
            }
            if(guild.isPvp()){
                guild.setPvp(false);
                player.sendMessage(Utils.addColors(Utils.getMessage("pvp-change",true).replace("%state%","&cKapalı")));
            }else{
                guild.setPvp(true);
                player.sendMessage(Utils.addColors(Utils.getMessage("pvp-change",true).replace("%state%","&aAçık")));
            }
            SettingsMenu.openMenu(player);
        }));
    }


    public boolean hasPermission(String tag, GuildMember.GuildPermission permission, Guild guild){
        if(permission == GuildMember.GuildPermission.OWNER) return true;
        if(permission == GuildMember.GuildPermission.MEMBER) return false;
        if(!guild.operatorPerms.get(tag)){
            return false;
        }else{
            return true;
        }
    }
    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-settings")
                .provider(new SettingsMenu())
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca Ayarları")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
