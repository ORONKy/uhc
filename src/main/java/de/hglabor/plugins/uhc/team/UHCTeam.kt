package de.hglabor.plugins.uhc.team

import de.hglabor.plugins.uhc.game.scenarios.Teams
import de.hglabor.plugins.uhc.player.UHCPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

class UHCTeam(val leader: UHCPlayer, private val teamIndex: Int) {
    val players = mutableSetOf(leader)
    private val invitedPlayers = mutableListOf<UHCPlayer>()
    val shouldFillTeam = true
    val backpack = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Backpack"))
    val teamKills: Int
        get() = players.sumBy { it.kills.get() }

    /**
     * invites player but invitation will be exipired
     */
    fun invite(uhcPlayer: UHCPlayer) {
        if (players.size >= Teams.maxTeamSize) {
            leader.sendMessage("${KColors.RED}Dein Team ist voll.")
            return
        }
        if (invitedPlayers.contains(uhcPlayer)) {
            leader.sendMessage("${KColors.RED}Du hast diesen Spieler bereits eingeladen.")
            return
        }
        if (players.contains(uhcPlayer)) {
            leader.sendMessage("${KColors.RED}Dieser Spieler ist bereits in deinem Team.")
            return
        }
        if (uhcPlayer.team != null) {
            leader.sendMessage("${KColors.RED}Dieser Spieler ist bereits in einem Team.")
            return
        }

        invitedPlayers += uhcPlayer
        uhcPlayer.sendMessage("${KColors.GREEN}Du wurdest von ${leader.name} in sein Team eingeladen.")
        uhcPlayer.sendMessage("${KColors.GREEN}Tippe ${KColors.YELLOW}/UHCTeam join ${leader.name}${KColors.GREEN} um zu joinen.")
        leader.sendMessage("${KColors.GREEN}Du hast ${uhcPlayer.name} in dein Team eingeladen.")

        task(sync = false, delay = 45 * 20) {
            if (invitedPlayers.contains(uhcPlayer)) {
                invitedPlayers -= uhcPlayer
                uhcPlayer.sendMessage("${KColors.GREEN}Die Einladung von ${leader.name} ist abgelaufen.")
                leader.sendMessage("${KColors.YELLOWGREEN}Die Einladung für ${uhcPlayer.name} ist abgelaufen.")
            }
        }
    }

    /**
     * Player will join a team if hes in no other team + he was invited
     */
    fun join(uhcPlayer: UHCPlayer) {
        if (invitedPlayers.contains(uhcPlayer)) {
            //No Team
            if (uhcPlayer.team == null) {

                //TEAM FULL
                if (players.size >= Teams.maxTeamSize) {
                    leader.sendMessage("${KColors.RED}${uhcPlayer.name} konnte nicht joinen weil das Team voll ist.")
                    uhcPlayer.sendMessage("${KColors.RED}Das Team von ${leader.name} ist bereits voll.")
                    return
                }

                uhcPlayer.team = this;
                uhcPlayer.teamIndex = teamIndex
                invitedPlayers.remove(uhcPlayer)
                players.forEach { it.sendMessage("${KColors.GREEN}${uhcPlayer.name} ist dem Team beigetreten.") }
                players.add(uhcPlayer)
                uhcPlayer.sendMessage("${KColors.GREEN}Du bist dem Team von ${leader.name}[${teamIndex}] beigetreten.")
            } else {
                uhcPlayer.sendMessage("${KColors.RED}Du bist bereits in einem Team.")
                uhcPlayer.sendMessage("${KColors.RED}/UHCTeam leave um dein Team zu verlassen.")
            }
        } else {
            uhcPlayer.sendMessage("${KColors.DARKRED}Du hast keine Einladung für dieses Team erhalten.")
        }
    }

    /**
     * Player will leave the team
     * If hes the leader team will be closed.
     */
    fun leave(uhcPlayer: UHCPlayer) {
        if (leader.uuid.equals(uhcPlayer.uuid)) {
            close()
        } else {
            if (players.remove(uhcPlayer)) {
                uhcPlayer.team = null
                uhcPlayer.teamIndex = -1
                uhcPlayer.sendMessage("${KColors.GREEN}Du hast das Team erfolgreich verlassen.")
                players.forEach {
                    it.sendMessage("${KColors.RED}${uhcPlayer.name} hat das Team verlassen.")
                }
            } else {
                uhcPlayer.sendMessage("${KColors.RED}Es gab ein Problem beim Verlassen des Teams.")
            }
        }
    }

    /**
     * Team will be deleted if team leader leaves the team
     */
    private fun close() {
        players.forEach {
            it.team = null
            it.teamIndex = -1
            it.sendMessage("${KColors.RED}Das Team wurde aufgelöst")
        }
        players.clear()
        invitedPlayers.clear()
    }

    // this will be used to fill the teams
    fun forceJoin() {

    }
}
