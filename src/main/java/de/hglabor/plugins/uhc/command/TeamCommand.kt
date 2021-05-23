package de.hglabor.plugins.uhc.command

import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.game.scenarios.Teams
import de.hglabor.plugins.uhc.game.scenarios.hasTeamChatToggled
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.team.UHCTeam
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object TeamCommand {
    init {
        //TEAM INVITE
        CommandAPICommand("uhcteam")
            .withRequirement { commandSender: CommandSender -> commandSender is Player }
            .withRequirement { Teams.isEnabled }
            .withRequirement { GameManager.INSTANCE.phaseType == PhaseType.LOBBY }
            .withSubcommand(
                CommandAPICommand("join")
                    .withArguments(PlayerArgument("player"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        if (PlayerList.INSTANCE.getPlayer(player).teamIndex != -1) {
                            return@PlayerCommandExecutor
                        }
                        val uhcTeamLeader = PlayerList.INSTANCE.getPlayer(args[0] as Player)
                        val teamToJoin = uhcTeamLeader.team
                        teamToJoin.join(PlayerList.INSTANCE.getPlayer(player))
                    })
            )
            .withSubcommand(
                CommandAPICommand("invite")
                    .withArguments(PlayerArgument("player"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        if (PlayerList.INSTANCE.getPlayer(player).teamIndex == -1) {
                            return@PlayerCommandExecutor
                        }
                        val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                        if (uhcPlayer.team.leader == uhcPlayer) {
                            uhcPlayer.team.invite(PlayerList.INSTANCE.getPlayer(args[0] as Player))
                        } else {
                            player.sendMessage("${KColors.RED}Du bist kein Team Leader.")
                            player.sendMessage("${KColors.RED}Erstelle ein eigenes Team mit /UHCTeam create.")
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("create")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        if (PlayerList.INSTANCE.getPlayer(player).teamIndex == 1 || PlayerList.INSTANCE.getPlayer(player).team != null) {
                            return@PlayerCommandExecutor
                        }
                        val leader = PlayerList.INSTANCE.getPlayer(player)
                        val teamIndex = Teams.currentTeamIndex.incrementAndGet()
                        leader.team = UHCTeam(leader, teamIndex)
                        leader.teamIndex = teamIndex
                        Teams.addTeam(teamIndex, leader.team)
                        player.sendMessage("${KColors.GREENYELLOW}Du hast erfolgreich das Team Nummer [${teamIndex}] erstellt")
                    })
            )
            .withSubcommand(
                CommandAPICommand("leave")
                    .executesPlayer(PlayerCommandExecutor { it, _ ->
                        if (PlayerList.INSTANCE.getPlayer(it).teamIndex == -1) {
                            return@PlayerCommandExecutor
                        }
                        val player = PlayerList.INSTANCE.getPlayer(it)
                        val team = player.team
                        team.leave(player)
                    })
            )
            .withSubcommand(
                CommandAPICommand("list")
                    .withArguments(PlayerArgument("leader"))
                    .executesPlayer(PlayerCommandExecutor { it, args ->
                        val player = PlayerList.INSTANCE.getPlayer(args[0] as Player)
                        val team = player.team
                        player.sendMessage("Team of ${team.leader.name}")
                        team.players.forEach { teamMember ->
                            it.sendMessage(" - ${teamMember.name} | Health: ${teamMember.player.health.toInt()}")
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("haha")
                    .executesPlayer(PlayerCommandExecutor { it, _ ->
                        it.sendMessage("${KColors.MEDIUMVIOLETRED}Wieso führst du diesen Command aus?")
                    })
            ).withSubcommand(
                CommandAPICommand("togglechat")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                        Teams.teamChat[uhcPlayer] = !uhcPlayer.hasTeamChatToggled
                        player.sendMessage("${GlobalChat.getPrefix()}Du schreibst nun standardmäßig im ${if (uhcPlayer.hasTeamChatToggled) "${KColors.GREEN}Teamchat" else "${KColors.TOMATO}öffentlichen Chat"}")
                    })
            ).register()
    }

}

