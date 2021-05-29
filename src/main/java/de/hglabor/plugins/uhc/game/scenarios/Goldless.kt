package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

object Goldless : Scenario("Goldless", ItemStack(Material.GOLD_INGOT)) {

    override fun registerEvents() {
        listen<BlockBreakEvent> {
            if (isEnabled) {
                val block = it.block
                if (block.type == Material.DIAMOND_ORE) {
                    it.block.drops.clear()
                }
            }
        }

        listen<PlayerDeathEvent> {
            if (isEnabled) {
                it.drops.add(ItemStack(Material.GOLD_INGOT, 4))
            }
        }
    }
}
