package de.hglabor.plugins.uhc.util

import de.hglabor.utils.localization.Localization
import de.hglabor.utils.noriskutils.ChatUtils
import org.bukkit.entity.Player

object PlayerExtensions {
    fun Player.sendMsg(key: String, values: Map<String, String>? = null) {
        this.sendMessage(Localization.INSTANCE.getMessage(key, values, ChatUtils.locale(this)))
    }
}