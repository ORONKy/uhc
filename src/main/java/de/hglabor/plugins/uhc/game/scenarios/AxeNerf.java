package de.hglabor.plugins.uhc.game.scenarios;

import de.hglabor.plugins.uhc.Uhc;
import de.hglabor.plugins.uhc.config.CKeys;
import de.hglabor.plugins.uhc.config.UHCConfig;
import de.hglabor.plugins.uhc.game.Scenario;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class AxeNerf extends Scenario {
    public final static AxeNerf INSTANCE = new AxeNerf();
    private final List<Material> axes;
    private double multiplier;

    private AxeNerf() {
        super("AxeNerf", new ItemBuilder(Material.DIAMOND_AXE).build());
        this.multiplier = 0.9;
        this.axes = Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);
    }

    @Override
    protected void saveToConfig() {
        Uhc plugin = Uhc.Companion.getINSTANCE();
        plugin.getConfig().addDefault(CKeys.SCENARIOS + "." + getName() + "." + "multiplier", multiplier);
        super.saveToConfig();
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        multiplier = UHCConfig.getInteger(CKeys.SCENARIOS + "." + getName() + "." + "multiplier");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (axes.contains(player.getInventory().getItemInMainHand().getType())) {
            event.setDamage(event.getDamage() * multiplier);
        }
    }
}
