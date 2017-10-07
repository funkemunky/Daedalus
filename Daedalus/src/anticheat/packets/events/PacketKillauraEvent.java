package anticheat.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketKillauraEvent extends Event {
	private Player Player;
	private static final HandlerList handlers;
	private PacketPlayerType type;

	static {
		handlers = new HandlerList();
	}

	public PacketKillauraEvent(final Player Player, final PacketPlayerType type) {
		super();
		this.Player = Player;
		this.type = type;
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

	public static HandlerList getHandlerList() {
		return PacketKillauraEvent.handlers;
	}
}
