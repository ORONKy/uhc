package de.hglabor.plugins.uhc.game.phases

import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.GamePhase
import de.hglabor.plugins.uhc.game.PhaseType
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
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
    override fun init() {
        Bukkit.getWorld("world")?.time = 0
        GameManager.INSTANCE.resetTimer()
        HandlerList.unregisterAll()
        registerListener()
        GameManager.INSTANCE.scenarios.stream().filter { it.isEnabled }.forEach { it.isEnabled = false }
        GlobalChat.INSTANCE.enable(true)
    }

    override fun tick(timer: Int) {
        val winner = PlayerList.INSTANCE.alivePlayers.first()
        if (timer <= 5) {
            broadcast("${GlobalChat.getPrefix()}${KColors.BOLD}${winner.name} won")
        }
        if (timer > maxPhaseTime) {
            Bukkit.shutdown()
        }
    }

    override fun getTimeString(timer: Int) = "Game Ended"

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
            it.player.kickPlayer("${ChatColor.RED}GAME ALREADY STARTED")
        }
    }
}