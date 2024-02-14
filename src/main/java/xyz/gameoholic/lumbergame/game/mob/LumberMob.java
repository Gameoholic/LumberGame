package xyz.gameoholic.lumbergame.game.mob;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberCreeperAttackGoal;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberMeleeAttackGoal;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberNearestAttackablePlayerGoal;
import xyz.gameoholic.lumbergame.game.goal.hostile.SkeletonTNTAttackGoal;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;


import javax.annotation.Nullable;
import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static xyz.gameoholic.lumbergame.util.OtherUtil.pullItemToPlayer;

public class LumberMob implements Listener {
    protected final LumberGamePlugin plugin;
    private final MobType mobType;
    private final int CR; // Challenge Rating

    protected Mob mob;
    private final boolean boneBlock;

    private Random rnd = new Random();

    /**
     * Use WaveManager to instantiate, don't use this constructor directly.
     *
     * @param mobType   The Lumber MobType of the mob.
     * @param CR        The challenge rating to spawn the mob with.
     * @param boneBlock Whether the mob should spawn with a bone block.
     */
    public LumberMob(LumberGamePlugin plugin, MobType mobType, int CR, boolean boneBlock) {
        this.plugin = plugin;
        this.mobType = mobType;
        this.CR = CR;
        this.boneBlock = boneBlock;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    public void unregisterEvents() {
        EntityDamageEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);
        ExplosionPrimeEvent.getHandlerList().unregister(this);
    }

    /**
     * Spawns the mob.
     *
     * @param location The location to spawn the mob at
     */
    public void spawnMob(Location location) {
        mob = (org.bukkit.entity.Mob) location.getWorld().spawnEntity(location, mobType.entityType(), false);

        // General attributes for all mobs
        mob.setCanPickupItems(false);
        int health = (int) Math.min(2000, new ExpressionBuilder(mobType.healthExpression())
            .variables("CR")
            .build()
            .setVariable("CR", CR).evaluate()); // Health cannot be above 2,048 in MC
        mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(Double.MAX_VALUE);
        mob.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN, true);

        // Required parameter - health-expression
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        mob.setHealth(health);

