package xyz.gameoholic.lumbergame.game.mob;

import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;


public record MobType(
    String id,
    String displayName,
    EntityType entityType,
    boolean isHostile,
    String healthExpression,
    String damageExpression,
    String speedExpression,
    String knockbackExpression,
    String knockbackResistanceExpression,
    String attackCooldownExpression,
    Boolean isBaby,
    @Nullable String itemInMainHandID,
    @Nullable String itemInOffHandID,
    @Nullable String itemInHelmetID,
    @Nullable String itemInChestplateID,
    @Nullable String itemInLeggingsID,
    @Nullable String itemInBootsID,
    boolean hasMeleeAttackGoal,
    boolean canSpawnWithBoneMeal
) {
}
