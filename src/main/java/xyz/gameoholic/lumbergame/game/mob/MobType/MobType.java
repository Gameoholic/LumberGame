package xyz.gameoholic.lumbergame.game.mob.MobType;

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
    Boolean isBaby,
    @Nullable String itemInMainHandID,
    @Nullable String itemInOffHandID,
    @Nullable String itemInHelmetID,
    @Nullable String itemInChestplateID,
    @Nullable String itemInLeggingsID,
    @Nullable String itemInBootsID
) {
}
