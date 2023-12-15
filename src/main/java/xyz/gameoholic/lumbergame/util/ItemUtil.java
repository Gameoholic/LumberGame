package xyz.gameoholic.lumbergame.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtil {
    public static ItemStack getIronItemStack(LumberGamePlugin plugin) {
        return getItemStack(
            Material.IRON_INGOT,
            plugin.getLumberConfig().strings().ironDisplayname(),
            plugin.getLumberConfig().strings().ironLore()
        );
    }
    public static ItemStack getGoldItemStack(LumberGamePlugin plugin) {
        return getItemStack(
            Material.GOLD_INGOT,
            plugin.getLumberConfig().strings().goldDisplayname(),
            plugin.getLumberConfig().strings().goldLore()
        );
    }
    public static ItemStack getDiamondItemStack(LumberGamePlugin plugin) {
        return getItemStack(
            Material.DIAMOND,
            plugin.getLumberConfig().strings().diamondDisplayname(),
            plugin.getLumberConfig().strings().diamondLore()
        );
    }

    /**
     * @return The item stack with the name and lore applied as MiniMessage components.
     */
    private static ItemStack getItemStack(Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize(displayName));

        List<String> lores = Arrays.stream(lore.split("<br>|<linebreak>")).toList();
        List<Component> componentLores = new ArrayList<>();
        lores.forEach(tempLore -> componentLores.add(MiniMessage.miniMessage().deserialize(tempLore)));
        meta.lore(componentLores);
        item.setItemMeta(meta);
        return item;
    }
}
