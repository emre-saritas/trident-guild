package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;

public class GuildMember {


    private OfflinePlayer player;
    private GuildPermission permission;
    private float totalDonate = 0;

    public GuildMember(String playerName){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = GuildPermission.MEMBER;
    }
    public GuildMember(String playerName, GuildPermission perm){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = perm;
    }
    public GuildMember(String playerName, GuildPermission perm, int totalDonate){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = perm;
        this.totalDonate = totalDonate;
    }



    public ItemStack getMemberShowItem(){
        YamlItem item = new YamlItem("members.member",TridentGuild.menus);
        item.setName(item.getName().replace("%name%",player.getName()));
        item.replaceLore("%perm%", permission.name);
        item.replaceLore("%donated%", Utils.nf.format(totalDonate));
        return item.complete();
    }
    public void makeOwner(){
        this.permission=GuildPermission.OWNER;
    }
    public GuildPermission getPermission() {
        return permission;
    }

    public void setPermission(GuildPermission permission) {
        this.permission = permission;
    }

    public void makeOperator(){
        this.permission=GuildPermission.OPERATOR;
    }
    public void makeMember(){
        this.permission=GuildPermission.MEMBER;
    }

    public float getTotalDonate() {
        return totalDonate;
    }

    public void setTotalDonate(float totalDonate) {
        this.totalDonate = totalDonate;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
    public enum GuildPermission{
        OWNER(2,"Kurucu"),
        OPERATOR(1,"Operatör"),
        MEMBER(0, "Üye");

        private final int power;
        private final String name;
        private GuildPermission(int power, String name){
            this.power=power;
            this.name=name;
        }

        public int getPower() {
            return power;
        }

        public String getName() {
            return name;
        }
    }
}
