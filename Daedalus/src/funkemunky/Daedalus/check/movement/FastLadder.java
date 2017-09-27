package funkemunky.Daedalus.check.movement;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class FastLadder extends Check {

	public FastLadder(Daedalus Daedalus) {
		super("FastLadder", "FastLadder", Daedalus);
		
		this.setEnabled(true);
		this.setBannable(true);
		this.setMaxViolations(7);
	}
	
	public static HashMap<Player, Integer> count =  new HashMap();
	
    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	
    	if(count.containsKey(p)) {
    		count.remove(p);
    	}
    }
 
	@EventHandler
	public void checkFastLadder(PlayerMoveEvent e) {
		 double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
		 double Limit = 0.13;
		 Player player = e.getPlayer();
		 
		 if(!count.containsKey(player)) {
			 count.put(player, 0);
			 return;
		 }
		 
		 if(getDaedalus().isSotwMode()) {
			 return;
		 }
		 
		 if(getDaedalus().getLastVelocity().containsKey(player.getUniqueId())) {
			 return;
		 }
		 
		 if(player.getAllowFlight()) {
			 return;
		 }
		 
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }
		 
		 int Count = count.get(player);
		 long Time = 0;
		 
		 if(!UtilPlayer.isOnClimbable(player, 1) || !UtilPlayer.isOnClimbable(player, 0)) {
			 return;
		 }
		 
		 if(e.getFrom().getY() == e.getTo().getY()) {
			 return;
		 }
		 if(getDaedalus().LastVelocity.containsKey(player.getUniqueId())) {
			 return;
		 }
		 
		 double yDist = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
		 double updown = e.getTo().getY() - e.getFrom().getY();
		 if(updown <= 0) {
			 return;
		 }
		 
		 
		if(OffsetY > Limit) {
			count.put(player, Count + 1);
			this.dumplog(player, "[Illegitmate] New Count: " + Count + " (+1); Speed: " + OffsetY + "; Max: " + Limit);
	    } else {
	    	count.put(player, 0);
	    }
		
		long percent = Math.round((OffsetY - Limit) * 120);
		
		if(Count >= 12) {
			count.remove(player);
			this.dumplog(player, "Flagged for FastLadder; Speed:" + OffsetY + "; Max: " + Limit + "; New Count: " + Count);
			this.getDaedalus().logCheat(this, player, percent + "% faster than normal", Chance.HIGH, new String[0]);
			return;
		}
		 
	}

}
