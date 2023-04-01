package team.hiaxn.hanhan.hiaxnsocial;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import team.hiaxn.hanhan.hiaxnsocial.friend.command.addfriend;
import team.hiaxn.hanhan.hiaxnsocial.friend.command.friend;
import team.hiaxn.hanhan.hiaxnsocial.friend.command.friendgui;
import team.hiaxn.hanhan.hiaxnsocial.friend.command.testsocket;
import team.hiaxn.hanhan.hiaxnsocial.friend.gui.buttionlistener.AddFriendListener;
import team.hiaxn.hanhan.hiaxnsocial.friend.gui.buttionlistener.removeFriendListener;
import team.hiaxn.hanhan.hiaxnsocial.friend.gui.FriendGui;
import team.hiaxn.hanhan.hiaxnsocial.friend.gui.buttionlistener.CanNotMoveListener;
import team.hiaxn.hanhan.hiaxnsocial.friend.listener.PlayerJoinListener;
import team.hiaxn.hanhan.hiaxnsocial.friend.listener.PlayerQuitListener;
import team.hiaxn.hanhan.hiaxnsocial.mySql.MySqlManager;
import team.hiaxn.hanhan.hiaxnsocial.redis.RedisUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @Author HanHan
 * @Date 2022.12
 *       　  　▃▆█▇▄▖
 * 　  　   ▟◤▖　　　◥█▎
 *      ◢◤　 ▐　　　 　▐▉
 * 　  ▗◤　　　▂　▗▖　　▕█▎
 *  　◤　▗▅▖◥▄　▀◣　　█▊
 * ▐　▕▎◥▖◣◤　　　　◢██
 * █◣　◥▅█▀　　　　▐██◤
 * ▐█▙▂　　     　◢██◤
 * ◥██◣　　　　◢▄◤
 *  　　▀██▅▇▀
 */
public final class HiaXnSocial extends JavaPlugin {
    public static RegisteredServiceProvider<LuckPerms> provider;
    public static LuckPerms lpapi;
    public static String serverName = "HiaXn";
    public static String serverAddress = "www.hiaxn.cn";
    public static JedisPubSub jedisPubSub;
    public static Connection connection;
    public static HiaXnSocial instance;
    public static HiaXnSocial getInstace() {
        return instance;
    }
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) {
                    lpapi = provider.getProvider();

                }
            }
        }.runTaskLater(this,20L*3);
        //数据库操作开始
        RedisConnect();
        connection = MySqlManager.getNewConnection(); //赋值connection
        MySqlManager.createTable("player_data","uuid varchar(255),name varchar(255),friends TEXT(65536),partys TEXT(65536),status varchar(255)");
        MySqlManager.keepConnect();
        //数据库操作结束

        //配置文件管理
        saveDefaultConfig();
        //Bukkit
        instance = this;
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
        getServer().getPluginManager().registerEvents(new AddFriendListener(),this);
        getServer().getPluginManager().registerEvents(new removeFriendListener(),this);
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
               // Bukkit.broadcastMessage("消息来自频道"+"---"+channel + ": " + message);
            }
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                getLogger().info(ChatColor.GREEN + "订阅频道" + "---" + channel + ": " + subscribedChannels);
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
                //getLogger().warning(ChatColor.RED + "[HiaXnSocial Redis] jedis资源获取异常，等待重试");
            }
        },0L,5L, TimeUnit.SECONDS);
    }
    //内部类 已弃用
    /*class Subscribe extends JedisPubSub{
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("onMessage"+"---"+channel + ":" + message);
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            System.out.println("onSubscribe"+"---"+channel + ":" + subscribedChannels);
        }
    }
     */

}
