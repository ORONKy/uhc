package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.player.PlayerList
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

object HealthAcceleration : Scenario("Health Acceleration", ItemStack(Material.POPPY)) {
    override fun onFarmPhase() {
        if (isEnabled) {
            for (player in Bukkit.getOnlinePlayers()) {
                val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                if (attribute != null) {
                    attribute.baseValue = 2.0
                    player.health = 2.0
                }
            }
        }
    }

    override fun registerEvents() {
        listen<PlayerDeathEvent> {
            if (isEnabled) {
                PlayerList.INSTANCE.alivePlayers.forEach { uhcPlayer ->
                    val player = uhcPlayer.player
                    val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    if (attribute != null) {
                        attribute.baseValue += 2.0
                        player.health += 2.0
                    }
                }
            }
        }
    }
}