package tc.trident.tridentguild.mysql;

import org.bukkit.Bukkit;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.TridentGuild;

public class MySQLManager {
    public static MySQL mySQL;
    public static MySQLHandler mysqlHandler;
    private final TridentGuild plugin;

    public MySQLManager(TridentGuild plugin) {
        this.plugin = plugin;
        this.loadMysql();
        this.startRefresh();
    }

    private void loadMysql() {
        try {
            Bukkit.getLogger().info("Veritabanı bağlantısı başlatılıyor.");
            mySQL = new MySQL(this.plugin);
            mysqlHandler = new MySQLHandler(mySQL, this.plugin);
            this.startRefresh();
            Bukkit.getLogger().info("Veritabanı bağlantısı başarıyla başlatıldı.");
        } catch (Exception var2) {
            Bukkit.getLogger().warning("Veritabanına bağlanırken sorun oluştu: " + var2.toString());
        }

    }

    public void startRefresh() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            public void run() {
                try {
                    tc.trident.sync.mysql.MYSQLManager.mySQL.refreshConnect();
                } catch (Exception var2) {
                    Bukkit.getLogger().warning("Failed to reload MySQL: " + var2.toString());
                }

            }
        }, 200L, 36000L);
    }
}
