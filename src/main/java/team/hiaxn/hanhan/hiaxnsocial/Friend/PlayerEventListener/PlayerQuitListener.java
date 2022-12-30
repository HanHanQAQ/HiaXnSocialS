package team.hiaxn.hanhan.hiaxnsocial.Friend.PlayerEventListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws SQLException {
        Player player = event.getPlayer();
        String select1 = "SELECT * FROM player_data WHERE uuid = ?";
        PreparedStatement selectPs = connection.prepareStatement(select1);
        selectPs.setString(1, String.valueOf(player.getUniqueId()));
        ResultSet select1Rs = selectPs.executeQuery();
        if (!select1Rs.next()) {
            String insert1 = "INSERT INTO player_data (uuid,name,friends)VALUES (?,?,?)"; //不存在 进行插入
            PreparedStatement inset1Ps = connection.prepareStatement(insert1);
            inset1Ps.setString(1,String.valueOf(player.getUniqueId()));
            inset1Ps.setString(2,player.getName());
            inset1Ps.setString(3,"friends:null");
            inset1Ps.executeUpdate();
        }
        String insert2 = "UPDATE player_data SET status = ? WHERE uuid = ?";
        PreparedStatement insert2Ps = connection.prepareStatement(insert2);
        insert2Ps.setString(1,"offline");
        insert2Ps.setString(2, String.valueOf(player.getUniqueId()));
        insert2Ps.executeUpdate();
    }
}
