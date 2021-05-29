package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.player.PlayerList
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack

object PiggyBack : Scenario("Piggy Back", ItemStack(Material.SADDLE)) {
    override val requiredScenario = Teams

    override fun registerEvents() {
        listen<PlayerInteractAtEntityEvent> {
            val player = it.player
            val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
            if (it.rightClicked is Player) {
                val uhcRightClicked = PlayerList.INSTANCE.getPlayer(it.rightClicked as Player)
                if (uhcPlayer.team == uhcRightClicked.team) {
                    uhcRightClicked.player.addPassenger(player)
                }
            }
        }
    }
}
