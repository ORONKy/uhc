package de.hglabor.plugins.uhc.game.phases

import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.GamePhase
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.game.scenarios.Teams
import de.hglabor.plugins.uhc.game.scenarios.Teams.teamList
import de.hglabor.plugins.uhc.player.PlayerList
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.PortalCreateEvent

object EndPhase : GamePhase(120, PhaseType.END) {
    private var winnerAnnouncement = 0;

    override fun init() {
        Bukkit.getWorld("world")?.time = 0
        HandlerList.unregisterAll()
        registerListener()
        GameManager.INSTANCE.scenarios.stream().filter { it.isEnabled }.forEach { it.isEnabled = false }
        GlobalChat.INSTANCE.enable(true)
    }

    override fun tick() {
        var winnerString = ""
        if (Teams.isEnabled) {
            winnerString += "${GlobalChat.getPrefix()}${KColors.GRAY}Die Gewinner sind: "
            val winnerTeam = teamList.values.first { !it.isEliminated }
            winnerTeam.players.iterator().forEach { player ->
                winnerString += "${KColors.RED}${player.name}"
                if (winnerTeam.players.last() != player) {
                    winnerString += "§8, "
                }
            }
        } else {
            winnerString += "${GlobalChat.getPrefix()}${KColors.GRAY}Der Gewinner ist: ${KColors.RED}${PlayerList.INSTANCE.alivePlayers.first().name}"
        }

        if (winnerAnnouncement++ <= 5) {
            broadcast(winnerString)
        }
        if (System.currentTimeMillis() > maxPhaseTimeStamp) {
            Bukkit.shutdown()
        }
    }

    override fun getTimeString() = "${ChatColor.RED}Game Ended"

    override fun getNextPhase() = null

    private fun registerListener() {
        listen<EntityDamageEvent> {
            it.isCancelled = it.entity is Player
        }

        listen<FoodLevelChangeEvent> {
            it.isCancelled = true
        }

        listen<WeatherChangeEvent> {
            it.isCancelled = true
        }

        listen<PortalCreateEvent> {
            it.isCancelled = true
        }

        listen<PlayerJoinEvent> {
            it.player.kickPlayer("${ChatColor.RED}GAME ALREADY ENDED")
        }
    }
}
