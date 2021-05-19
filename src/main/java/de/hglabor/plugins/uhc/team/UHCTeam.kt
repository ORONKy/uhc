package de.hglabor.plugins.uhc.team

import de.hglabor.plugins.uhc.player.UHCPlayer
import de.hglabor.plugins.uhc.util.PlayerExtensions.sendMsg
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

class UHCTeam(val leader: UHCPlayer) {
    val players = mutableListOf(leader)
    val invitedPlayers = mutableListOf<UHCPlayer>()
    val shouldFillTeam = true
    val backpack = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("Backpack"))
    val teamKills = 0

    fun invite(uhcPlayer: UHCPlayer) {
        if (players.size >= Teams.maxTeamSize) {
            uhcPlayer.player.sendMsg("teams.invitation.teamIsFull")
            return
        }
        if (invitedPlayers.contains(uhcPlayer)) {
            uhcPlayer.player.sendMsg("teams.invitation.alreadySent", mutableMapOf("playerName" to uhcPlayer.name))
            return
        }
        invitedPlayers += uhcPlayer
        uhcPlayer.player.sendMsg("teams.invitation.received", mutableMapOf("teamLeaderName" to leader.name))
        leader.player.sendMsg("teams.invitation.sent", mutableMapOf("playerName" to uhcPlayer.name))

        task(sync = false, delay = 45 * 20) {
            invitedPlayers -= uhcPlayer
            uhcPlayer.player.sendMsg("teams.invitation.expired", mutableMapOf("teamLeaderName" to leader.name))
        }
    }

    fun join(uhcPlayer: UHCPlayer) {
        if (!invitedPlayers.contains(uhcPlayer)) {

        }
    }

    // this will be used to fill the teams
    fun forceJoin() {
        
    }
}