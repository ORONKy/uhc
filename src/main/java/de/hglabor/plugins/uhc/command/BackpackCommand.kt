package de.hglabor.plugins.uhc.command

import de.hglabor.plugins.uhc.game.scenarios.Backpack
import de.hglabor.plugins.uhc.player.PlayerList
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor

object BackpackCommand {
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