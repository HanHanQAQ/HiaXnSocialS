package team.hiaxn.hanhan.hiaxnsocial.friend.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class addfriend implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.broadcastMessage("§e[DEBUG] 检测到发送者不是玩家 取消");
            return true;
        }
        if (!label.equalsIgnoreCase("addfriend")) {
            Bukkit.broadcastMessage("§e[DEBUG] 检测到不是addfriend执行本命令 取消");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            String targetName = args[0]; //玩家名称为  /命令 args[0]
            String select1 = "SELECT * FROM player_data WHERE name = ?";

            try {
                PreparedStatement selps1 = connection.prepareStatement(select1);
                selps1.setString(1, targetName);
                ResultSet sel1rs = selps1.executeQuery(); //目标的查询结果集
                if (!sel1rs.next() || sel1rs.getString("status").equals("offline")) {
                    player.sendMessage("§c这个玩家不存在或者不在线!");
                    return true;
                }
                player.sendMessage("§a你将玩家 " + targetName + " 添加为你的好友!");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            //命令发送者的数据修改PrepareStateMent
                            String insertOwn = "UPDATE player_data SET friends = ? WHERE uuid = ?";
                            PreparedStatement insOwnPs = connection.prepareStatement(insertOwn);
                            insOwnPs.setString(2, String.valueOf(player.getUniqueId()));
                            //目标的数据修改PrepareStateMent
                            String insertTaget = "UPDATE player_data SET friends = ? WHERE name = ?";
                            PreparedStatement insTargetPs = connection.prepareStatement(insertTaget);
                            insTargetPs.setString(2,targetName);
                            //命令发送者的查询结果集
                            String select2 = "SELECT * FROM player_data WHERE uuid = ?";
                            PreparedStatement sel2ps = connection.prepareStatement(select2);
                            sel2ps.setString(1, String.valueOf(player.getUniqueId()));
                            ResultSet sel2rs = sel2ps.executeQuery();
                            sel2rs.next();
                            //空检测 friends为预设friends:null就执行直接更改 否则就执行使用 原好友 + “，” + 新好友 的方式进行更改
                            //发送者的
                            if (sel2rs.getString("friends").equals("friends:null")) {
                                insOwnPs.setString(1,targetName);
                            } else {
                                insOwnPs.setString(1,sel2rs.getString("friends") + "," + targetName);
                            }
                            insOwnPs.executeUpdate();
                            //目标的
                            if (sel1rs.getString("friends").equals("friends:null")) {
                                insTargetPs.setString(1,player.getName());
                            } else {
                                insTargetPs.setString(1,sel1rs.getString("friends") + "," + player.getName());
                            }
                            insTargetPs.executeUpdate();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }.run();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
