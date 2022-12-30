package team.hiaxn.hanhan.hiaxnsocial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Command.addfriend;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Command.friend;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Command.friendgui;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Command.testsocket;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Gui.FriendGui;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Gui.ButtionListener.CanNotMoveListener;
import team.hiaxn.hanhan.hiaxnsocial.Friend.PlayerEventListener.PlayerJoinListener;
import team.hiaxn.hanhan.hiaxnsocial.Friend.PlayerEventListener.PlayerQuitListener;
import team.hiaxn.hanhan.hiaxnsocial.Friend.SocketListener.Socket;
import team.hiaxn.hanhan.hiaxnsocial.Redis.RedisUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class HiaXnSocial extends JavaPlugin {
    public static String serverName = "HiaXn";
    public static String serverAddress = "www.hiaxn.cn";
    public static JedisPubSub jedisPubSub;

    public static Connection connection;

    @Override
    public void onEnable() {
        //Redis
        RedisConnect();

        saveDefaultConfig();
        connection = getNewConnection(); //赋值connection
        keepConnectMySql();

        //创建TABLE
        String createSql = "CREATE TABLE IF NOT EXISTS player_data(uuid varchar(255),name varchar(255),friends TEXT(65536),partys TEXT(65536),status varchar(255))";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(createSql);
            ps.executeUpdate();
            getLogger().info("成功的创建/找到了HiaXnSocial数据库");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        jedisPubSub.unsubscribe("friends");
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new CanNotMoveListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(),this);
        getServer().getPluginManager().registerEvents(new FriendGui(),this);
        getServer().getPluginManager().registerEvents(new Socket(),this);
    }
    public void registerCommands() {
        getCommand("friend").setExecutor(new friend());
        getCommand("f").setExecutor(new friend());
        getCommand("friendgui").setExecutor(new friendgui());
        getCommand("addfriend").setExecutor(new addfriend());
        getCommand("testsocket").setExecutor(new testsocket());
    }
    public void RedisConnect() {
        jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Bukkit.broadcastMessage("消息来自频道"+"---"+channel + ":" + message);
            }
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                getLogger().info(ChatColor.GREEN + "订阅频道" + "---" + channel + ":" + subscribedChannels);
            }
        };

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try(Jedis jedis = RedisUtil.getJedis()){
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] jedis资源获取成功");
                // 订阅频道消息
                jedis.subscribe(jedisPubSub,"friends");
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] 订阅频道: friends 成功");
                jedis.subscribe(jedisPubSub,"party");
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] 订阅频道: party 成功");
            } catch (Exception e) {
              //  getLogger().warning(ChatColor.RED + "[HiaXnSocial Redis] jedis资源获取异常，等待重试");
            }
        },0L,5L, TimeUnit.SECONDS);
    }
    class Subscribe extends JedisPubSub{
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("onMessage"+"---"+channel + ":" + message);
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            System.out.println("onSubscribe"+"---"+channel + ":" + subscribedChannels);
        }
    }
    private Connection getNewConnection() { //获取链接柄
        // 获取Config中的MySql配置信息
        String host = getConfig().getString("MySql.ip");
        int port = getConfig().getInt("MySql.port");
        String database = getConfig().getString("MySql.database");
        String user = getConfig().getString("MySql.user");
        String password = getConfig().getString("MySql.password");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            Connection connection = DriverManager.getConnection(url, user, password);
            getLogger().info(ChatColor.GREEN + "[HiaXnSocial MySql] 链接至MySql");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getLogger().warning(ChatColor.RED + "[HiaXnSocial MySql] 没有链接至MySql");
            return null;
        }
    }
    public void keepConnectMySql() {
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
        }).runTaskTimerAsynchronously(this, 60 * 20, 60 * 20);
    }

}
