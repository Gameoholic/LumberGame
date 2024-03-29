package xyz.gameoholic.lumbergame.config;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
import xyz.gameoholic.lumbergame.util.Pair;

import java.nio.file.Paths;
import java.util.*;

// todo: this entire class is awful,  use serializers.

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
        SoundsConfig soundsConfig = getSoundsConfig();

        return new LumberConfig(stringsConfig, mobTypes, mapConfig, gameConfig, waves, soundsConfig);
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

            Map<Integer, String> newWaveStartMessages = new HashMap<>();
            root.node("new-wave-start-messages").childrenList().forEach(
                    waveMessage -> {
                        try {
                            int waveNumber = waveMessage.node("wave").require(Integer.class);
                            String waveMessageString = waveMessage.node("message").require(String.class);
                            newWaveStartMessages.put(waveNumber, waveMessageString);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

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
                    Objects.requireNonNull(root.node("scoreboard-title").getString()),
                    Objects.requireNonNull(root.node("scoreboard-lines").getList(String.class)),
                    Objects.requireNonNull(root.node("player-scoreboard-line").getString()),
                    Objects.requireNonNull(root.node("new-wave-start-message").getString()),
                    Objects.requireNonNull(root.node("tree-damaged-actionbar-message")).getString(),
                    Objects.requireNonNull(root.node("purchasable-item-lore").getString()),
                    root.node("iron-icon").require(Character.class),
                    root.node("gold-icon").require(Character.class),
                    root.node("wood-icon").require(Character.class),
                    Objects.requireNonNull(root.node("tree-death-message").getString()),
                    Objects.requireNonNull(root.node("player-death-message").getString()),
                    Objects.requireNonNull(root.node("tree-health-threshold-message").getString()),
                    Objects.requireNonNull(root.node("shop-npc-displayname").getString()),
                    Objects.requireNonNull(root.node("bone-meal-use-message").getString()),
                    Objects.requireNonNull(root.node("bone-block-use-message").getString()),
                    Objects.requireNonNull(root.node("respawn-cooldown-message").getString()),
                    Objects.requireNonNull(root.node("respawned-message").getString()),
                    Objects.requireNonNull(root.node("tree-heal-max-health-message").getString()),
                    Objects.requireNonNull(root.node("team-perk-buy-message").getString()),
                    Objects.requireNonNull(root.node("stats-message").getString()),
                    Objects.requireNonNull(root.node("stats-command-message").getString()),
                    newWaveStartMessages,
                    Objects.requireNonNull(root.node("gold-vault-text").getString()),
                    Objects.requireNonNull(root.node("boss-bar-text").getString()),
                    Objects.requireNonNull(root.node("stats-command-error-message").getString()),
                    Objects.requireNonNull(root.node("stats-upload-error-message").getString())
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
                    root.node("gold-meter-fill-expression").require(String.class),
                    root.node("bone-meal-meter-fill-expression").require(String.class),
                    root.node("scoreboard-player-line-margin").require(Integer.class),
                    root.node("respawn-cooldown").require(Integer.class),
                    root.node("fire-staff-dps-expression").require(String.class),
                    root.node("fire-staff-cooldown").require(Integer.class)
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

        Location goldVaultLocation;
        try {
            goldVaultLocation = new Location(
                    Bukkit.getWorld(root.node("gold-vault-location", "world").require(String.class)),
                    root.node("gold-vault-location", "x").require(Double.class),
                    root.node("gold-vault-location", "y").require(Double.class),
                    root.node("gold-vault-location", "z").require(Double.class),
                    root.node("gold-vault-location", "yaw").getFloat(0f),
                    root.node("gold-vault-location", "pitch").getFloat(0f)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gold vault location argument for map is invalid.");
        }

        Location playerSpawnLocation;
        try {
            playerSpawnLocation = new Location(
                    Bukkit.getWorld(root.node("player-spawn-location", "world").require(String.class)),
                    root.node("player-spawn-location", "x").require(Double.class),
                    root.node("player-spawn-location", "y").require(Double.class),
                    root.node("player-spawn-location", "z").require(Double.class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Player spawn location argument for map is invalid.");
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

        List spawnDisplayLocations = new ArrayList();
        root.node("spawn-display-locations").childrenList().forEach(
                spawnLocationDisplay -> {
                    try {
                        spawnDisplayLocations.add(new Location(
                                Bukkit.getWorld(spawnLocationDisplay.node("world").require(String.class)),
                                spawnLocationDisplay.node("x").require(Double.class),
                                spawnLocationDisplay.node("y").require(Double.class),
                                spawnLocationDisplay.node("z").require(Double.class)
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("One of the arguments for spawn location display number " +
                                root.childrenList().indexOf(spawnLocationDisplay) + 1 + " is invalid or was not provided.\n");
                    }
                }
        );

        Location shopNPCLocation;
        try {
            shopNPCLocation = new Location(
                    Bukkit.getWorld(root.node("shop-npc-location", "world").require(String.class)),
                    root.node("shop-npc-location", "x").require(Double.class),
                    root.node("shop-npc-location", "y").require(Double.class),
                    root.node("shop-npc-location", "z").require(Double.class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Shop NPC location argument for map is invalid.");
        }

        List treeBlockTypes = new ArrayList();
        root.node("tree-block-types").childrenList().forEach(
                treeBlockType -> {
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
                    playerSpawnLocation,
                    spawnLocations,
                    spawnDisplayLocations,
                    shopNPCLocation,
                    root.node("tree-radius").require(Integer.class),
                    treeBlockTypes,
                    Objects.requireNonNull(root.node("tree-level-schematics-provided").getList(Integer.class)),
                    goldVaultLocation
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
                                mobType.node("is-boss").getBoolean(false),
                                mobType.node("health-expression").require(String.class),
                                mobType.node("damage-expression").require(String.class),
                                mobType.node("speed-expression").getString("0.23"),
                                mobType.node("knockback-expression").getString("0"),
                                mobType.node("knockback-resistance-expression").getString("0"),
                                mobType.node("attack-cooldown-expression").getString("20"),
                                mobType.node("is-baby").getBoolean(false),
                                mobType.node("item-in-main-hand-id").getString(),
                                mobType.node("item-in-off-hand-id").getString(),
                                mobType.node("item-in-helmet-id").getString(),
                                mobType.node("item-in-chestplate-id").getString(),
                                mobType.node("item-in-leggings-id").getString(),
                                mobType.node("item-in-boots-id").getString(),
                                mobType.node("has-melee-attack-goal").getBoolean(true),
                                mobType.node("can-spawn-with-bone-meal").getBoolean(true),
                                mobType.node("is-charged").getBoolean(false)
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


    private SoundsConfig getSoundsConfig() {
        YamlConfigurationLoader conf =
                YamlConfigurationLoader.builder().path(Paths.get(plugin.getDataFolder() + "/sounds.yml")).build();
        CommentedConfigurationNode root;
        try {
            root = conf.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }


        try {
            return new SoundsConfig(
                    getSound(root, "tree-damaged-sound"),
                    getSound(root, "tree-died-sound"),
                    getSound(root, "wave-start-sound"),
                    getSound(root, "boss-wave-start-sound"),
                    getSound(root, "tree-heal-sound"),
                    getSound(root, "tree-level-up-sound"),
                    getSound(root, "gold-vault-deposit-sound"),
                    getSound(root, "gold-vault-collect-sound"),
                    getSound(root, "fire-wand-sound"),
                    getSound(root, "fire-wand-hurt-sound")
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("One of the arguments for sounds is invalid or was not provided.");
        }
    }

    private Sound getSound(CommentedConfigurationNode root, String key) throws SerializationException {
        return Sound.sound(
                Key.key(root.node(key, "sound").require(String.class)),
                Sound.Source.valueOf(root.node(key, "source").getString("MASTER")),
                root.node(key, "volume").getFloat(1f),
                root.node(key, "pitch").getFloat(1f)
        );
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

                        List<Pair<MobType, Integer>> guaranteedMobTypesWithIndex = new ArrayList<>();
                        for (CommentedConfigurationNode mobType : wave.node("guaranteed-mob-types-with-index").childrenList()) {
                            try {
                                String mobTypeID = mobType.node("id").require(String.class);
                                guaranteedMobTypesWithIndex.add(new Pair(loadedMobTypes.stream()
                                        .filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get(),
                                        mobType.node("index-from-last").require(Integer.class)));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        waves.add(new Wave(
                                wave.node("wave-cr").require(Integer.class),
                                wave.node("active-spawns").require(Integer.class),
                                wave.node("spawn-timer-min").require(Integer.class),
                                wave.node("spawn-timer-max").require(Integer.class),
                                wave.node("mob-min-cr").require(Integer.class),
                                wave.node("mob-max-cr").require(Integer.class),
                                mobTypes,
                                mobTypesChances,
                                wave.node("bone-block-mob-type-id").getString() != null ?
                                        loadedMobTypes.stream().filter(filteredMobType ->
                                                filteredMobType.id().equals(wave.node("bone-block-mob-type-id").getString())
                                        ).findFirst().get() : null, // bone block mobtype can be null, or can be a mob id
                                guaranteedMobTypes,
                                guaranteedMobTypesWithIndex
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("One of the arguments for wave number " + (root.childrenList().indexOf(wave) + 1) +
                                " is invalid or was not provided.\n");
                    }
                }
        );
        return waves;
    }


}
