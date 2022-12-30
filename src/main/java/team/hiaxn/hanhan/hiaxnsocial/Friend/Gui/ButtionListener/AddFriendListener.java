package team.hiaxn.hanhan.hiaxnsocial.Friend.Gui.ButtionListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;
import team.hiaxn.hanhan.hiaxnsocial.Socket.SocketNotification;

public class AddFriendListener implements Listener {
    @EventHandler
    public void onClickAdd(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§0好友")) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("添加好友")) {

                SocketNotification socketNotification = new SocketNotification(HiaXnSocial.getPlugin(HiaXnSocial.class),1145);
                socketNotification.init();
                socketNotification.send("6");
            }
        }
    }
}
