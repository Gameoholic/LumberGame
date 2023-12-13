package xyz.gameoholic.lumbergame.config;

import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.MobType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigParser {
    public static LumberConfig parse(LumberGamePlugin plugin) {


        StringsConfig stringsConfig = getStringsConfig(plugin);
        List<MobType> mobTypes = getMobTypes(plugin);


        return new LumberConfig(stringsConfig, mobTypes);
    }

    private static StringsConfig getStringsConfig(LumberGamePlugin plugin) {
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
        return stringsConfig;
    }

    private static List<MobType> getMobTypes(LumberGamePlugin plugin) {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/mob_types.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        List mobTypes = new ArrayList();
        root.childrenList().forEach(
            mobType -> {
                try {
                    mobTypes.add(new MobType(
                        Objects.requireNonNull(mobType.node("id").getString()),
                        Objects.requireNonNull(mobType.node("display-name").getString()),
                        Objects.requireNonNull(EntityType.valueOf(mobType.node("entity-type").getString())),
                        Objects.requireNonNull(mobType.node("is-hostile").getBoolean()),
                        Objects.requireNonNull(mobType.node("health-expression").getString()),
                        Objects.requireNonNull(mobType.node("player-damage-expression").getString()),
                        Objects.requireNonNull(mobType.node("tree-damage-expression").getString())
                    ));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("One of the arguments for mob " +
                        mobType.node("id").getString("number " + root.childrenList().indexOf(mobType)) +
                        " is invalid or was not provided.\n"); // Provide ID or mob number whose invalid
                }
            }
        );

        return mobTypes;
    }
}
