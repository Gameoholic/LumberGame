package xyz.gameoholic.lumbergame.config;

import net.kyori.adventure.sound.Sound;

public record SoundsConfig(Sound treeDamagedSound, Sound treeDiedSound, Sound waveStartSound, Sound bossWaveStartSound,
                           Sound treeHealSound, Sound treeLevelUpSound, Sound goldVaultDepositSound, Sound goldVaultCollectSound,
                           Sound fireWandSound, Sound fireWandHurtSound) {
}
