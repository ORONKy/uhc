package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.player.PlayerList
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.inventory.ItemStack

object SharedHealth: Scenario("Shared Health", ItemStack(Material.PRISMARINE_SHARD)) {
    override val requiredScenario = Teams

    override fun registerEvents() {
        listen<EntityDamageEvent> {
            if (isEnabled) {
                setHealthForTeam(it)
            }
        }

        listen<EntityRegainHealthEvent> {
            if (isEnabled) {
                setHealthForTeam(it)
            }
        }
    }

    private fun setHealthForTeam(event: EntityEvent) {
        task(delay = 1) {
            val player = event.entity
            if (player is Player) {
                val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                val team = uhcPlayer.team
                team.players.filter { p -> p.isAlive }.forEach { p ->
                    if (player.health < p.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue!!) {
                        p.player.health = player.health
                    }
                }
            }
        }
    }
}