package xyz.gameoholic.lumbergame.config;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public record MapConfig(Location treeLocation, List<Location> spawnLocations, int treeRadius,
                        List<Material> treeBlockTypes, List<Integer> treeLevelSchematicsProvided) {

}
