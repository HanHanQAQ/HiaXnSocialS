package team.hiaxn.hanhan.hiaxnsocial.PlayerProfile;

import org.bukkit.Bukkit;
import team.hiaxn.hanhan.hiaxnsocial.Friend.Request.AddFriendsRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class PlayerProfile {
    private boolean Exist = true;
    private static ResultSet prfileResultSet;
    private UUID playerUUID;
    private String playerName;
    private String playerStatus; //玩家在线状态
    private String[] playerFriendsArray; //处理过的好友列表
    private String playerFriendsString; //直接获取未处理的好友列表
    private String party; //未处理的组队列表
    private static HashMap<String, AddFriendsRequest> friendsRequestsMap = new HashMap<>();

    public PlayerProfile(UUID uuid) throws Exception { //参数为UUID的构造方法
        friendsRequestsMap.put("null",new AddFriendsRequest());
        Bukkit.broadcastMessage("§e[DEBUG] 成功的使用了使用玩家UUID作为引索的构造方法(来自PlayerProfile)");
        this.playerUUID = uuid;
        String sql = "SELECT * FROM player_data WHERE uuid = ?";
        PreparedStatement ps1 = connection.prepareStatement(sql);
        ps1.setString(1, String.valueOf(playerUUID));
        ResultSet rs = ps1.executeQuery();
        this.prfileResultSet = rs;
        if (!rs.next()) { //判断玩家数据是否存在
            Bukkit.getPlayer(uuid).sendMessage("§c出现错误!错误内容:空的玩家数据!无法继续该操作");
            Exist = false;
            return;
        }
        this.playerName = rs.getString("name");
        this.playerStatus = rs.getNString("status");
        this.playerFriendsString = rs.getString("friends");
        this.playerFriendsArray = rs.getString("friends").split(",");
    }

    public PlayerProfile(String playerName) throws Exception { //参数为玩家名称的构造方法
        friendsRequestsMap.put("null",new AddFriendsRequest());
        Bukkit.broadcastMessage("§e[DEBUG] 成功的使用了用PlayerName作为引锁的构造方法 (来自PlayerProfile)");
        this.playerName = playerName;
        String sql = "SELECT * FROM player_data WHERE name = ?";
        PreparedStatement ps1 = connection.prepareStatement(sql);
        ps1.setString(1, this.playerName); //有没有可能 他不只在那一个地方用了
        ResultSet rs = ps1.executeQuery();
        prfileResultSet = rs;
        Bukkit.broadcastMessage("§c[DEBUG] PlayerName为: " + playerName);
        if (!rs.next()) { //判断玩家数据是否存在
            Bukkit.getPlayer(playerName).sendMessage("§c出现错误!错误内容:空的玩家数据!无法继续该操作");
            return;
        }
        this.playerUUID = UUID.fromString(rs.getString("uuid"));
        this.playerStatus = rs.getNString("status");
        this.playerFriendsString = rs.getString("friends");
        this.playerFriendsArray = rs.getString("friends").split(",");
    }

    public UUID getUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isOnline() {
        Bukkit.broadcastMessage("§e[DEBUG] 当前玩家" + playerName + "的status为: " + playerStatus + " 应当返回一个: " + playerStatus.equals("online"));
        return playerStatus.equals("online");
    }

    public boolean isExist() {
        return Exist;
    }

    public String[] getPlayerFriendsArray() {
        return playerFriendsArray;
    }

    public String getPlayerFriendsString() {
        return playerFriendsString;
    }

    public ResultSet getResultSet() {
        return prfileResultSet;
    }

    public void putFriendsRequestsMap(String sender, AddFriendsRequest addFriendsRequest) {
        friendsRequestsMap.put(sender, addFriendsRequest);
    }

    public void remFriendsRequests(String sender) {
        friendsRequestsMap.remove(sender);
    }

    public static HashMap getRequestsSet() {
        return friendsRequestsMap;
    }

    public AddFriendsRequest getFriendsRequests(String requestSender) {
        return friendsRequestsMap.get(requestSender);
    }
}
