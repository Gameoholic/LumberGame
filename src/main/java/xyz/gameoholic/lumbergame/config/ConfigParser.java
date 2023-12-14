package xyz.gameoholic.lumbergame.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private LumberGamePlugin plugin;
    public ConfigParser(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    public LumberConfig parse() {
        StringsConfig stringsConfig = getStringsConfig();
        List<MobType> mobTypes = getAllMobTypes();
        MapConfig mapConfig = getMapConfig();

        return new LumberConfig(stringsConfig, mobTypes, mapConfig);
    }

    private StringsConfig getStringsConfig() {
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
            root.node("start-command-success-message").getString(""),
            Objects.requireNonNull(root.node("mob-displayname").getString())
        );
        return stringsConfig;
    }

    private MapConfig getMapConfig() {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/map.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }


        Location treeLocation = new Location(Bukkit.getWorld(Objects.requireNonNull(root.node("tree-location", "world").getString())),
            root.node("tree-location", "x").getDouble(),
            root.node("tree-location", "y").getDouble(),
            root.node("tree-location", "z").getDouble()
        );

        MapConfig mapConfig = new MapConfig(
            treeLocation
        );
        return mapConfig;
    }

    private List<MobType> getAllMobTypes() {
        List mobTypes = new ArrayList();

        YamlConfigurationLoader hostileMobsConf =
            YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/hostile_mob_types.yml")).build();
        CommentedConfigurationNode hostileMobsRoot;
        try {
            hostileMobsRoot = hostileMobsConf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        mobTypes.addAll(getMobTypes(hostileMobsRoot, true));

        YamlConfigurationLoader treeMobsConf =
            YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/tree_mob_types.yml")).build();
        CommentedConfigurationNode treeMobsRoot;
        try {
            treeMobsRoot = treeMobsConf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        mobTypes.addAll(getMobTypes(treeMobsRoot, false));

        return mobTypes;
    }

    /**
     * Returns the mob types for a certain configuration node (hostile/tree mobs)
     */
    private List<MobType> getMobTypes(CommentedConfigurationNode root, boolean isHostile) {
        List mobTypes = new ArrayList();
        root.childrenList().forEach(
            mobType -> {
                try {
                    mobTypes.add(new MobType(
                        Objects.requireNonNull(mobType.node("id").getString()),
                        Objects.requireNonNull(mobType.node("display-name").getString()),
                        Objects.requireNonNull(EntityType.valueOf(mobType.node("entity-type").getString())),
                        isHostile,
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
