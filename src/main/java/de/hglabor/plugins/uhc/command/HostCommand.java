package de.hglabor.plugins.uhc.command;

import de.hglabor.plugins.uhc.config.ConfigInventory;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class HostCommand {
    public HostCommand() {
        new CommandAPICommand("host")
                .withPermission("hglabor.forcestart")
                .withRequirement(commandSender -> commandSender instanceof Player)
                .executesPlayer((player, objects) -> {
                    ConfigInventory.INSTANCE.openGUI(player);
                }).register();
    }
}
