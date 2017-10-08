package anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.utils.MiscUtils;

@ChecksListener(events = {PlayerMoveEvent.class})
public class Jesus extends Checks {
	
	public Jesus() {
		super("Jesus", ChecksType.MOVEMENT, Daedalus.getAC(), 10, true);
	}
	
	public static Map<Player, Integer> onWater = new HashMap();
	public static ArrayList<Player> placedBlockOnWater = new ArrayList();
	public static Map<Player, Integer> count = new HashMap();
	public static Map<UUID, Double> velocity =  new HashMap();
	
	@Override
	protected void onEvent(Event event) {
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			if ((e.getFrom().getX() == e.getTo().getX()) && (e.getFrom().getZ() == e.getTo().getZ())) {
		        return;
		      }
		      Player p = e.getPlayer();
		      if(p.getVelocity().length() < velocity.getOrDefault(p.getUniqueId(), -1.0D)) {
		    	  return;
		      }
			     if(p.hasPermission("daedalus.bypass")) {
			         return;
			     }
		      if (p.getAllowFlight()) {
		        return;
		      }
		      if (!p.getNearbyEntities(1.0D, 1.0D, 1.0D).isEmpty()) {
		        return;
		      }
		      if (MiscUtils.isOnLilyPad(p)) {
		        return;
		      }
		      
		      if (this.placedBlockOnWater.remove(p)) {
		        return;
		      }
		      int Count = 0;
		      if(count.containsKey(p)) {
		    	  Count = count.get(p);
		      }
		      if ((MiscUtils.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation()))) && 
		        (MiscUtils.isHoveringOverWater(p.getLocation())) && 
		        (!MiscUtils.isFullyInWater(p.getLocation()))) {
		        count.put(p, Count + 1);
		      }
		      
		      if(Count >= 20) {
		    	  count.remove(p);
		    	  this.Alert(p, null);
		      }
		      if(!p.isOnGround()) {
		    	  this.velocity.put(p.getUniqueId(), p.getVelocity().length());
		      } else {
		    	  this.velocity.put(p.getUniqueId(), -1.0D);
		      }
		}
	}

}
