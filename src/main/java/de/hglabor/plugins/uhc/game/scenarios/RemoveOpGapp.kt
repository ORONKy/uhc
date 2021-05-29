package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.player.PlayerItemConsumeEvent

object RemoveOpGapp : Scenario("RemoveOPGapp", ItemBuilder(Material.GOLDEN_APPLE).build()) {
    override fun registerEvents() {
        listen<PlayerItemConsumeEvent> {
            if (it.item.type == Material.ENCHANTED_GOLDEN_APPLE) {
                it.isCancelled = true
                it.player.sendMessage("${KColors.RED}OP Gold Apples are disabled.")
            }
        }
    }
}
