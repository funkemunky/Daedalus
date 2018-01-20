package funkemunky.Daedalus.packets.events;

import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PacketEntityActionEvent extends Event {
	public int Action;
	public Player Player;
	private static final HandlerList handlers;

	static {
		handlers = new HandlerList();
	}

	public PacketEntityActionEvent(final Player Player, final int Action) {
		super();
		this.Player = Player;
		this.Action = Action;
	}

	public Player getPlayer() {
		return this.Player;
	}

	public int getAction() {
		return this.Action;
	}

	public HandlerList getHandlers() {
		return PacketEntityActionEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return PacketEntityActionEvent.handlers;
	}

	public class PlayerAction {
	}
}