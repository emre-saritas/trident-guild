//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tc.trident.tridentguild.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

public class MySQLHandler {
    private final TridentGuild plugin;
    private MySQL sql;

    public MySQLHandler(MySQL mysql, TridentGuild plugin) {
        this.sql = mysql;
        this.plugin = plugin;
    }

    public void updateGuild(String uuid, String name, int level, int minerLevel, int lumberLevel, int hunterLevel, int farmerLevel, float balance, String bannerPatterns, String memberPerms, String opPerms, String bannerMaterial, String createDate, boolean pvp){
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("REPLACE INTO survival_Guild SET `uuid` = ? , `name` = ? , `level` = ? , `minerLevel` = ? , `lumberLevel` = ? , `hunterLevel` = ?, farmerLevel = ?, balance = ?, bannerPatterns = ?, memberPerms = ?,opPerms = ?, bannerMaterial = ?, createDate = ?, pvp = ?");
            Throwable var12 = null;
            try {
                st.setString(1, uuid);
                st.setString(2, name);
                st.setInt(3, level);
                st.setInt(4, minerLevel);
                st.setInt(5, lumberLevel);
                st.setInt(6, hunterLevel);
                st.setInt(7, farmerLevel);
                st.setFloat(8, balance);
                st.setString(9, bannerPatterns);
                st.setString(10, memberPerms);
                st.setString(11, opPerms);
                st.setString(12, bannerMaterial);
                st.setString(13, createDate);
                st.setBoolean(14, pvp);
                st.executeUpdate();
                Utils.debug("[TridentGuild] MySQL updated "+uuid);
            } catch (Throwable var22) {
                var12 = var22;
                throw var22;
            } finally {
                if (st != null) {
                    if (var12 != null) {
                        try {
                            st.close();
                        } catch (Throwable var21) {
                            var12.addSuppressed(var21);
                        }
                    } else {
                        st.close();
                    }
                }

            }
        } catch (SQLException var24) {
            var24.printStackTrace();
        }
    }
    public void updateGuildMember(String playerName, String guildUUID, String permission, float totalDonate, int points){
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("REPLACE INTO survival_GuildMember SET `playerName` = ? , `guildUUID` = ? , `permission` = ? , `totalDonate` = ?, `points` = ?");
            Throwable var12 = null;

            try {
                st.setString(1, playerName);
                st.setString(2, guildUUID);
                st.setString(3, permission);
                st.setFloat(4, totalDonate);
                st.setInt(5, points);
                st.executeUpdate();
                Utils.debug("[TridentGuild] MySQL updated member "+playerName);
            } catch (Throwable var22) {
                var12 = var22;
                throw var22;
            } finally {
                if (st != null) {
                    if (var12 != null) {
                        try {
                            st.close();
                        } catch (Throwable var21) {
                            var12.addSuppressed(var21);
                        }
                    } else {
                        st.close();
                    }
                }

            }
        } catch (SQLException var24) {
            var24.printStackTrace();
        }
    }

    public void deleteGuild(String uuid) {
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("DELETE FROM survival_Guild WHERE uuid=?");
            Throwable var4 = null;

            try {
                st.setString(1, uuid);
                st.executeUpdate();
                Utils.debug("[TridentGuild] MySQL removed "+uuid);
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (st != null) {
                    if (var4 != null) {
                        try {
                            st.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        st.close();
                    }
                }

            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        try {
            PreparedStatement st = conn.prepareStatement("SELECT playerName FROM survival_GuildMember WHERE guildUUID=?");
            Throwable var4 = null;

            try {
                st.setString(1, uuid);

                ResultSet result = st.executeQuery();
                while(result.next()){
                    deleteGuildMember(result.getString("playerName"));
                }
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (st != null) {
                    if (var4 != null) {
                        try {
                            st.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        st.close();
                    }
                }

            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }
    }
    public void deleteGuildMember(String playerName) {
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("DELETE FROM survival_GuildMember WHERE playerName=?");
            Throwable var4 = null;

            try {
                st.setString(1, playerName);
                st.executeUpdate();
                Utils.debug("[TridentGuild] MySQL removed member "+playerName);
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (st != null) {
                    if (var4 != null) {
                        try {
                            st.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        st.close();
                    }
                }

            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

    }
    public List<String> getGuildNames(){
        Connection conn = this.sql.getConnection();
        List<String> names = new ArrayList<>();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT name FROM survival_Guild");
            st.executeQuery();

            ResultSet result = st.executeQuery();
            while (result.next()){
                String str = result.getString("name").toLowerCase();
                names.add(str);
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return null;
        }
        return names;
    }
    public boolean hasGuild(String playerName){
        UUID uuid = TridentGuild.getSqlHandler().getGuildUUID(playerName);
        return uuid != null;
    }

    public Guild getGuild(UUID uuid){

        Connection conn = this.sql.getConnection();
        Guild guild = null;
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM survival_Guild WHERE uuid=?");
            st.setString(1, uuid.toString());
            st.executeQuery();

            ResultSet result = st.executeQuery();
            if(result.next()){
                List<GuildMember> members = new ArrayList<>(getGuildMembers(uuid.toString()));
                // member create
                guild = new Guild(UUID.fromString(uuid.toString()),result.getString("name"),
                        Guild.deserializePatterns(result.getString("bannerPatterns")),
                        Material.valueOf(result.getString("bannerMaterial")),
                        result.getInt("level"),
                        result.getInt("balance"),
                        result.getInt("minerLevel"),
                        result.getInt("lumberLevel"),
                        result.getInt("hunterLevel"),
                        result.getInt("farmerLevel"),
                        members,
                        Guild.deserializeMemberPerms(result.getString("memberPerms")),
                        Guild.deserializeOpPerms(result.getString("opPerms")),
                        result.getString("createDate"),
                        result.getBoolean("pvp"));
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return null;
        }

        return guild;
    }
    public UUID getGuildUUID(String playerName){
        Connection conn = this.sql.getConnection();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT guildUUID FROM survival_GuildMember WHERE playerName=?");
            st.setString(1, playerName);
            st.executeQuery();

            ResultSet result = st.executeQuery();
            if(result.next()){
                return UUID.fromString(result.getString("guildUUID"));
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        return null;
    }
    public List<GuildMember> getGuildMembers(String guildUUID){
        List<GuildMember> members = new ArrayList<>();
        Connection conn = this.sql.getConnection();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM survival_GuildMember WHERE guildUUID=?");
            st.setString(1, guildUUID);
            ResultSet result = st.executeQuery();
            while(result.next()){
                members.add(new GuildMember(result.getString("playerName"),
                        GuildMember.GuildPermission.valueOf(result.getString("permission")),
                        result.getInt("totalDonate"),
                        result.getInt("points")));
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        return members;
    }
    public ItemStack getGuildBanner(String playerName){
        ItemStack meta = null;
        Connection conn = this.sql.getConnection();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM survival_Guild WHERE uuid=?");
            st.setString(1, getGuildUUID(playerName).toString());
            ResultSet result = st.executeQuery();
            if(result.next()){
                meta = Guild.getGuildBanner(Material.valueOf(result.getString("bannerMaterial")),result.getString("bannerPatterns"));
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        return meta;
    }
}
