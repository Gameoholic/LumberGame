package xyz.gameoholic.lumbergame.config;

public record GameConfig(String treeHealthExpression, String ironDropExpression, String goldMeterFillExpression,
                         String boneMealMeterFillExpression, int scoreboardPlayerLineMargin, int respawnCooldown, String fireStaffDPSExpression,
                         int fireStaffCooldown) {
}
