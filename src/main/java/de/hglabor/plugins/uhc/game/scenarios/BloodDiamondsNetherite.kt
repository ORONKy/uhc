package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object BloodDiamondsNetherite : Scenario("Blood Diamonds and Netherite", ItemStack(Material.DIAMOND_ORE)) {
    override fun registerEvents() {
        listen<BlockBreakEvent> {
            if (it.block.type == Material.DIAMOND_ORE) {
                it.player.damage(1.0)
            }
            if (it.block.type == Material.ANCIENT_DEBRIS) {
                it.player.damage(2.0)
            }
        }
    }
}