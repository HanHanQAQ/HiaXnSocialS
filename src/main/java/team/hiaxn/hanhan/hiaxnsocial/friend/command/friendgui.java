package team.hiaxn.hanhan.hiaxnsocial.friend.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import team.hiaxn.hanhan.hiaxnsocial.friend.gui.FriendGui;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class friendgui implements CommandExecutor {
    //Bukkit.broadcastMessage("§e[DEBUG] ");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (!label.equalsIgnoreCase("friendgui")) {
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
                    if (!rs.next()) { //判断玩家数据是否存在
                        String insert1 = "INSERT INTO player_data (uuid,name,friends)VALUES (?,?,?)"; //不存在 进行插入
                        try {
                            PreparedStatement insertPs = connection.prepareStatement(insert1);
                            insertPs.setString(1, String.valueOf(player.getUniqueId()));
                            insertPs.setString(2,player.getName());
                            insertPs.setString(3,"friends:null");
                            insertPs.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    FriendGui friendGui = new FriendGui(player);
                    friendGui.open();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //
            }
        }.runTask(HiaXnSocial.getPlugin(HiaXnSocial.class));



        return false;
    }
}
