package tc.trident.tridentguild.mysql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tc.trident.tridentguild.TridentGuild;

public class MySQL {
    private TridentGuild plugin;
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;
    private Connection conn;

    public MySQL(TridentGuild plugin) throws Exception {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "mysql.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String db = "database.";
        cfg.addDefault(db + "host", "localhost");
        cfg.addDefault(db + "port", 3306);
        cfg.addDefault(db + "user", "root");
        cfg.addDefault(db + "password", "");
        cfg.addDefault(db + "database", "mineweb");
        cfg.options().copyDefaults(true);

        try {
            cfg.save(file);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        this.host = cfg.getString(db + "host");
        this.port = cfg.getInt(db + "port");
        this.user = cfg.getString(db + "user");
        this.password = cfg.getString(db + "password");
        this.database = cfg.getString(db + "database");
        this.conn = this.openConnection();
    }

    public Connection openConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&characterEncoding=utf-8", this.user, this.password);
        return conn;
    }

    public void refreshConnect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&characterEncoding=utf-8", this.user, this.password);
    }

    public Connection getConnection() {
        try {
            if (!this.conn.isValid(1)) {
                System.out.println("[SkyNoticer] Lost MySQL-Connection! Reconnecting...");

                try {
                    this.conn = this.openConnection();
                } catch (Exception var17) {
                    var17.printStackTrace();
                }
            }
        } catch (SQLException var18) {
            var18.printStackTrace();
        }

        try {
            PreparedStatement stmt = this.conn.prepareStatement("SELECT 1");
            Throwable var2 = null;

            try {
                stmt.executeQuery();
            } catch (Throwable var16) {
                var2 = var16;
                throw var16;
            } finally {
                if (stmt != null) {
                    if (var2 != null) {
                        try {
                            stmt.close();
                        } catch (Throwable var15) {
                            var2.addSuppressed(var15);
                        }
                    } else {
                        stmt.close();
                    }
                }

            }
        } catch (SQLException var20) {
            System.out.println("[SkyNoticer] SELECT 1 - failled. Reconnecting...");

            try {
                this.conn = this.openConnection();
            } catch (Exception var14) {
                var14.printStackTrace();
            }
        }

        return this.conn;
    }

    public boolean hasConnecion() {
        try {
            return this.conn != null || this.conn.isValid(1);
        } catch (SQLException var2) {
            return false;
        }
    }

    public void queryUpdate(String query) {
        Connection connection = this.conn;

        try {
            PreparedStatement st = connection.prepareStatement(query);
            Throwable var4 = null;

            try {
                st.executeUpdate();
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

    public void closeRessources(ResultSet rs, PreparedStatement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException var5) {
                var5.printStackTrace();
            }
        }

        if (st != null) {
            try {
                st.close();
            } catch (SQLException var4) {
                var4.printStackTrace();
            }
        }

    }

    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException var5) {
            var5.printStackTrace();
        } finally {
            this.conn = null;
        }

    }
}
