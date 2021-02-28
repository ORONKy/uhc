package de.hglabor.plugins.uhc.game.phases;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.uhc.Uhc;
import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.config.CKeys;
import de.hglabor.plugins.uhc.config.UHCConfig;
import de.hglabor.plugins.uhc.game.mechanics.CombatLogger;
import de.hglabor.plugins.uhc.game.mechanics.HeartDisplay;
import de.hglabor.plugins.uhc.game.scenarios.NoCooldown;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.PotionUtils;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FarmPhase extends IngamePhase {
    private final int finalHeal;
    private boolean wasFinalHeal;

    protected FarmPhase() {
        super(UHCConfig.getInteger(CKeys.FARM_FARM_TIME), PhaseType.FARM);
        this.finalHeal = UHCConfig.getInteger(CKeys.FARM_FINAL_HEAL);
    }

    @Override
    protected void init() {
        GameManager.INSTANCE.enableScenarios();
        Bukkit.getPluginManager().registerEvents(CombatLogger.INSTANCE, Uhc.getPlugin());
        Bukkit.broadcastMessage(ChatColor.GRAY + "You are now able to relog");
        for (Player player : Bukkit.getOnlinePlayers()) {
            HeartDisplay.INSTANCE.enableHealthBar(player);
            player.sendTitle(ChatColor.AQUA + "UHC" + ChatColor.WHITE + " | " + ChatColor.GREEN + "Farmphase",
                    ChatColor.GOLD + "gl hf", 20, 20, 20);
            player.setHealth(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 20));
            PotionUtils.removePotionEffects(player);

            if (NoCooldown.INSTANCE.isEnabled()) {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);
            }
        }
    }

    @Override
    protected void tick(int timer) {
        announceNextPhase(timer);
        handleFinalHeal(timer);
        if (timer > maxPhaseTime) {
            this.startNextPhase();
        }
    }

    private void handleFinalHeal(int timer) {
        if (wasFinalHeal) return;
        int timeLeft = finalHeal - timer;
        if (timeLeft == 0) {
            wasFinalHeal = true;
            Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(20));
            Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Final Heal");
        } else if (timeLeft % (2 * 60) == 0) {
            String timeString = TimeConverter.stringify(timeLeft);
            ChatUtils.broadcastMessage("farm.finaHealIn", ImmutableMap.of("time", timeString));
        }
    }

    public int getFinalHeal() {
        return finalHeal;
    }

    private void announceNextPhase(int timer) {
        int timeLeft = maxPhaseTime - timer;
        if (timeLeft == 0) {
            Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "PvP has been enabled");
        } else if (timeLeft % (5 * 60) == 0) {
            String timeString = TimeConverter.stringify(timeLeft);
            ChatUtils.broadcastMessage("farm.pvpIn", ImmutableMap.of("time", timeString));
        }
    }

    @Override
    public String getTimeString(int timer) {
        return ChatColor.AQUA + "Duration: " + ChatColor.GREEN + TimeConverter.stringify(timer);
    }

    @Override
    protected GamePhase getNextPhase() {
        return new PvPPhase();
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
