package team.hiaxn.hanhan.hiaxnsocial.Friend.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;
import team.hiaxn.hanhan.hiaxnsocial.Redis.RedisUtil;


public class testsocket implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Jedis jedis = RedisUtil.getJedis();
        //返回订阅者数量
        Long countSubscribe = jedis.publish("friends", args[0]);
        System.out.println("发布成功，订阅数量:"+ countSubscribe);
        return false;
    }
}
