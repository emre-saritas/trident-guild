//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tc.trident.tridentguild.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public void updateGuild(String uuid, String name, int level, int minerLevel, int lumberLevel, int hunterLevel, int farmerLevel, float balance, String bannerMeta, String memberPerms, String opPerms){
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("REPLACE INTO survival_Guild SET `uuid` = ? , `name` = ? , `level` = ? , `minerLevel` = ? , `lumberLevel` = ? , `hunterLevel` = ?, farmerLevel = ?, balance = ?, bannerMeta = ?, memberPerms = ?,opPerms = ?");
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
                st.setString(9, bannerMeta);
                st.setString(10, memberPerms);
                st.setString(11, opPerms);
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
    public void updateGuildMember(String playerName, String guildUUID, String permission, float totalDonate){
        Connection conn = this.sql.getConnection();

        try {
            PreparedStatement st = conn.prepareStatement("REPLACE INTO survival_GuildMember SET `playerName` = ? , `guildUUID` = ? , `permission` = ? , `totalDonate` = ?");
            Throwable var12 = null;

            try {
                st.setString(1, playerName);
                st.setString(2, guildUUID);
                st.setString(3, permission);
                st.setFloat(4, totalDonate);
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
                if(result.next()){
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
                        null,
                        result.getInt("level"),
                        result.getInt("balance"),
                        result.getInt("minerLevel"),
                        result.getInt("lumberLevel"),
                        result.getInt("hunterLevel"),
                        result.getInt("farmerLevel"),
                        members,
                        Guild.deserializeMemberPerms("memberPerms"),
                        Guild.deserializeMemberPerms("opPerms"));
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
            st.executeQuery();

            ResultSet result = st.executeQuery();
            if(result.next()){
                members.add(new GuildMember(result.getString("playerName"),
                        GuildMember.GuildPermission.valueOf(result.getString("permission")),
                        result.getInt("totalDonate")));
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        return members;
    }
}
