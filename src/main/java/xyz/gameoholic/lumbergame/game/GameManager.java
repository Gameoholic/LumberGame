package xyz.gameoholic.lumbergame.game;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;
import xyz.gameoholic.lumbergame.util.ParticleManager;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class GameManager {
    private final LumberGamePlugin plugin;
    private Set<LumberPlayer> players = new HashSet<>();
    /**
     * By how much to multiply every wave's challenge rating.
     */
    private final double waveCRMultiplier;
    /**
     * By how much to divide every wave's spawn rate interval.
     */
    private final double waveSpawnRateMultiplier;
    private final TreeManager treeManager;
    private WaveManager waveManager;
    /**
     * Meter fills up whenever a mob spawns. When reaches 100, a mob will spawn with bone meal.
     */
    private double boneMealMeter = 0.0;
    private final ParticleManager particleManager;

    /**
     * The wave number, uses zeroth index numbering.
     */
    int waveNumber = 0;

    public GameManager(LumberGamePlugin plugin, Set<UUID> players, double waveCRMultiplier, double waveSpawnRateMultiplier) {
        this.plugin = plugin;
        this.waveCRMultiplier = waveCRMultiplier;
        this.waveSpawnRateMultiplier = waveSpawnRateMultiplier;
        this.particleManager = new ParticleManager(plugin);
        treeManager = new TreeManager(plugin);
        players.forEach(
            uuid -> {
                LumberPlayer lumberPlayer = new LumberPlayer(plugin, uuid);
                this.players.add(lumberPlayer);
            }
        );
        plugin.getLogger().info("Starting game with wave CR multiplier of " + waveCRMultiplier +
            " and wave spawn rate multiplier of " + waveSpawnRateMultiplier);

        startGame();
    }

    private void startGame() {
        plugin.getPlayerNPCManager().reset();
        clearOldEntities();
        startCurrentWave();
        plugin.getLogger().info("Game has started with " + players.size() + " players.");
    }

    private void clearOldEntities() {
        // If entity contains lumber_mob key, it's an old mob from previous waves.
        plugin.getLumberConfig().mapConfig().treeLocation().getWorld().getEntities().forEach(entity ->
            {
                if (entity.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN) != null)
                    entity.remove();
            }
        );
    }

    /**
     * Should be called when the wave ends.
     */
    public void onWaveEnd() {
        startNewWave();
    }

    /**
     * Increments waveNumber and starts the wave.
     */
    private void startNewWave() {
        waveNumber++;
        startCurrentWave();
    }

    /**
     * Starts a specific wave.
     *
     * @param waveNumber The new wave number.
     */
    public void startSpecificWave(int waveNumber) {
        this.waveNumber = waveNumber;
        startCurrentWave();
    }

    /**
     * Starts the wave as per the waveNumber variable.
     */
    private void startCurrentWave() {
        // Wave manager should be alerted that the wave has ended, if it doesn't already know (if forced by command for example)
        if (waveManager != null && !waveManager.getWaveEnded())
            waveManager.onWaveEnd();
        waveManager = new WaveManager(plugin, plugin.getLumberConfig().waves().get(waveNumber),
            waveCRMultiplier, waveSpawnRateMultiplier);

        // Send message
        players.forEach(lumberPlayer ->
            lumberPlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().newWaveStartMessage(),
                Placeholder.component("wave", text(waveNumber + 1))
            )));


        // Play sound
        Sound sound = ((waveNumber + 1) % 5 == 0) ?
            plugin.getLumberConfig().soundsConfig().bossWaveStartSound() : plugin.getLumberConfig().soundsConfig().waveStartSound();
        players.forEach(lumberPlayer -> lumberPlayer.playSound(sound));

        updatePlayerScoreboards(); // Update wave number
    }


    /**
     * Called after the game has fully loaded and the first wave has started.
     */
    public void onGameLoad() {
        players.forEach(player -> player.onGameLoad());
        treeManager.onGameLoad();
    }

    /**
     * Called when the game has ended.
     */
    public void onGameEnd() {
        players.forEach(lumberPlayer -> lumberPlayer.sendMessage(MiniMessage.miniMessage()
            .deserialize(plugin.getLumberConfig().strings().treeDeathMessage())));
        waveManager.onGameEnd();
        players.forEach(LumberPlayer::unregisterEvents);
        particleManager.stopParticles();
        plugin.onGameEnd();
    }

    /**
     * Updates scoreboards for every player.
     */
    public void updatePlayerScoreboards() {
        players.forEach(player -> player.updateScoreboard());
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public TreeManager getTreeManager() {
        return treeManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public Set<LumberPlayer> getPlayers() {
        return players;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public void increaseBoneMealMeter(double amount) {
        boneMealMeter += amount;
    }
    public void resetBoneMealMeter() {
        boneMealMeter = 0.0;
    }
    public double getBoneMealMeter() {
        return boneMealMeter;
    }

}
