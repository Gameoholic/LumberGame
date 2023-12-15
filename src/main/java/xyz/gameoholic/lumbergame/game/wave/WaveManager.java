package xyz.gameoholic.lumbergame.game.wave;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.mob.TreeMob;
import xyz.gameoholic.lumbergame.util.MobUtil;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.util.RandomUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Represents a single wave and handles the mob spawning for that single wave.
 */
public class WaveManager {
    private LumberGamePlugin plugin;

    /**
     * Alive mobs mapped to their UUID's.
     */
    private Map<UUID, Mob> aliveMobs;
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

        int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);

        spawnMob(
            plugin,
            selectedMobType,
            mobCR,
            selectedSpawnLocation
        );
    }

    /**
     * @param uuid The UUID of the mob entity.
     * @return The Lumber mob instance for which it belongs, or null if not found.
     */
    public @Nullable Mob getMob(UUID uuid) {
        return aliveMobs.get(uuid);
    }

    /**
     * Should be called when a mob dies.
     */
    public void onMobDeath(Mob mob) {
        aliveMobs.remove(mob.getMob().getUniqueId());
    }
    /**
     * Spawns and returns the Mob.
     * @param mobTypeID The ID of the mob type.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param location The location to spawn the mob at.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public Mob spawnMob(LumberGamePlugin plugin, String mobTypeID, int CR, Location location) {
        MobType mobType = plugin.getLumberConfig().mobTypes()
            .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get();
        Mob mob;
        if (mobType.isHostile())
            mob = new Mob(plugin, mobType, CR, location);
        else
            mob = new TreeMob(plugin, mobType, CR, location);

        aliveMobs.put(mob.getMob().getUniqueId(), mob);
        leftWaveCR -= CR;
        return mob;
    }

    /**
     * Spawns and returns the Mob.
     * @param mobType The Lumber MobType of the mob.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param location The location to spawn the mob at.
     */
    public Mob spawnMob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        Mob mob;
        if (mobType.isHostile())
            mob = new Mob(plugin, mobType, CR, location);
        else
            mob = new TreeMob(plugin, mobType, CR, location);

        aliveMobs.put(mob.getMob().getUniqueId(), mob);
        leftWaveCR -= CR;
        return mob;
    }

}
