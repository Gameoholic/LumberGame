package xyz.gameoholic.lumbergame.game.mob.MobType;

import org.bukkit.entity.EntityType;


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
    Boolean isBaby
) {
}
