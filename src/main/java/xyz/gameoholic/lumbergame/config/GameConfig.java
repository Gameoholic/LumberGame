package xyz.gameoholic.lumbergame.config;

public record GameConfig(String treeHealthExpression, String ironDropExpression, String goldDropExpression,
                         String boneMealSpawnExpression, int scoreboardPlayerLineMargin, int respawnCooldown) {
}
