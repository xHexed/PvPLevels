package me.MathiasMC.PvPLevels.api.events;

import me.MathiasMC.PvPLevels.PvPLevels;
import me.MathiasMC.PvPLevels.data.PlayerConnect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PlayerLostMultiplierEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final PvPLevels plugin;

    private boolean cancelled = false;

    private final OfflinePlayer offlinePlayer;

    private final PlayerConnect playerConnect;

    private double multiplier;

    private long seconds;

    private List<String> commands = null;

    public PlayerLostMultiplierEvent(final OfflinePlayer offlinePlayer, final PlayerConnect playerConnect, final double multiplier, final long seconds) {
        this.plugin = PvPLevels.getInstance();
        this.offlinePlayer = offlinePlayer;
        this.playerConnect = playerConnect;
        this.multiplier = multiplier;
        this.seconds = seconds;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public PlayerConnect getPlayerConnect() {
        return this.playerConnect;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public void setMultiplier(final double multiplier) {
        this.multiplier = multiplier;
    }

    public void setSeconds(final long seconds) {
        this.seconds = seconds;
    }

    public void setCommands(final List<String> commands) {
        this.commands = commands;
    }

    public void execute() {
        if (offlinePlayer.isOnline()) {
            plugin.getXPManager().sendCommands((Player) offlinePlayer, commands);
        }
        playerConnect.stopMultiplier();
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