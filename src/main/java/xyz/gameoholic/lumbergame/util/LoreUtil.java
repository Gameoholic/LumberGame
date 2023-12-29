package xyz.gameoholic.lumbergame.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.List;

public class LoreUtil {
    public static void addLore(List<Component> dest, Component lore) {
        String[] lores = MiniMessage.miniMessage().serialize(lore).split("<br>|<linebreak>|\n");
        for (String s : lores) {
            dest.add(MiniMessage.miniMessage().deserialize(s)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE));
        }
    }
}
