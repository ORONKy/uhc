package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object GappleRoulette : Scenario("Gapple Roulette", ItemBuilder(Material.GOLDEN_APPLE).build()) {
    override fun registerEvents() {
        listen<PlayerItemConsumeEvent> {
            if (isEnabled) {
                val randomAmplifier = 1..3
                if (it.item.type == Material.GOLDEN_APPLE) {
                    val potionEffect =
                        PotionEffect(PotionEffectType.values().random(), 3 * 20, randomAmplifier.random())
                    it.player.addPotionEffect(potionEffect)
                }
            }
        }
    }
}
