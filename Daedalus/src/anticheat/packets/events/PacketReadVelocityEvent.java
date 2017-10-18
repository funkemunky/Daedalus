package anticheat.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketReadVelocityEvent extends Event {
	private Player Player;
	private double x;
	private double y;
	private double z;
	private static final HandlerList handlers;

	static {
		handlers = new HandlerList();
	}

	public PacketReadVelocityEvent(final Player Player, final double x, final double y, final double z) {
		super();
		this.Player = Player;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Player getPlayer() {
		return this.Player;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public HandlerList getHandlers() {
		return PacketReadVelocityEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return PacketReadVelocityEvent.handlers;
	}
}