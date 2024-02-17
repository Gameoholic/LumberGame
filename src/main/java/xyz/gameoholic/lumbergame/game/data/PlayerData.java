package xyz.gameoholic.lumbergame.game.data;

import java.util.UUID;

/**
 * Represents player data.
 * @param uuid The UUID of the player.
 * @param maxWaveReached Max 0-th index wave reached.
 */
public record PlayerData(UUID uuid, int wins, int losses, int wavesCompleted, int kills, int deaths,
                         int ironCollected, int goldCollected, int woodCollected, int maxWaveReached) {

    /**
     *
     * @param uuid The UUID of the player.
     * @return Default (empty) PlayerData
     */
    public static PlayerData getDefault(UUID uuid) {
        return new PlayerData(uuid, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
