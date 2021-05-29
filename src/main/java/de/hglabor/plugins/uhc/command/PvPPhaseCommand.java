package de.hglabor.plugins.uhc.command;

import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.game.phases.FarmPhase;
import de.hglabor.utils.noriskutils.PermissionUtils;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPPhaseCommand {
    public PvPPhaseCommand() {
        new CommandAPICommand("pvpphase")
                .withPermission("hglabor.uhc.host")
                .withRequirement(commandSender -> {
                    if (commandSender.isOp()) return true;
                    if (commandSender instanceof Player) {
                        return !PermissionUtils.checkForHigherRank((Player) commandSender);
                    }
                    return true;
                })
                .executesPlayer((player, objects) -> {
                    if (GameManager.INSTANCE.getPhaseType().equals(PhaseType.FARM)) {
                        FarmPhase farmPhase = (FarmPhase) GameManager.INSTANCE.getPhase();
                        farmPhase.startNextPhase();
                        player.sendMessage(GlobalChat.getPrefix() + GlobalChat.hexColor("#F45959") + "PvPPhase wurde gestartet");
                        return;
                    }
                    player.sendMessage(GlobalChat.getPrefix() + ChatColor.RED + "PvPPhase konnte nicht gestartet werden");
                }).register();
    }
}
