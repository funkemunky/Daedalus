package me.funke.daedalus.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketKillauraEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private Player Player;
    private PacketPlayerType type;

    public PacketKillauraEvent(final Player Player, final PacketPlayerType type) {
        super();
        this.Player = Player;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return PacketKillauraEvent.handlers;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public PacketPlayerType getType() {
        return this.type;
    }

    public HandlerList getHandlers() {
        return PacketKillauraEvent.handlers;
    }
}
