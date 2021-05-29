package de.hglabor.plugins.uhc.game.scenarios;

import de.hglabor.plugins.uhc.game.Scenario;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class Timber extends Scenario {
    public final static Timber INSTANCE = new Timber();

    private Timber() {
        super("Timber", new ItemBuilder(Material.WOODEN_AXE).build());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isEnabled()) {
            return;
        }
        Block block = event.getBlock();
        String blockTypeName = block.getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            breakSurroundingWood(block, 0);
        }
    }

    @Override
    public void onPvPPhase() {
        this.setEnabled(false);
    }

    public void breakSurroundingWood(Block block, int amount) {
        String blockTypeName = block.getType().name().toLowerCase();

        if (amount > 12) {
            return;
        }

        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            block.breakNaturally();
            for (BlockFace face : BlockFace.values()) {
                breakSurroundingWood(block.getRelative(face), amount++);
            }
        }
    }
}
