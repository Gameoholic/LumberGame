package xyz.gameoholic.lumbergame.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.nio.file.Paths;

public class ConfigParser {
    public static LumberConfig parse(LumberGamePlugin plugin) {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/strings.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }


        StringsConfig stringsConfig = new StringsConfig(
            root.node("add-all-to-queue-command-none-found-message").getString(""),
            root.node("add-all-to-queue-command-added-message").getString(""),
            root.node("add-to-queue-command-no-player-provided-message").getString(""),
            root.node("add-to-queue-command-player-offline-message").getString(""),
            root.node("add-to-queue-command-player-already-queued-message").getString(""),
            root.node("add-to-queue-command-added-message").getString(""),
            root.node("remove-all-from-queue-command-none-found-message").getString(""),
            root.node("removed-all-from-queue-command-removed-message").getString(""),
            root.node("remove-from-queue-command-no-player-provided-message").getString(""),
            root.node("remove-from-queue-command-player-offline-message").getString(""),
            root.node("remove-from-queue-command-player-not-queued-message").getString(""),
            root.node("remove-from-queue-command-removed-message").getString(""),
            root.node("start-command-no-queued-players-message").getString(""),
            root.node("start-command-game-in-progress-message").getString(""),
            root.node("start-command-success-message").getString("")
        );

        return new LumberConfig(stringsConfig);
    }
}
