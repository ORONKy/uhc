package de.hglabor.plugins.uhc.command

import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.scenarios.Teams
import de.hglabor.plugins.uhc.player.PlayerList
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
import org.bukkit.entity.Player

object SendCoordsCommand {
    init {
        CommandAPICommand("sendcoords")
            .withAliases("sc")
            .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.FARM || GameManager.INSTANCE.phaseType == PhaseType.PVP }
            .withRequirement { commandSender -> commandSender is Player }
            .withRequirement { Teams.isEnabled }
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                val loc = player.location
                val locationString = "${KColors.WHITE}x: ${KColors.DODGERBLUE}${loc.blockX} " +
                        "${KColors.WHITE}y: ${KColors.DODGERBLUE}${loc.blockY} " +
                        "${KColors.WHITE}z: ${KColors.DODGERBLUE}${loc.blockZ}"
                if (uhcPlayer.isAlive) {
                    uhcPlayer.team.players.forEach {
                        it.sendMessage("${KColors.RED}${player.name} ${KColors.GRAY}ist bei $locationString")
                    }
                } else {
                    uhcPlayer.sendMessage("${KColors.RED}Du bist bereits ausgeschieden.")
                }
            }).register()
    }
}