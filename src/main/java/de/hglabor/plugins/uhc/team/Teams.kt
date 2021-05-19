package de.hglabor.plugins.uhc.team

import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material

object Teams {
    val maxTeamSize = UHCConfig.getInteger(CKeys.TEAMS_SIZE)

    val teamList = mutableMapOf<Int, UHCTeam>()

    val createTeamItem = itemStack(Material.CYAN_BANNER) { meta { name = "${KColors.DODGERBLUE}Create Team" } }
}