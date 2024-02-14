package xyz.gameoholic.lumbergame.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.List;

public class OtherUtil {
    public static void addLore(List<Component> dest, String lore, TagResolver... tagResolvers) {
        String[] lores = lore.split("<br>|<linebreak>|\n");
        for (String s : lores) {
            dest.add(MiniMessage.miniMessage().deserialize(s, tagResolvers)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE)
            );
        }
    }


    public static String intToRoman(int number)
    {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[number / 1000] + hundreds[(number % 1000) / 100] + tens[(number % 100) / 10] + units[number % 10];
    }

    /**
     * Gives the item velocity in the direction of the player for easier pickup. If not picked up after 10 ticks, forces it into the player's inventory.
     * @param item The spawned item.
     * @param player The player who is supposed to pick it up.
     */
    public static void pullItemToPlayer(LumberGamePlugin plugin, Item item, Player player) {
        item.setOwner(player.getUniqueId()); // Prevent stealing mob loot from players
        item.setPickupDelay(5);
        item.setVelocity(player.getLocation().subtract(item.getLocation()).toVector().normalize().multiply(0.5));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!item.isDead())
                    item.teleport(player.getLocation());
            }
        }.runTaskLater(plugin, 10L);

    }
}
