package xyz.gameoholic.lumbergame.game.wave;

import xyz.gameoholic.lumbergame.game.mob.MobType;

import java.util.List;
import java.util.Map;

/**
 * Represents a wave's settings and parameters.
 *
 * @param waveCR          The total Challenge Rating (CR) to be used on the wave's mobs.
 * @param spawnTimerMin   The min amount of ticks to wait before spawning mob/s.
 * @param spawnTimerMax   The max amount of ticks to wait before spawning mob/s.
 * @param mobMinCR        The minimum CR to spawn mobs with.
 * @param mobMaxCR        The maximum CR to spawn mobs with.
 * @param mobTypes        The mob types that can spawn.
 * @param mobTypesChances The chances (0.0-1.0) of the mobs types to spawn, must be same indices as mobTypes.
 * @param boneBlock       Whether a mob with a bone block should spawn.
 */
public record Wave(int waveCR, int spawnTimerMin, int spawnTimerMax, int mobMinCR, int mobMaxCR, List<MobType> mobTypes,
                   List<Double> mobTypesChances, boolean boneBlock) {
}
