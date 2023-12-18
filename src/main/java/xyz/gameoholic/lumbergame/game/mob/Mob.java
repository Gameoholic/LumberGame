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
import org.bukkit.entity.Ageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberMeleeAttackGoal;
import xyz.gameoholic.lumbergame.game.goal.hostile.LumberNearestAttackablePlayerGoal;
import xyz.gameoholic.lumbergame.game.mob.MobType.MobType;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;


import javax.annotation.Nullable;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class Mob {
    protected final LumberGamePlugin plugin;
    private final MobType mobType;
    private final int CR; // Challenge Rating

    protected org.bukkit.entity.Mob mob;
    private final boolean boneBlock;

    private Random rnd = new Random();

    /**
     * Use WaveManager to instantiate, don't use this constructor directly.
     *
     * @param mobType   The Lumber MobType of the mob.
     * @param CR        The challenge rating to spawn the mob with.
     * @param boneBlock Whether the mob should spawn with a bone block.
     */
    public Mob(LumberGamePlugin plugin, MobType mobType, int CR, boolean boneBlock) {
        this.plugin = plugin;
        this.mobType = mobType;
        this.CR = CR;
        this.boneBlock = boneBlock;
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
        //todo: armor & equipment here. Make sure to make it undroppable.


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
    }

    //todo: override in TreeMob.

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
        if (wrappedMeleeAttackGoal == null || wrappedNearestAttackableTargetGoal == null) {
            throw new RuntimeException("Mob doesn't have Vanilla attack and/or target goals!\n1: " +
                wrappedMeleeAttackGoal + "\n2: " + wrappedNearestAttackableTargetGoal);
        }

        // Remove unneeded Vanilla goals.
        NMSMob.goalSelector.removeGoal(wrappedMeleeAttackGoal.getGoal());
        NMSMob.targetSelector.removeGoal(wrappedNearestAttackableTargetGoal.getGoal());

        // Replace them with our goals, with the same exact priorities.
        // The lower the priority of the goal, the more it will be prioritized.
        NMSMob.targetSelector.addGoal(wrappedNearestAttackableTargetGoal.getPriority(),
            new LumberNearestAttackablePlayerGoal(NMSMob)); // Target and lock onto player
        NMSMob.goalSelector.addGoal(wrappedMeleeAttackGoal.getPriority(),
            new LumberMeleeAttackGoal((PathfinderMob) NMSMob, 1.0)); // Attack and follow player
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
