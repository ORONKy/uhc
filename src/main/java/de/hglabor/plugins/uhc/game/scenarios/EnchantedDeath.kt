package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.enchantment.EnchantItemEvent

object EnchantedDeath: Scenario("Enchanted Death", ItemBuilder(Material.WITHER_ROSE).build()) {
    override fun registerEvents() {
        listen<EnchantItemEvent> {
            it.enchanter.damage(2.0)
        }
    }
}