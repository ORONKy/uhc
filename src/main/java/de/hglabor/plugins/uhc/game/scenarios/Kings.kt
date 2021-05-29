package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Kings : Scenario("Kings", ItemStack(Material.GOLDEN_HELMET)) {
    override val requiredScenario = Teams

    override fun onFarmPhase() {
        if (isEnabled) {
            Teams.teamList.values.forEach { uhcTeam ->
                val leader = uhcTeam.leader.player
                leader.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, Int.MAX_VALUE, 0))
                leader.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
                val attribute: AttributeInstance? = leader.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                if (attribute != null) {
                    attribute.baseValue = attribute.baseValue * 2
                    leader.health = attribute.baseValue
                }
            }
        }
    }

    override fun registerEvents() {
        listen<PlayerItemConsumeEvent> {
            if (isEnabled) {
                if (it.item.type == Material.MILK_BUCKET) {
                    taskRunLater(1) {
                        val player = it.player
                        val uhcPlayer = PlayerList.INSTANCE.getPlayer(player)
                        val team = uhcPlayer.team
                        if (team.leader == uhcPlayer) {
                            player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, Int.MAX_VALUE, 0))
                            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
                        }
                    }
                }
            }
        }

        listen<PlayerDeathEvent> {
            if (isEnabled) {
                val leader = it.entity
                val uhcLeader = PlayerList.INSTANCE.getPlayer(leader)
                val team = uhcLeader.team
                if (team.leader == uhcLeader) {
                    team.players.filter { uhcPlayer -> uhcPlayer.isAlive }.forEach { uhcPlayer ->
                        val player = uhcPlayer.player
                        player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 180 * 20, 0))
                        player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 15 * 20, 0))
                        player.sendMessage("${GlobalChat.getPrefix()}Your king ${KColors.DARKRED}${leader.name} ${KColors.RED}died${KColors.GRAY}.")
                    }
                }
            }
        }
    }
}