package xyz.gameoholic.lumbergame.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.game.wave.Wave;

import java.nio.file.Paths;
import java.util.*;

public class ConfigParser {
    private LumberGamePlugin plugin;

    public ConfigParser(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    public LumberConfig parse() {
        StringsConfig stringsConfig = getStringsConfig();
        List<MobType> mobTypes = getAllMobTypes();
        MapConfig mapConfig = getMapConfig();
        GameConfig gameConfig = getGameConfig();
        List<Wave> waves = getWavesConfig(mobTypes);

        return new LumberConfig(stringsConfig, mobTypes, mapConfig, gameConfig, waves);
    }

    private StringsConfig getStringsConfig() {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/strings.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        StringsConfig stringsConfig = null;
        try {
            stringsConfig = new StringsConfig(
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
                Objects.requireNonNull(root.node("mob-displayname").getString()),
                Objects.requireNonNull(root.node("iron-displayname").getString()),
                Objects.requireNonNull(root.node("gold-displayname").getString()),
                Objects.requireNonNull(root.node("iron-lore").getString()),
                Objects.requireNonNull(root.node("gold-lore").getString()),
                Objects.requireNonNull(root.node("bone-meal-displayname").getString()),
                Objects.requireNonNull(root.node("bone-meal-lore").getString()),
                Objects.requireNonNull(root.node("bone-block-displayname").getString()),
                Objects.requireNonNull(root.node("bone-block-lore").getString()),
                Objects.requireNonNull(root.node("wood-displayname").getString()),
                Objects.requireNonNull(root.node("wood-lore").getString()),
                Objects.requireNonNull(root.node("scoreboard-title").getString()),
                Objects.requireNonNull(root.node("scoreboard-lines").getList(String.class)),
                Objects.requireNonNull(root.node("player-scoreboard-line").getString()),
                Objects.requireNonNull(root.node("new-wave-start-message").getString()),
                Objects.requireNonNull(root.node("tree-damaged-actionbar-message")).getString()
            );

        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        return stringsConfig;
    }

    private GameConfig getGameConfig() {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/game.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        GameConfig gameConfig;
        try {
            gameConfig = new GameConfig(
                root.node("tree-health-expression").require(String.class),
                root.node("iron-drop-expression").require(String.class),
                root.node("gold-drop-expression").require(String.class),
                root.node("bone-meal-spawn-expression").require(String.class),
                root.node("scoreboard-player-line-margin").require(Integer.class)
            );
        } catch (SerializationException e) {
            e.printStackTrace();
            throw new RuntimeException("One of the arguments for game config is invalid.");
        }
        return gameConfig;
    }

    private MapConfig getMapConfig() {
        YamlConfigurationLoader conf = YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/map.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }


        Location treeLocation;
        try {
            treeLocation = new Location(
                Bukkit.getWorld(root.node("tree-location", "world").require(String.class)),
                root.node("tree-location", "x").require(Double.class),
                root.node("tree-location", "y").require(Double.class),
                root.node("tree-location", "z").require(Double.class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Tree location argument for map is invalid.");
        }

        List spawnLocations = new ArrayList();
        root.node("spawn-locations").childrenList().forEach(
            spawnLocation -> {
                try {
                    spawnLocations.add(new Location(
                        Bukkit.getWorld(spawnLocation.node("world").require(String.class)),
                        spawnLocation.node("x").require(Double.class),
                        spawnLocation.node("y").require(Double.class),
                        spawnLocation.node("z").require(Double.class)
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("One of the arguments for spawn location number " +
                        root.childrenList().indexOf(spawnLocation) + 1 + " is invalid or was not provided.\n");
                }
            }
        );

        List treeBlockTypes = new ArrayList();
        root.node("tree-block-types").childrenList().forEach(
            treeBlockType ->  {
                try {
                    treeBlockTypes.add(Material.valueOf(treeBlockType.get(String.class)));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Invalid argument/s for tree block types.");
                }
            }
        );

        MapConfig mapConfig;
        try {
            mapConfig = new MapConfig(
                treeLocation,
                spawnLocations,
                root.node("tree-radius").require(Integer.class),
                treeBlockTypes
            );
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
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
                        mobType.node("id").require(String.class),
                        mobType.node("display-name").require(String.class),
                        EntityType.valueOf(mobType.node("entity-type").require(String.class)),
                        isHostile,
                        mobType.node("health-expression").require(String.class),
                        mobType.node("damage-expression").require(String.class),
                        mobType.node("speed").getDouble(0.23)
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("One of the arguments for mob " +
                        mobType.node("id").getString("number " + 1 + root.childrenList().indexOf(mobType)) +
                        " is invalid or was not provided.\n"); // Provide ID or mob number whose invalid
                }
            }
        );
        return mobTypes;
    }

    private List<Wave> getWavesConfig(List<MobType> loadedMobTypes) {
        List waves = new ArrayList();

        YamlConfigurationLoader conf =
            YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/waves.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        root.childrenList().forEach(
            wave -> {
                try {
                    List<MobType> mobTypes = new ArrayList<>();
                    List<Double> mobTypesChances = new ArrayList<>();
                    wave.node("mob-types").childrenList().forEach(
                        mobType -> {
                            try {
                                String mobTypeID = mobType.node("id").require(String.class);
                                mobTypes.add(loadedMobTypes.stream()
                                    .filter(filteredMobType ->
                                        filteredMobType.id().equals(mobTypeID)).findFirst().get());
                                mobTypesChances.add(mobType.node("chance").require(Double.class));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    );

                    Map<MobType, Integer> guaranteedMobTypes = new HashMap<>();
                    wave.node("guaranteed-mob-types").childrenList().forEach(
                        mobType -> {
                            try {
                                String mobTypeID = mobType.node("id").require(String.class);
                                guaranteedMobTypes.put(loadedMobTypes.stream()
                                        .filter(filteredMobType ->
                                            filteredMobType.id().equals(mobTypeID)).findFirst().get(),
                                    mobType.node("amount").require(Integer.class));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    );

                    waves.add(new Wave(
                        wave.node("wave-cr").require(Integer.class),
                        wave.node("spawn-timer-min").require(Integer.class),
                        wave.node("spawn-timer-max").require(Integer.class),
                        wave.node("mob-min-cr").require(Integer.class),
                        wave.node("mob-max-cr").require(Integer.class),
                        mobTypes,
                        mobTypesChances,
                        wave.node("bone-block").getBoolean(false),
                        guaranteedMobTypes
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("One of the arguments for wave number " + root.childrenList().indexOf(wave) + 1 +
                        " is invalid or was not provided.\n");
                }
            }
        );
        return waves;
    }


}
