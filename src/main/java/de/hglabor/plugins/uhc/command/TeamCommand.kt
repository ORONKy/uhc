package de.hglabor.plugins.uhc.command

import com.sun.tools.javac.tree.TreeInfo.args
import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.team.Teams
import de.hglabor.plugins.uhc.team.UHCTeam
import de.hglabor.plugins.uhc.util.PlayerExtensions.sendMsg
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
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
                    val invitedPlayer = args[1] as Player
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
                    val playerToJoin = PlayerList.INSTANCE.getPlayer(player)
                    val teamLeader = args[1] as Player
                    val uhcTeamLeader = PlayerList.INSTANCE.getPlayer(teamLeader)
                    val teamToJoin = uhcTeamLeader.team

                    if (teamToJoin != null) {

                        if (teamToJoin.invitedPlayers.contains(playerToJoin)) {
                            teamToJoin.players.add(playerToJoin)
                            teamToJoin.invitedPlayers.remove(playerToJoin)
                        }


                    }

                }).register()
        CommandAPICommand("team")
                .withRequirement { commandSender: CommandSender -> commandSender is Player }
                .withRequirement { Teams.maxTeamSize > 1 }
                .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.LOBBY }
                .withArguments(GreedyStringArgument("create"))
                .executesPlayer(PlayerCommandExecutor { player, _ ->
                    val leader = PlayerList.INSTANCE.getPlayer(player)
                    leader.team = UHCTeam(leader)
                    leader.teamIndex = Teams.currentTeamIndex.get()
                    Teams.addTeam(Teams.currentTeamIndex.get(), leader.team)
                    player.sendMessage("${KColors.GREENYELLOW}Du hast erfolgreich das Team Nummer [${Teams.currentTeamIndex.get()}] erstellt")
                }).register()
        CommandAPICommand("team")
                .withRequirement { commandSender: CommandSender -> commandSender is Player }
                .withRequirement { Teams.maxTeamSize > 1 }
                .withRequirement { PlayerList.INSTANCE.getPlayer(it as Player).team != null }
                .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.LOBBY }
                .withArguments(GreedyStringArgument("leave"))
                .executesPlayer(PlayerCommandExecutor { it, _ ->
                    val player = PlayerList.INSTANCE.getPlayer(it)
                    val team = player.team
                    if (team.leader.uuid.equals(player.uuid)) {
                        team.players.forEach {
                            it.teamIndex = -1
                            it.team = null
                            it.bukkitPlayer.ifPresent { player -> player.sendMessage("${KColors.ORANGERED}Das Team wurde aufgelöst.") }
                        }
                    } else {
                        team.players.remove(player)
                        team.players.forEach {
                            it.bukkitPlayer.ifPresent { player ->
                                player.sendMessage("${KColors.ALICEBLUE}${it.player.name} hat das Team verlassen.")
                            }
                        }
                        it.sendMessage("${KColors.ALICEBLUE}Du hast das Team verlassen.")
                    }
                }).register()
    }
}

