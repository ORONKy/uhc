package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import org.bukkit.Material

object Backpack: Scenario("Backpack", ItemBuilder(Material.ENDER_CHEST).build())