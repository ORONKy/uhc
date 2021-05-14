package de.hglabor.plugins.uhc.config

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.hglabor.plugins.uhc.game.GameManager
import de.hglabor.plugins.uhc.game.Scenario
import de.hglabor.utils.noriskutils.ItemBuilder
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.gui.elements.GUIRectSpaceCompound
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*

object ConfigInventory {

    fun openGUI(player: Player) {
        player.openGUI(kSpigotGUI(GUIType.FIVE_BY_NINE) {
            title = "${KColors.DODGERBLUE}UHC Settings"
            page(1) {
                placeholder(Slots.Border, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = null } })
                placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight,
                    itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { name = null } })

                button(Slots.RowThreeSlotThree, itemStack(Material.BOOK) { meta { name = "${KColors.PURPLE}Scenarios" } }) {
                    it.guiInstance.gotoPage(2)
                }
            }

            page(2) {
                val compound = createRectCompound<Scenario>(Slots.RowTwoSlotTwo, Slots.RowFourSlotEight,
                    iconGenerator = { scenario ->
                        ItemBuilder(scenario.displayItem).setName("${KColors.DODGERBLUE}${scenario.name}")
                            .hideItemFlags().hideEnchants().build()
                    }, onClick = { clickEvent, scenario ->
                        clickEvent.bukkitEvent.isCancelled = true
                        scenario.displayItem.meta {
                            if (scenario.isEnabled) {
                                removeEnchant(Enchantment.LUCK)
                            } else {
                                addEnchant(Enchantment.LUCK, 1, true)
                            }
                        }
                        clickEvent.guiInstance.reloadCurrentPage()
                        scenario.isEnabled = !scenario.isEnabled
                        clickEvent.player.sendMessage("${KColors.DODGERBLUE}${scenario.name} ${KColors.WHITE}is now ${KColors.RED}${scenario.isEnabled}")
                    })
                setScrollableCompoundLayout(compound)
                compound.setContent(GameManager.INSTANCE.scenarios)
            }
        })
    }

    private fun GUIPageBuilder<ForInventoryFiveByNine>.setDefaultLayout() {
        this.placeholder(Slots.Border, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = null } })
        this.button(Slots.RowOneSlotOne, getSkull(
            "https://textures.minecraft.net/texture/91459cfc44cc51ddf27988596d2de8ac8556e93d7946219cf64c90c8c05fca",
            "${KColors.CORNFLOWERBLUE}Main Menu")) {
            it.bukkitEvent.isCancelled = true
            it.guiInstance.gotoPage(1)
        }

        this.button(Slots.RowOneSlotNine, itemStack(Material.BARRIER) { meta { name = "${KColors.RED}Close" } }) {
            it.bukkitEvent.isCancelled = true
            it.player.closeInventory()
        }
    }

    private fun GUIPageBuilder<ForInventoryFiveByNine>.setScrollableCompoundLayout(compound: GUIRectSpaceCompound<ForInventoryFiveByNine, Scenario>) {
        setDefaultLayout()
        compoundScroll(Slots.RowFiveSlotFive, getSkull(
            "https://textures.minecraft.net/texture/f4628ace7c3afc61a476dc144893aaa642ba976d952b51ece26abafb896b8",
            "${KColors.CORNFLOWERBLUE}Scroll Up"), compound, 7 * 3, true)
        compoundScroll(Slots.RowOneSlotFive, getSkull(
            "https://textures.minecraft.net/texture/2ae425c5ba9f3c2962b38178cbc23172a6c6215a11accb92774a4716e96cada",
            "${KColors.CORNFLOWERBLUE}Scroll down"), compound, 7 * 3)
    }

    val lobbyPhaseItem = itemStack(Material.DIAMOND) { meta { name = "${KColors.DODGERBLUE}Host Configurations" } }

    private fun getSkull(url: String, itemName: String): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), null)
        val encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).toByteArray())
        profile.properties.put("textures", Property("textures", String(encodedData)))
        var profileField: Field
        return itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                name = itemName
                profileField = javaClass.getDeclaredField("profile")
                profileField.isAccessible = true
                profileField[this] = profile
            }
        }
    }
}
