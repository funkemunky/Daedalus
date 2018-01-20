package funkemunky.Daedalus.packets.events;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketUseEntityEvent extends Event {
	public EnumWrappers.EntityUseAction Action;
	public Player Attacker;
	public Entity Attacked;
	private static final HandlerList handlers = new HandlerList();

	public PacketUseEntityEvent(EnumWrappers.EntityUseAction Action, Player Attacker, Entity Attacked) {
		this.Action = Action;
		this.Attacker = Attacker;
		this.Attacked = Attacked;
	}

	public EnumWrappers.EntityUseAction getAction() {
		return this.Action;
	}

	public Player getAttacker() {
		return this.Attacker;
	}

	public Entity getAttacked() {
		return this.Attacked;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
