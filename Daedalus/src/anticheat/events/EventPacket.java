package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Daedalus;
import anticheat.packets.events.PacketPlayerEvent;

public class EventPacket implements Listener {
	
	@EventHandler
	public void packet(PacketPlayerEvent e) {
		Daedalus.getAC().getchecksmanager().event(e);
	}

}
