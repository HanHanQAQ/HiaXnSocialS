package team.hiaxn.hanhan.hiaxnsocial.Friend.Request;


import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.PlayerProfile.PlayerProfile;
import team.hiaxn.hanhan.hiaxnsocial.Redis.RedisUtil;
import team.hiaxn.hanhan.hiaxnsocial.Utils.HoverTextUtil;

import java.sql.PreparedStatement;

import static team.hiaxn.hanhan.hiaxnsocial.Friend.Command.friend.profileHashMap;
import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

/**
 * @Author HanHan
 * @Date 2022.12.31
 */
public class FriendRequest {
    private final FriendRequest instanceRequest = this; //实例化后的对像内存地址值
    private boolean responded = false; //接收好友请求的玩家是否响应了这个好友请求
    private String senderName;
    private PlayerProfile senderProfile;
    private String reciverName;
    private PlayerProfile reciverProfile;

    /**
     * @param senderName 发送好友请求的玩家昵称
     * @param reciverName 接受好友请求的玩家昵称
     */
    public FriendRequest(String senderName,String reciverName) throws Exception {
        this.senderName = senderName;
        this.senderProfile = new PlayerProfile(this.senderName);
        this.reciverName = reciverName;
        if (profileHashMap.get(reciverName) != null) {
            this.reciverProfile = profileHashMap.get(this.reciverName);
        } else {
            this.reciverProfile = new PlayerProfile(this.reciverName);
        }
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ")
        sendRequest();
    }

    private void sendRequest() {
        if (!reciverProfile.isOnline()) {
            Bukkit.getServer().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                    "§c" + reciverName + "不在线!\n" +
                    "§9§m-----------------------------------------------------" );
            return;
        }
        if (Bukkit.getPlayer(reciverName) == null) {
            sendRedisRequest();
        } else {
            sendLocalRequest();
        }
    }
    private void sendLocalRequest() {
        reciverProfile.addRequester(senderName);
        reciverProfile.addFriendRequest(instanceRequest);
        profileHashMap.put(reciverName,reciverProfile);
        Bukkit.broadcastMessage("§e[DEBUG] 玩家在本服务器 执行指令");
        Bukkit.getServer().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                "§e你的好友请求已发送给" + reciverName + "!该请求需要在五分钟内接受!\n" +
                "§9§m-----------------------------------------------------" );
        Player targetPlayer = Bukkit.getServer().getPlayer(reciverName);
        Bukkit.getServer().getPlayer(reciverName).sendMessage("§9§m-----------------------------------------------------\n");
        Bukkit.getServer().getPlayer(reciverName).sendMessage("§e好友请求: " + senderName);
        TextComponent accept = HoverTextUtil.BuildTextComponent("§a§l[§r§a接受§l]","§b点击接受好友请求","/friend accept " + senderName);
        TextComponent barLine = HoverTextUtil.BuildNoClickEventTextComponent(" §r§7§m-§r ");
        TextComponent deny = HoverTextUtil.BuildTextComponent("§c§l[§r§c拒绝§l]","§b点击拒绝好友请求","/friend deny " + senderName);
        HoverTextUtil.sendHoverMessage(targetPlayer,accept,barLine,deny);
        Bukkit.getServer().getPlayer(reciverName).sendMessage("§9§m-----------------------------------------------------\n");
        countDown();
    }
    private void sendRedisRequest() {
        Jedis jedis = RedisUtil.getJedis();
        //返回订阅者数量
        Long countSubscribe = jedis.publish("friends","redisCommandRequest");
        System.out.println("发布redis好友请求成功(没写完)，订阅数量:"+ countSubscribe);
    }
    private void countDown() {
        new BukkitRunnable() {
            int count = 300;
            @Override
            public void run() {
                if (count == 0) {
                    Bukkit.getPlayer(reciverName).sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§e来自" + senderName + "的好友申请已失效。\n" +
                            "§9§m-----------------------------------------------------");
                    Bukkit.getPlayer(senderName).sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§e你向" + reciverName + "发送的好友申请已失效。\n" +
                            "§9§m-----------------------------------------------------");
                    profileHashMap.get(reciverName).removeRequester(senderName);
                    profileHashMap.get(reciverName).removeFriendRequest(instanceRequest);
                    profileHashMap.put(reciverName,reciverProfile);
                    this.cancel();
                }
                if (responded) {
                    profileHashMap.get(reciverName).removeRequester(senderName);
                    profileHashMap.get(reciverName).removeFriendRequest(instanceRequest);
                    profileHashMap.put(reciverName,reciverProfile);
                    cancel();
                }
            }
        }.runTaskTimer(HiaXnSocial.getPlugin(HiaXnSocial.class),0L,20L);
    }
    public void response() {
        this.responded = true;
    }
}
