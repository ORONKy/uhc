package de.hglabor.plugins.uhc.team

import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import java.util.concurrent.atomic.AtomicInteger

object Teams {
    val maxTeamSize = UHCConfig.getInteger(CKeys.TEAMS_SIZE)
    private val teamList = mutableMapOf<Int, UHCTeam>()
    val createTeamItem = itemStack(Material.CYAN_BANNER) { meta { name = "${KColors.DODGERBLUE}Create Team" } }
    var currentTeamIndex = AtomicInteger()

    fun addTeam(index: Int, team: UHCTeam) = run { teamList[index] = team; }

    fun isEnabled() = maxTeamSize > 1
}
