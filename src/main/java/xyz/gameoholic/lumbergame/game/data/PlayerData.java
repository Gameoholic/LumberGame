package xyz.gameoholic.lumbergame.game.data;

import java.util.UUID;

/**
 * Represents mutable player data.
 */
public class PlayerData {

    private UUID uuid;
    private int wins; //todo: unused stat
    private int losses;
    private int wavesCompleted;
    private int kills;
    private int deaths;
    private int ironCollected;
    private int goldCollected;
    private int woodCollected;
    private int maxWaveCompleted;

    /**
     * @param uuid           The UUID of the player.
     * @param maxWaveCompleted Max 0-th index wave completed.
     */
    public PlayerData(UUID uuid, int wins, int losses, int wavesCompleted, int kills, int deaths, int ironCollected, int goldCollected, int woodCollected, int maxWaveCompleted) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.wavesCompleted = wavesCompleted;
        this.kills = kills;
        this.deaths = deaths;
        this.ironCollected = ironCollected;
        this.goldCollected = goldCollected;
        this.woodCollected = woodCollected;
        this.maxWaveCompleted = maxWaveCompleted;
    }

    // POJO mongo constructor
    public PlayerData() {

    }


    /**
     * @param uuid The UUID of the player.
     * @return Default (empty) PlayerData
     */
    public static PlayerData getDefault(UUID uuid) {
        return new PlayerData(uuid, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public void incWins(int wins) {
        this.wins += wins;
    }

    public int getLosses() {
        return losses;
    }

    public void incLosses(int losses) {
        this.losses += losses;
    }

    public int getWavesCompleted() {
        return wavesCompleted;
    }

    public void incWavesCompleted(int wavesCompleted) {
        this.wavesCompleted += wavesCompleted;
    }

    public int getKills() {
        return kills;
    }

    public void incKills(int kills) {
        this.kills += kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incDeaths(int deaths) {
        this.deaths += deaths;
    }

    public int getIronCollected() {
        return ironCollected;
    }

    public void incIronCollected(int ironCollected) {
        this.ironCollected += ironCollected;
    }

    public int getGoldCollected() {
        return goldCollected;
    }

    public void incGoldCollected(int goldCollected) {
        this.goldCollected += goldCollected;
    }

    public int getWoodCollected() {
        return woodCollected;
    }

    public void incWoodCollected(int woodCollected) {
        this.woodCollected += woodCollected;
    }

    public int getMaxWaveCompleted() {
        return maxWaveCompleted;
    }

    public void setMaxWaveCompleted(int maxWaveCompleted) {
        this.maxWaveCompleted = maxWaveCompleted;
    }
}
