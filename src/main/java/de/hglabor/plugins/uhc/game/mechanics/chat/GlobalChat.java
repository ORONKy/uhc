package de.hglabor.plugins.uhc.game.mechanics.chat;

import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.plugins.uhc.team.Teams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalChat implements Listener {
    public final static GlobalChat INSTANCE = new GlobalChat();
    public final static Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    private static final String prefix = hexColor("#EC2828") + "UHC" + ChatColor.RESET + " \u00BB ";
    private final String chatFormat;
    private boolean isEnabled;


    private GlobalChat() {
        this.chatFormat = "%1$s: %2$s";
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String translateHexColorCodes(String message) {
        //Sourced from this post by imDaniX: https://github.com/SpigotMC/BungeeCord/pull/2883#issuecomment-653955600
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    public static net.md_5.bungee.api.ChatColor hexColor(String message) {
        return net.md_5.bungee.api.ChatColor.of(Color.decode(message));
    }

    @EventHandler
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UHCPlayer uhcPlayer = PlayerList.INSTANCE.getPlayer(player);
        int teamIndex = uhcPlayer.getTeamIndex();

        event.setFormat((Teams.INSTANCE.isEnabled() && teamIndex != -1 ? "Team[" + teamIndex + "] " : "") + chatFormat);

        if (isEnabled) {
            return;
        }
        event.setCancelled(!player.hasPermission("group.mod") && !player.isOp());
    }

    public void enable(boolean enable) {
        isEnabled = enable;
        if (enable) {
            Bukkit.broadcastMessage(getPrefix() + ChatColor.GREEN + ChatColor.BOLD + "CHAT HAS BEEN ENABLED");
        } else {
            Bukkit.broadcastMessage(getPrefix() + GlobalChat.hexColor("#F45959") + ChatColor.BOLD + "CHAT HAS BEEN DISABLED");
        }
    }
}
