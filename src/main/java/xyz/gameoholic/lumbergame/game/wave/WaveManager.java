package xyz.gameoholic.lumbergame.game.wave;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.LumberMob;
import xyz.gameoholic.lumbergame.game.mob.TreeLumberMob;
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
    private Map<UUID, LumberMob> aliveMobs = new HashMap<>();
    /**
     * The amount of CR (challenge rating) allotted to this wave.
     */
    private int waveCR;
    private Wave wave;

    /**
     * Spawn location indices that are currently active (monsters can spawn at), 0 indexed.
     */
    private final Set<Integer> activeSpawns;

    /**
     * By how much to divide every wave's spawn rate interval.
     */
    private final double waveSpawnRateMultiplier;

    /**
     * Delay before spawning next mob/s, in ticks.
     */
    private int spawnDelay = 0;
    /**
     * BukkitTask responsible for spawning the mobs every X ticks.
     * When finished, will cancel and be set to null.
     */
    private @Nullable BukkitTask mobSpawnerTask;

    private List<LumberMob> mobQueue = new ArrayList<>();
    private final Random rnd = new Random();
    private boolean waveEnded = false;

    public WaveManager(LumberGamePlugin plugin, Wave wave, double waveCRMultiplier, double waveSpawnRateMultiplier) {
        this.plugin = plugin;
        this.waveCR = (int) (wave.waveCR() * waveCRMultiplier);
        this.wave = wave;
        this.waveSpawnRateMultiplier = waveSpawnRateMultiplier;

        activeSpawns = genActiveSpawns();
        loadMobQueue(waveCRMultiplier);
        startWave();
    }

    /**
     * Activates random spawns based on the amount of spawns that should be active.
     *
     * @return The numbers of the activated spawns.
     */
    private Set<Integer> genActiveSpawns() {
        Set<Integer> selectedActiveSpawns = new HashSet<>();

        // Generate pool of available spawn indices
        Set<Integer> availableSpawnsPool = new HashSet<>();
        for (int i = 0; i < plugin.getLumberConfig().mapConfig().spawnLocations().size(); i++) {
            availableSpawnsPool.add(i);
        }

        // Generate initial active spawn
        int initialActiveSpawn = new Random().nextInt(plugin.getLumberConfig().mapConfig().spawnLocations().size());
        availableSpawnsPool.remove(initialActiveSpawn);
        selectedActiveSpawns.add(initialActiveSpawn);

        // Generate rest of active spawns
        for (int i = 1; i < wave.activeSpawns(); i++) {
            int selectedSpawn;
            if (i == 1) // 2nd active spawn must be adjacent
                selectedSpawn = availableSpawnsPool.contains(initialActiveSpawn + 1) ? initialActiveSpawn + 1 : initialActiveSpawn - 1;
            else {
                // Rest can be random
                List<Integer> poolAsList = new ArrayList<>(availableSpawnsPool);
                Collections.shuffle(poolAsList);
                selectedSpawn = poolAsList.get(0);
            }
            selectedActiveSpawns.add(selectedSpawn);
            availableSpawnsPool.remove(selectedSpawn);
        }

        return selectedActiveSpawns;
    }


    /**
     * Loads the mobs into the mob queue.
     */
    private void loadMobQueue(double waveCRMultiplier) {
        int leftWaveCR = waveCR;

        // Guaranteed mobs with specific index from last position
        Map<LumberMob, Integer> mobsWithIndex = new HashMap();
        for (Map.Entry<MobType, Integer> guaranteedMob : wave.guaranteedMobTypesWithIndex().entrySet()) {
            for (int i = 0; i < (guaranteedMob.getKey().isBoss() ? 1 : waveCRMultiplier); i++) { // Scale guaranteed mob count with cr scale, unless they're bosses
                if (leftWaveCR <= 0) {  // This shouldn't happen from a design viewpoint, but is technically possible with the right config
                    return;
                }
                int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
                mobsWithIndex.put(getMob(guaranteedMob.getKey(), mobCR, false, true), guaranteedMob.getValue());
                leftWaveCR -= mobCR;
            }
        }
        // Guaranteed mobs
        for (Map.Entry<MobType, Integer> guaranteedMob : wave.guaranteedMobTypes().entrySet()) {
            for (int i = 0; i < guaranteedMob.getValue() * waveCRMultiplier; i++) { // Scale guaranteed mob count with cr scale
                if (leftWaveCR <= 0) {  // This shouldn't happen from a design viewpoint, but is technically possible with the right config
                    return;
                }
                int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
                mobQueue.add(getMob(guaranteedMob.getKey(), mobCR, false, false));
                leftWaveCR -= mobCR;
            }
        }
        // Put bone block mob at end of queue if needed
        if (wave.boneBlockMobType() != null) {
            int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
            mobsWithIndex.put(getMob(wave.boneBlockMobType(), mobCR, true, false), 0);
            leftWaveCR -= mobCR;
        }
        // Random mobs
        while (leftWaveCR > 0) {
            MobType selectedMobType = RandomUtil.getRandom(wave.mobTypes(), wave.mobTypesChances());
            int mobCR = rnd.nextInt(wave.mobMinCR(), wave.mobMaxCR() + 1);
            mobQueue.add(getMob(selectedMobType, mobCR, false, false));
            leftWaveCR -= mobCR;
        }
        Collections.shuffle(mobQueue); // We shuffle because of guaranteed mobs being at the start of the list.

        // Iterate over mobs with specific indices, add them to the mobQueue at wanted index
        for (Map.Entry<LumberMob, Integer> mobTypeEntry : mobsWithIndex.entrySet()) {
            // Index specified is the index from the end of the list. (for example, 0 would be end of list, 1 would be one before)
            mobQueue.add(mobQueue.size() - mobTypeEntry.getValue(), mobTypeEntry.getKey());
        }

        plugin.getLogger().info("Loaded " + mobQueue.size() + " mobs for the round.");

    }

    private void startWave() {
        mobSpawnerTask = new BukkitRunnable() {
            @Override
            public void run() {
                attemptSpawn();
            }
        }.runTaskTimer(plugin, 0L, 1L);

        spawnDelay = (int) (rnd.nextInt(wave.spawnTimerMin(), wave.spawnTimerMax() + 1) / waveSpawnRateMultiplier);
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
        spawnDelay = (int) (rnd.nextInt(wave.spawnTimerMin(), wave.spawnTimerMax() + 1) / waveSpawnRateMultiplier);
    }

    private void spawnMob() {
        List<Integer> activeSpawnsAsList = new ArrayList<>(activeSpawns);
        Collections.shuffle(activeSpawnsAsList);
        Location selectedSpawnLocation = plugin.getLumberConfig().mapConfig().spawnLocations().get(activeSpawnsAsList.get(0));

        LumberMob mob = mobQueue.get(0);
        mobQueue.remove(0);
        mob.spawnMob(selectedSpawnLocation);
    }

    /**
     * @param uuid The UUID of the mob entity.
     * @return The Lumber mob instance for which it belongs, or null if not found.
     */
    public @Nullable LumberMob getMob(UUID uuid) {
        return aliveMobs.get(uuid);
    }

    /**
     * Called when a mob spawns, either from a wave or a debug command.
     */
    public void onMobSpawn(LumberMob mob) {
        aliveMobs.put(mob.getMob().getUniqueId(), mob);
        plugin.getGameManager().updatePlayerScoreboards(); // Update mob count
    }

    /**
     * Called when a mob dies.
     */
    public void onMobDeath(LumberMob mob) {
        aliveMobs.remove(mob.getMob().getUniqueId());

        if (aliveMobs.size() == 0 && mobQueue.size() == 0) {
            plugin.getLogger().info("All mobs in wave are dead!");
            if (!waveEnded) { // This should never be called twice, just a sanity check
                onWaveEnd();
                plugin.getGameManager().onWaveEnd();
            }
        } else
            plugin.getGameManager().updatePlayerScoreboards(); // Update mob count - Starting a new wave updates scoreboard anyway so only if wave hasn't ended
    }

    /**
     * Called when the wave ends, either by all mobs being eliminated or otherwise.
     */
    public void onWaveEnd() {
        // Remove all mobs in case they're not dead already
        for (Map.Entry<UUID, LumberMob> aliveMobEntry : aliveMobs.entrySet()) {
            aliveMobEntry.getValue().remove();
        }
        // Task will have cancelled if it'd finished spawning the mobs
        if (mobSpawnerTask != null) {
            mobSpawnerTask.cancel();
            mobSpawnerTask = null;
        }
        waveEnded = true;
        plugin.getGameManager().getPlayers().forEach(lumberPlayer ->
                plugin.getPlayerDataManager().getCachedPlayerData(lumberPlayer.getUuid()).incWavesCompleted(1)
        );
        plugin.getGameManager().getPlayers().forEach(lumberPlayer ->
                plugin.getPlayerDataManager().getCachedPlayerData(lumberPlayer.getUuid()).setMaxWaveCompleted(plugin.getGameManager().getWaveNumber())
        );
    }

    /**
     * Instantiates the mob class.
     *
     * @param mobTypeID The ID of the mob type.
     * @param CR        The Challenge Rating to spawn the mob with.
     * @param boneBlock Whether the mob has a bone block.
     * @param guaranteedSpawn Whether the mob was a guaranteed spawn.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public LumberMob getMob(String mobTypeID, int CR, boolean boneBlock, boolean guaranteedSpawn) {
        MobType mobType = plugin.getLumberConfig().mobTypes()
                .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get();

        return getMob(mobType, CR, boneBlock, guaranteedSpawn);
    }

    /**
     * Instantiates the mob class.
     *
     * @param mobType               The Lumber MobType of the mob.
     * @param CR                    The Challenge Rating to spawn the mob with.
     * @param boneBlock             Whether the mob has a bone block.
     * @param guaranteedSingleSpawn Whether the mob was a guaranteed spawn and was spawned on its own (guaranteed with index).
     */
    public LumberMob getMob(MobType mobType, int CR, boolean boneBlock, boolean guaranteedSingleSpawn) {
        LumberMob mob;
        if (mobType.isHostile())
            mob = new LumberMob(plugin, mobType, CR, boneBlock, guaranteedSingleSpawn);
        else
            mob = new TreeLumberMob(plugin, mobType, CR, boneBlock, guaranteedSingleSpawn);
        return mob;
    }

    public void onGameEnd() {
        // Disable all mobs & unregister events for them
        aliveMobs.values().forEach(
                mob -> {
                    mob.getMob().setAI(false);
                    mob.unregisterEvents();
                }
        );
        // Task will have cancelled if it'd finished spawning the mobs
        if (mobSpawnerTask != null) {
            mobSpawnerTask.cancel();
            mobSpawnerTask = null;
        }
    }


    public int getAliveMobsSize() {
        return aliveMobs.size();
    }

    public boolean getWaveEnded() {
        return waveEnded;
    }

    public int getMobQueueSize() {
        return mobQueue.size();
    }
    public Set<Integer> getActiveSpawns() {
        return activeSpawns;
    }
}
