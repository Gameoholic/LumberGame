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
     * The amount of CR (challenge rating) allotted to this wave.
     */
    private int waveCR;
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
        this.waveCR = wave.waveCR();
        this.wave = wave;

        loadMobQueue();
        startWave();
    }

    /**
     * Loads the mobs into the mob queue.
     */
    private void loadMobQueue() {
        int leftWaveCR = waveCR;
        // Guaranteed mobs
        for (Map.Entry<MobType, Integer> guaranteedMob : wave.guaranteedMobTypes().entrySet()) {
            for (int i = 0; i < guaranteedMob.getValue(); i++) {
                if (leftWaveCR <= 0) {  // This shouldn't happen from a design viewpoint, but is technically possible with the right config
                    return;
                }
                int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
                mobQueue.add(getMob(guaranteedMob.getKey(), mobCR, false));
                leftWaveCR -= mobCR;
            }
        }
        // Random mobs
        while (leftWaveCR > 0) {
            MobType selectedMobType = RandomUtil.getRandom(wave.mobTypes(), wave.mobTypesChances());
            int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
            mobQueue.add(getMob(selectedMobType, mobCR, false));
            leftWaveCR -= mobCR;
        }
        // Spawn with bone block if needed
        if (wave.boneBlock()) {
            Mob firstMob = mobQueue.get(0); // Replace first mob with bone block variant
            mobQueue.set(0, getMob(firstMob.getMobType(), firstMob.getCR(), true));
        }
        Collections.shuffle(mobQueue); // We shuffle because of guaranteed mobs & bone blocks being at the start of the list.

        plugin.getLogger().info("Loaded " + mobQueue.size() + " mobs for the round.");

    }
    private void startWave() {
        mobSpawnerTask = new BukkitRunnable() {
            @Override
            public void run() {
                attemptSpawn();
            }
        }.runTaskTimer(plugin, 0L, 1L);

        spawnDelay = rnd.nextInt(wave.spawnTimerMin(), wave.spawnTimerMax() + 1);
    }

    /**
     * Runs the mob spawning logic. Runs every tick, only spawns if conditions are met.
     */
    private void attemptSpawn() {
        spawnDelay--;
        if (spawnDelay > 0)
            return;
        if (mobQueue.size() == 0) {
            mobSpawnerTask.cancel();
            mobSpawnerTask = null;
            plugin.getLogger().info("Finished spawning mobs.");
            return;
        }

        spawnMob();
        spawnDelay = rnd.nextInt(wave.spawnTimerMin(), wave.spawnTimerMax() + 1);
    }
    private void spawnMob() {
        List<Location> spawnLocations = plugin.getLumberConfig().mapConfig().spawnLocations();
        Location selectedSpawnLocation = spawnLocations.get(rnd.nextInt(spawnLocations.size()));

        mobQueue.get(0).spawnMob(selectedSpawnLocation);
        mobQueue.remove(0);
    }

    /**
     * @param uuid The UUID of the mob entity.
     * @return The Lumber mob instance for which it belongs, or null if not found.
     */
    public @Nullable Mob getMob(UUID uuid) {
        return aliveMobs.get(uuid);
    }

    /**
     * Called when a mob spawns, either from a wave or a debug command.
     */
    public void onMobSpawn(Mob mob) {
        aliveMobs.put(mob.getMob().getUniqueId(), mob);
    }
    /**
     * Called when a mob dies.
     */
    public void onMobDeath(Mob mob) {
        aliveMobs.remove(mob.getMob().getUniqueId());

        if (aliveMobs.size() == 0) {
            plugin.getLogger().info("All mobs in wave are dead!");
            plugin.getGameManager().onWaveEnd();
        }

    }
    /**
     * Instantiates the mob class.
     * @param mobTypeID The ID of the mob type.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param boneBlock Whether the mob has a bone block.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public Mob getMob(String mobTypeID, int CR, boolean boneBlock) {
        MobType mobType = plugin.getLumberConfig().mobTypes()
            .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get();

        return getMob(mobType, CR, boneBlock);
    }

    /**
     * Instantiates the mob class.
     * @param mobType The Lumber MobType of the mob.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param boneBlock Whether the mob has a bone block.
     */
    public Mob getMob(MobType mobType, int CR, boolean boneBlock) {
        Mob mob;
        if (mobType.isHostile())
            mob = new Mob(plugin, mobType, CR, boneBlock);
        else
            mob = new TreeMob(plugin, mobType, CR, boneBlock);
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


    public int getAliveMobsSize() {
        return aliveMobs.size();
    }
}
