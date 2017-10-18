package anticheat.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class PacketReadVelocityEvent extends Event {
	private Player Player;
	private Vector vector;
	private static final HandlerList handlers;

	static {
		handlers = new HandlerList();
	}

	public PacketReadVelocityEvent(final Player Player, Vector vector) {
		super();
		this.Player = Player;
		this.vector = vector;
	}

	public Player getPlayer() {
		return this.Player;
	}

	public Vector getVelocity() {
		return this.vector;
	}

	public HandlerList getHandlers() {
		return PacketReadVelocityEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return PacketReadVelocityEvent.handlers;
	}
}