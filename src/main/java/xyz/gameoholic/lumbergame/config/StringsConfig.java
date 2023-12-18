package xyz.gameoholic.lumbergame.config;


import java.util.List;

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
    String ironDisplayname,
    String goldDisplayname,
    String ironLore,
    String goldLore,
    String boneMealDisplayname,
    String boneMealLore,
    String boneBlockDisplayname,
    String boneBlockLore,
    String woodDisplayname,
    String woodLore,
    String scoreboardTitle,
    List<String> scoreboardLines,
    String playerScoreboardLine,
    String newWaveStartMessage,
    String treeDamagedActionbarMessage,
    String bowDisplayname,
    String bowLore,
    String woodenSwordDisplayname,
    String woodenSwordLore,
    String stoneSwordDisplayname,
    String stoneSwordLore,
    String ironSwordDisplayname,
    String ironSwordLore,
    String diamondSwordDisplayname,
    String diamondSwordLore,
    String arrowDisplayname,
    String arrowLore

) {

}
