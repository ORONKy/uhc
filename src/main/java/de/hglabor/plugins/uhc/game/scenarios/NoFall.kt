package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack

object NoFall: Scenario("NoFall", ItemStack(Material.FEATHER)) {
    override fun registerEvents() {
        listen<EntityDamageEvent> {
            if (isEnabled) {
                if (it.entity is Player) {
                    if (it.cause == EntityDamageEvent.DamageCause.FALL) {
                        it.isCancelled = true
                    }
                }
            }
        }
    }
}