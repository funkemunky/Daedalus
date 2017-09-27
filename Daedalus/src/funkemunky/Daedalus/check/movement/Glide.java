package funkemunky.Daedalus.check.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class Glide extends Check
{
    public static Map<UUID, Long> flyTicks;

    public Glide(Daedalus Daedalus) {
        super("Glide", "Glide", Daedalus);
        this.flyTicks = new HashMap<UUID, Long>();
        
        this.setEnabled(false);
        this.setBannable(true);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	if(flyTicks.containsKey(uuid)) {
    		flyTicks.remove(uuid);
    	}
    }

    @EventHandler
    public void CheckGlide(PlayerMoveEvent event) {
        if (!this.getDaedalus().isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }
        if (player.getAllowFlight()) {
            return;
        }
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}
        if (UtilCheat.isInWeb(player)) {
            return;
        }
        
        if(getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
        	return;
        }
        
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}
        
        if(event.isCancelled()) {
        	return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (UtilCheat.blocksNear(player)) {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        if (event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ()) {
            return;
        }
        double OffsetY = event.getFrom().getY() - event.getTo().getY();
        if (OffsetY <= 0.0 || OffsetY > 0.16) {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        this.dumplog(player, "OffsetY: " + OffsetY);
        long Time = System.currentTimeMillis();
        if (this.flyTicks.containsKey(player.getUniqueId())) {
            Time = this.flyTicks.get(player.getUniqueId());
        }
        long MS = System.currentTimeMillis() - Time;
        this.dumplog(player, "MS: " + MS);
        if (MS > 500L) {
            this.dumplog(player, "Logged. MS: " + MS);
            this.flyTicks.remove(player.getUniqueId());
            if(getDaedalus().getLag().getPing(player) < 275) {
            	this.getDaedalus().logCheat(this, player, null, Chance.LIKELY, new String[0]);
            }
            return;
        }
        this.flyTicks.put(player.getUniqueId(), Time);
    }
}