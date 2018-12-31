package me.funke.daedalus.listeners;

import me.funke.daedalus.utils.PearlGlitchType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PearlGlitchEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private Player player;
    private Location from;
    private Location to;
    private ItemStack pearls;
    private PearlGlitchType type;
    private boolean cancelled = false;

    public PearlGlitchEvent(Player player, Location from, Location to, ItemStack pearls, PearlGlitchType type) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.pearls = pearls;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }

    public ItemStack getItems() {
        return this.pearls;
    }

    public PearlGlitchType getType() {
        return type;
    }

    public boolean isType(PearlGlitchType type) {
        return type == this.type;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}