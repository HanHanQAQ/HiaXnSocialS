package team.hiaxn.hanhan.hiaxnsocial.Utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.xml.soap.Text;

public class HoverTextUtil {
    public static void sendHoverMessage(Player targetPlayer,TextComponent... textComponents) {
        targetPlayer.spigot().sendMessage(textComponents);
    }
    public static TextComponent BuildTextComponent(String message,String hoverText,String runCommand) {
        TextComponent textComponent = new TextComponent(message);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent(hoverText)}));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, runCommand));
        return textComponent;
    }
}
