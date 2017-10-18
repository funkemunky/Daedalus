package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Daedalus;
import anticheat.packets.events.PacketReadVelocityEvent;

public class EventPacketReadVelocity implements Listener {
	
	@EventHandler
	public void onRead(PacketReadVelocityEvent event) {
		Daedalus.getAC().getchecksmanager().event(event);
	}

}
