package de.hglabor.plugins.uhc.game.phases;

import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.game.Scenario;
import de.hglabor.plugins.uhc.game.mechanics.border.Border;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.game.scenarios.Teams;
import de.hglabor.plugins.uhc.game.scenarios.Timber;
import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.team.UHCTeam;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class PvPPhase extends IngamePhase implements Listener {
    private final int farmPhaseDuration;
    private final long startTimeStamp;

    protected PvPPhase(int farmPhaseDuration) {
        super(0, PhaseType.PVP);
        this.farmPhaseDuration = farmPhaseDuration;
        this.startTimeStamp = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        GameManager.INSTANCE.getScenarios().stream().filter(Scenario::isEnabled).forEach(Scenario::onPvPPhase);
    }

    @Override
    protected void tick() {
        Border border = Border.INSTANCE;
        border.announceBorderShrink();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (border.getBorderSize() > border.getShortestBorderSize()) {
                player.sendActionBar(GlobalChat.hexColor("#EC2828") + "Next bordershrink " + border.getNextBorderSize() + " in: " + ChatColor.GRAY + TimeConverter.stringify(border.getNextShrinkTimeInSeconds()));
            }
        });
        border.handleNextBorderShrink();
        if (Teams.INSTANCE.isEnabled()) {
            if (Teams.INSTANCE.getTeamList().values().stream().filter(uhcTeam -> !uhcTeam.isEliminated()).count() == 1) {
                startNextPhase();
            }
        } else {
            //TODO glaube wenn man ausloggt wird man nicht irgendwie korrekt gedingst
            if (PlayerList.INSTANCE.getAlivePlayers().size() == 1) {
                startNextPhase();
            }
        }
    }

    @Override
    public String getTimeString() {
        int timer = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTimeStamp) + farmPhaseDuration;
        if (timer >= 3600) {
            return GlobalChat.hexColor("#EC2828") + "Duration: " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify(timer, "%02d:%02d:%02d");
        } else {
            return GlobalChat.hexColor("#EC2828") + "Duration: " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify(timer);
        }
    }

    @Override
    protected GamePhase getNextPhase() {
        return EndPhase.INSTANCE;
    }
}
