package xyz.gameoholic.lumbergame.config;

public record GameConfig(String treeHealthExpression, String ironDropExpression, String goldDropExpression,
                         String boneMealMeterFillExpression, int scoreboardPlayerLineMargin, int respawnCooldown) {
}
