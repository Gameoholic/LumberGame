package xyz.gameoholic.lumbergame.game;

import java.util.UUID;

public class LumberPlayer {
    private UUID uuid;
    private int wood = 0;
    private int iron = 0;
    private int gold = 0;
    private int diamond = 0;

    public LumberPlayer(UUID uuid) {
        this.uuid = uuid;
    }
}
