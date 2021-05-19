package de.hglabor.plugins.uhc.game.phases;

import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.game.Scenario;
import de.hglabor.plugins.uhc.game.mechanics.border.Border;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.game.scenarios.Timber;
import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPPhase extends IngamePhase implements Listener {
    protected PvPPhase() {
        super(0, PhaseType.PVP);
    }

    @Override
    protected void init() {
        Timber.INSTANCE.setEnabled(false);
        GameManager.INSTANCE.getScenarios().stream().filter(Scenario::isEnabled).forEach(Scenario::onPvPPhase);
    }

    @Override
    protected void tick(int timer) {
        Border border = GameManager.INSTANCE.getBorder();
        if (PlayerList.INSTANCE.getAlivePlayers().size() == 1) {
            startNextPhase();
        }
        border.announceBorderShrink(timer);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (border.getBorderSize() > border.getShortestBorderSize()) {
                player.sendActionBar(GlobalChat.hexColor("#EC2828") + "Next bordershrink " + border.getNextBorderSize() + " in: " + ChatColor.GRAY + TimeConverter.stringify(border.getNextShrinkTime() - timer));
            }
        });
        if (timer == border.getNextShrinkTime()) {
            border.run(false);
        }
    }

    @Override
    public String getTimeString(int timer) {
        if (timer >= 3600) {
            return GlobalChat.hexColor("#EC2828") + "Duration: " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify(timer, "%02d:%02d:%02d");
        } else {
            return GlobalChat.hexColor("#EC2828") + "Duration: " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify(timer);
        }
    }

    @Override
    protected GamePhase getNextPhase() { return EndPhase.INSTANCE; }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (PlayerList.INSTANCE.getPlayer((Player) event.getEntity()) == PlayerList.INSTANCE.getPlayer((Player) event.getDamager())) {
                event.setDamage(0.0);
            }
        }
    }
}
