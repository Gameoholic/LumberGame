package xyz.gameoholic.lumbergame.config;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public record MapConfig(Location treeLocation, Location playerSpawnLocation, List<Location> spawnLocations, int treeRadius,
                        List<Material> treeBlockTypes, List<Integer> treeLevelSchematicsProvided) {

}
