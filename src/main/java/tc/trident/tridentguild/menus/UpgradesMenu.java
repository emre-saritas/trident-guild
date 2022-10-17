package tc.trident.tridentguild.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class UpgradesMenu implements InventoryProvider {

    private final Guild guild;
    private final Player player;

    public UpgradesMenu(Player player){
        this.guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
        this.player=player;
    }

    public void init(Player player, InventoryContents contents){
        YamlItem item = new YamlItem("guild",TridentGuild.upgrades);
        int guildLevel =guild.getGuildLevel();
        item.replaceLore("%current-level%",guildLevel+"");
        item.replaceLore("%current-limit%",TridentGuild.upgrades.getInt("guild.levels."+guildLevel+".limit")+"");
        item.replaceLore("%price%",Utils.nf.format(TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price"))+"$");
        item.replaceLore("%next-limit%",TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".limit")+"");
        contents.set(0,4,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(guild.getBalance() < TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price")){
                Utils.sendError(player,"upgrade-balance-error");
                player.closeInventory();
                return;
            }
            if(guild.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.OPERATOR){
                if(!guild.operatorPerms.get("guild.levelup")){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
            }
            guild.setGuildLevel(guildLevel+1);
            guild.setBalance(guild.getBalance()-TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price"));
            TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
            TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
            player.sendMessage(Utils.addColors(Utils.getMessage("guild-levelup",true).replace("%level%",(guildLevel+1)+"" )));
        }));
        item = new YamlItem("miner",TridentGuild.upgrades);
        item.setName(item.getName().replace("%level%",guild.getMinerLevel()+""));
        contents.set(1,1, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            clickEvent(inventoryClickEvent,"miner");
        }));
        item = new YamlItem("lumber",TridentGuild.upgrades);
        item.setName(item.getName().replace("%level%",guild.getLumberLevel()+""));
        contents.set(1,3, ClickableItem.of(item.complete(),inventoryClickEvent -> {

            clickEvent(inventoryClickEvent,"lumber");
        }));
        item = new YamlItem("hunter",TridentGuild.upgrades);
        item.setName(item.getName().replace("%level%",guild.getHunterLevel()+""));
        contents.set(1,5, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            clickEvent(inventoryClickEvent,"hunter");

        }));
        item = new YamlItem("farmer",TridentGuild.upgrades);
        item.setName(item.getName().replace("%level%",guild.getFarmerLevel()+""));
        contents.set(1,7, ClickableItem.of(item.complete(),inventoryClickEvent -> {

            clickEvent(inventoryClickEvent,"farmer");
        }));
    }

    public void clickEvent(InventoryClickEvent e, String upgradeID){
        if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
            Utils.sendError(player, "you-not-guild-member");
            return;
        }
        int upgradeLvl = 0;
        switch (upgradeID){
            case "miner":
                upgradeLvl=guild.getMinerLevel();
            case "lumber":
                upgradeLvl=guild.getLumberLevel();
            case "hunter":
                upgradeLvl=guild.getHunterLevel();
            default:
                upgradeLvl=guild.getFarmerLevel();
        }
        if(guild.getGuildLevel() < TridentGuild.upgrades.getInt(upgradeID+".levels."+(upgradeLvl+1)+".guild-level")){
            Utils.sendError(player, "upgrade-glevel-error");
            player.closeInventory();
            return;
        }
        if(guild.getBalance() < TridentGuild.upgrades.getInt(upgradeID+".levels."+(upgradeLvl+1)+".price")){
            Utils.sendError(player,"upgrade-balance-error");
            player.closeInventory();
            return;
        }
        guild.setBalance(guild.getBalance()-TridentGuild.upgrades.getInt(upgradeID+".levels."+(upgradeLvl+1)+".price"));
        switch (upgradeID){
            case "miner":
                guild.setMinerLevel(guild.getMinerLevel()+1);
            case "lumber":
                guild.setLumberLevel(guild.getLumberLevel()+1);
            case "hunter":
                guild.setHunterLevel(guild.getHunterLevel()+1);
            default:
                guild.setFarmerLevel(guild.getFarmerLevel()+1);
        }
        TridentGuild.getSyncManager().syncGuild(guild,SyncType.UPDATE);
        TridentGuild.getGuildManager().syncToSqlGuild(guild, SyncType.UPDATE);
        player.sendMessage(Utils.addColors(Utils.getMessage("upgrade-done",true)));
    }

    public static void openMenu(Player player){
        SmartInventory INVENTORY = SmartInventory.builder() //  Builds the menu
                .id("guild-upgrades")
                .provider(new UpgradesMenu(player))
                .size(3, 9)
                .title(ChatColor.BLACK + "Lonca Geliştirmeleri")
                .build();
        INVENTORY.open(player); //    Opens the menu
    }

    public void update(Player player, InventoryContents inventoryContents) {

    }
}
