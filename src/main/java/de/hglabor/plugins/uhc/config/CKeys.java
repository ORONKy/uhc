package de.hglabor.plugins.uhc.config;

public interface CKeys {
    String PHASE = "phase";

    String LOBBY = PHASE + "." + "lobby";
    String LOBBY_START_TIME = LOBBY + "." + "startTime";

    String SCATTER = PHASE + "." + "scatter";
    String SCATTER_TELEPORT_DELAY = SCATTER + "." + "teleportDelay";
    String SCATTER_AMOUNT_TO_TELEPORT_EACH_TIME = SCATTER + "." + "playerAmountToTeleport";

    String FARM = PHASE + "." + "farm";
    String FARM_FINAL_HEAL = FARM + "." + "finalHeal";
    String FARM_FARM_TIME = FARM + "." + "farmTime";

    String MECHANICS = "MECHANICS";
    String RELOG_TIME = MECHANICS + "." + "relogTime";
    String PREGEN_WORLD = MECHANICS + "." + "preGenWorld";

    String BORDER = "border";
    String BORDER_FIRST_SHRINK = BORDER + "." + "firstShrink";
    String BORDER_START_SIZE = BORDER + "." + "startSize";
    String BORDER_SHRINK_INTERVAL = BORDER + "." + "shrinkInterval";
    String BORDER_MAX_SIZE = BORDER + "." + "maxSize";
    /**
     * Shrink size til border hits 500 mark
     */
    String BORDER_SHRINK_SIZE = "border" + "." + "shrinkSize";

    String SCENARIOS = "scenarios";

    String BROADCAST = "broadcast";
    String BROADCAST_NEW_TYPE = BROADCAST + "." + "newBroadcastInSeconds";

    String TEAMS = "teams";
    String TEAMS_SIZE = TEAMS + "." + "maxTeamSize";
}
