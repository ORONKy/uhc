package de.hglabor.plugins.uhc.game;

import de.hglabor.plugins.uhc.Uhc;
import de.hglabor.plugins.uhc.game.mechanics.border.Border;
import de.hglabor.plugins.uhc.game.phases.LobbyPhase;
import de.hglabor.plugins.uhc.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class GameManager {
    public static final GameManager INSTANCE = new GameManager();
    private final Set<Scenario> scenarios;
    private GamePhase phase;

    private GameManager() {
        this.phase = new LobbyPhase();
        this.scenarios = new HashSet<>();
    }

    public void run() {
        phase.init();
        Bukkit.getScheduler().runTaskTimer(Uhc.Companion.getINSTANCE(), () -> {
            phase.tick();
        }, 0, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Uhc.Companion.getINSTANCE(), ScoreboardManager::updateForEveryone,0,20);
    }

    public void enableScenarios() {
        scenarios.forEach(Scenario::loadConfig);
        scenarios.stream().filter(Scenario::isEnabled).forEach(scenario -> {
            if (scenario.getRequiredScenario() != null && !scenario.getRequiredScenario().isEnabled()) {
                scenario.setEnabled(false);
            }
        });
    }

    public void registerScenarioEvents() {
        for (Scenario scenario : scenarios) {
            if (scenario.isEnabled()) {
                Bukkit.getPluginManager().registerEvents(scenario, Uhc.Companion.getINSTANCE());
                scenario.registerEvents();
            }
        }
    }

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    public void addScenario(Scenario scenario) {
        scenarios.add(scenario);
    }

    public PhaseType getPhaseType() {
        return getPhase().getType();
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }
}
