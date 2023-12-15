package xyz.gameoholic.lumbergame.game.wave;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.mob.TreeMob;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.util.RandomUtil;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a single wave and handles the mob spawning for that single wave.
 */
public class WaveManager {
    private LumberGamePlugin plugin;

    /**
     * Alive mobs mapped to their UUID's.
     */
    private Map<UUID, Mob> aliveMobs = new HashMap<>();
    /**
     * The amount of CR (challenge rating) left, allotted to this wave.
     */
    private int leftWaveCR;
    private Wave wave;
    /**
     * Delay before spawning next mob/s, in ticks.
     */
    private int spawnDelay = 0;
    /**
     * BukkitTask responsible for spawning the mobs every X ticks.
     * When finished, will cancel and be set to null.
     */
    private @Nullable BukkitTask mobSpawnerTask;

    private List<Mob> mobQueue = new ArrayList<>();
    Random rnd = new Random();
    public WaveManager(LumberGamePlugin plugin, Wave wave) {
        this.plugin = plugin;
        this.leftWaveCR = wave.waveCR();
        this.wave = wave;

        loadMobQueue();
        startWave();
    }

    private void loadMobQueue() {
        //todo: this does almost everything attemptSpawn() does.
    }
    private void startWave() {
        mobSpawnerTask = new BukkitRunnable() {
            @Override
            public void run() {
                attemptSpawn();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void attemptSpawn() {

        //todo: this should just spawn the alread-determined mob from the queue at a random location
        spawnDelay--;
        if (spawnDelay > 0)
            return;
        if (leftWaveCR <= 0) {
            mobSpawnerTask.cancel();
            mobSpawnerTask = null;
            plugin.getLogger().info("Finished spawning mobs.");
            return;
        }

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

        spawnDelay = rnd.nextInt(wave.spawnTimerMin(), wave.spawnTimerMax() + 1);
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

        if (aliveMobs.size() == 0 && leftWaveCR <= 0) {
            plugin.getLogger().info("All mobs in wave are dead!");
            plugin.getGameManager().onWaveEnd();
        }

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

    public void onGameEnd() {
        // Disable all mobs
        aliveMobs.values().forEach(
            mob -> mob.getMob().setAI(false)
        );
        mobSpawnerTask.cancel();
        mobSpawnerTask = null;
    }
}
