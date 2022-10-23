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

    private final Player player;

    public UpgradesMenu(Player player){
        this.player=player;
    }

    public void init(Player player, InventoryContents contents){

        Guild guild = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
        YamlItem item = new YamlItem("guild",TridentGuild.upgrades);
        int guildLevel =guild.getGuildLevel();
        item.replaceLore("%current-level%",guildLevel+"");
        item.replaceLore("%current-limit%",TridentGuild.upgrades.getInt("guild.levels."+guildLevel+".limit")+"");
        item.replaceLore("%price%",Utils.nf.format(TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price"))+"$");
        item.replaceLore("%next-limit%",TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".limit")+"");
        contents.set(0,4,ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());

            if (!TridentGuild.getGuildManager().hasGuild(player.getName())) {
                Utils.sendError(player, "you-not-guild-member");
                return;
            }
            if(guildClick.getBalance() < TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price")){
                Utils.sendError(player,"upgrade-balance-error");
                player.closeInventory();
                return;
            }
            if(guildClick.getGuildMember(player.getName()).getPermission() == GuildMember.GuildPermission.OPERATOR){
                if(!guildClick.operatorPerms.get("guild.levelup")){
                    Utils.sendError(player,"no-perm");
                    player.closeInventory();
                    return;
                }
            }
            guildClick.setGuildLevel(guildLevel+1);
            guildClick.setBalance(guildClick.getBalance()-TridentGuild.upgrades.getInt("guild.levels."+(guildLevel+1)+".price"));
            TridentGuild.getSyncManager().syncGuild(guildClick,SyncType.UPDATE);
            TridentGuild.getGuildManager().syncToSqlGuild(guildClick, SyncType.UPDATE);
            player.sendMessage(Utils.addColors(Utils.getMessage("guild-levelup",true).replace("%level%",(guildLevel+1)+"" )));
            player.closeInventory();
        }));
        item = new YamlItem("miner",TridentGuild.upgrades);
        item.replaceLore("%price%", TridentGuild.upgrades.getInt("miner.levels."+(guildLevel+1)+".price")+"");
        item.replaceLore("%level%", TridentGuild.upgrades.getInt("miner.levels."+(guildLevel+1)+".guild-level")+"");
        item.setName(item.getName().replace("%level%",guild.getMinerLevel()+""));
        contents.set(1,1, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            clickEvent(inventoryClickEvent,"miner",guildClick);
        }));
        item = new YamlItem("lumber",TridentGuild.upgrades);
        item.replaceLore("%price%", TridentGuild.upgrades.getInt("lumber.levels."+(guildLevel+1)+".price")+"");
        item.replaceLore("%level%", TridentGuild.upgrades.getInt("lumber.levels."+(guildLevel+1)+".guild-level")+"");
        item.setName(item.getName().replace("%level%",guild.getLumberLevel()+""));
        contents.set(1,3, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            clickEvent(inventoryClickEvent,"lumber",guildClick);
        }));
        item = new YamlItem("hunter",TridentGuild.upgrades);
        item.replaceLore("%price%", TridentGuild.upgrades.getInt("hunter.levels."+(guildLevel+1)+".price")+"");
        item.replaceLore("%level%", TridentGuild.upgrades.getInt("hunter.levels."+(guildLevel+1)+".guild-level")+"");
        item.setName(item.getName().replace("%level%",guild.getHunterLevel()+""));
        contents.set(1,5, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            clickEvent(inventoryClickEvent,"hunter",guildClick);

        }));
        item = new YamlItem("farmer",TridentGuild.upgrades);
        item.replaceLore("%price%", TridentGuild.upgrades.getInt("farmer.levels."+(guildLevel+1)+".price")+"");
        item.replaceLore("%level%", TridentGuild.upgrades.getInt("farmer.levels."+(guildLevel+1)+".guild-level")+"");
        item.setName(item.getName().replace("%level%",guild.getFarmerLevel()+""));
        contents.set(1,7, ClickableItem.of(item.complete(),inventoryClickEvent -> {
            Guild guildClick = TridentGuild.getGuildManager().getPlayerGuild(player.getName());
            clickEvent(inventoryClickEvent,"farmer",guildClick);
        }));
    }

    public void clickEvent(InventoryClickEvent e, String upgradeID, Guild guild){
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
        player.closeInventory();
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
