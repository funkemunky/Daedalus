package me.funke.daedalus.update;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UpdateType Type;

    public UpdateEvent(UpdateType Type) {
        this.Type = Type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UpdateType getType() {
        return this.Type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
