package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemStack

object DoubleHealth: Scenario("Double Health", ItemStack(Material.POPPY, 2)) {
    override fun onFarmPhase() {
        onlinePlayers.forEach { player ->
            val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            attribute?.baseValue = 40.0
            player.health = 40.0
        }
    }
}