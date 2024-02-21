package xyz.gameoholic.lumbergame.config;


import java.util.List;
import java.util.Map;

public record StringsConfig(
    String addAllToQueueCommandNoneFoundMessage,
    String addAllToQueueCommandAddedMessage,
    String addToQueueCommandNoPlayerProvidedMessage,
    String addToQueueCommandPlayerOfflineMessage,
    String addToQueueCommandPlayerAlreadyQueuedMessage,
    String addToQueueCommandPlayerAddedMessage,
    String removeAllFromQueueCommandNoneFoundMessage,
    String removeAllFromQueueCommandRemovedMessage,
    String removedFromQueueCommandNoPlayerProvidedMessage,
    String removedFromQueueCommandPlayerOfflineMessage,
    String removedFromQueueCommandPlayerNotQueuedMessage,
    String removedFromQueueCommandPlayerRemovedMessage,
    String startCommandNoQueuedPlayersMessage,
    String startCommandGameInProgressMessage,
    String startCommandSuccessMessage,
    String mobDisplayname,
    String scoreboardTitle,
    List<String> scoreboardLines,
    String playerScoreboardLine,
    String newWaveStartMessage,
    String treeDamagedActionbarMessage,
    String purchasableItemLore,
    Character ironIcon,
    Character goldIcon,
    Character woodIcon,
    String treeDeathMessage,
    String playerDeathMessage,
    String treeHealthThresholdMessage,
    String shopNPCDisplayname,
    String boneMealUseMessage,
    String boneBlockUseMessage,
    String respawnCooldownMessage,
    String respawnedMessage,
    String treeHealMaxHealthMessage,
    String teamPerkBuyMessage,
    String statsMessage,
    String statsCommandMessage,
    Map<Integer, String> newWaveStartMessages,
    String goldVaultText,
    String bossBarText,
    String statsCommandErrorMessage

) {

}