        // Required parameter - damage-expression
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
            ExpressionUtil.evaluateExpression(mobType.damageExpression(), Map.of("CR", (double) CR))
        );

        // Optional parameter - speed-expression
        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
            ExpressionUtil.evaluateExpression(mobType.speedExpression(), Map.of("CR", (double) CR))
        );

        // Optional parameter - knockback-expression
        mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(
            ExpressionUtil.evaluateExpression(mobType.knockbackExpression(), Map.of("CR", (double) CR))
        );

        // Optional parameter - knockback-resistance-expression
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(
            ExpressionUtil.evaluateExpression(mobType.knockbackResistanceExpression(), Map.of("CR", (double) CR))
        );

        // Optional parameter - is-baby
        if (mob instanceof Ageable ageableMob) {
            if (mobType.isBaby())
                ageableMob.setBaby();
        }

        // Make undeads not burn in daylight
        mob.getEquipment().setItem(EquipmentSlot.HEAD, new ItemStack(Material.OAK_BUTTON, 1));

        // Optional parameters - equipment
        if (mobType.itemInMainHandID() != null)
            mob.getEquipment().setItemInMainHand(plugin.getItemManager().getItem(mobType.itemInMainHandID()));
        if (mobType.itemInOffHandID() != null)
            mob.getEquipment().setItemInOffHand(plugin.getItemManager().getItem(mobType.itemInOffHandID()));
        if (mobType.itemInHelmetID() != null)
            mob.getEquipment().setItem(EquipmentSlot.HEAD, plugin.getItemManager().getItem(mobType.itemInHelmetID()), true);
        if (mobType.itemInChestplateID() != null)
            mob.getEquipment().setItem(EquipmentSlot.CHEST, plugin.getItemManager().getItem(mobType.itemInChestplateID()), true);
        if (mobType.itemInLeggingsID() != null)
            mob.getEquipment().setItem(EquipmentSlot.LEGS, plugin.getItemManager().getItem(mobType.itemInLeggingsID()), true);
        if (mobType.itemInBootsID() != null)
            mob.getEquipment().setItem(EquipmentSlot.FEET, plugin.getItemManager().getItem(mobType.itemInBootsID()), true);

        // Mob's custom name to be applied after parameters
        mob.setCustomNameVisible(true);
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text((int) mob.getHealth())),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));

        // Post-spawn parameters/attributes (bone block / bone meal)
        if (fillBoneMealMeter())
            mob.getEquipment().setItemInMainHand(plugin.getItemManager().getBoneMealItem());
        if (boneBlock) {
            mob.getEquipment().setHelmet(plugin.getItemManager().getBoneBlockItem());
        }

        applyGoals();

        plugin.getGameManager().getWaveManager().onMobSpawn(this);
        registerEvents();
    }


    /**
     * Applies NMS Goals for the mob to target players regardless of distance, etc.
     */
    protected void applyGoals() {
        net.minecraft.world.entity.Mob NMSMob = ((CraftMob) mob).getHandle();

        // Find Vanilla goals needed to be replaced by our custom ones.
        @Nullable WrappedGoal wrappedMeleeAttackGoal = NMSMob.goalSelector.getAvailableGoals().stream()
            .filter(goal -> goal.getGoal() instanceof MeleeAttackGoal).findFirst().orElse(null);
        @Nullable WrappedGoal wrappedNearestAttackableTargetGoal = NMSMob.targetSelector.getAvailableGoals().stream()
            .filter(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal).findFirst().orElse(null);

        // If goals exist, get their priority. Otherwise, provide default value 2.
        int nearestAttackableTargetGoalPriority = wrappedNearestAttackableTargetGoal != null ?
            wrappedNearestAttackableTargetGoal.getPriority() : 2;
        int meleeAttackGoalPriority = wrappedMeleeAttackGoal != null ?
            wrappedNearestAttackableTargetGoal.getPriority() : 2;

        // Remove unneeded Vanilla goals, if the mob has them
        if (wrappedMeleeAttackGoal != null) {
//            if (mobType.hasMeleeAttackGoal())  // If mob has (should have) melee attack goal (not in cases like bowed skeletons)
            NMSMob.goalSelector.removeGoal(wrappedMeleeAttackGoal.getGoal());
        }
        if (wrappedNearestAttackableTargetGoal != null) {
            NMSMob.targetSelector.removeGoal(wrappedNearestAttackableTargetGoal.getGoal());
        }

        // Attack speed is needed for goal, can't be added via mob attribute.
        int attackCooldown = (int) ExpressionUtil.evaluateExpression(
            mobType.attackCooldownExpression(), Map.of("CR", (double) CR));


        // Replace them with our goals, with the same exact priorities.
        // The lower the priority of the goal, the more it will be prioritized.
        NMSMob.targetSelector.addGoal(nearestAttackableTargetGoalPriority,
            new LumberNearestAttackablePlayerGoal(NMSMob)); // Target and lock onto player

        // Specific mob goals - Ranged Bomber (TNT launching)
        if (NMSMob instanceof net.minecraft.world.entity.monster.AbstractSkeleton &&
            mob.getEquipment().getItemInMainHand().getType() == Material.TNT) {
            NMSMob.goalSelector.addGoal(4, new SkeletonTNTAttackGoal(
                plugin, (net.minecraft.world.entity.monster.AbstractSkeleton) NMSMob, attackCooldown,
                mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()));
            return;
        }
        // Specific mob goals - Bomber (Hostile creeper)
        if (NMSMob instanceof net.minecraft.world.entity.monster.Creeper) {
            NMSMob.goalSelector.addGoal(meleeAttackGoalPriority,
                new LumberCreeperAttackGoal((PathfinderMob) NMSMob, attackCooldown) // Attack and follow player
            );
            // Remove vanilla Swell goal, as we have our own logic in LumberCreeperAttackGoal
            @Nullable WrappedGoal wrappedSwellGoal = NMSMob.targetSelector.getAvailableGoals().stream()
                .filter(goal -> goal.getGoal() instanceof SwellGoal).findFirst().orElse(null);
            if (wrappedSwellGoal != null)
                NMSMob.targetSelector.removeGoal(wrappedSwellGoal.getGoal());
            return;
        }

        if (mobType.hasMeleeAttackGoal()) // If mob has (should have) melee attack goal (not in cases like bowed skeletons)
            NMSMob.goalSelector.addGoal(meleeAttackGoalPriority,
                new LumberMeleeAttackGoal((PathfinderMob) NMSMob, attackCooldown) // Attack and follow player
            );
    }

    /**
     * Fills up the bone meal meter. If reaches 100, the mob should spawn holding bone meal.
     * @return Whether the mob should hold bone meal or not.
     */
    private boolean fillBoneMealMeter() {
        plugin.getGameManager().increaseBoneMealMeter(CR);
        if (!mobType.canSpawnWithBoneMeal()) // If mob can't spawn with bone meal, skip this mob but don't reset the meter
            return false;
        if (plugin.getGameManager().getBoneMealMeter() >= 100) {
            plugin.getGameManager().resetBoneMealMeter();
            return true;
        }
        return false;
    }


    /**
     * Should be called when the mob takes damage.
     */
    public void onTakeDamage(double damageDealt) {
        updateMobCustomName(mob.getHealth() - damageDealt);
    }

    /**
     * Should be called when the mob dies.
     *
     * @param player The player that killed the mob, or null if it died otherwise.
     */
    public void onDeath(@Nullable Player player) {
        if (plugin.getGameManager() == null) // If game has ended by the time this was fired (if a creeper killed the tree, and has also died)
            return;
        plugin.getGameManager().getWaveManager().onMobDeath(this);
        unregisterEvents();

        if (player != null) // Only drop loot if player killed mob
            for (ItemStack itemStack : getDrops()) {
                Item item = mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), itemStack);
                pullItemToPlayer(plugin, item, player); // Item magnetism: Give velocity towards player for easier pickup
            }

        // If mob is skeleton and was holding TNT (aka Ranged Bomber), spawn TNT on death
        if (mob instanceof AbstractSkeleton && mob.getEquipment().getItemInMainHand().getType() == Material.TNT) {
            TNTPrimed tnt = (TNTPrimed) mob.getLocation().getWorld().spawnEntity(mob.getLocation(), EntityType.PRIMED_TNT);
            tnt.getPersistentDataContainer().set(new NamespacedKey(plugin, "tnt_damage"),
                PersistentDataType.DOUBLE, mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()); // Set custom tnt damage
        }

        // If mob was holding bone meal
        if (plugin.getItemManager().compareItems(
            mob.getEquipment().getItemInMainHand(),
            plugin.getItemManager().getBoneMealItem()
        )) {
            Item item = mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), mob.getEquipment().getItemInMainHand());
            if (player != null) {  // Only pull item towards player, if mob was killed by a player and not on its own
                pullItemToPlayer(
                    plugin,
                    item,
                    player
                );
            }
        }
        // If mob had bone block on head
        if (plugin.getItemManager().compareItems(
            mob.getEquipment().getHelmet(),
            plugin.getItemManager().getBoneBlockItem()
        )) {
            Item item = mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), plugin.getItemManager().getBoneBlockItem());
            if (player != null) // Only pull item towards player, if mob was killed by a player and not on its own
                pullItemToPlayer(
                    plugin,
                    item,
                    player
                );
        }
    }

    /**
     * @return List of drops for the dead mob.
     */
    private List<ItemStack> getDrops() {
        List<ItemStack> items = new ArrayList<>();
        double ironChance = ExpressionUtil.evaluateExpression(
            plugin.getLumberConfig().gameConfig().ironDropExpression(),
            Map.of("CR", (double) CR)
        );
        for (int i = 0; i < getSpecificDropAmount(ironChance); i++) {
            items.add(plugin.getItemManager().getIronItem());
        }
        if (fillGoldMeter())
            items.add(plugin.getItemManager().getGoldItem());

        return items;
    }

    /**
     * Fills up the gold meter. If reaches 100, the mob should drop 1 gold.
     * @return Whether the mob should drop gold or not.
     */
    private boolean fillGoldMeter() {
        plugin.getGameManager().increaseGoldMeter(CR);
        if (plugin.getGameManager().getGoldMeter() >= 100) {
            plugin.getGameManager().resetGoldMeter();
            return true;
        }
        return false;
    }

    /**
     * Removes this mob and unregisters its events.
     */
    public void remove() {
        unregisterEvents();
        mob.remove();
    }

    /**
     * Generates a random amount of items to drop.
     *
     * @param chance The (%) chance that an item will be dropped. If above 100(%), a drop would be guaranteed and the rest will be used for rolling again for extras.
     * @return The amount of items to drop.
     */
    private int getSpecificDropAmount(double chance) {
        int dropAmount = 0;
        while (chance > 0) {
            double random = rnd.nextDouble(100) + 1; //1-100
            if (random <= chance)
                dropAmount++;
            chance -= 100;
        }
        return dropAmount;
    }

    private void updateMobCustomName(double newHealth) {
        int newHealthAdjusted = (int) Math.max(newHealth, 1);
        if (newHealth <= 0)
            newHealthAdjusted = 0;
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text(newHealthAdjusted)),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));
    }
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        if (e instanceof EntityDamageByEntityEvent byEntityEvent) {
            // Creeper explosion damage should match the explosion's damage attribute, otherwise vanilla explosion damage is applied
            if (byEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof Creeper creeper) {
                e.setDamage((e.getDamage() / 43.0) * creeper.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()); // After testing, 43.0 is max damage of a default creeper. We let it do all the calculating for us, and get the % of the maxdamage done, then multiply it by the damage we want.
            }
            // TNT explosion damage should match the mob's damage attribute, otherwise vanilla explosion damage is applied
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof TNTPrimed tnt) {
                @Nullable Double tntDamage = tnt.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "tnt_damage"), PersistentDataType.DOUBLE);
                if (tntDamage == null) // If wasn't launched by LumberMob
                    return;
                e.setDamage((e.getDamage() / 56.0) * tntDamage); // After testing, 56.0 is max damage of default TNT. We let it do all the calculating for us, and get the % of the maxdamage done, then multiply it by the damage we want.
            }
            // Arrow damage should match the skeleton's damage attribute, otherwise vanilla arrow damage is applied
            if (byEntityEvent.getDamager() instanceof Arrow) {
                @Nullable Double arrowDamage = byEntityEvent.getDamager().getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "arrow_damage"), PersistentDataType.DOUBLE);
                if (arrowDamage == null) // If wasn't shot by LumberMob
                    return;
                e.setDamage(arrowDamage);
            }
        }

        onTakeDamage(e.getFinalDamage());
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        @Nullable Player killerPlayer = null;
        // Mobs should only drop loot in case a player killed it
        if (e.getEntity().getLastDamageCause() != null &&
            e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            if (damageEvent.getDamager() instanceof Player player)
                killerPlayer = player;
        }

        e.setDroppedExp(0);
        e.getDrops().clear();
        onDeath(killerPlayer);
    }

    @EventHandler
    public void onExplosionPrimeEvent(ExplosionPrimeEvent e) {
        if (plugin.getGameManager() == null) // In certain edge cases this event might be fired after the game has ended and before the event was cancelled. //todo: shitty fix. if this fixes it then just keep it
            return;
        // Creeper does not go through normal death logic cycle when it explodes, and neither does tnt, so we handle in this event
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        // If creeper is tree mob, and it exploded, we can assume it was near the tree and should deal damage to it
        if (this instanceof TreeLumberMob)
            plugin.getGameManager().getTreeManager().onMobDamage(this);
        onDeath(null);
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent e) {
        if (plugin.getGameManager().getWaveManager() == null)
            return;
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        e.getProjectile().getPersistentDataContainer().set(
            new NamespacedKey(plugin, "arrow_damage"),
            PersistentDataType.DOUBLE,
            mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()
        );
    }

    public MobType getMobType() {
        return mobType;
    }

    public int getCR() {
        return CR;
    }

    public org.bukkit.entity.Mob getMob() {
        return mob;
    }


}
