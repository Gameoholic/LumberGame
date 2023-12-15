package xyz.gameoholic.lumbergame.game.wave;

import xyz.gameoholic.lumbergame.game.mob.MobType;

import java.util.List;
import java.util.Map;

/**
 * Represents a wave's settings and parameters.
 * @param waveCR The total Challenge Rating (CR) to be used on the wave's mobs.
 * @param mobMinCR The minimum CR to spawn mobs with.
 * @param mobMaxCR The maximum CR to spawn mobs with.
 * @param mobTypes The mob types that can spawn.
 * @param mobTypesChances The chances (0.0-1.0) of the mobs types to spawn, must be same indices as mobTypes.
 */
public record Wave(int waveCR, int mobMinCR, int mobMaxCR, List<MobType> mobTypes, List<Double> mobTypesChances) {
}
