package de.hglabor.plugins.uhc.game.scenarios;

import de.hglabor.plugins.uhc.Uhc;
import de.hglabor.plugins.uhc.game.Scenario;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.metadata.FixedMetadataValue;

public class OldKnockback extends Scenario {
    public final static OldKnockback INSTANCE = new OldKnockback();

    private OldKnockback() {
        super("Old Knockback", new ItemBuilder(Material.PISTON).build());
    }

    @Override
    public void onFarmPhase() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setMetadata("oldKnockback", new FixedMetadataValue(Uhc.Companion.getINSTANCE(), ""));
        });
    }

    @Override
    public void onPvPPhase() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setMetadata("oldKnockback", new FixedMetadataValue(Uhc.Companion.getINSTANCE(), ""));
        });
    }
}
