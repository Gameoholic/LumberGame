package xyz.gameoholic.lumbergame.game.wave;

import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.util.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Represents a wave's settings and parameters.
 *
 * @param waveCR          The total Challenge Rating (CR) to be used on the wave's mobs.
 * @param activeSpawns    The amount of active spawn locations that can spawn monsters.
 * @param spawnTimerMin   The min amount of ticks to wait before spawning mob/s.
 * @param spawnTimerMax   The max amount of ticks to wait before spawning mob/s.
 * @param mobMinCR        The minimum CR to spawn mobs with.
 * @param mobMaxCR        The maximum CR to spawn mobs with.
 * @param mobTypes        The mob types that can spawn.
 * @param mobTypesChances The chances (0.0-1.0) of the mobs types to spawn, must be same indices as mobTypes.
 * @param boneBlockMobType The mob type to spawn a bone block with, if any.
 */
public record Wave(int waveCR, int activeSpawns, int spawnTimerMin, int spawnTimerMax, int mobMinCR, int mobMaxCR, List<MobType> mobTypes,
                   List<Double> mobTypesChances, @Nullable MobType boneBlockMobType, Map<MobType, Integer> guaranteedMobTypes,
                   List<Pair<MobType, Integer>> guaranteedMobTypesWithIndex) {
}
