package team.hiaxn.hanhan.hiaxnsocial.mySql;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @Author HanHan
 * @Date 2022.12.31
 */
import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class MySqlManager {
    public MySqlManager() {}
    /**
     * @param tableName 表名
     * @param params 表的参数 示例写法：uuid varchar(255),name varchar(255),friends TEXT(65536),partys TEXT(65536),status varchar(255)
     */
    public static void createTable(String tableName,String params) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + params + ")";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(createSql);
            ps.executeUpdate();
            HiaXnSocial.getPlugin(HiaXnSocial.class).getLogger().info("成功的创建/找到了HiaXnSocial数据库");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getNewConnection() { //获取链接柄
        // 获取Config中的MySql配置信息
        String host = HiaXnSocial.getPlugin(HiaXnSocial.class).getConfig().getString("MySql.ip");
        int port = HiaXnSocial.getPlugin(HiaXnSocial.class).getConfig().getInt("MySql.port");
        String database = HiaXnSocial.getPlugin(HiaXnSocial.class).getConfig().getString("MySql.database");
        String user = HiaXnSocial.getPlugin(HiaXnSocial.class).getConfig().getString("MySql.user");
        String password = HiaXnSocial.getPlugin(HiaXnSocial.class).getConfig().getString("MySql.password");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            Connection connection = DriverManager.getConnection(url, user, password);
            HiaXnSocial.getPlugin(HiaXnSocial.class).getLogger().info(ChatColor.GREEN + "[HiaXnSocial MySql] 链接至MySql");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            HiaXnSocial.getPlugin(HiaXnSocial.class).getLogger().warning(ChatColor.RED + "[HiaXnSocial MySql] 没有链接至MySql");
            return null;
        }
    }
    public static void keepConnect() {
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    connection = getNewConnection();
                }
            }
        }).runTaskTimerAsynchronously(HiaXnSocial.getPlugin(HiaXnSocial.class), 60 * 20, 60 * 20);
    }

}
