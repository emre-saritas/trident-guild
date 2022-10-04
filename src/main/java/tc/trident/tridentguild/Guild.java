package tc.trident.tridentguild;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Guild {
    public HashMap<String, GuildMember> guildMembers = new HashMap<>();
    public List<GuildMember> memberList = new ArrayList<>();
    public HashMap<String, Boolean> memberPerms = new HashMap<>();
    public HashMap<String, Boolean> operatorPerms = new HashMap<>();
    private final String guildName;
    private final UUID guildUUID;
    private int minerLevel = 0;
    private int lumberLevel = 0;
    private int hunterLevel = 0;
    private int farmerLevel = 0;
    private int guildLevel;
    private double balance;

    public Guild(UUID guildUUID, String guildName){
        this.guildUUID=guildUUID;
        this.guildName=guildName;
        ItemStack bannerItem = new ItemStack(Material.WHITE_BANNER);
        //this.bannerMeta = (BannerMeta) bannerItem.getItemMeta();
        this.guildLevel = 0;
        this.balance = 0;
        this.memberPerms.put("guild.invite",false);
        this.operatorPerms.put("guild.invite",true);
        this.operatorPerms.put("guild.kick",false);
        this.operatorPerms.put("guild.bannerchange",false);
        this.operatorPerms.put("guild.levelup",false);
        this.operatorPerms.put("guild.upgrade",false);
    }

    public Guild(UUID guildUUID, String guildName, BannerMeta bannerMeta, int guildLevel, double balance, int minerLevel, int lumberLevel, int hunterLevel, int farmerLevel, List<GuildMember> guildMembers, HashMap<String, Boolean> memberPerms, HashMap<String, Boolean> operatorPerms) {
        this.guildUUID=guildUUID;
        this.guildName=guildName;
        //this.bannerMeta = bannerMeta;
        this.guildLevel = guildLevel;
        this.minerLevel = minerLevel;
        this.lumberLevel = lumberLevel;
        this.hunterLevel = hunterLevel;
        this.farmerLevel = farmerLevel;
        this.balance = balance;
        setGuildMembers(guildMembers);
        setGuildPermissions(memberPerms,operatorPerms);
        memberList.addAll(this.guildMembers.values());
    }



    public static String serializePerms(HashMap<String, Boolean> perms){
        String ser = "";
        StringBuilder sb = new StringBuilder(ser);
        perms.forEach((permID, bool) -> {
            sb.append(bool).append(";");
        });
        return sb.toString();
    }
    public static HashMap<String, Boolean> deserializeOpPerms(String str){
        HashMap<String, Boolean> map = new HashMap<>();
        String[] bools = str.split(";");
        map.put("guild.invite",Boolean.valueOf(bools[0]));
        map.put("guild.kick",Boolean.valueOf(bools[1]));
        map.put("guild.bannerchange",Boolean.valueOf(bools[2]));
        map.put("guild.levelup",Boolean.valueOf(bools[3]));
        map.put("guild.upgrade",Boolean.valueOf(bools[4]));
        return map;
    }
    public static HashMap<String, Boolean> deserializeMemberPerms(String str){
        HashMap<String, Boolean> map = new HashMap<>();
        String[] bools = str.split(";");
        map.put("guild.invite",Boolean.valueOf(bools[0]));
        return map;
    }
    public boolean isGuildMember(String playerName){
        return guildMembers.containsKey(playerName);
    }
    public int getMemberLimit(){
        return TridentGuild.config.getInt("guild.levels."+guildLevel+".member-limit");
    }
    public void makeOwner(String playerName){
        guildMembers.get(playerName).makeOwner();
    }
    public void makeOperator(String playerName){
        guildMembers.get(playerName).makeOperator();
    }
    public GuildMember getGuildMember(String playerName){
        return guildMembers.get(playerName);
    }
    public UUID getGuildUUID() {
        return guildUUID;
    }
    public String getGuildName() {
        return guildName;
    }
    public void makeMember(String playerName){
        guildMembers.get(playerName).makeMember();
    }
    public void setGuildLevel(int guildLevel) {
        this.guildLevel = guildLevel;
    }
    public int getGuildLevel() {
        return guildLevel;
    }

    public void setFarmerLevel(int farmerLevel) {
        this.farmerLevel = farmerLevel;
    }

    public void setHunterLevel(int hunterLevel) {
        this.hunterLevel = hunterLevel;
    }

    public void setLumberLevel(int lumberLevel) {
        this.lumberLevel = lumberLevel;
    }

    public void setMinerLevel(int minerLevel) {
        this.minerLevel = minerLevel;
    }

    public int getFarmerLevel() {
        return farmerLevel;
    }

    public int getHunterLevel() {
        return hunterLevel;
    }

    public int getLumberLevel() {
        return lumberLevel;
    }

    public int getMinerLevel() {
        return minerLevel;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public double getBalance() {
        return balance;
    }
    public void setGuildMembers(List<GuildMember> guildMembers) {
        guildMembers.forEach(guildMember -> {
            this.guildMembers.put(guildMember.getPlayer().getName(),guildMember);
        });
    }
    public void changeMemberPermission(String permKey, boolean state){
        memberPerms.put(permKey,state);
    }
    public void changeOperatorPermission(String permKey, boolean state){
        operatorPerms.put(permKey,state);
    }
    public void setGuildPermissions(HashMap<String, Boolean> memberPerms, HashMap<String, Boolean> operatorPerms) {
        this.memberPerms = new HashMap<>(memberPerms);
        this.operatorPerms = new HashMap<>(operatorPerms);
    }
    public HashMap<String, Boolean> getMemberPerms() {
        return memberPerms;
    }
    public HashMap<String, Boolean> getOperatorPerms() {
        return operatorPerms;
    }
    public void addGuildMember(String playerName){
        GuildMember guildMember = new GuildMember(playerName);
        guildMembers.put(playerName,guildMember);
        memberList.add(guildMember);
        TridentGuild.getGuildManager().syncGuild(this);
    }
    public void removeGuildMember(String playerName){
        guildMembers.remove(playerName);
        TridentGuild.getGuildManager().syncGuild(this);
    }
}
