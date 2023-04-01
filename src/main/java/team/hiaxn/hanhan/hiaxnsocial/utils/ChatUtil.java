package team.hiaxn.hanhan.hiaxnsocial.utils;

import org.bukkit.entity.Player;

public class ChatUtil {
        private Player targetPlayer;
        public ChatUtil(Player player) {
                this.targetPlayer = player;
        }
        public void sendMessage(String Message) {
                targetPlayer.sendMessage(Message.replace("&","§"));
        }
}
