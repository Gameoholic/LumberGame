package xyz.gameoholic.lumbergame.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.Material;

public class WorldEditUtil {
    /**
     * Loads a schematic at a certain location.
     * <p>
     * Must be ran async.
     */


    public static void loadSchematic(File schematicFile, Location location) {
        World world = BukkitAdapter.adapt(location.getWorld());
        try {
            // Load the schematic file
            FileInputStream schematicStream = new FileInputStream(schematicFile);
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

            // Create a WorldEdit clipboard
            Clipboard clipboard = format.getReader(schematicStream).read();

            // Get the WorldEdit edit session
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                // Paste schematic at location
                Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.x(), location.y(), location.z()))
                    .build();
                Operations.complete(operation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills an area with a block of a certain type.
     * <p>
     * Must be ran async.
     */
    public static void fillArea(Location location1, Location location2, Material material) {
        CuboidRegion region = new CuboidRegion(
            BlockVector3.at(
                location1.x(),
                location1.y(),
                location1.z()
            ),
            BlockVector3.at(
                location2.x(),
                location2.y(),
                location2.z()
            )
        );
        fillCuboid(region, material, location1.getWorld());
    }

    private static void fillCuboid(CuboidRegion cuboid, Material material, org.bukkit.World world) {
        Region region = cuboid.getBoundingBox();

        var editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
        Pattern pattern = new BaseBlock(BukkitAdapter.adapt(material.createBlockData()));
        Mask mask = new ExistingBlockMask(editSession.getExtent());
        editSession.replaceBlocks(region, mask, pattern);
    }
}
