package team.hiaxn.hanhan.hiaxnsocial.Redis;

import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;

public final class RedisUtil {
    private static Plugin plugin = HiaXnSocial.getPlugin(HiaXnSocial.class);
    private static  JedisPool jedisPool = null;

    private static String host = plugin.getConfig().getString("Redis.ip");
    private static Integer port = plugin.getConfig().getInt("Redis.port");
    private static Integer timeout = 5 * 1000;
    private static String password = null;

    private RedisUtil(){

    }

    public static synchronized Jedis getJedis(){
        if(jedisPool==null){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            //指定连接池中最大空闲连接数
            jedisPoolConfig.setMaxIdle(20);
            //链接池中创建的最大连接数
            jedisPoolConfig.setMaxTotal(100);
            //创建连接前先测试连接是否可用
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPool = new JedisPool(jedisPoolConfig,host,port,timeout,password);
        }
        return jedisPool.getResource();
    }
}