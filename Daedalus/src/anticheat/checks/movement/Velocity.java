package anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketReadVelocityEvent;
import anticheat.user.User;
import anticheat.utils.Latency;
import anticheat.utils.MiscUtils;

@ChecksListener(events = {PacketReadVelocityEvent.class, PlayerQuitEvent.class})
public class Velocity extends Checks {
	
    private Map<UUID, ArrayList<Double>> velocity;
	public Velocity() {
		super("Velocity", ChecksType.MOVEMENT, Daedalus.getAC(), 10, true, true);
		this.velocity = new HashMap<UUID, ArrayList<Double>>();
	}
	
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		if(event instanceof PacketReadVelocityEvent) {
			PacketReadVelocityEvent e = (PacketReadVelocityEvent) event;
			Player player = e.getPlayer();
			if(Latency.getLag(player) > 68) {
				return;
			}
			ArrayList<Double> velocityValues = new ArrayList<Double>();
			
			if(!MiscUtils.blocksNear(player) && (e.getVelocity().getX() + e.getVelocity().getZ()) < 0.048) {
				return;
			}
			
			if(this.velocity.containsKey(player.getUniqueId())) {
				velocityValues = this.velocity.get(player.getUniqueId());
			}
			
			velocityValues.add(e.getVelocity().getY());
			System.out.print("Velocity Y:" + e.getVelocity().getY());
			
			if(velocityValues.size() >= 5) {
				double all = 0;
				for(double y : velocityValues) {
					all+= y;
				}
				if((all / 3D) < 0.1) {
				    User user = Daedalus.getUserManager().getUser(player.getUniqueId());
				    user.setVL(this, user.getVL(this) + 1);
					this.Alert(player, "*");
				}
				System.out.print("Velocity Avrage:" + all / 3D);
				velocityValues.clear();
			}
			
			this.velocity.put(player.getUniqueId(), velocityValues);
		}
	}

}
