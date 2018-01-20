package funkemunky.Daedalus.packets.events;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSwingArmEvent extends Event {
	public Player Player;
	public PacketEvent Event;
	private static final HandlerList handlers = new HandlerList();

	public PacketSwingArmEvent(PacketEvent Event, Player Player) {
		this.Player = Player;
		this.Event = Event;
	}

	public PacketEvent getPacketEvent() {
		return this.Event;
	}

	public Player getPlayer() {
		return this.Player;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
