package team.hiaxn.hanhan.hiaxnsocial.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
        private String inventoryName;
        private String inventoryOwner = "John_Doe";
        private int inventorySize;
        private Inventory theInventory;
        public InventoryUtil(String invName,int invSize) {
                this.inventoryName = invName;
                this.inventorySize = invSize;
                this.theInventory = Bukkit.createInventory(Bukkit.getPlayer(inventoryOwner),inventorySize,inventoryName);
        }

        public void setItem(int slot,ItemStack itemStack) {
                this.theInventory.setItem(slot,itemStack);
        }
        public void open(Player player) {
                player.openInventory(this.theInventory);
        }
}
