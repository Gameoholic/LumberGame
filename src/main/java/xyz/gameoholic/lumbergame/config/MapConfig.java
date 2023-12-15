package xyz.gameoholic.lumbergame.config;

import org.bukkit.Location;

import java.util.List;

public record MapConfig(Location treeLocation, List<Location> spawnLocations) {

}
