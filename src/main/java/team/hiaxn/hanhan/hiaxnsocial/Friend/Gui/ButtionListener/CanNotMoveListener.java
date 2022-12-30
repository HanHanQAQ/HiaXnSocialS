package team.hiaxn.hanhan.hiaxnsocial.Friend.Gui.ButtionListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CanNotMoveListener implements Listener {
    String[] invs = {"§0好友"};
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        for (String name : invs) {
            if (event.getView().getTitle().equals(name)) {
                event.setCancelled(true);
            }
        }
    }
}
