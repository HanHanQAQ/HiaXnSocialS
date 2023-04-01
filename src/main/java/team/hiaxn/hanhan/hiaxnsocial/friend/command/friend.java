package team.hiaxn.hanhan.hiaxnsocial.friend.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import team.hiaxn.hanhan.hiaxnsocial.playerprofile.PlayerProfile;
import team.hiaxn.hanhan.hiaxnsocial.redis.RedisUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class friend implements CommandExecutor {
    //public static HashMap<String,PlayerProfile> profileHashMap = new HashMap<>(); //收到请求的玩家Map 是个成员变量
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.broadcastMessage("§e[DEBUG] 检测到发送者不是玩家 取消");
            return true;
        }
        Player senderPlayer = (Player) sender;
        switch (args[0]) { // command: /f args[0] args[1] args[2] args[3]
            case "accept":
                try {
                    acceptFriends(senderPlayer, args[1]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "add": //添加好友
                if (args[1].equals(senderPlayer.getName())) {
                    senderPlayer.sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§c你不能向自己发送好友申请!\n" +
                            "§9§m-----------------------------------------------------");
                    return true;
                }
                try {
                    addFriends(senderPlayer, args[1]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case "best": //TODO:特别关心
                break;
            case "deny":
                try {
                    denyFriends(senderPlayer, args[1]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "help"://TODO:帮助命令
                break;
            case "list"://TODO:好友列表
                break;
            case "nickname"://TODO:设置备注
                break;
            case "notificactions"://TODO:设置加入/退出消息
                break;
            case "remove":
                try {
                    removeFriends(senderPlayer, args[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "removeall"://TODO:移除所有好友
                break;
            case "requests"://TODO:好友请求清单
                break;
            case "toggle"://TODO:切换是否可添加好友
                break;
            default:
                if (args[0].equals(senderPlayer.getName())) {
                    senderPlayer.sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§c你不能向自己发送好友申请!\n" +
                            "§9§m-----------------------------------------------------");
                    return true;
                }
                try {
                    addFriends(senderPlayer, args[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    public void acceptFriends(Player senderPlayer, String targetName) throws SQLException { //这里 接受方法
        String find = "SELECT * FROM player_data WHERE name = ?";
        PreparedStatement findPs = connection.prepareStatement(find);
        findPs.setString(1, targetName);
        ResultSet findRs = findPs.executeQuery();
        if (!findRs.next()) {
            senderPlayer.sendMessage("§c没有找到叫做" + targetName + "的玩家!");
            return;
        } //先去看看有没有这个人
        sendRedisCommand("accept",senderPlayer.getName(),targetName);
    }

    public void denyFriends(Player senderPlayer, String targetName) throws SQLException { //这里 接受方法
        String find = "SELECT * FROM player_data WHERE name = ?";
        PreparedStatement findPs = connection.prepareStatement(find);
        findPs.setString(1, targetName);
        ResultSet findRs = findPs.executeQuery();
        if (!findRs.next()) {
            senderPlayer.sendMessage("§c没有找到叫做" + targetName + "的玩家!");
            return;
        } //先去看看有没有这个人
        sendRedisCommand("deny",senderPlayer.getName(),targetName);
    }

    public void addFriends(Player sender, String targetPlayerName) throws Exception {
        PreparedStatement findPs = connection.prepareStatement("SELECT * FROM player_data WHERE name = ?");
        findPs.setString(1, targetPlayerName);
        ResultSet findRs = findPs.executeQuery();
        if (!findRs.next()) {
            sender.sendMessage("§c没有找到叫做" + targetPlayerName + "的玩家!");
            return;
        } //玩家是否存在判断
        sendRedisCommand("add",sender.getName(),targetPlayerName);
    }
    public void removeFriends(Player sender, String targetPlayerName) throws Exception {
        PreparedStatement findPs = connection.prepareStatement("SELECT * FROM player_data WHERE name = ?");
        findPs.setString(1, targetPlayerName);
        ResultSet findRs = findPs.executeQuery();
        if (!findRs.next()) {
            sender.sendMessage("§c没有找到叫做" + targetPlayerName + "的玩家!");
            return;
        } //玩家是否存在判断
        PlayerProfile senderProfile = new PlayerProfile(sender.getUniqueId());

        PreparedStatement updatePs = connection.prepareStatement("UPDATE player_data SET friends = ? WHERE name = ?");
        String senderNewFriends = "";
        if (senderProfile.getPlayerFriendsArray().length == 1) {
            senderNewFriends = "friends:null";
        } else {
            for (int i = 0;i < senderProfile.getPlayerFriendsArray().length;i++) {
                if (i == 0) {
                    senderNewFriends = senderProfile.getPlayerFriendsArray()[i].equals(targetPlayerName) ? "" : senderProfile.getPlayerFriendsArray()[i];
                } else {
                    senderNewFriends = senderNewFriends + (senderProfile.getPlayerFriendsArray()[i].equals(targetPlayerName) ?
                            "" : "," + senderProfile.getPlayerFriendsArray()[i]);
                }
            }
        }
        updatePs.setString(1,senderNewFriends);
        updatePs.setString(2,sender.getName());
        updatePs.executeUpdate();

        PlayerProfile targetProfile = new PlayerProfile(targetPlayerName);

        PreparedStatement updatePsTarget = connection.prepareStatement("UPDATE player_data SET friends = ? WHERE name = ?");
        String targetNewFriends = "";
        if (targetProfile.getPlayerFriendsArray().length == 1) {
            targetNewFriends = "friends:null";
        } else {
            for (int i = 0;i < targetProfile.getPlayerFriendsArray().length;i++) {
                if (i == 0) {
                    targetNewFriends = targetProfile.getPlayerFriendsArray()[i].equals(sender.getName()) ? "" : targetProfile.getPlayerFriendsArray()[i];
                } else {
                    targetNewFriends = targetNewFriends + (targetProfile.getPlayerFriendsArray()[i].equals(sender.getName()) ?
                            "" : "," + targetProfile.getPlayerFriendsArray()[i]);
                }
            }
        }

        updatePsTarget.setString(1,targetNewFriends);
        updatePsTarget.setString(2,targetPlayerName);
        updatePsTarget.executeUpdate();

        sender.sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§e你删除了 " + targetPlayerName + "从你的好友列表中!\n" +
                        "§9§m-----------------------------------------------------");

    }
    private void sendRedisCommand(String todo,String senderName,String reciverName) {
        Jedis jedis = RedisUtil.getJedis();
        //返回订阅者数量
        Long countSubscribe = jedis.publish("friends","frequest " + todo + " " + senderName + " " + reciverName);
        System.out.println("发布redis " + todo + " 请求命令成功，订阅数量:"+ countSubscribe);
    }
}
