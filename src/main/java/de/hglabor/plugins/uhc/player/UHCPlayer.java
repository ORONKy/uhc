package de.hglabor.plugins.uhc.player;

import de.hglabor.plugins.uhc.team.UHCTeam;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public interface UHCPlayer extends ScoreboardPlayer {
    UUID getUuid();

    boolean isAlive();

    UserStatus getStatus();

    void setStatus(UserStatus status);

    String getName();

    UUID getCombatLogMob();

    void setCombatLogMob(UUID zombie);

    AtomicInteger getOfflineTime();

    boolean isInCombat();

    void setInCombat(boolean inCombat);

    AtomicInteger getKills();

    Optional<Player> getBukkitPlayer();

    Location getSpawnLocation();

    void setSpawnLocation(Location location);

    UHCTeam getTeam();

    void setTeam(UHCTeam team);

    int getTeamIndex();

    void setTeamIndex(int index);

    void sendMessage(String message);
}
