package xyz.gameoholic.lumbergame.util;

import com.destroystokyo.paper.Namespaced;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class ItemUtil {

    /**
     * @return The first item stack that matches the given item ID in a player's inventory
     */
    public static @Nullable ItemStack getItemInInventory(LumberGamePlugin plugin, Player player, String itemId) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                    .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING) == itemId) {
                return item;
            }
        }
        return null;
    }

    public static int countItemsInInventory(LumberGamePlugin plugin, Player player, String itemId) {
        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getItemMeta().getPersistentDataContainer()
                    .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING).equals(itemId)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    /**
     * Removes a certain amount of items from the player's inventory, but only if the player has enough items of that type.
     *
     * @param player The player.
     * @param items Item amounts mapped to Lumber Item ID's.
     * @return Whether the items were removed or not.
     */
    public static boolean removeItemsFromInventory(LumberGamePlugin plugin, Player player, Map<String, Integer> items) {
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            if (countItemsInInventory(plugin, player, item.getKey()) < item.getValue())
                return false;
        }
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            int amountRemaining = item.getValue();
            for (ItemStack invItem : player.getInventory()) {
                if (invItem != null && invItem.getItemMeta().getPersistentDataContainer()
                        .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING).equals(item.getKey())) {
                    int amountRemoved = Math.min(invItem.getAmount(), amountRemaining);
                    invItem.setAmount(invItem.getAmount() - amountRemoved);
                    amountRemaining -= amountRemoved;
                }
            }
        }
        return true;
    }

    /**
     * @return Whether two items are the same Lumber item (matching ID's).
     */
    public static boolean compareItems(LumberGamePlugin plugin, @Nullable ItemStack item1, @Nullable ItemStack item2) {
        @Nullable String item1Id = getLumberItemId(plugin, item1);
        @Nullable String item2Id = getLumberItemId(plugin, item2);
        return item1Id != null && item2Id != null && item1Id.equals(item2Id);
    }
    /**
     * Checks if an item is a Lumber item.
     *
     * @return The lumber item ID if it is one, or null otherwise.
     */
    public static @Nullable String getLumberItemId(LumberGamePlugin plugin, @Nullable ItemStack item) {
        // In case of item.AIR or other faulty itemstack the meta will be null
        if (item == null || item.getItemMeta() == null)
            return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING);
    }

    /**
     * @return Whether the item provided matches the ID of a lumber item
     */
    public static boolean isLumberItem(LumberGamePlugin plugin, @Nullable ItemStack item, String lumberItemId) {
        @Nullable String itemId = ItemUtil.getLumberItemId(plugin, item);
        return itemId != null && itemId.equals(lumberItemId);
    }

    /**
     * @return The Lumber item for a given ID.
     */
    public static @Nullable ItemStack getItem(LumberGamePlugin plugin, String itemId) {
        return switch (itemId) {
            case "IRON" -> getIronItem(plugin);
            case "GOLD" -> getGoldItem(plugin);
            case "WOOD" -> getWoodItem(plugin);
            case "BONE_MEAL" -> getBoneMealItem(plugin);
            case "BONE_BLOCK" -> getBoneBlockItem(plugin);
            case "BOW" -> getBowItem(plugin);
            case "TNT" -> getTNTItem(plugin);
            case "ARROW" -> getArrowItem(plugin);
            case "WOODEN_SWORD" -> getWoodenSwordItem(plugin);
            case "STONE_SWORD" -> getStoneSwordItem(plugin);
            case "IRON_SWORD" -> getIronSwordItem(plugin);
            case "DIAMOND_SWORD" -> getDiamondSwordItem(plugin);
            case "LEATHER_BOOTS" -> getLeatherBootsItem(plugin);
            case "LEATHER_LEGGINGS" -> getLeatherLeggingsItem(plugin);
            case "LEATHER_CHESTPLATE" -> getLeatherChestplateItem(plugin);
            case "LEATHER_HELMET" -> getLeatherHelmetItem(plugin);
            case "IRON_BOOTS" -> getIronBootsItem(plugin);
            case "IRON_LEGGINGS" -> getIronLeggingsItem(plugin);
            case "IRON_CHESTPLATE" -> getIronChestplateItem(plugin);
            case "IRON_HELMET" -> getIronHelmetItem(plugin);
            case "DIAMOND_BOOTS" -> getDiamondBootsItem(plugin);
            case "DIAMOND_LEGGINGS" -> getDiamondLeggingsItem(plugin);
            case "DIAMOND_CHESTPLATE" -> getDiamondChestplateItem(plugin);
            case "DIAMOND_HELMET" -> getDiamondHelmetItem(plugin);
            case "HEALTH_POTION" -> getHealthPotionItem(plugin);
            case "STONE_AXE" -> getStoneAxeItem(plugin);
            case "MENU_WEAPONS" -> getMenuWeaponsItem(plugin);
            case "MENU_ARMOR" -> getMenuArmorItem(plugin);
            case "MENU_UTILITY" -> getMenuUtilityItem(plugin);
            case "MENU_PERKS" -> getMenuPerksItem(plugin);
            case "MENU_BACK" -> getMenuBackItem(plugin);
            case "REGEN_PERK" -> getRegenPerkItem(plugin);
            case "SPEED_PERK" -> getSpeedPerkItem(plugin);
            case "STRENGTH_PERK" -> getStrengthPerkItem(plugin);
            case "HEALTH_BOOST_PERK" -> getHealthBoostPerkItem(plugin);
            case "DOUBLE_JUMP_PERK" -> getDoubleJumpPerkItem(plugin);
            case "FIRE_STAFF" -> getFireStaffItem(plugin);
            default -> null;
        };
    }

    public static ItemStack getGoldItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "GOLD",
                Material.GOLD_INGOT,
                "<gold>Gold",
                "<i><gray>Rare drop from mobs.<br><i><gray>Use to buy <red>perks</red> from the shop."
        ));
    }

    public static ItemStack getIronItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "IRON",
                Material.IRON_INGOT,
                "<white>Iron",
                "<i><gray>Common drop from mobs."
        ));
    }

    public static ItemStack getWoodItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "WOOD",
                Material.OAK_WOOD,
                "<color:#ff8d03>Lumber",
                "<i><gray>Valuable item used for powerful crafts.<br><i><gray>Can be converted to <gold>gold</gold> in the shop."
        ));
    }

    public static ItemStack getBoneMealItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyPlaceableKeys(getItem(plugin,
                "BONE_MEAL",
                Material.BONE_MEAL,
                "<white>Bone Meal",
                "<i><gray>Place near the tree to regenerate its health.<br><i><gray>Regenerates <white>10%</white> of its health."
        ), Arrays.asList(Material.GRASS_BLOCK)));
    }

    public static ItemStack getBoneBlockItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyPlaceableKeys(getItem(plugin,
                "BONE_BLOCK",
                Material.BONE_BLOCK,
                "<white><bold>Bone Block",
                "<i><gray>Place near the tree to level it up.<br><i><gray>Levelling it up increases its Max HP."
        ), Arrays.asList(Material.GRASS_BLOCK)));
    }

    public static ItemStack getBowItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "BOW",
                Material.BOW,
                "Bow",
                "<i><gray>Surprisingly ineffective (don't use this item)!"
        ));
    }

    public static ItemStack getStoneAxeItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyDestroyableKeys(applyAttackSpeed(applyAttackDamage(getItem(plugin,
                "STONE_AXE",
                Material.STONE_AXE,
                "Stone Axe",
                "<i><gray>Use to chop the tree for lumber."
        ), 6), -3.2), plugin.getLumberConfig().mapConfig().treeBlockTypes()));
    }

    public static ItemStack getWoodenSwordItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(plugin,
                "WOODEN_SWORD",
                Material.WOODEN_SWORD,
                "Wooden Sword",
                "<i><gray>A pretty rusty sword."
        ), 4), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public static ItemStack getStoneSwordItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(plugin,
                "STONE_SWORD",
                Material.STONE_SWORD,
                "Stone Sword",
                "<i><gray>A slightly better starter sword."
        ), 8), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public static ItemStack getIronSwordItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(plugin,
                "IRON_SWORD",
                Material.IRON_SWORD,
                "Iron Sword",
                "<i><gray>A decent weapon fit to cut enemies into pieces."
        ), 10), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public static ItemStack getDiamondSwordItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(plugin,
                "DIAMOND_SWORD",
                Material.DIAMOND_SWORD,
                "Diamond Sword",
                "<i><gray>The best you can buy for your buck."
        ), 15), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public static ItemStack getArrowItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "ARROW",
                Material.ARROW,
                "Arrow",
                "<i><gray>Normal arrows."
        ));
    }

    public static ItemStack getLeatherBootsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "LEATHER_BOOTS",
                Material.LEATHER_BOOTS,
                "Leather Boots",
                "<blue>+1 Armor"
        ));
    }

    public static ItemStack getLeatherLeggingsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "LEATHER_LEGGINGS",
                Material.LEATHER_LEGGINGS,
                "Leather Pants",
                "<blue>+2 Armor"
        ));
    }

    public static ItemStack getLeatherChestplateItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "LEATHER_CHESTPLATE",
                Material.LEATHER_CHESTPLATE,
                "Leather Tunic",
                "<blue>+3 Armor"
        ));
    }

    public static ItemStack getLeatherHelmetItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "LEATHER_HELMET",
                Material.LEATHER_HELMET,
                "Leather Cap",
                "<blue>+1 Armor"
        ));
    }

    public static ItemStack getIronBootsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "IRON_BOOTS",
                Material.IRON_BOOTS,
                "Iron Boots",
                "<blue>+2 Armor"
        ));
    }

    public static ItemStack getIronLeggingsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "IRON_LEGGINGS",
                Material.IRON_LEGGINGS,
                "Iron Leggings",
                "<blue>+5 Armor"
        ));
    }

    public static ItemStack getIronChestplateItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "IRON_CHESTPLATE",
                Material.IRON_CHESTPLATE,
                "Iron Chestplate",
                "<blue>+6 Armor"
        ));
    }

    public static ItemStack getIronHelmetItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "IRON_HELMET",
                Material.IRON_HELMET,
                "Iron Helmet",
                "<blue>+2 Armor"
        ));
    }

    public static ItemStack getDiamondBootsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "DIAMOND_BOOTS",
                Material.DIAMOND_BOOTS,
                "Diamond Boots",
                "<blue>+3 Armor"
        ));
    }

    public static ItemStack getDiamondLeggingsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "DIAMOND_LEGGINGS",
                Material.DIAMOND_LEGGINGS,
                "Diamond Leggings",
                "<blue>+6 Armor"
        ));
    }

    public static ItemStack getDiamondChestplateItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "DIAMOND_CHESTPLATE",
                Material.DIAMOND_CHESTPLATE,
                "Diamond Chestplate",
                "<blue>+8 Armor"
        ));
    }

    public static ItemStack getDiamondHelmetItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "DIAMOND_HELMET",
                Material.DIAMOND_HELMET,
                "Diamond Helmet",
                "<blue>+3 Armor"
        ));
    }

    public static ItemStack getHealthPotionItem(LumberGamePlugin plugin) {
        ItemStack item = getItem(plugin,
                "HEALTH_POTION",
                Material.SPLASH_POTION,
                "Health Potion",
                "<i><gray>Heals back some health."
        );
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 0, 1), false);
        item.setItemMeta(meta);
        return applyItemInformationLore(item);
    }
    public static ItemStack getFireStaffItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "FIRE_STAFF",
                Material.BLAZE_ROD,
                "Fire Staff",
                "Click to launch a fire ring that damages enemies.<br><gray>Uses Fire Charges as ammo.<br>40 second cooldown.</gray><br><red><bold>DYNAMIC WEAPON</red></bold><br><gray>This weapon deals more damage with each wave.<br><red>Damage is 1.5 DPS per wave."
        ));
    }

    // ------------------------------------
    //              MOB EXCLUSIVE ITEMS
    // ------------------------------------
    public static ItemStack getTNTItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "TNT",
                Material.TNT,
                "",
                ""
        ));
    }

    // ------------------------------------
    //              MENU ITEMS
    // ------------------------------------
    public static ItemStack getMenuWeaponsItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "MENU_WEAPONS",
                Material.IRON_SWORD,
                "<gold>Weapons",
                "<gray><i>Purchase basic weapons."
        ));
    }

    public static ItemStack getMenuArmorItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "MENU_ARMOR",
                Material.DIAMOND_CHESTPLATE,
                "<aqua>Armor",
                "<gray><i>Purchase basic armor."
        ));
    }

    public static ItemStack getMenuUtilityItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "MENU_UTILITY",
                Material.GOLDEN_APPLE,
                "<red>Utility & Currencies",
                "<gray><i>Purchase utility items.<br><gray><i>Additionally, convert your currencies to gold."
        ));
    }

    public static ItemStack getMenuPerksItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "MENU_PERKS",
                Material.GOLDEN_APPLE,
                "<gold>Perks",
                "<gray><i>Purchase perks."
        ));
    }

    public static ItemStack getMenuBackItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "MENU_BACK",
                Material.ARROW,
                "<gray>Go back",
                "<gray><i>Go back to the main menu."
        ));
    }

    // ------------------------------------
    //              PERK MENU ITEMS
    // ------------------------------------

    public static ItemStack getRegenPerkItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "REGEN_PERK",
                Material.APPLE,
                "<red>Regeneration Perk",
                ""
        ));
    }

    public static ItemStack getSpeedPerkItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "SPEED_PERK",
                Material.FEATHER,
                "<yellow>Speed Perk",
                ""
        ));
    }

    public static ItemStack getStrengthPerkItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "STRENGTH_PERK",
                Material.GOLDEN_SWORD,
                "<dark_red>Strength Perk",
                ""
        ));
    }

    public static ItemStack getHealthBoostPerkItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "HEALTH_BOOST_PERK",
                Material.ENCHANTED_GOLDEN_APPLE,
                "<aqua>Health Boost Perk",
                ""
        ));
    }

    public static ItemStack getDoubleJumpPerkItem(LumberGamePlugin plugin) {
        return applyItemInformationLore(getItem(plugin,
                "DOUBLE_JUMP_PERK",
                Material.FEATHER,
                "<aqua>Double Jump Perk",
                ""
        ));
    }

    /**
     * @param id          The Lumber ID of this item.
     * @param material    Item's material.
     * @param displayName Item's displayname.
     * @param lore        Lore to be applied on the item.
     * @return An item stack with everything applied.
     */
    private static ItemStack getItem(LumberGamePlugin plugin, String id, Material material, String displayName, String lore) {
        ItemStack item = applyUnbreakable(new ItemStack(material));
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_item_id"), PersistentDataType.STRING, id);

        // Displayname
        meta.displayName(MiniMessage.miniMessage().deserialize(displayName)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE));

        // Lore
        List<String> lores = lore.equals("") ? new ArrayList() : // If lore is empty "", don't add lore
                Arrays.stream(lore.split("<br>|<linebreak>")).toList();
        List<Component> componentLores = new ArrayList<>();
        lores.forEach(tempLore -> componentLores.add(
                getPlainLore(MiniMessage.miniMessage().deserialize(tempLore))
        ));
        meta.lore(componentLores);

        // Item flags
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);

        item.setItemMeta(meta);
        return item;
    }

    private static Component getPlainLore(Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE);
    }

    /**
     * Applies the item's attribute information to the lore (attack damage, enchants, etc.)
     *
     * @param item
     * @return The new item with the lore added
     */
    private static ItemStack applyItemInformationLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        List<Component> lores = meta.lore();

        if (meta.hasAttributeModifiers()) {
            @Nullable AttributeModifier damageAttribute = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE)
                    .stream().filter(a -> a.getName() == "set_damage").findFirst().orElse(null);
            if (damageAttribute != null) {
                String damageAmount = (damageAttribute.getAmount() + 1) + ""; // Damage attribute always 1 lower than the actual damage
                if (damageAttribute.getAmount() == (int) damageAttribute.getAmount())
                    damageAmount = "" + (int) (damageAttribute.getAmount() + 1); // Display "4" instead of "4.0"
                lores.add(getPlainLore(
                        text(damageAmount + " Attack Damage")
                                .color(NamedTextColor.GOLD)
                ));
            }
        }
        meta.lore(lores);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack applyPlaceableKeys(ItemStack item, List<Material> placeableMaterials) {
        ItemMeta meta = item.getItemMeta();
        List<Namespaced> keys = new ArrayList<>();
        placeableMaterials.forEach(material -> keys.add(material.getKey()));
        meta.setPlaceableKeys(keys);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack applyDestroyableKeys(ItemStack item, List<Material> destroyableMaterials) {
        ItemMeta meta = item.getItemMeta();
        List<Namespaced> keys = new ArrayList<>();
        destroyableMaterials.forEach(material -> keys.add(material.getKey()));
        meta.setDestroyableKeys(keys);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack applyAttackDamage(ItemStack item, double damage) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "set_damage", damage - 1, AttributeModifier.Operation.ADD_NUMBER); // value needs to be lower by 1 for some reason
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack applyAttackSpeed(ItemStack item, double attackSpeed) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "set_attack_speed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack applyUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Applies enchantments to the ItemStack.
     *
     * @param item
     * @param enchants
     * @return New ItemStack with the enchantments applied.
     */
    private static ItemStack applyEnchants(ItemStack item, Map<Enchantment, Integer> enchants) {
        ItemMeta meta = item.getItemMeta();
        enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));

        item.setItemMeta(meta);
        return item;
    }
}
