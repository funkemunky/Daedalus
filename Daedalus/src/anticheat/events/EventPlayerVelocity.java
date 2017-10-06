package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;

import anticheat.Daedalus;

public class EventPlayerVelocity implements Listener {

	@EventHandler
	public void onMove(PlayerVelocityEvent event) {
		Daedalus.getAC().getchecksmanager().event(event);
	}

}
