package me.MathiasMC.PvPLevels.api.events;

import me.MathiasMC.PvPLevels.PvPLevels;
import me.MathiasMC.PvPLevels.data.PlayerConnect;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelUPEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final PvPLevels plugin;

    private boolean cancelled = false;

    private final Player player;

    private final PlayerConnect playerConnect;

    private final long level;

    public PlayerLevelUPEvent(Player player, PlayerConnect playerConnect, long level) {
        this.plugin = PvPLevels.getInstance();
        this.player = player;
        this.playerConnect = playerConnect;
        this.level = level;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerConnect getPlayerConnect() {
        return this.playerConnect;
    }

    public long getLevel() {
        return this.level;
    }

    public void execute() {
        plugin.getXPManager().sendCommands(player, plugin.getFileUtils().levels.getString(playerConnect.getGroup() + "." + playerConnect.getLevel() + ".execute") + ".level.up", plugin.getFileUtils().execute, "", 0, 0, 0, 0);
        playerConnect.setLevel(level);
        playerConnect.save();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean set) {
        cancelled = set;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}