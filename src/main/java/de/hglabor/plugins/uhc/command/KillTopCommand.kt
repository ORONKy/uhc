package de.hglabor.plugins.uhc.command

import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.player.PlayerList
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
import org.bukkit.entity.Player

object KillTopCommand {
    init {
        CommandAPICommand("killtop")
            .withAliases("kt")
            .withRequirement { GameManager.INSTANCE.phaseType != PhaseType.LOBBY }
            .withRequirement { commandSender -> commandSender is Player }
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                val uhcPlayers = ArrayList(PlayerList.INSTANCE.allPlayers)
                uhcPlayers.sortedBy { it.kills.get() }
                val strike =  "${KColors.RESET}${KColors.STRIKETHROUGH}               "
                player.sendMessage("${strike}${KColors.RED}Killtop${strike}")
                for (i in 0..9) {
                    val uhcKiller = uhcPlayers[i] ?: return@PlayerCommandExecutor
                    player.sendMessage("${KColors.DARKRED}${i + 1}. ${KColors.ORANGE}${uhcKiller.name}${KColors.GRAY}: ${KColors.WHITE}${uhcKiller.kills.get()} Kills")
                }
            }).register()
    }
}