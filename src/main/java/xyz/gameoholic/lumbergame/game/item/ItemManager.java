package xyz.gameoholic.lumbergame.game.item;

import com.destroystokyo.paper.Namespaced;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

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
     *
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
            case "TNT" -> getTNTItem();
            case "ARROW" -> getArrowItem();
            case "WOODEN_SWORD" -> getWoodenSwordItem();
            case "STONE_SWORD" -> getStoneSwordItem();
            case "IRON_SWORD" -> getIronSwordItem();
            case "DIAMOND_SWORD" -> getDiamondSwordItem();
            case "LEATHER_BOOTS" -> getLeatherBootsItem();
            case "LEATHER_LEGGINGS" -> getLeatherLeggingsItem();
            case "LEATHER_CHESTPLATE" -> getLeatherChestplateItem();
            case "LEATHER_HELMET" -> getLeatherHelmetItem();
            case "IRON_BOOTS" -> getIronBootsItem();
            case "IRON_LEGGINGS" -> getIronLeggingsItem();
            case "IRON_CHESTPLATE" -> getIronChestplateItem();
            case "IRON_HELMET" -> getIronHelmetItem();
            case "HEALTH_POTION" -> getHealthPotionItem();
            case "STONE_AXE" -> getStoneAxeItem();
            case "MENU_WEAPONS" -> getMenuWeaponsItem();
            case "MENU_ARMOR" -> getMenuArmorItem();
            case "MENU_UTILITY" -> getMenuUtilityItem();
            case "MENU_PERKS" -> getMenuPerksItem();
            case "MENU_BACK" -> getMenuBackItem();
            case "REGEN_PERK" -> getRegenPerkItem();
            case "SPEED_PERK" -> getSpeedPerkItem();
            default -> null;
        };
    }

    public ItemStack getGoldItem() {
        return applyItemInformationLore(getItem(
            "GOLD",
            Material.GOLD_INGOT,
            plugin.getLumberConfig().strings().goldDisplayname(),
            plugin.getLumberConfig().strings().goldLore()
        ));
    }

    public ItemStack getIronItem() {
        return applyItemInformationLore(getItem(
            "IRON",
            Material.IRON_INGOT,
            plugin.getLumberConfig().strings().ironDisplayname(),
            plugin.getLumberConfig().strings().ironLore()
        ));
    }

    public ItemStack getWoodItem() {
        return applyItemInformationLore(getItem(
            "WOOD",
            Material.OAK_WOOD,
            plugin.getLumberConfig().strings().woodDisplayname(),
            plugin.getLumberConfig().strings().woodLore()
        ));
    }

    public ItemStack getBoneMealItem() {
        return applyItemInformationLore(applyPlaceableKeys(getItem(
            "BONE_MEAL",
            Material.BONE_MEAL,
            plugin.getLumberConfig().strings().boneMealDisplayname(),
            plugin.getLumberConfig().strings().boneMealLore()
        ), Arrays.asList(Material.GRASS_BLOCK)));
    }

    public ItemStack getBoneBlockItem() {
        return applyItemInformationLore(applyPlaceableKeys(getItem(
            "BONE_BLOCK",
            Material.BONE_BLOCK,
            plugin.getLumberConfig().strings().boneBlockDisplayname(),
            plugin.getLumberConfig().strings().boneBlockLore()
        ), Arrays.asList(Material.GRASS_BLOCK)));
    }

    public ItemStack getBowItem() {
        return applyItemInformationLore(getItem(
            "BOW",
            Material.BOW,
            plugin.getLumberConfig().strings().bowDisplayname(),
            plugin.getLumberConfig().strings().bowLore()
        ));
    }

    public ItemStack getStoneAxeItem() {
        return applyItemInformationLore(applyAttackDamage(applyDestroyableKeys(applyAttackSpeed(applyAttackDamage(getItem(
            "STONE_AXE",
            Material.STONE_AXE,
            plugin.getLumberConfig().strings().stoneAxeDisplayname(),
            plugin.getLumberConfig().strings().stoneAxeLore()
        ), 6), -3.2), plugin.getLumberConfig().mapConfig().treeBlockTypes()), 6));
    }

    public ItemStack getWoodenSwordItem() {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(
            "WOODEN_SWORD",
            Material.WOODEN_SWORD,
            plugin.getLumberConfig().strings().woodenSwordDisplayname(),
            plugin.getLumberConfig().strings().woodenSwordLore()
        ), 4), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public ItemStack getStoneSwordItem() {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(
            "STONE_SWORD",
            Material.STONE_SWORD,
            plugin.getLumberConfig().strings().stoneSwordDisplayname(),
            plugin.getLumberConfig().strings().stoneSwordLore()
        ), 8), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public ItemStack getIronSwordItem() {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(
            "IRON_SWORD",
            Material.IRON_SWORD,
            plugin.getLumberConfig().strings().ironSwordDisplayname(),
            plugin.getLumberConfig().strings().ironSwordLore()
        ), 10), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public ItemStack getDiamondSwordItem() {
        return applyItemInformationLore(applyEnchants(applyAttackSpeed(applyAttackDamage(getItem(
            "DIAMOND_SWORD",
            Material.DIAMOND_SWORD,
            plugin.getLumberConfig().strings().diamondSwordDisplayname(),
            plugin.getLumberConfig().strings().diamondSwordLore()
        ), 15), -2.4), Map.of(Enchantment.SWEEPING_EDGE, 1)));
    }

    public ItemStack getArrowItem() {
        return applyItemInformationLore(getItem(
            "ARROW",
            Material.ARROW,
            plugin.getLumberConfig().strings().arrowDisplayname(),
            plugin.getLumberConfig().strings().arrowLore()
        ));
    }

    public ItemStack getLeatherBootsItem() {
        return applyItemInformationLore(getItem(
            "LEATHER_BOOTS",
            Material.LEATHER_BOOTS,
            plugin.getLumberConfig().strings().leatherBootsDisplayname(),
            plugin.getLumberConfig().strings().leatherBootsLore()
        ));
    }

    public ItemStack getLeatherLeggingsItem() {
        return applyItemInformationLore(getItem(
            "LEATHER_LEGGINGS",
            Material.LEATHER_LEGGINGS,
            plugin.getLumberConfig().strings().leatherLeggingsDisplayname(),
            plugin.getLumberConfig().strings().leatherLeggingsLore()
        ));
    }

    public ItemStack getLeatherChestplateItem() {
        return applyItemInformationLore(getItem(
            "LEATHER_CHESTPLATE",
            Material.LEATHER_CHESTPLATE,
            plugin.getLumberConfig().strings().leatherChestplateDisplayname(),
            plugin.getLumberConfig().strings().leatherChestplateLore()
        ));
    }

    public ItemStack getLeatherHelmetItem() {
        return applyItemInformationLore(getItem(
            "LEATHER_HELMET",
            Material.LEATHER_HELMET,
            plugin.getLumberConfig().strings().leatherHelmetDisplayname(),
            plugin.getLumberConfig().strings().leatherHelmetLore()
        ));
    }

    public ItemStack getIronBootsItem() {
        return applyItemInformationLore(getItem(
            "IRON_BOOTS",
            Material.IRON_BOOTS,
            plugin.getLumberConfig().strings().ironBootsDisplayname(),
            plugin.getLumberConfig().strings().ironBootsLore()
        ));
    }

    public ItemStack getIronLeggingsItem() {
        return applyItemInformationLore(getItem(
            "IRON_LEGGINGS",
            Material.IRON_LEGGINGS,
            plugin.getLumberConfig().strings().ironLeggingsDisplayname(),
            plugin.getLumberConfig().strings().ironLeggingsLore()
        ));
    }

    public ItemStack getIronChestplateItem() {
        return applyItemInformationLore(getItem(
            "IRON_CHESTPLATE",
            Material.IRON_CHESTPLATE,
            plugin.getLumberConfig().strings().ironChestplateDisplayname(),
            plugin.getLumberConfig().strings().ironChestplateLore()
        ));
    }

    public ItemStack getIronHelmetItem() {
        return applyItemInformationLore(getItem(
            "IRON_HELMET",
            Material.IRON_HELMET,
            plugin.getLumberConfig().strings().ironHelmetDisplayname(),
            plugin.getLumberConfig().strings().ironHelmetLore()
        ));
    }

    public ItemStack getHealthPotionItem() {
        ItemStack item = getItem(
            "HEALTH_POTION",
            Material.SPLASH_POTION,
            plugin.getLumberConfig().strings().healthPotionDisplayname(),
            plugin.getLumberConfig().strings().healthPotionLore()
        );
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 0, 1), false);
        item.setItemMeta(meta);
        return applyItemInformationLore(item);
    }


    // ------------------------------------
    //              MOB EXCLUSIVE ITEMS
    // ------------------------------------
    public ItemStack getTNTItem() {
        return applyItemInformationLore(getItem(
            "TNT",
            Material.TNT,
            "",
            ""
        ));
    }

    // ------------------------------------
    //              MENU ITEMS
    // ------------------------------------
    public ItemStack getMenuWeaponsItem() {
        return applyItemInformationLore(getItem(
            "MENU_WEAPONS",
            Material.IRON_SWORD,
            "<gold>Weapons",
            "<gray><i>Purchase basic weapons."
        ));
    }
    public ItemStack getMenuArmorItem() {
        return applyItemInformationLore(getItem(
            "MENU_ARMOR",
            Material.DIAMOND_CHESTPLATE,
            "<aqua>Armor",
            "<gray><i>Purchase basic armor."
        ));
    }
    public ItemStack getMenuUtilityItem() {
        return applyItemInformationLore(getItem(
            "MENU_UTILITY",
            Material.GOLDEN_APPLE,
            "<red>Utility Items",
            "<gray><i>Purchase utility items."
        ));
    }
    public ItemStack getMenuPerksItem() {
        return applyItemInformationLore(getItem(
            "MENU_PERKS",
            Material.GOLDEN_APPLE,
            "<gold>Perks",
            "<gray><i>Purchase perks."
        ));
    }

    public ItemStack getMenuBackItem() {
        return applyItemInformationLore(getItem(
            "MENU_BACK",
            Material.ARROW,
            "<gray>Go back",
            "<gray><i>Go back to the main menu."
        ));
    }

    // ------------------------------------
    //              PERK MENU ITEMS
    // ------------------------------------

    public ItemStack getRegenPerkItem() {
        return applyItemInformationLore(getItem(
            "REGEN_PERK",
            Material.APPLE,
            "<red>Regeneration Perk",
            ""
        ));
    }
    public ItemStack getSpeedPerkItem() {
        return applyItemInformationLore(getItem(
            "SPEED_PERK",
            Material.FEATHER,
            "<yellow>Speed Perk",
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
    private ItemStack getItem(String id, Material material, String displayName, String lore) {
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

    private Component getPlainLore(Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.WHITE);
    }

    /**
     * Applies the item's attribute information to the lore (attack damage, enchants, etc.)
     *
     * @param item
     * @return The new item with the lore added
     */
    private ItemStack applyItemInformationLore(ItemStack item) {
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

    private ItemStack applyPlaceableKeys(ItemStack item, List<Material> placeableMaterials) {
        ItemMeta meta = item.getItemMeta();
        List<Namespaced> keys = new ArrayList<>();
        placeableMaterials.forEach(material -> keys.add(material.getKey()));
        meta.setPlaceableKeys(keys);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack applyDestroyableKeys(ItemStack item, List<Material> destroyableMaterials) {
        ItemMeta meta = item.getItemMeta();
        List<Namespaced> keys = new ArrayList<>();
        destroyableMaterials.forEach(material -> keys.add(material.getKey()));
        meta.setDestroyableKeys(keys);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack applyAttackDamage(ItemStack item, double damage) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "set_damage", damage - 1, AttributeModifier.Operation.ADD_NUMBER); // value needs to be lower by 1 for some reason
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack applyAttackSpeed(ItemStack item, double attackSpeed) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "set_attack_speed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
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
     *
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
