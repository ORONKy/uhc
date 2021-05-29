package de.hglabor.plugins.uhc.command

import de.hglabor.plugins.uhc.player.PlayerList
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.Player

object HelpOPCommand {
    init {
        CommandAPICommand("helpop")
            .withRequirement { commandSender -> commandSender is Player }
            .executesPlayer(PlayerCommandExecutor { player, args ->
                if (args.isNotEmpty()) {
                    var message = ""
                    args.forEach { message += "$it " }
                    onlinePlayers.filter { it.hasPermission("group.mod") && !PlayerList.INSTANCE.getPlayer(it).isAlive }
                        .forEach { staff ->
                            staff.sendMessage("${KColors.DARKGRAY}[${KColors.DARKRED}HelpOP${KColors.DARKGRAY}] ${KColors.RED}${player.name} ${KColors.DARKGRAY}» ${KColors.WHITE}$message")
                        }
                }
            }).register()
    }
}