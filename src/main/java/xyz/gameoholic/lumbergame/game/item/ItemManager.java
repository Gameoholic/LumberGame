package xyz.gameoholic.lumbergame.game.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.*;

public class ItemManager {

    private LumberGamePlugin plugin;

    public ItemManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Utility method.
     *
     * @param player
     * @param itemId
     * @return
     */
    public @Nullable ItemStack getItemInInventory(Player player, String itemId) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING) == itemId) {
                return item;
            }
        }
        return null;
    }

    public int countItemsInInventory(Player player, String itemId) {
        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING) == itemId) {
                count += item.getAmount();
            }
        }
        return count;
    }

    /**
     * Removes a certain amount of items from the player's inventory, but only if the player has enough items of that type.
     * @param player The player.
     * @param itemId The ID of the Lumber item.
     * @param amount The amount of items to remove.
     * @return Whether the items were removed or not.
     */
    public boolean removeItemsFromInventory(Player player, String itemId, int amount) {
        if (countItemsInInventory(player, itemId) < amount)
            return false;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING) == itemId) {
                int amountRemoved = Math.min(item.getAmount(), amount);
                item.setAmount(item.getAmount() - amountRemoved);
                amount -= amountRemoved;
            }
        }
        return true;
    }

    /**
     * Utility method.
     *
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

    /**
     * Utility method.
     *
     * @param itemId
     * @return
     */
    public @Nullable ItemStack getItem(String itemId) {
        return switch (itemId) {
            case "IRON" -> getIronItem();
            case "GOLD" -> getGoldItem();
            case "WOOD" -> getWoodItem();
            case "BONE_MEAL" -> getBoneMealItem();
            case "BONE_BLOCK" -> getBoneBlockItem();
            case "BOW" -> getBowItem();
            case "ARROW" -> getArrowItem();
            case "WOODEN_SWORD" -> getWoodenSwordItem();
            case "STONE_SWORD" -> getStoneSwordItem();
            case "IRON_SWORD" -> getIronSwordItem();
            case "DIAMOND_SWORD" -> getDiamondSwordItem();
            default -> null;
        };
    }

    public ItemStack getGoldItem() {
        return getItem(
            "GOLD",
            Material.GOLD_INGOT,
            plugin.getLumberConfig().strings().goldDisplayname(),
            plugin.getLumberConfig().strings().goldLore()
        );
    }

    public ItemStack getIronItem() {
        return getItem(
            "IRON",
            Material.IRON_INGOT,
            plugin.getLumberConfig().strings().ironDisplayname(),
            plugin.getLumberConfig().strings().ironLore()
        );
    }

    public ItemStack getWoodItem() {
        return getItem(
            "WOOD",
            Material.OAK_WOOD,
            plugin.getLumberConfig().strings().woodDisplayname(),
            plugin.getLumberConfig().strings().woodLore()
        );
    }

    public ItemStack getBoneMealItem() {
        return getItem(
            "BONE_MEAL",
            Material.BONE_MEAL,
            plugin.getLumberConfig().strings().boneMealDisplayname(),
            plugin.getLumberConfig().strings().boneMealLore()
        );
    }

    public ItemStack getBoneBlockItem() {
        return getItem(
            "BONE_BLOCK",
            Material.BONE_BLOCK,
            plugin.getLumberConfig().strings().boneBlockDisplayname(),
            plugin.getLumberConfig().strings().boneBlockLore()
        );
    }

    public ItemStack getBowItem() {
        return getItem(
            "BOW",
            Material.BOW,
            plugin.getLumberConfig().strings().bowDisplayname(),
            plugin.getLumberConfig().strings().bowLore()
        );
    }

    public ItemStack getWoodenSwordItem() {
        return applyUnbreakable(applyAttackDamage(getItem(
            "WOODEN_SWORD",
            Material.WOODEN_SWORD,
            plugin.getLumberConfig().strings().woodenSwordDisplayname(),
            plugin.getLumberConfig().strings().woodenSwordLore()
        ), 4));
    }
    public ItemStack getWoodenAxeItem() {
        return applyUnbreakable(applyAttackDamage(getItem(
            "WOODEN_AXE",
            Material.WOODEN_AXE,
            plugin.getLumberConfig().strings().woodenAxeDisplayname(),
            plugin.getLumberConfig().strings().woodenAxeLore()
        ), 6));
    }

    public ItemStack getStoneSwordItem() {
        return applyAttackDamage(getItem(
            "STONE_SWORD",
            Material.STONE_SWORD,
            plugin.getLumberConfig().strings().stoneSwordDisplayname(),
            plugin.getLumberConfig().strings().stoneSwordLore()
        ), 7);
    }

    public ItemStack getIronSwordItem() {
        return applyAttackDamage(getItem(
            "IRON_SWORD",
            Material.IRON_SWORD,
            plugin.getLumberConfig().strings().ironSwordDisplayname(),
            plugin.getLumberConfig().strings().ironSwordLore()
        ), 10);
    }

    public ItemStack getDiamondSwordItem() {
        return applyAttackDamage(getItem(
            "DIAMOND_SWORD",
            Material.DIAMOND_SWORD,
            plugin.getLumberConfig().strings().diamondSwordDisplayname(),
            plugin.getLumberConfig().strings().diamondSwordLore()
        ), 15);
    }

    public ItemStack getArrowItem() {
        return getItem(
            "ARROW",
            Material.ARROW,
            plugin.getLumberConfig().strings().arrowDisplayname(),
            plugin.getLumberConfig().strings().arrowLore()
        );
    }

    /**
     * @param id The Lumber ID of this item.
     * @param material Item's material.
     * @param displayName Item's displayname.
     * @param lore Lore to be applied on the item.
     * @return An item stack with everything applied.
     */
    private ItemStack getItem(String id, Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING, id);

        // Displayname
        meta.displayName(MiniMessage.miniMessage().deserialize(displayName)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE));

        // Lore
        List<String> lores = Arrays.stream(lore.split("<br>|<linebreak>")).toList();
        List<Component> componentLores = new ArrayList<>();
        lores.forEach(tempLore -> componentLores.add(
            MiniMessage.miniMessage().deserialize(tempLore)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE)));
        meta.lore(componentLores);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack applyAttackDamage(ItemStack item, int damage) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "set_damage", damage - 1, AttributeModifier.Operation.ADD_NUMBER); // value needs to be lower by 1 for some reason
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack applyUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Applies enchantments to the ItemStack.
     * @param item
     * @param enchants
     * @return New ItemStack with the enchantments applied.
     */
    private ItemStack applyEnchants(ItemStack item, Map<Enchantment, Integer> enchants) {
        ItemMeta meta = item.getItemMeta();
        enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));

        item.setItemMeta(meta);
        return item;
    }
}
