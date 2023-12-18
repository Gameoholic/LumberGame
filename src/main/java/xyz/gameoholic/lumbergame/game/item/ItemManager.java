package xyz.gameoholic.lumbergame.game.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemManager {

    private LumberGamePlugin plugin;
    public ItemManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Utility method.
     * @param player
     * @param itemId
     * @return
     */
    public @Nullable ItemStack getItemInInventory(Player player, String itemId) {
        Bukkit.broadcastMessage("testing!");
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING) == itemId) {
                Bukkit.broadcastMessage("Returning bone block");
                return item;
            }
        }
        return null;
    }

    /**
     * Utility method.
     * @param item1
     * @param item2
     * @return
     */
    public boolean compareItems(@Nullable ItemStack item1, @Nullable ItemStack item2) {
        // In case of item.AIR or other faulty itemstack the meta will be null
        if (item1 == null || item2 == null || item1.getItemMeta() == null || item2.getItemMeta() == null)
            return false;
        @Nullable String item1Id = item1.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING);
        @Nullable String item2Id = item2.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING);
        return item1Id == item2Id;
    }
    public ItemStack getGoldItem() {
        return getItem(
            "gold",
            Material.GOLD_INGOT,
            plugin.getLumberConfig().strings().goldDisplayname(),
            plugin.getLumberConfig().strings().goldLore()
        );
    }

    public ItemStack getIronItem() {
        return getItem(
            "iron",
            Material.IRON_INGOT,
            plugin.getLumberConfig().strings().ironDisplayname(),
            plugin.getLumberConfig().strings().ironLore()
        );
    }
    public ItemStack getWoodItem() {
        return getItem(
            "wood",
            Material.OAK_WOOD,
            plugin.getLumberConfig().strings().woodDisplayname(),
            plugin.getLumberConfig().strings().woodLore()
        );
    }
    public ItemStack getBoneMealItem() {
        return getItem(
            "bone_meal",
            Material.BONE_MEAL,
            plugin.getLumberConfig().strings().boneMealDisplayname(),
            plugin.getLumberConfig().strings().boneMealLore()
        );
    }
    public ItemStack getBoneBlockItem() {
        return getItem(
            "bone_block",
            Material.BONE_BLOCK,
            plugin.getLumberConfig().strings().boneBlockDisplayname(),
            plugin.getLumberConfig().strings().boneBlockLore()
        );
    }
    private ItemStack getItem(String id, Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING, id);

        meta.displayName(MiniMessage.miniMessage().deserialize(displayName)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE));

        List<String> lores = Arrays.stream(lore.split("<br>|<linebreak>")).toList();
        List<Component> componentLores = new ArrayList<>();
        lores.forEach(tempLore -> componentLores.add(
            MiniMessage.miniMessage().deserialize(tempLore)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE)));
        meta.lore(componentLores);
        item.setItemMeta(meta);

        return item;
    }
}
