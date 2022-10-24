package tc.trident.tridentguild;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.mysql.SyncType;
import tc.trident.tridentguild.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class Guild {
    public HashMap<String, GuildMember> guildMembers = new HashMap<>();
    public HashMap<String, Boolean> memberPerms = new HashMap<>();
    public HashMap<String, Boolean> operatorPerms = new HashMap<>();
    private final String guildName;
    private final UUID guildUUID;
    public transient SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final String createDate;
    private int minerLevel = 0;
    private int lumberLevel = 0;
    private HashMap<PatternType, DyeColor> patterns;
    private Material bannerMaterial = Material.WHITE_BANNER;
    private int hunterLevel = 0;
    private int farmerLevel = 0;
    private boolean pvp = false;
    private int guildLevel;
    private float balance;

    public Guild(UUID guildUUID, String guildName){
        this.guildUUID=guildUUID;
        this.guildName=guildName;
        this.guildLevel = 0;
        this.balance = 0;
        this.patterns = new HashMap<>();
        this.createDate = dateFormat.format(new Date());
        this.memberPerms.put("guild.invite",false);
        this.operatorPerms.put("guild.invite",true);
        this.operatorPerms.put("guild.kick",false);
        this.operatorPerms.put("guild.bannerchange",false);
        this.operatorPerms.put("guild.levelup",false);
        this.operatorPerms.put("guild.upgrade",false);
    }

    public Guild(UUID guildUUID, String guildName, HashMap<PatternType, DyeColor> bannerPatterns, Material bannerMaterial,int guildLevel, float balance, int minerLevel, int lumberLevel, int hunterLevel, int farmerLevel, List<GuildMember> guildMembers, HashMap<String, Boolean> memberPerms, HashMap<String, Boolean> operatorPerms, String createDate, boolean pvp) {
        this.guildUUID=guildUUID;
        this.guildName=guildName;
        this.guildLevel = guildLevel;
        this.minerLevel = minerLevel;
        this.lumberLevel = lumberLevel;
        this.hunterLevel = hunterLevel;
        this.farmerLevel = farmerLevel;
        this.balance = balance;
        this.bannerMaterial = bannerMaterial;
        this.patterns = new HashMap<>(bannerPatterns);
        this.createDate = createDate;
        this.pvp = pvp;
        setGuildMembers(guildMembers);
        setGuildPermissions(memberPerms,operatorPerms);
    }



    public static String serializeOpPerms(HashMap<String, Boolean> perms){
        return "" + perms.get("guild.invite") + ";" +
                perms.get("guild.kick") + ";" +
                perms.get("guild.bannerchange") + ";" +
                perms.get("guild.levelup") + ";" +
                perms.get("guild.upgrade");
    }
    public static String serializeMemberPerms(HashMap<String, Boolean> perms){
        return "" + perms.get("guild.invite");
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
    public String serializePatterns(){
        if(patterns.size() == 0 ) return null;
        StringBuilder sb = new StringBuilder();
        patterns.forEach((patternType, dyeColor) -> {
            sb.append(patternType+":"+dyeColor+";");
        });
        return sb.toString();
    }
    public static HashMap<PatternType, DyeColor> deserializePatterns(String patternStr){
        HashMap<PatternType, DyeColor> map = new HashMap<>();
        if(patternStr == null) return map;
        String[] patterns = patternStr.split(";");
        for(String pattern : patterns){
            map.put(PatternType.valueOf(pattern.split(":")[0]),DyeColor.valueOf(pattern.split(":")[1]));
        }
        return map;
    }

    public void setBannerPatterns(BannerMeta bannerMeta){
        this.patterns = new HashMap<>();
        bannerMeta.getPatterns().forEach(pattern -> {
            this.patterns.put(pattern.getPattern(),pattern.getColor());
        });
    }
    public ItemStack getGuildBanner(){
        ItemStack banner = new ItemStack(bannerMaterial);
        if(patterns.size() == 0) return banner;
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        patterns.forEach((patternType, dyeColor) -> {
            meta.addPattern(new Pattern(dyeColor,patternType));
        });
        banner.setItemMeta(meta);
        return banner;
    }
    public String getCreateDate() {
        return createDate;
    }
    public boolean isGuildFull(){
        return TridentGuild.upgrades.getInt("guild.levels."+guildLevel+".limit") <= guildMembers.size();
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
    public Material getBannerMaterial() {
        return bannerMaterial;
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
    public boolean isPvp() {
        return pvp;
    }

    public void setBannerMaterial(Material bannerMaterial) {
        this.bannerMaterial = bannerMaterial;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
        TridentGuild.getSyncManager().syncGuild(this,SyncType.UPDATE);
        TridentGuild.getGuildManager().syncToSqlGuild(this, SyncType.UPDATE);
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

    public void setBalance(float balance) {
        this.balance = balance;
    }
    public float getBalance() {
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
    public void addGuildMember(String playerName, GuildMember.GuildPermission permission){
        if(TridentGuild.getGuildManager().hasGuild(playerName)) return;
        TridentGuild.getGuildManager().onlinePlayerGuilds.put(playerName, guildUUID);
        GuildMember guildMember = new GuildMember(playerName, permission);
        guildMembers.put(playerName,guildMember);
        TridentGuild.getSyncManager().syncGuild(this,SyncType.UPDATE);
        TridentGuild.getGuildManager().syncToSqlGuildMember(guildMember, getGuildUUID(), SyncType.UPDATE);
    }
    public void removeGuildMember(String playerName){
        TridentGuild.getGuildManager().onlinePlayerGuilds.remove(playerName);
        TridentGuild.getGuildManager().syncToSqlGuildMember(guildMembers.get(playerName), getGuildUUID(), SyncType.REMOVE_PLAYER);
        guildMembers.remove(playerName);
        TridentGuild.getSyncManager().syncGuild(this,SyncType.REMOVE_PLAYER, playerName);
    }
}
