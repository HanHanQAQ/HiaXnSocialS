package team.hiaxn.hanhan.hiaxnsocial.Friend.Request;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.PlayerProfile.PlayerProfile;
import team.hiaxn.hanhan.hiaxnsocial.Redis.RedisUtil;
import team.hiaxn.hanhan.hiaxnsocial.Utils.HoverTextUtil;

import java.sql.SQLException;
import java.util.UUID;

/**
 * 这是一个添加好友请求对象 进行好友请求的处理
 * @author by HanHan
 *
 */
public class AddFriendsRequest { //这就是那个好友请求对象
    private boolean isContinue = true; //这是是否继续执行 给的一个boolean 等下就知道有什么作用了  这里作用就体现出来了
    private boolean responsed = false; //这个是被发送好友申请的那个人是否做出响应 用来给好友申请过期倒计时那里进行一个判断
    private UUID senderUuid; //发送好友申请的人的uuid
    private String targeterName; //被发送申请的人的Name
    private String redisCommand = " 5 ";
    private PlayerProfile senderPrifle;
    private PlayerProfile targeterPrifle;
    public AddFriendsRequest() {
    }
    public AddFriendsRequest(UUID senderUuid,String targeterName) throws Exception { //对应这里的uuid 对应这里的name
        this.senderUuid = senderUuid;
        this.targeterName = targeterName;
        this.senderPrifle = new PlayerProfile(this.senderUuid); //这个是创建两者的PlayerProfile
        this.targeterPrifle = new PlayerProfile(this.targeterName);
        this.targeterName = targeterName;
        Bukkit.broadcastMessage("§e[DEBUG] 请求初始化完成");
        //从onLocaServerFind方法使用 之后会自动进行方法切换
        onLocalServerFind(); //执行一个方法
        Bukkit.broadcastMessage("§e[DEBUG] 已经执行完毕发送请求方法");
    }
    private void onLocalServerFind() { //在发送者所在的服务器进行查询
        Bukkit.broadcastMessage("§e[DEBUG] 进入请求方法");
        Bukkit.broadcastMessage("§e[DEBUG] 开始判断是否在线");
        if (!this.targeterPrifle.isOnline()) {
            Bukkit.getServer().getPlayer(senderUuid).sendMessage("§9§m-----------------------------------------------------\n" +
                    "§c" + targeterName + "不在线!\n" + "§9§m-----------------------------------------------------" );
            isContinue = false;
            return;
        }
        Bukkit.broadcastMessage("§e[DEBUG] 开始判断是否在本服务器");
        if (Bukkit.getServer().getPlayer(targeterName) == null) { //条件是本服务器获取这个玩家为null 并且在Mysql的表里面有这个玩家
            Bukkit.broadcastMessage("§e[DEBUG] 玩家不存在于本服务器 发送redis指令");
            redisFind(); //这里就进行redis通讯 发送redis指令 然后就不由现在的这个处理了
            isContinue = false; //这里进行把是否继续的改成false
            return;
        }
        Bukkit.broadcastMessage("§e[DEBUG] 玩家在本服务器 执行指令");
        //检测到玩家在本服务器 进行服内发送
        Bukkit.getServer().getPlayer(senderUuid).sendMessage("§9§m-----------------------------------------------------\n" +
                "§e你的好友请求已发送给" + targeterName + "!该请求需要在五分钟内接受!\n" + "§9§m-----------------------------------------------------" );

        Player targetPlayer = Bukkit.getServer().getPlayer(targeterName);
        Bukkit.getServer().getPlayer(targeterName).sendMessage("§9§m-----------------------------------------------------\n");
        Bukkit.getServer().getPlayer(targeterName).sendMessage("§e好友请求: " + Bukkit.getServer().getPlayer(senderUuid).getName());
        //这个HoverTextUtil是我写的一个东西 没什么太大关系 runCommand就是玩家点击那个聊天信息会执行的指令
        TextComponent accept = HoverTextUtil.BuildTextComponent("§a§l[§r§a接受§l] §r§7§m-§r ","§b点击接受好友请求","/friend accept " + Bukkit.getServer().getPlayer(senderUuid).getName());
        TextComponent deny = HoverTextUtil.BuildTextComponent("§c§l[§r§c拒绝§l]","§b点击拒绝好友请求","/friend deny " + Bukkit.getServer().getPlayer(senderUuid).getName());
        HoverTextUtil.sendHoverMessage(targetPlayer,accept,deny);
        Bukkit.getServer().getPlayer(targeterName).sendMessage("§9§m-----------------------------------------------------\n");
    } //到这里方法结束了
    private void redisFind() { //使用Redis进行查找 还没写
        Jedis jedis = RedisUtil.getJedis();
        //返回订阅者数量
        Long countSubscribe = jedis.publish("friends", redisCommand);
        System.out.println("发布成功，订阅数量:"+ countSubscribe);
    }
    public boolean isContinue() {
        return isContinue;
    }
    public UUID getSenderUuid(){
        return senderUuid;
    }
    public String getTargeterName(){
        return targeterName;
    }
    public void setResponsed() {
        this.responsed = true;
    }
    public boolean isResponsed() {
        return responsed;
    }
}