package xyz.gameoholic.lumbergame.game.mob;

import org.bukkit.entity.EntityType;

public record MobType(
    String id,
    String displayName,
    EntityType entityType,
    boolean isHostile,
    String healthExpression,
    String damageExpression,
    Double speed,
    int swellSpeed
) {
}
