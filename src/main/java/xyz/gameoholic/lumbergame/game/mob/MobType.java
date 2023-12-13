package xyz.gameoholic.lumbergame.game.mob;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record MobType(
    @NotNull String id,
    @NotNull String displayName,
    @NotNull EntityType entityType,
    boolean isHostile,
    @NotNull String healthExpression,
    @NotNull String playerDamageExpression,
    @NotNull String treeDamageExpression
) {
}
