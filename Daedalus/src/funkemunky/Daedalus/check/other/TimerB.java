package funkemunky.Daedalus.check.other;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class TimerB extends Check {

	public TimerB(Daedalus Daedalus) {
		super("TimerB", "Timer (Type B)", Daedalus);
		
		this.setViolationsToNotify(1);
		this.setMaxViolations(9);
		
		this.setEnabled(true);
		this.setBannable(false);
	}
	public static Map<UUID, Map.Entry<Integer, Long>> timerTicks = new HashMap();
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		if(!getDaedalus().isEnabled()) {
			return;
		}
		Player player = e.getPlayer();
		if(player.hasPermission("daedalus.bypass")) {
			return;
		}
		if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ() &&
				e.getFrom().getY() == e.getTo().getY()) {
			return;
		}
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.timerTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.timerTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.timerTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        
        Count++;
        
        if ((this.timerTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 1000L)))
        {
            if (Count > 35) {
                this.getDaedalus().logCheat(this, player, null, Chance.LIKELY, new String[] {"Experimental"});
            }
            Count = 0;
            Time = UtilTime.nowlong();
        }
		this.timerTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
	}
	
	

}
