package xyz.gameoholic.lumbergame.config;


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
    String diamondDisplayname,
    String ironLore,
    String goldLore,
    String diamondLore,
    String boneMealDisplayname,
    String boneMealLore


) {

}
