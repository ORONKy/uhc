package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.Uhc.Companion.INSTANCE
import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.player.PlayerList
import de.hglabor.plugins.uhc.player.UHCPlayer
import de.hglabor.plugins.uhc.team.UHCTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger

object Teams : Scenario("teams", ItemStack(Material.BELL)) {
    val teamConfigItem = itemStack(Material.CYAN_BANNER) { meta { name = "${KColors.DODGERBLUE}Team Configuration" } }
    var maxTeamSize: Int = 1
    val teamList = mutableMapOf<Int, UHCTeam>()
    var currentTeamIndex = AtomicInteger()
    val teamChat = mutableMapOf<UHCPlayer, Boolean>()

    fun addTeam(index: Int, team: UHCTeam) = run { teamList[index] = team; }

    override var isEnabled = super.isEnabled && maxTeamSize > 1

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
            val teams = teamList.values.filter { it.players.size < maxTeamSize && it.shouldFill }
            if (teams.isNotEmpty()) {
                teamList.values.first { it.players.size < maxTeamSize && it.shouldFill }.forceJoin(uhcPlayer)
            } else {
                val teamIndex = currentTeamIndex.incrementAndGet()
                uhcPlayer.team = UHCTeam(uhcPlayer, teamIndex)
                uhcPlayer.teamIndex = teamIndex
                addTeam(teamIndex, uhcPlayer.team)
            }
        }
    }
}

val UHCPlayer.hasTeamChatToggled get() = Teams.teamChat[this] ?: false

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


