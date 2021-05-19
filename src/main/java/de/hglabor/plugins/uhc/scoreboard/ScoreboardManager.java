package de.hglabor.plugins.uhc.scoreboard;

import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.plugins.uhc.game.scenarios.Teams;
import de.hglabor.utils.noriskutils.TimeConverter;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ScoreboardManager implements Listener {
    public final static ScoreboardManager INSTANCE = new ScoreboardManager();

    private ScoreboardManager() {
    }

    public static void setBasicScoreboardLayout(ScoreboardPlayer player) {
        int score;
        if (Teams.INSTANCE.getMaxTeamSize() > 1) score = 6; else score = 5;
        String placeHolder = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "                   ";
        ScoreboardFactory.addEntry(player, "placeHolder2", placeHolder, score--);
        ScoreboardFactory.addEntry(player, "time", "Start: " + TimeConverter.stringify(180), score--);
        ScoreboardFactory.addEntry(player, "players", "Players: " + Bukkit.getOnlinePlayers().size(), score--);
        if (Teams.INSTANCE.getMaxTeamSize() > 1) {
            ScoreboardFactory.addEntry(player, "teamKills", "Teamkills: 0", score--);
        }
        ScoreboardFactory.addEntry(player, "kills", "Kills: 0", score--);
        ScoreboardFactory.addEntry(player, "border", "Border: " + GameManager.INSTANCE.getBorder().getBorderSize(), score--);
        ScoreboardFactory.addEntry(player, "placeHolder1", placeHolder, score);
    }

    public static void updateForEveryone(int time) {
        for (UHCPlayer uhcPlayer : PlayerList.INSTANCE.getAllPlayers()) {
            uhcPlayer.getBukkitPlayer().ifPresent(player -> {
                GamePhase phase = GameManager.INSTANCE.getPhase();
                ScoreboardFactory.updateEntry(uhcPlayer, "time", phase.getTimeString(time));
                ScoreboardFactory.updateEntry(uhcPlayer, "players", GlobalChat.hexColor("#EC2828") + "Players: " + GlobalChat.hexColor("#F45959") + phase.getAlivePlayers());
                if (Teams.INSTANCE.getMaxTeamSize() > 1) {
                    ScoreboardFactory.updateEntry(uhcPlayer, "teamKills", GlobalChat.hexColor("#EC2828") + "Teamkills: " + GlobalChat.hexColor("#F45959") + uhcPlayer.getTeam().getTeamKills());
                }
                ScoreboardFactory.updateEntry(uhcPlayer, "kills", GlobalChat.hexColor("#EC2828") + "Kills: " + GlobalChat.hexColor("#F45959") + uhcPlayer.getKills().get());
                ScoreboardFactory.updateEntry(uhcPlayer, "border", GameManager.INSTANCE.getBorder().getBorderString(time));
            });
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UHCPlayer uhcPlayer = PlayerList.INSTANCE.getPlayer(player);
        if (uhcPlayer.getScoreboard() == null) {
            ScoreboardFactory.create(uhcPlayer, ChatColor.BOLD + "uhc.hglabor.de", false);
            ScoreboardManager.setBasicScoreboardLayout(uhcPlayer);
            if (uhcPlayer.getScoreboard() != null) {
                Scoreboard scoreboard = uhcPlayer.getScoreboard();
                Objective objective = scoreboard.registerNewObjective("showhealth", Criterias.HEALTH, Criterias.HEALTH);
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(ChatColor.RED + "Health");
                objective.getScore(ChatColor.WHITE + "").setScore(0);
            }
        }
        player.setScoreboard(uhcPlayer.getScoreboard());
    }
}
