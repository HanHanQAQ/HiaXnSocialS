package team.hiaxn.hanhan.hiaxnsocial.Friend.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Gui.FriendGui;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class friendgui implements CommandExecutor {
    //Bukkit.broadcastMessage("§e[DEBUG] ");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.broadcastMessage("§e[DEBUG] 检测到发送者不是玩家 取消");
            return true;
        }
        if (!label.equalsIgnoreCase("friendgui")) {
            Bukkit.broadcastMessage("§e[DEBUG] 检测到不是friendgui执行本命令 取消");
            return true;
        }
        Player player = (Player) sender;

        //进入菜单查询
        new BukkitRunnable() {
            @Override
            public void run() {
                String select1 = "SELECT * FROM player_data WHERE uuid = ?";
                try {
                    PreparedStatement selectPs = connection.prepareStatement(select1);
                    selectPs.setString(1, String.valueOf(player.getUniqueId()));
                    ResultSet rs = selectPs.executeQuery();

                    Bukkit.broadcastMessage("§e[DEBUG] 成功的创建了ResultSet (位于开菜单检测)");

                    if (!rs.next()) { //判断玩家数据是否存在

                        Bukkit.broadcastMessage("§e[DEBUG] 检测到 获取的rs为空 进行INSERT");

                        String insert1 = "INSERT INTO player_data (uuid,name,friends)VALUES (?,?,?)"; //不存在 进行插入
                        try {
                            PreparedStatement insertPs = connection.prepareStatement(insert1);
                            insertPs.setString(1, String.valueOf(player.getUniqueId()));
                            insertPs.setString(2,player.getName());
                            insertPs.setString(3,"friends:null");
                            insertPs.executeUpdate();

                            Bukkit.broadcastMessage("§e[DEBUG] INSERT成功");

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Bukkit.broadcastMessage("§e[DEBUG] 玩家打开菜单前操作完成");
                    Bukkit.broadcastMessage("§e[DEBUG] 应该执行打开菜单操作");
                    FriendGui friendGui = new FriendGui(player);
                    friendGui.open();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //
            }
        }.run();



        return false;
    }
}
