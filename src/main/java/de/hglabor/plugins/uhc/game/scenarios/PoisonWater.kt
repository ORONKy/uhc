package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.Uhc
import de.hglabor.plugins.uhc.game.Scenario
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PoisonWater : Scenario("Poison Water", ItemStack(Material.WATER_BUCKET)) {

    /**
     * Time until potion effect is applied
     */
    private const val timeToPoison:Long = 3

    /**
     * Duration of the potion effect
     */
    private const val poisonDuration:Int = 2

    override fun registerEvents() {
        listen {
            event: PlayerMoveEvent ->
            run {
                if (event.player.isInWater) {
                    var waterTimer = Bukkit.getScheduler().runTaskLater(Uhc.Companion.INSTANCE,Runnable{
                        if(event.player.isInWater) {
                            event.player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 20* poisonDuration, 1))
                        } }, 20* timeToPoison);
                }
            }
        }
    }
}