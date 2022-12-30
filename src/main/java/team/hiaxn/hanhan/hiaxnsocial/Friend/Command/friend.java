package team.hiaxn.hanhan.hiaxnsocial.Friend.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Request.AddFriendsRequest;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.PlayerProfile.PlayerProfile;
import java.util.Map.Entry;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class friend implements CommandExecutor {
    public static HashMap<String, PlayerProfile> profileHashMap = new HashMap<>(); //收到请求的玩家Map 是个成员变量

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
                    senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
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
                addFriends(senderPlayer, args[0]);
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

        PlayerProfile targetProfile = profileHashMap.get(targetName); // 这里是从上面的HashMap进行一个获取 value就是这个PlayerProfile 按理说就应该正确获取才对
        int times = 0; //看看循环了几次

        Iterator var1 = PlayerProfile.getRequestsSet().entrySet().iterator();
        while (var1.hasNext()) {
            Entry entry = (Entry) var1.next();
            if (entry.getValue() instanceof AddFriendsRequest) {
                if (entry.getKey() instanceof String) {
                    String senderName = (String) entry.getKey();
                    AddFriendsRequest friendsRequest = (AddFriendsRequest) entry.getValue();
                    if (!senderName.equals(senderPlayer.getName())) {
                        if () {
                            
                        }
                        if (times + 1 == PlayerProfile.getRequestsSet().size()) {
                            senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
                                    "§c那人没有邀请你成为好友!请尝试§e/friend " + targetName + "\n" +
                                    "§9§m-----------------------------------------------------");
                            return;
                        }
                    } else { //如果找到了
                        targetProfile.remFriendsRequests(Bukkit.getPlayer(friendsRequest.getSenderUuid()).getName());
                        break;
                    }
                    Bukkit.broadcastMessage(senderName + "/" + friendsRequest);
                }
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
            times++;

            Bukkit.broadcastMessage("Debug: " + " / Times: " + times);
        }
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
        senderPlayer.sendMessage("§9§m-----------------------------------------------------\n" +
                "§a你现在是" + targetName + "的好友了\n" +
                "§9§m-----------------------------------------------------");
        if (Bukkit.getPlayer(targetName) == null) {
            // TODO:进行Redis发送
        } else {
            Bukkit.getPlayer(targetName).sendMessage("§9§m-----------------------------------------------------\n" +
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
        }.run();
    }

    public void addFriends(Player sender, String targetPlayerName) {
        try { //这里 是玩家加好友
            String find = "SELECT * FROM player_data WHERE name = ?";
            PreparedStatement findPs = connection.prepareStatement(find);
            findPs.setString(1, targetPlayerName);
            ResultSet findRs = findPs.executeQuery();
            if (!findRs.next()) {
                sender.sendMessage("§c没有找到叫做" + targetPlayerName + "的玩家!");
                return;
            } //玩家是否存在判断

            PlayerProfile targeterProfile = new PlayerProfile(targetPlayerName);
            for (String name : targeterProfile.getPlayerFriendsArray()) {
                if (Objects.equals(name, sender.getName())) {
                    sender.sendMessage("§9§m-----------------------------------------------------\n" +
                            "§c你已经是该玩家的好友了!\n" +
                            "§9§m-----------------------------------------------------");
                    return;
                }
            } //是否已经是好友判断
            if (targeterProfile.getFriendsRequests(sender.getName()) != null) {
                sender.sendMessage("§9§m-----------------------------------------------------\n" +
                        "§e你已经向这个玩家发送了好友申请!请等待一会儿\n" +
                        "§9§m-----------------------------------------------------");
                return;
            }
            AddFriendsRequest addFriendsRequest = new AddFriendsRequest(sender.getUniqueId(), targetPlayerName); //实例化一个好友请求对象 好了 现在这个对象已经实例化了 程序继续往下走
            if (profileHashMap.get(addFriendsRequest.getTargeterName()) == null) { //判断这个玩家有没有存过PlayerProfile在Map里面
                Bukkit.broadcastMessage("§e[DEBUG] 检测到没有" + targetPlayerName + "这个玩家没有过PlayerProfile 进行新的实例化");
                PlayerProfile playerProfile = new PlayerProfile(addFriendsRequest.getTargeterName());
                playerProfile.putFriendsRequestsMap(Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).getName(), addFriendsRequest); //获取发送者id然后传到PlayerProfile的Map里面
                profileHashMap.put(addFriendsRequest.getTargeterName(), playerProfile); //存到上面的成员变量Map里面了
                Bukkit.broadcastMessage("§e[DEBUG] 玩家的Profile已经存储了");
            } else { //这是存过的
                Bukkit.broadcastMessage("§e[DEBUG] 检测到" + targetPlayerName + "这个玩家有PlayerProfile 直接获取");
                PlayerProfile playerProfile = profileHashMap.get(addFriendsRequest.getTargeterName());
                playerProfile.putFriendsRequestsMap(Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).getName(), addFriendsRequest); //获取发送者id然后传到PlayerProfile的Map里面
                profileHashMap.put(addFriendsRequest.getTargeterName(), playerProfile); //下面的也有存
                Bukkit.broadcastMessage("§e[DEBUG] 玩家的Profile已经存储了");
            }
            Bukkit.broadcastMessage("§e[DEBUG] 检测是否需要继续执行");
            if (!addFriendsRequest.isContinue()) { //这里就是刚刚那个东西了 非常的有用
                return;
            }
            Bukkit.broadcastMessage("§e[DEBUG] 需要继续执行");
            new BukkitRunnable() { //销毁处理器 这就是一个销毁器 进行一个到时间&玩家响应后的销毁 以免出现Bug
                int count = 60 * 5;

                @Override
                public void run() {
                    if (count == 0) { //过期销毁
                        //TODO:跨服发送
                        Bukkit.getPlayer(addFriendsRequest.getTargeterName()).sendMessage("§9§m-----------------------------------------------------\n" +
                                "§e来自" + Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).getName() + "的好友申请已失效。\n" +
                                "§9§m-----------------------------------------------------");
                        Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).sendMessage("§9§m-----------------------------------------------------\n" +
                                "§e你向" + addFriendsRequest.getTargeterName() + "发送的好友申请已失效。\n" +
                                "§9§m-----------------------------------------------------");
                        profileHashMap.get(addFriendsRequest.getTargeterName()).remFriendsRequests(Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).getName());
                        cancel();
                    }
                    if (addFriendsRequest.isResponsed()) { //玩家响应销毁
                        profileHashMap.get(addFriendsRequest.getTargeterName()).remFriendsRequests(Bukkit.getPlayer(addFriendsRequest.getSenderUuid()).getName());
                    }
                    count--;
                }
            }.runTaskTimer(HiaXnSocial.getPlugin(HiaXnSocial.class), 0, 20L);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
