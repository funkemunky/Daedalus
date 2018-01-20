package funkemunky.Daedalus.packets.events;

import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PacketKeepAliveEvent extends Event {
	public Player Player;
	private static final HandlerList handlers;

	static {
		handlers = new HandlerList();
	}

	public PacketKeepAliveEvent(final Player Player) {
		super();
		this.Player = Player;
	}

	public Player getPlayer() {
		return this.Player;
	}

	public HandlerList getHandlers() {
		return PacketKeepAliveEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return PacketKeepAliveEvent.handlers;
	}
}