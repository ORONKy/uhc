package de.hglabor.plugins.uhc.game.scenarios

import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object FlowerPower : Scenario("Flower Power", ItemBuilder(Material.RED_TULIP).build()) {
    private val flowers = listOf(
        Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
        Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
        Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.WITHER_ROSE, Material.SUNFLOWER, Material.LILAC,
        Material.ROSE_BUSH, Material.PEONY
    )

    private val materials = Material.values().filter {
        !flowers.contains(it) && !it.name.contains("COMMAND") && !it.name.contains("POTTED") &&
                !it.name.contains("_WALL_") && !it.name.contains("_GATEWAY") && !it.name.contains("PORTAL")
    }


    override fun registerEvents() {
        listen<BlockBreakEvent> {
            val block = it.block
            if (flowers.contains(block.type) || block.type.name.toLowerCase().contains("_coral")) {
                var i = 0
                do {
                    try {
                        i = 0
                        it.isDropItems = false
                        val itemStack = ItemStack(materials.random())
                        if (itemStack.maxStackSize > 1) itemStack.amount = randomAmount
                        val location = block.location.add(0.5, 0.0, 0.5)
                        location.world.dropItem(location, itemStack)
                    } catch (e: IllegalArgumentException) {
                        i++
                    }
                } while (i > 0)
            }
        }
    }

    private val randomAmount: Int
        get() {
            val random = Random()
            val r1 = random.nextInt(10)
            return if (r1 < 3) random.nextInt(10) else if (r1 < 6) random.nextInt(15) + 10 else if (r1 < 8) random.nextInt(
                20
            ) + 25 else if (r1 < 9) random.nextInt(
                19
            ) + 45 else random.nextInt(64)
        }
}