package xyz.gameoholic.lumbergame.config;

import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.game.wave.Wave;

import java.util.List;

public record LumberConfig(StringsConfig strings, List<MobType> mobTypes, MapConfig mapConfig, GameConfig gameConfig,
                           List<Wave> waves, SoundsConfig soundsConfig) {
}
