package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.Uhc.Companion.INSTANCE
import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.team.UHCTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger

object Teams : Scenario("teams", ItemStack(Material.BELL)) {
    var maxTeamSize: Int = 1
    private val teamList = mutableMapOf<Int, UHCTeam>()
    val createTeamItem = itemStack(Material.CYAN_BANNER) { meta { name = "${KColors.DODGERBLUE}Create Team" } }
    var currentTeamIndex = AtomicInteger()

    fun addTeam(index: Int, team: UHCTeam) = run { teamList[index] = team; }

    override fun isEnabled() = super.isEnabled() && maxTeamSize > 1

    override fun saveToConfig() {
        INSTANCE.config.addDefault(CKeys.SCENARIOS + "." + name + "." + "teamSize", maxTeamSize)
        super.saveToConfig()
    }

    override fun loadConfig() {
        maxTeamSize = UHCConfig.getInteger(CKeys.SCENARIOS + "." + name + "." + "teamSize")
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        if (isEnabled) {
            return
        }
        if (!(event.entity is Player && event.damager is Player)) {
            return
        }
        val entity = PlayerList.INSTANCE.getPlayer(event.entity as Player)
        val damager = PlayerList.INSTANCE.getPlayer(event.damager as Player)
        if (damager.team.equals(entity.team)) {
            event.damage = 0.0
        }
    }
}
