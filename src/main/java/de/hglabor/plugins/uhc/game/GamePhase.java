package de.hglabor.plugins.uhc.game;

import de.hglabor.plugins.uhc.Uhc;
import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GamePhase implements Listener {
    protected final JavaPlugin plugin;
    protected final PlayerList playerList;
    protected final PhaseType type;
    protected long maxPhaseTimeStamp;

    protected GamePhase(int maxPhaseTime, PhaseType type) {
        this.maxPhaseTimeStamp = System.currentTimeMillis() + maxPhaseTime * 1000L;
        this.type = type;
        this.plugin = Uhc.Companion.getINSTANCE();
        this.playerList = PlayerList.INSTANCE;
    }

    public void startNextPhase() {
        HandlerList.unregisterAll(this);
        GamePhase nextPhase = getNextPhase();
        nextPhase.init();
        Bukkit.getPluginManager().registerEvents(nextPhase, plugin);
        GameManager.INSTANCE.setPhase(nextPhase);
    }

    protected void init() {
    }

    protected abstract void tick();

    public PhaseType getType() {
        return type;
    }

    public abstract String getTimeString();

    public int getAlivePlayers() {
        return (int) playerList.getAllPlayers().stream().filter(UHCPlayer::isAlive).count();
    }

    protected abstract GamePhase getNextPhase();

    public long getMaxPhaseTimeStamp() {
        return maxPhaseTimeStamp;
    }
}
