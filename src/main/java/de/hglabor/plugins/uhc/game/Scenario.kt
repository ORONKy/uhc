package de.hglabor.plugins.uhc.game

import de.hglabor.plugins.uhc.Uhc.Companion.INSTANCE
import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class Scenario(val name: String, val displayItem: ItemStack) : Listener {
    open var isEnabled = false
    open val requiredScenario: Scenario? = null
    open fun onFarmPhase() {}
    open fun onPvPPhase() {}
    open fun registerEvents() {}

    protected open fun saveToConfig() {
        val plugin = INSTANCE
        plugin.config.addDefault(CKeys.SCENARIOS + "." + name + "." + "enabled", false)
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
    }

    protected open fun loadConfig() {
        isEnabled = UHCConfig.getBoolean(CKeys.SCENARIOS + "." + name + "." + "enabled")
    }

    init {
        saveToConfig()
    }
}