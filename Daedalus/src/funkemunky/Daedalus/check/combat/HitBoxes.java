package funkemunky.Daedalus.check.combat;

import org.bukkit.event.EventHandler;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;

public class HitBoxes extends Check {
	
	public HitBoxes(Daedalus Daedalus) {
		super("HitBoxes", "Hitboxes", Daedalus);
	}
	
	@EventHandler
	public void onUse(PacketUseEntityEvent e) {
		
	}

}
