package xyz.gameoholic.lumbergame.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    // todo: have a predicate function for this
    public static void removeItemFromInventory(Player player, Material type, int amount) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getType() == type) {
                item.add(-amount);
            }
        }
    }
}
