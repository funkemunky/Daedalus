package anticheat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class TickEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
