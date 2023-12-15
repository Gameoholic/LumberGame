package xyz.gameoholic.lumbergame.game.wave;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.MobUtil;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.util.RandomUtil;

import java.util.List;
import java.util.Random;

public class WaveManager {
    private LumberGamePlugin plugin;
    /**
     * The amount of CR (challenge rating) left, allotted to this wave.
     */
    private int leftWaveCR;
    Wave wave;

    Random rnd = new Random();
    public WaveManager(LumberGamePlugin plugin, Wave wave) {
        this.plugin = plugin;
        this.leftWaveCR = wave.waveCR();
        this.wave = wave;

        startWave();
    }

    private void startWave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                attemptSpawn();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void attemptSpawn() {
        if (leftWaveCR <= 0)
            return;

        MobType selectedMobType = RandomUtil.getRandom(wave.mobTypes(), wave.mobTypesChances());

        List<Location> spawnLocations = plugin.getLumberConfig().mapConfig().spawnLocations();
        Location selectedSpawnLocation = spawnLocations.get(rnd.nextInt(spawnLocations.size()));

        int mobCR = 1;

        MobUtil.spawnMob(
            plugin,
            selectedMobType,
            mobCR,
            selectedSpawnLocation
        );

        leftWaveCR -= mobCR;
    }
}
