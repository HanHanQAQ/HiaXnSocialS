package team.hiaxn.hanhan.hiaxnsocial.Friend.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Request.FriendRequest;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.PlayerProfile.PlayerProfile;
import java.util.Map.Entry;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class friend implements CommandExecutor {
    public static HashMap<String,PlayerProfile> profileHashMap = new HashMap<>(); //收到请求的玩家Map 是个成员变量
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
                addFriends(senderPlayer, args[1]);
                break;
            case "best": //TODO:特别关心
                break;
            case "deny": //TODO:拒绝一条好友请求
                break;
            case "help"://TODO:帮助命令
                break;
            case "list"://TODO:好友列表
                break;
            case "nickname"://TODO:设置备注
                break;
            case "notificactions"://TODO:设置加入/退出消息
                break;
            case "remove"://TODO:移除好友
                break;
            case "removeall"://TODO:移除所有好友
                break;
            case "requests"://TODO:好友请求清单
                break;
            case "toggle"://TODO:切换是否可添加好友
                break;
            default:
                if (args[0].equals(senderPlayer.getName())) {
                    senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
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
        PlayerProfile senderProfile = profileHashMap.get(senderPlayer.getName()); //因为好友申请是存在接受者的Profile里面的 所以叫做senderProfile
        if (senderProfile == null) { //为空就是从来没人给他发好友请求
            senderPlayer.sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                    "§c那人没有邀请你成为好友!请尝试§e/friend " + targetName + "\n" +
                    "§9§m-----------------------------------------------------");
            return;
        }
            //Bukkit.broadcastMessage("Debugs: " + entry.getKey());
            /*String uit = var1.next().toString();
            String[] split = uit.split("=");
            String senderName = split[0];
            if (!senderName.equals(senderPlayer.getName())) {
                if (times + 1 == targetProfile.getRequestsSet().size()) {
                    senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
                            "§c那人没有邀请你成为好友!请尝试§e/friend " + targetName + "\n" +
                            "§9§m-----------------------------------------------------");
                    return;
                }
            } else { //如果找到了
                break;
            }*/
        //Bukkit.broadcastMessage("§c[DEBUG] RequestSet: " + targetProfile.getRequestsSet());

//        for (String senderName : targetProfile.getRequestsSet()) { //这里是获取这个玩家的好友申请列表 看看都有谁申请了
//            if (!senderName.equals(senderPlayer.getName())) { //如果找不到
//                if (times + 1 == targetProfile.getRequestsSet().size()) {
//                    senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
//                            "§c那人没有邀请你成为好友!请尝试§e/friend " + targetName + "\n" +
//                            "§9§m-----------------------------------------------------");
//                    return;
//                }
//            } else { //如果找到了
//                break;
//            }
//            times++;
//        }
        profileHashMap.get(senderPlayer.getName()).getFriendsRequests(targetName).setResponsed();
        senderPlayer.sendMessage(
                "§9§m-----------------------------------------------------\n" +
                "§a你现在是" + targetName + "的好友了\n" +
                "§9§m-----------------------------------------------------");
        if (Bukkit.getPlayer(targetName) == null) {
            // TODO:进行Redis发送
        } else {
            Bukkit.getPlayer(targetName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                    "§a你现在是" + senderPlayer.getName() + "的好友了\n" +
                    "§9§m-----------------------------------------------------");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //目标玩家的查询结果集
                    String select1 = "SELECT * FROM player_data WHERE name = ?";
                    PreparedStatement selps1 = connection.prepareStatement(select1);
                    selps1.setString(1, targetName);
                    ResultSet sel1rs = selps1.executeQuery();
                    sel1rs.next();
                    //命令发送者的查询结果集
                    String select2 = "SELECT * FROM player_data WHERE uuid = ?";
                    PreparedStatement sel2ps = connection.prepareStatement(select2);
                    sel2ps.setString(1, String.valueOf(senderPlayer.getUniqueId()));
                    ResultSet sel2rs = sel2ps.executeQuery();
                    sel2rs.next();
                    //命令发送者的数据修改PrepareStateMent
                    String insertOwn = "UPDATE player_data SET friends = ? WHERE uuid = ?";
                    PreparedStatement insOwnPs = connection.prepareStatement(insertOwn);
                    insOwnPs.setString(2, String.valueOf(senderPlayer.getUniqueId()));
                    //目标的数据修改PrepareStateMent
                    String insertTaget = "UPDATE player_data SET friends = ? WHERE name = ?";
                    PreparedStatement insTargetPs = connection.prepareStatement(insertTaget);
                    insTargetPs.setString(2, targetName);

                    //空检测 friends为预设friends:null就执行直接更改 否则就执行使用 原好友 + “，” + 新好友 的方式进行更改
                    //发送者的
                    if (sel2rs.getString("friends").equals("friends:null")) {
                        insOwnPs.setString(1, targetName);
                    } else {
                        insOwnPs.setString(1, sel2rs.getString("friends") + "," + targetName);
                    }
                    insOwnPs.executeUpdate();
                    //目标的
                    if (sel1rs.getString("friends").equals("friends:null")) {
                        insTargetPs.setString(1, senderPlayer.getName());
                    } else {
                        insTargetPs.setString(1, sel1rs.getString("friends") + "," + senderPlayer.getName());
                    }
                    insTargetPs.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTask(HiaXnSocial.getPlugin(HiaXnSocial.class));
    }

    public FriendRequest addFriends(Player sender, String targetPlayerName) throws Exception {
        PreparedStatement findPs = connection.prepareStatement("SELECT * FROM player_data WHERE name = ?");
        findPs.setString(1, targetPlayerName);
        ResultSet findRs = findPs.executeQuery();
        if (!findRs.next()) {
            sender.sendMessage("§c没有找到叫做" + targetPlayerName + "的玩家!");
            return null;
        } //玩家是否存在判断
        PlayerProfile targeterProfile = new PlayerProfile(targetPlayerName);
        for (String name : targeterProfile.getPlayerFriendsArray()) {
            if (Objects.equals(name, sender.getName())) {
                sender.sendMessage(
                        "§9§m-----------------------------------------------------\n" +
                        "§c你已经是该玩家的好友了!\n" +
                        "§9§m-----------------------------------------------------");
                return null;
            }
        }
        for (String name : targeterProfile.getRequesters()) {
            if (name.equals(sender.getName())) {
                sender.sendMessage(
                        "§9§m-----------------------------------------------------\n" +
                        "§e你已经向这个玩家发送了好友申请!请等待一会儿\n" +
                        "§9§m-----------------------------------------------------");
                return null;
            }
        }
        FriendRequest friendRequest = new FriendRequest(sender.getName(), targetPlayerName); //实例化一个好友请求对象
        return friendRequest;
    }
}
