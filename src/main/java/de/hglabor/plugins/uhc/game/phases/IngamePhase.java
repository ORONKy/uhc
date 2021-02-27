package de.hglabor.plugins.uhc.game.phases;

import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.game.mechanics.DeathMessenger;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.plugins.uhc.player.UserStatus;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class IngamePhase extends GamePhase {
    private final DeathMessenger deathMessenger;

    protected IngamePhase(int maxPhaseTime, PhaseType type) {
        super(maxPhaseTime, type);
        this.deathMessenger = new DeathMessenger();
    }

    @Override
    protected void tick(int timer) {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        UHCPlayer uhcPlayer = playerList.getPlayer(player);
        if (uhcPlayer.getStatus().equals(UserStatus.OFFLINE)) {
            uhcPlayer.setStatus(UserStatus.INGAME);
        } else if (uhcPlayer.getStatus().equals(UserStatus.ELIMINATED)) {
            player.kickPlayer(Localization.INSTANCE.getMessage("ingamePhase.join.death", ChatUtils.getPlayerLocale(player)));
        } else if (uhcPlayer.getStatus().equals(UserStatus.LOBBY)) {
            player.kickPlayer(ChatColor.RED + "GAME ALREADY STARTED");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        UHCPlayer uhcPlayer = playerList.getPlayer(player);
        if (uhcPlayer.getStatus().equals(UserStatus.INGAME)) {
            uhcPlayer.setStatus(UserStatus.OFFLINE);
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UHCPlayer uhcPlayer = playerList.getPlayer(player);

        if (player.getKiller() != null) {
            UHCPlayer killer = playerList.getPlayer(player.getKiller());
            killer.getKills().incrementAndGet();
            deathMessenger.broadcast(killer, uhcPlayer);
        } else {
            if (event.getDeathMessage() != null) {
                deathMessenger.broadcast(uhcPlayer, event.getDeathMessage());
            } else {
                deathMessenger.broadcast(uhcPlayer);
            }
        }

        if (player.hasPermission("hglabor.spectator")) {
            player.setGameMode(GameMode.SPECTATOR);
            uhcPlayer.setStatus(UserStatus.SPECTATOR);
        } else {
            uhcPlayer.setStatus(UserStatus.ELIMINATED);
            player.kickPlayer("Death");
        }
        event.setDeathMessage(null);
    }

    @Override
    public String getTimeString(int timer) {
        return null;
    }

    @Override
    protected GamePhase getNextPhase() {
        return null;
    }
}
