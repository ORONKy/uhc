package de.hglabor.plugins.uhc.game.phases;

import de.hglabor.plugins.uhc.config.CKeys;
import de.hglabor.plugins.uhc.config.ConfigInventory;
import de.hglabor.plugins.uhc.config.UHCConfig;
import de.hglabor.plugins.uhc.game.GameManager;
import de.hglabor.plugins.uhc.game.GamePhase;
import de.hglabor.plugins.uhc.game.PhaseType;
import de.hglabor.plugins.uhc.game.mechanics.border.Border;
import de.hglabor.plugins.uhc.game.mechanics.chat.GlobalChat;
import de.hglabor.plugins.uhc.game.scenarios.TeamInventory;
import de.hglabor.plugins.uhc.game.scenarios.Teams;
import de.hglabor.plugins.uhc.player.UHCPlayer;
import de.hglabor.plugins.uhc.player.UserStatus;
import de.hglabor.utils.noriskutils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;

import java.util.concurrent.TimeUnit;

public class LobbyPhase extends GamePhase {
    private final World lobby;

    public LobbyPhase() {
        super(UHCConfig.getInteger(CKeys.LOBBY_START_TIME), PhaseType.LOBBY);
        this.lobby = Bukkit.getWorld("schematic");
        this.lobby.setSpawnLocation(0, 104, 0);         //hardcoded lobby spawn
        this.lobby.getWorldBorder().setSize(250);
    }

    @Override
    protected void init() {
        this.maxPhaseTimeStamp = System.currentTimeMillis() + UHCConfig.getInteger(CKeys.LOBBY_START_TIME) * 1000L;
        Border.INSTANCE.createBorder();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        UHCConfig.setLobbySettings(lobby);
    }

    @Override
    protected void tick() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            maxPhaseTimeStamp = System.currentTimeMillis() + UHCConfig.getInteger(CKeys.LOBBY_START_TIME) * 1000L;
            return;
        }
        if (maxPhaseTimeStamp - System.currentTimeMillis() <= 0) {
            this.startNextPhase();
        }
    }

    @Override
    public int getAlivePlayers() {
        return (int) playerList.getAllPlayers().stream().filter(player -> player.getStatus().equals(UserStatus.LOBBY)).count();
    }

    @Override
    public String getTimeString() {
        long timeLeft = maxPhaseTimeStamp - System.currentTimeMillis();
        return GlobalChat.hexColor("#EC2828") + "Start: " + GlobalChat.hexColor("#F45959") + TimeConverter.stringify((int) TimeUnit.MILLISECONDS.toSeconds(timeLeft));
    }

    @Override
    protected GamePhase getNextPhase() {
        return new ScatteringPhase();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        UHCPlayer uhcPlayer = playerList.getPlayer(player);
        player.teleport(lobby.getSpawnLocation());
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);

        if (player.hasPermission("group.admin")) {
            player.getInventory().setItem(4, ConfigInventory.INSTANCE.getLobbyPhaseItem());
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        playerList.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(true);

        if (event.getItem() == null) return;
        if (event.getItem().equals(ConfigInventory.INSTANCE.getLobbyPhaseItem())) {
            if (event.getPlayer().hasPermission("hglabor.forcestart")) {
                ConfigInventory.INSTANCE.openGUI(event.getPlayer());
            }
        }

        if (event.getItem().equals(Teams.INSTANCE.getTeamConfigItem())) {
            TeamInventory.INSTANCE.openGUI(event.getPlayer());
        }
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onVehicleDamage(VehicleDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }
}
