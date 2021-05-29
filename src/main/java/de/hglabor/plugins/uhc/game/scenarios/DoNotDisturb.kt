package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.player.UHCPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

object DoNotDisturb: Scenario("DoNotDisturb", ItemStack(Material.LEAD)) {
    var threads = HashMap<UHCPlayer, NoCleanPlusThread>()

    override fun registerEvents() {
        listen<EntityDamageByEntityEvent> { event ->
            if (isEnabled) {
                if (event.entity is Player && event.damager is Player) {
                    val player = PlayerList.INSTANCE.getPlayer(event.entity as Player)
                    val damager = PlayerList.INSTANCE.getPlayer(event.damager as Player)
                    if (Teams.isEnabled) {
                        if (player.team == damager.team) {
                            return@listen
                        }
                    }
                    if (threads.containsKey(player)) {
                        if (threads[player]!!.opponent !== damager) {
                            event.isCancelled = true
                            val thread = threads[player]
                            damager.sendMessage("${GlobalChat.getPrefix()}${KColors.DARKRED}${player.name} ${KColors.GRAY}kämpft bereits gegen ${KColors.DARKRED}${thread?.opponent?.name}${KColors.GRAY}.")
                        } else {
                            threads[player]!!.resetRunnable()
                            threads[damager]!!.resetRunnable()
                        }
                    } else if (threads.containsKey(damager)) {
                        val thread = threads[damager]
                        if (thread!!.opponent !== player) {
                            event.isCancelled = true
                            damager.sendMessage("${GlobalChat.getPrefix()}Du kämpfst bereits gegen ${KColors.DARKRED}${thread?.opponent?.name}${KColors.GRAY}.")
                        }
                    } else {
                        NoCleanPlusThread(player, damager)
                        NoCleanPlusThread(damager, player)
                    }
                }
            }
        }
    }

    class NoCleanPlusThread(private val player: UHCPlayer, val opponent: UHCPlayer) {
        private val start: Long = System.currentTimeMillis()
        private var task: KSpigotRunnable? = null
        private fun startRunnable() {
            task = task(false, howOften = 30, delay = 20, period = 20) {
                if (remainingTime == 0) {
                    player.sendMessage("${GlobalChat.getPrefix()}Du bist nun nicht mehr im Kampf gegen ${KColors.RED}${opponent.name}")
                    threads.remove(player)
                } else {
                    if (remainingTime % 10 == 0 || remainingTime == 5 || remainingTime in 1..3) {
                        player.sendMessage("${GlobalChat.getPrefix()}Der Kampf gegen ${KColors.RED}${opponent.name} ${KColors.GRAY}endet in ${KColors.TOMATO}" +
                                "${if (remainingTime == 1) "Sekunde" else "Sekunden"}${KColors.GRAY}.")
                    }
                }
            }
        }

        fun resetRunnable() {
            task!!.cancel()
            startRunnable()
        }

        private val remainingTime: Int
            get() {
                val now = System.currentTimeMillis()
                val rest = start + 1000 * 30 - now
                return rest.toInt() / 1000
            }

        init {
            startRunnable()
            threads[player] = this
        }
    }
}