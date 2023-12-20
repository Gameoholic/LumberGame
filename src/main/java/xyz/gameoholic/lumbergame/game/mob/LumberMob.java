package xyz.gameoholic.lumbergame.game.mob;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberMeleeAttackGoal;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberNearestAttackablePlayerGoal;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;


import javax.annotation.Nullable;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

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
    private void unregisterEvents() {
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
        if (shouldHoldBoneMeal())
            mob.getEquipment().setItemInMainHand(plugin.getItemManager().getBoneMealItem());

        // todo: bone block should only be placed on entity that can have item on its helmet. not like creeper for example.
        // todo: also, it should not just be the first entity, rather a randm one otherwise guaranteed mob fgets it
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

        if (wrappedNearestAttackableTargetGoal == null)
            throw new RuntimeException("Mob doesn't have Vanilla target goal!");
        if (wrappedMeleeAttackGoal == null && mobType.hasMeleeAttackGoal())
            throw new RuntimeException("Mob doesn't have Vanilla attack goal, and wasn't marked for no melee attack goal required!");

        // Remove unneeded Vanilla goals.
        if (mobType.hasMeleeAttackGoal())
            NMSMob.goalSelector.removeGoal(wrappedMeleeAttackGoal.getGoal());
        NMSMob.targetSelector.removeGoal(wrappedNearestAttackableTargetGoal.getGoal());

        // Attack speed is needed for goal, can't be added via mob attribute.
        int attackCooldown = (int) ExpressionUtil.evaluateExpression(
            mobType.attackCooldownExpression(), Map.of("CR", (double) CR));

        // Replace them with our goals, with the same exact priorities.
        // The lower the priority of the goal, the more it will be prioritized.
        NMSMob.targetSelector.addGoal(wrappedNearestAttackableTargetGoal.getPriority(),
            new LumberNearestAttackablePlayerGoal(NMSMob)); // Target and lock onto player
        if (mobType.hasMeleeAttackGoal())
            NMSMob.goalSelector.addGoal(wrappedMeleeAttackGoal.getPriority(),
                new LumberMeleeAttackGoal((PathfinderMob) NMSMob, 1.0, attackCooldown) // Attack and follow player
            );
    }

    /**
     * Randomly determines if the mob should be holding a bone meal.
     */
    private boolean shouldHoldBoneMeal() {
        int random = rnd.nextInt(99) + 1; // Gen num 1-100
        // If smaller than random evaluation, return true


        return random <=
            ExpressionUtil.evaluateExpression(
                plugin.getLumberConfig().gameConfig().boneMealSpawnExpression(),
                Map.of("CR", (double) CR)
            );
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
     * @param dropLoot Whether the mob should drop its loot, should only be true if a player killed the mob.
     */
    public void onDeath(Boolean dropLoot) {
        plugin.getGameManager().getWaveManager().onMobDeath(this);

        if (!dropLoot)
            return;
        for (ItemStack itemStack : getDrops()) {
            mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), itemStack);
        }

        // If mob was holding bone meal
        if (plugin.getItemManager().compareItems(
            mob.getEquipment().getItemInMainHand(),
            plugin.getItemManager().getBoneMealItem()
        ))
            mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), mob.getEquipment().getItemInMainHand());
        // If mob had bone block on head
        if (plugin.getItemManager().compareItems(
            mob.getEquipment().getHelmet(),
            plugin.getItemManager().getBoneBlockItem()
        ))
            mob.getLocation().getWorld().dropItemNaturally(mob.getLocation(), plugin.getItemManager().getBoneBlockItem());
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
        double goldChance = ExpressionUtil.evaluateExpression(
            plugin.getLumberConfig().gameConfig().goldDropExpression(),
            Map.of("CR", (double) CR)
        );

        for (int i = 0; i < getSpecificDropAmount(ironChance); i++) {
            items.add(plugin.getItemManager().getIronItem());
        }
        for (int i = 0; i < getSpecificDropAmount(goldChance); i++) {
            items.add(plugin.getItemManager().getGoldItem());
        }

        return items;
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
            if (byEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof Creeper) {
                e.setDamage(((Creeper) byEntityEvent.getDamager()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
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
        if (plugin.getGameManager().getWaveManager() == null)
            return;
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        // Mobs should only drop loot in case a player killed it
        boolean dropLoot = false;
        if (e.getEntity().getLastDamageCause() != null &&
            e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            if (damageEvent.getDamager().getType() == EntityType.PLAYER)
                dropLoot = true;
        }

        e.setDroppedExp(0);
        e.getDrops().clear();
        onDeath(dropLoot);
    }

    @EventHandler
    public void onExplosionPrimeEvent(ExplosionPrimeEvent e) {
        // Creeper does not go through normal death logic cycle when it explodes, and neither does tnt, so we handle in this event
        if (plugin.getGameManager().getWaveManager() == null)
            return;
        if (plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId()) != this)
            return;

        // If creeper is tree mob, and it exploded, we can assume it was near the tree and should deal damage to it
        if (this instanceof TreeLumberMob)
            plugin.getGameManager().getTreeManager().onMobDamage(this);
        onDeath(false);
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