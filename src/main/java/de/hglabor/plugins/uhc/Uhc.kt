package de.hglabor.plugins.uhc

import de.hglabor.plugins.uhc.command.*
import de.hglabor.plugins.uhc.config.CKeys
import de.hglabor.plugins.uhc.config.UHCConfig
import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.mechanics.GoldenHead
import de.hglabor.plugins.uhc.game.mechanics.MobAIRemover
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat
import de.hglabor.plugins.uhc.game.scenarios.*
import de.hglabor.plugins.uhc.scoreboard.ScoreboardManager
import de.hglabor.utils.localization.Localization
import de.hglabor.utils.noriskutils.DataPackUtils
import dev.jorel.commandapi.CommandAPI
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.nio.file.Paths

class Uhc : KSpigot() {

    companion object {
        lateinit var INSTANCE: Uhc; private set
    }

    override fun load() {
        INSTANCE = this
        UHCConfig.load()
        CommandAPI.onLoad(true)
        Localization.INSTANCE.loadLanguageFiles(Paths.get("$dataFolder/lang"), "\u00A7")
    }

    override fun startup() {
        val homeDir = dataFolder.parentFile.absolutePath.replace("/plugins".toRegex(), "")
        DataPackUtils.generateNewWorld(homeDir, "de.hglabor.uhc.worldgenerator")

        val gameManager = GameManager.INSTANCE
        gameManager.addScenario(BloodDiamondsNetherite.INSTANCE)
        gameManager.addScenario(CrossBowless.INSTANCE)
        gameManager.addScenario(CutClean.INSTANCE)
        gameManager.addScenario(Fireless.INSTANCE)
        gameManager.addScenario(HasteyBoys.INSTANCE)
        gameManager.addScenario(Netherless.INSTANCE)
        gameManager.addScenario(RodKnockback.INSTANCE)
        gameManager.addScenario(AppleDrop.INSTANCE)
        gameManager.addScenario(Timebomb.INSTANCE)
        gameManager.addScenario(Soup.INSTANCE)
        gameManager.addScenario(NoCooldown.INSTANCE)
        gameManager.addScenario(NoClean.INSTANCE)
        gameManager.addScenario(Timber.INSTANCE)
        gameManager.addScenario(Diamondless.INSTANCE)
        gameManager.addScenario(Enchantmentless.INSTANCE)
        gameManager.addScenario(ColdWeapons.INSTANCE)
        gameManager.addScenario(HealingKill.INSTANCE)
        gameManager.addScenario(Horseless.INSTANCE)
        gameManager.addScenario(EnchantedDeath.INSTANCE)
        gameManager.addScenario(DoubleOres.INSTANCE)
        gameManager.addScenario(DoubleHealth.INSTANCE)
        gameManager.addScenario(FlowerPower.INSTANCE)
        gameManager.addScenario(Teams)
        gameManager.addScenario(Shieldless.INSTANCE)
        gameManager.addScenario(DoNotDisturb.INSTANCE)
        gameManager.addScenario(Potionless.INSTANCE)
        gameManager.addScenario(OldKnockback.INSTANCE)
        gameManager.addScenario(Backpack)
        gameManager.run()
        gameManager.enableScenarios()

        GoldenHead.INSTANCE.register()

        CommandAPI.onEnable(this)
        registerCommand()
        registerListener()
        if (UHCConfig.getBoolean(CKeys.PREGEN_WORLD)) {
            Bukkit.broadcastMessage(GlobalChat.getPrefix() + ChatColor.BOLD + "PREGENERATING WORLD")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky cancel")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky confirm")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky world world")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky worldborder")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky start")
        }
    }

    override fun shutdown() {

    }

    private fun registerCommand() {
        StartCommand()
        GlobalChatCommand()
        InfoCommand()
        WorldTp()
        PvPPhaseCommand()
        BorderCommand()
        HostCommand()
        TeamCommand
        SendCoordsCommand
    }

    private fun registerListener() {
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(ScoreboardManager.INSTANCE, this)
        pluginManager.registerEvents(GoldenHead.INSTANCE, this)
        pluginManager.registerEvents(GlobalChat.INSTANCE, this)
        pluginManager.registerEvents(MobAIRemover(), this)
    }
}
