package funkemunky.Daedalus.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import funkemunky.Daedalus.utils.PearlGlitchType;

public class PearlGlitchEvent extends Event implements Cancellable {

	private Player player;
	private Location from;
	private Location to;
	private ItemStack pearls;
	private PearlGlitchType type;
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	public PearlGlitchEvent(Player player, Location from, Location to, ItemStack pearls, PearlGlitchType type) {
		this.player = player;
		this.from = from;
		this.to = to;
		this.pearls = pearls;
		this.type = type;
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
		if (type == this.type) {
			return true;
		}
		return false;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

}
