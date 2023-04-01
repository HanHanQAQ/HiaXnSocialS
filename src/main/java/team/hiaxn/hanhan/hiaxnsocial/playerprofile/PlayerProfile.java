package team.hiaxn.hanhan.hiaxnsocial.playerprofile;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import static team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial.connection;

public class PlayerProfile {
    private boolean Exist = true;
    private final ResultSet profileResultSet;
    private UUID playerUUID;
    private String playerName;
    private String playerStatus; //玩家在线状态
    private String[] playerFriendsArray; //处理过的好友列表
    private String playerFriendsString; //直接获取未处理的好友列表
    private String party; //未处理的组队列表
    private ArrayList<String> requesters = new ArrayList<>();
    public PlayerProfile(UUID uuid) throws Exception { //参数为UUID的构造方法
        this.playerUUID = uuid;
        PreparedStatement playerPs = connection.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");
        playerPs.setString(1, String.valueOf(playerUUID));
        ResultSet rs = playerPs.executeQuery();
        this.profileResultSet = rs;
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
        this.playerName = playerName;
        PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM player_data WHERE name = ?");
        ps1.setString(1, this.playerName); //有没有可能 他不只在那一个地方用了
        ResultSet rs = ps1.executeQuery();
        this.profileResultSet = rs;
        //Bukkit.broadcastMessage("§c[DEBUG] PlayerName为: " + playerName);
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
        return this.playerUUID;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public boolean isOnline() {
        return this.playerStatus.equals("online");
    }

    public boolean isExist() {
        return this.Exist;
    }

    public String[] getPlayerFriendsArray() {
        return this.playerFriendsArray;
    }

    public String getPlayerFriendsString() {
        return this.playerFriendsString;
    }

    public ResultSet getResultSet() {
        return profileResultSet;
    }
    public ArrayList<String> getRequesters() {
        return this.requesters;
    }
    public void addRequester(String requesterName) {
        this.requesters.add(requesterName);
    }
    public void removeRequester(String requesterName) {
        this.requesters.remove(requesterName);
    }

}
