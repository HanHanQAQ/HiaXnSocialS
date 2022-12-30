package team.hiaxn.hanhan.hiaxnsocial.Friend.SocketListener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.hiaxn.hanhan.hiaxnsocial.Socket.SocketNotificationEvent;

import java.net.ServerSocket;

public class Socket implements Listener {
    @EventHandler
    public void socketListener(SocketNotificationEvent event) {
        Bukkit.broadcastMessage("HiaXnSocail-来自Socket的信息：" + event.getMessage());
    }
}
