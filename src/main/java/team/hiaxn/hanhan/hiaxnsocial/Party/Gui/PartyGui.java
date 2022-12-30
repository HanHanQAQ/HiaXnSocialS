package team.hiaxn.hanhan.hiaxnsocial.Party.Gui;

import org.bukkit.entity.Player;
import team.hiaxn.hanhan.hiaxnsocial.Utils.InventoryUtil;

public class PartyGui {
    private Player ownPlayer;
    private InventoryUtil friendGui;
    PartyGui(Player targetPlayer) {
        this.ownPlayer = targetPlayer;
        this.friendGui = new InventoryUtil("§0组队",6 * 9);
    }
    public void open() {
        friendGui.open(ownPlayer);
    }
}
