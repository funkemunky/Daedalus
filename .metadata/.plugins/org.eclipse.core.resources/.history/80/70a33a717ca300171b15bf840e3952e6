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
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class Fly
        extends Check
{
    public Fly(Daedalus Daedalus)
    {
        super("Fly", "Fly", Daedalus);

        this.setBannable(true);
        setMaxViolations(5);
    }

    public static Map<UUID, Long> flyTicksA = new HashMap();
    
    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	if(flyTicksA.containsKey(uuid)) {
    		flyTicksA.remove(uuid);
    	}
    }

    @EventHandler
    public void CheckFlyA(PlayerMoveEvent event)
    {
        if (!getDaedalus().isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }
        if(Latency.getLag(player) >= 80) {
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
        if (UtilPlayer.isInWater(player)) {
            return;
        }
        if (UtilCheat.isInWeb(player)) {
            return;
        }
        if (UtilCheat.blocksNear(player.getLocation()))
        {
            if (this.flyTicksA.containsKey(player.getUniqueId())) {
                this.flyTicksA.remove(player.getUniqueId());
            }
            return;
        }
        if ((event.getTo().getX() == event.getFrom().getX()) &&
                (event.getTo().getZ() == event.getFrom().getZ())) {
            return;
        }
        if (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0.1)
        {
            if (this.flyTicksA.containsKey(player.getUniqueId())) {
                this.flyTicksA.remove(player.getUniqueId());
            }
            return;
        }
        long Time = System.currentTimeMillis();
        if (this.flyTicksA.containsKey(player.getUniqueId())) {
            Time = ((Long)this.flyTicksA.get(player.getUniqueId())).longValue();
        }
        long MS = System.currentTimeMillis() - Time;
        if (MS > 500L)
        {
            dumplog(player, "Logged Fly. MS: " + MS);
            getDaedalus().logCheat(this, player, "Hovering for " + UtilMath.trim(1, Double.valueOf((MS/1000))) + " second(s)", Chance.HIGH, new String[0]);
            this.flyTicksA.remove(player.getUniqueId());
            return;
        }
        this.flyTicksA.put(player.getUniqueId(), Time);
    }
}
