package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.Uhc.Companion.INSTANCE
import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.ConfigInventory.openGUI
import de.hglabor.plugins.uhc.config.UHCConfig
import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.team.UHCTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger

object Teams : Scenario("teams", ItemStack(Material.BELL)) {
    val teamConfigItem = itemStack(Material.CYAN_BANNER) { meta { name = "${KColors.DODGERBLUE}Team Configuration" } }
    var maxTeamSize: Int = 1
    val teamList = mutableMapOf<Int, UHCTeam>()
    var currentTeamIndex = AtomicInteger()

    fun addTeam(index: Int, team: UHCTeam) = run { teamList[index] = team; }

    override fun isEnabled() = super.isEnabled() && maxTeamSize > 1

    override fun saveToConfig() {
        INSTANCE.config.addDefault(CKeys.SCENARIOS + "." + name + "." + "teamSize", maxTeamSize)
        super.saveToConfig()
    }

    override fun loadConfig() {
        super.loadConfig()
        maxTeamSize = UHCConfig.getInteger(CKeys.SCENARIOS + "." + name + "." + "teamSize")
    }

    override fun onPvPPhase() {
        listen<EntityDamageByEntityEvent> {
            if (!isEnabled) {
                return@listen
            }
            if (!(it.entity is Player && it.damager is Player)) {
                return@listen
            }
            val entity = PlayerList.INSTANCE.getPlayer(it.entity as Player)
            val damager = PlayerList.INSTANCE.getPlayer(it.damager as Player)

            if (damager.team == null || entity.team == null) {
                return@listen
            }

            if (damager.team.equals(entity.team)) {
                it.damage = 0.0
            }
        }
    }

    fun fillTeams() {
        PlayerList.INSTANCE.scatteringPlayers.filter { it.team == null }.forEach { uhcPlayer ->
            teamList.values.first { it.players.size < maxTeamSize && it.shouldFill }.forceJoin(uhcPlayer)
        }
    }
}

object TeamInventory {
    fun openGUI(player: Player) {
        val uhcTeam = PlayerList.INSTANCE.getPlayer(player).team
        player.openGUI(kSpigotGUI(GUIType.THREE_BY_THREE) {
            title = "${KColors.DODGERBLUE}Team Settings"
            page(1) {
                placeholder(Slots.Border, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = null } })
                button(Slots.RowTwoSlotTwo, itemStack(Material.OAK_DOOR) {
                    meta {
                        name = "${KColors.PURPLE}Team Auffüllen"
                    }
                }) {
                    uhcTeam.shouldFill = !uhcTeam.shouldFill
                    if (!uhcTeam.shouldFill) {
                        player.sendMessage("${GlobalChat.getPrefix()}${KColors.GRAY}Dein Team wird beim Scattern ${KColors.RED}nicht aufgefüllt${KColors.GRAY}.")
                    } else {
                        player.sendMessage("${GlobalChat.getPrefix()}${KColors.GRAY}Dein Team wird beim Scattern ${KColors.GREEN}aufgefüllt${KColors.GRAY}.")
                    }
                    it.guiInstance.reloadCurrentPage()
                }
            }
        })
    }
}


