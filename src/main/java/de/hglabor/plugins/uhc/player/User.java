package de.hglabor.plugins.uhc.player;

import de.hglabor.plugins.uhc.config.CKeys;
import de.hglabor.plugins.uhc.config.UHCConfig;
import de.hglabor.plugins.uhc.team.UHCTeam;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User implements UHCPlayer {
    private final UUID uuid;
    private final String name;
    private final AtomicInteger kills;
    private final AtomicInteger offlineTime;
    private boolean isTeleporting, inCombat;
    private int teamIndex;
    private UHCTeam uhcTeam;
    private UUID combatLogMob;
    private Location spawn;
    private UserStatus status;
    private Objective objective;
    private Scoreboard scoreboard;

    public User(UUID uuid, String name) {
        this.status = UserStatus.LOBBY;
        this.kills = new AtomicInteger();
        this.offlineTime = new AtomicInteger(UHCConfig.getInteger(CKeys.RELOG_TIME));
        this.uuid = uuid;
        this.name = name;
        this.teamIndex = -1;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean isAlive() {
        return status == UserStatus.INGAME || status == UserStatus.OFFLINE;
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getCombatLogMob() {
        return combatLogMob;
    }

    @Override
    public void setCombatLogMob(UUID zombie) {
        this.combatLogMob = zombie;
    }

    @Override
    public AtomicInteger getOfflineTime() {
        return offlineTime;
    }

    @Override
    public boolean isInCombat() {
        return inCombat;
    }

    @Override
    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    @Override
    public AtomicInteger getKills() {
        return kills;
    }

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    @Override
    public Locale getLocale() {
        return ChatUtils.locale(uuid);
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @Override
    public Location getSpawnLocation() {
        return spawn;
    }

    @Override
    public void setSpawnLocation(Location location) {
        spawn = location;
    }

    @Override
    public UHCTeam getTeam() {
        return uhcTeam;
    }

    @Override
    public void setTeam(UHCTeam team) {
        this.uhcTeam = team;
    }

    @Override
    public int getTeamIndex() {
        return teamIndex;
    }

    @Override
    public void setTeamIndex(int index) {
        this.teamIndex = index;
    }

    @Override
    public void sendMessage(String message) {
        getBukkitPlayer().ifPresent(player -> player.sendMessage(message));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uuid.equals(user.uuid) && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }
}
