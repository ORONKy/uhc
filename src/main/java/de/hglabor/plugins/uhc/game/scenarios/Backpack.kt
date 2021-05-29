package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.utils.noriskutils.ItemBuilder
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Material

object Backpack : Scenario("Backpack", ItemBuilder(Material.ENDER_CHEST).build()) {
    override val requiredScenario = Teams
    init {
        CommandAPICommand("backpack")
            .withAliases("bp")
            .withRequirement { Backpack.isEnabled }
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                if (PlayerList.INSTANCE.getPlayer(player).isAlive) {
                    player.openInventory(PlayerList.INSTANCE.getPlayer(player).team.backpack)
                }
            }).register()
    }
}
