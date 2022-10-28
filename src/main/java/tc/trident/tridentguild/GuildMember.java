package tc.trident.tridentguild;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import tc.trident.tridentguild.utils.Utils;
import tc.trident.tridentguild.utils.YamlItem;
import tc.trident.tridentjobs.TridentJobsMain;

public class GuildMember {


    private OfflinePlayer player;
    private GuildPermission permission;
    private float totalDonate = 0;
    private int points = 0;

    public GuildMember(String playerName){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = GuildPermission.MEMBER;
    }
    public GuildMember(String playerName, GuildPermission perm){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = perm;
    }
    public GuildMember(String playerName, GuildPermission perm, int totalDonate, int points){
        this.player = Bukkit.getOfflinePlayer(playerName);
        this.permission = perm;
        this.totalDonate = totalDonate;
        this.points = points;
    }
    public ItemStack getMemberShowItem(){
        YamlItem item = new YamlItem("members.member",TridentGuild.menus);
        item.setName(item.getName().replace("%name%",player.getName()).replace("%colorcode%",permission.colorCode));
        item.replaceLore("%perm%", permission.name);
        item.replaceLore("%colorcode%", permission.colorCode);
        item.replaceLore("%colorcode-2%", permission.colorCode2);
        item.replaceLore("%donated%", Utils.nf.format(totalDonate));
        ItemStack comp = item.complete();
        SkullMeta meta = (SkullMeta) comp.getItemMeta();
        meta.setOwningPlayer(player);
        comp.setItemMeta(meta);
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
    public int getPoints() {
        return points;
    }
    public void syncPoints(){
        this.points = TridentJobsMain.getPlayerManager().getMasteryLvl(player.getName());
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
        OWNER(2,"Kurucu", "&c","&4"),
        OPERATOR(1,"Operatör", "&b","&3"),
        MEMBER(0, "Üye", "&a","&2");

        private final int power;
        private final String name;
        private final String colorCode;
        private final String colorCode2;
        private GuildPermission(int power, String name, String colorCode, String colorCode2){
            this.power=power;
            this.name=name;
            this.colorCode2 = colorCode2;
            this.colorCode = colorCode;
        }

        public String getColorCode2() {
            return colorCode2;
        }

        public String getColorCode() {
            return colorCode;
        }

        public int getPower() {
            return power;
        }

        public String getName() {
            return name;
        }
    }
}
