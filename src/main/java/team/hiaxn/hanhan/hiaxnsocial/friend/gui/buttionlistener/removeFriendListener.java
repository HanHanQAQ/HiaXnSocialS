package team.hiaxn.hanhan.hiaxnsocial.friend.gui.buttionlistener;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.hiaxn.hanhan.hiaxnsocial.HiaXnSocial;

import java.util.Arrays;

public class removeFriendListener implements Listener {
    @EventHandler
    public void onClickAdd(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§0好友")) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§c删除好友")) {
                ItemStack left = new ItemStack(Material.PAPER);
                ItemMeta leftMeta = left.getItemMeta();
                leftMeta.setDisplayName("输入用户名");
                left.setItemMeta(leftMeta);
                new AnvilGUI.Builder()
                        .onComplete((completion) -> {
                            Player player = (Player) event.getWhoClicked();
                            player.chat("/friend remove " + completion.getText());
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        })
                        .itemLeft(left)
                        .title("§8填入用户名")
                        .plugin(HiaXnSocial.getPlugin(HiaXnSocial.class))
                        .open((Player) event.getWhoClicked());
            }
        }
    }
}
