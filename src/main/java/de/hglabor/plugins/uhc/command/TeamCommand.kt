package de.hglabor.plugins.uhc.command

import com.sun.tools.javac.tree.TreeInfo.args
import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.team.Teams
import de.hglabor.plugins.uhc.util.PlayerExtensions.sendMsg
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TeamCommand {
    init {
        val inviteArguments = listOf(GreedyStringArgument("invite"), PlayerArgument("player"))
        CommandAPICommand("team")
            .withRequirement { commandSender: CommandSender -> commandSender is Player }
            .withRequirement { Teams.maxTeamSize > 1 }
            .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.LOBBY }
            .withArguments(inviteArguments)
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                val invitedPlayer = args[0] as Player
                if (uhcPlayer.team != null && uhcPlayer.team.leader == uhcPlayer) {
                    uhcPlayer.team.invite(PlayerList.INSTANCE.getPlayer(invitedPlayer))
                } else {

                }
            }).register()

        val joinArguments = listOf(GreedyStringArgument("join"), PlayerArgument("player"))
        CommandAPICommand("team")
            .withRequirement { commandSender: CommandSender -> commandSender is Player }
            .withRequirement { Teams.maxTeamSize > 1 }
            .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.LOBBY }
            .withArguments(joinArguments)
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val teamLeader = args[0] as Player
                val uhcTeamLeader = PlayerList.INSTANCE.getPlayer(teamLeader)
                if (uhcTeamLeader.team != null && uhcTeamLeader.team.leader == uhcTeamLeader){
                    uhcTeamLeader.team.join(PlayerList.INSTANCE.getPlayer(player))
                } else {

                }
            }).register()
    }
}