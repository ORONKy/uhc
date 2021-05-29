package de.hglabor.plugins.uhc.game.mechanics.border;

import de.hglabor.plugins.uhc.config.CKeys;
import de.hglabor.plugins.uhc.config.UHCConfig;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.player.PlayerList;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.*;

import java.util.concurrent.TimeUnit;

public final class Border {
    public final static Border INSTANCE = new Border();

    private World overWorld;
    private long nextShrinkTime;
    private long shrinkInterval;
    private int borderShrinkSize;
    private int shortestBorderSize;
    private int borderSize;
    private int nextBorderSize;
    private boolean cutInHalf;

    public void init() {
        long currentTimeMillis = System.currentTimeMillis();
        nextShrinkTime = currentTimeMillis + UHCConfig.getInteger(CKeys.BORDER_FIRST_SHRINK) * 1000L;
        shrinkInterval = UHCConfig.getInteger(CKeys.BORDER_SHRINK_INTERVAL) * 1000L;
        shortestBorderSize = 25;
        this.recalculateBorder();
    }

    public void createBorder() {
        borderShrinkSize = UHCConfig.getInteger(CKeys.BORDER_SHRINK_SIZE);
        borderSize = UHCConfig.getInteger(CKeys.BORDER_START_SIZE);
        overWorld = Bukkit.getWorld("world");
        if (overWorld != null) {
            overWorld.getWorldBorder();
            overWorld.getWorldBorder().setDamageAmount(0);
            overWorld.getWorldBorder().setDamageBuffer(0);
            overWorld.getWorldBorder().setSize(borderSize * 2);
        }
    }

    public void run(boolean force) {
        if (borderSize > shortestBorderSize) {
            if (!force) {
                nextShrinkTime += shrinkInterval;
            }
            borderSize = nextBorderSize;
            recalculateBorder();
            if (cutInHalf && borderSize <= 100) {
                // createBorder(Bukkit.getWorld("world"), borderSize, 60, 10);
            } else {
            }
            overWorld.getWorldBorder().setSize(borderSize * 2);
            teleportToCorner();
        }
    }

    public void handleNextBorderShrink() {
        if (System.currentTimeMillis() >= getNextShrinkTime()) {
            run(false);
        }
    }

    private void teleportToCorner() {
        for (UHCPlayer uhcPlayer : PlayerList.INSTANCE.getAlivePlayers()) {
            uhcPlayer.getBukkitPlayer().ifPresent(player -> {
                World world = borderSize > 500 ? player.getWorld() : Bukkit.getWorld("world");
                Location location = player.getLocation();
                Corner corner = Corner.getCorner(location);
                Outside outside = Outside.get(location, borderSize);
                int coord = borderSize - 5;
                int x, z;
                switch (outside) {
                    case X:
                        x = corner.xConverter.convert(coord);
                        player.teleportAsync(new Location(world, x, world.getHighestBlockYAt(x, location.getBlockZ(), HeightMap.MOTION_BLOCKING_NO_LEAVES), location.getZ()).clone().add(0, 1, 0));
                        break;
                    case Z:
                        z = corner.zConverter.convert(coord);
                        player.teleportAsync(new Location(world, location.getX(), world.getHighestBlockYAt(location.getBlockX(), z, HeightMap.MOTION_BLOCKING_NO_LEAVES), z).clone().add(0, 1, 0));
                        break;
                    case BOTH:
                        x = corner.xConverter.convert(coord);
                        z = corner.zConverter.convert(coord);
                        player.teleportAsync(new Location(world, x, world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES), z).clone().add(0, 1, 0));
                        break;
                }
            });
        }
    }

    private void recalculateBorder() {
        if (borderSize > 500) {
            nextBorderSize = Math.max(borderSize - borderShrinkSize, 400);
        } else if (borderSize > getShortestBorderSize()) {
            if (!cutInHalf) {
                cutInHalf = true;
                nextBorderSize = 400;
            }
            nextBorderSize = nextBorderSize / 2;
        }
    }

    public void announceBorderShrink() {
        if (borderSize <= getShortestBorderSize()) {
            return;
        }
        int timeLeft = getNextShrinkTimeInSeconds();
        if (timeLeft <= 300) {
            if (timeLeft % 60 == 0 || timeLeft <= 5 || timeLeft == 10) {
                Bukkit.broadcastMessage(GlobalChat.hexColor("#EC2828") + "Border will be shrinked to " +
                        ChatColor.RED + ChatColor.BOLD + nextBorderSize + "x" + nextBorderSize + ChatColor.RESET + GlobalChat.hexColor("#EC2828") +
                        " in " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify(timeLeft));
            }
        }
    }

    public long getNextShrinkTime() {
        return nextShrinkTime;
    }

    public int getNextShrinkTimeInSeconds() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(getNextShrinkTime() - System.currentTimeMillis());
    }

    public String getBorderString() {
        return GlobalChat.hexColor("#EC2828") + "Border: " + GlobalChat.hexColor("#F45959") + borderSize;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getNextBorderSize() {
        return nextBorderSize;
    }

    public int getShortestBorderSize() {
        return shortestBorderSize;
    }
}
