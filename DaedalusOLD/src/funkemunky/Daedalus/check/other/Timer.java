package funkemunky.Daedalus.check.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketPlayerEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class Timer extends Check
{
    public static Map<UUID, Long> lastTimer;
    public static Map<UUID, List<Long>> MS;
    public static Map<UUID, Integer> timerTicks;

    public Timer(Daedalus Daedalus) {
        super("TimerA", "Timer (Type A)", Daedalus);
        this.lastTimer = new HashMap<UUID, Long>();
        this.MS = new HashMap<UUID, List<Long>>();
        this.timerTicks = new HashMap<UUID, Integer>();
        
        this.setEnabled(true);
        this.setBannable(false);
        setMaxViolations(5);
    }
    
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(lastTimer.containsKey(e.getPlayer().getUniqueId())) {
    		lastTimer.remove(e.getPlayer().getUniqueId());
    	}
    	if(MS.containsKey(e.getPlayer().getUniqueId())) {
    		MS.remove(e.getPlayer().getUniqueId());
    	}
    	if(timerTicks.containsKey(e.getPlayer().getUniqueId())) {
    		timerTicks.remove(e.getPlayer().getUniqueId());
    	}
    }

    @EventHandler
    public void PacketPlayer(PacketPlayerEvent event) {
        Player player = event.getPlayer();
        if (!this.getDaedalus().isEnabled()) {
            return;
        }
        
	    if(player.hasPermission("daedalus.bypass")) {
	        return;
	    }
	    
	    if(getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
	    	return;
	    }
        
        if(getDaedalus().getLag().getPing(player) > 340) {
        	return;
        }
        
        int Count = 0;
        if (this.timerTicks.containsKey(player.getUniqueId())) {
          Count = ((Integer)this.timerTicks.get(player.getUniqueId())).intValue();
        }
        if (this.lastTimer.containsKey(player.getUniqueId()))
        {
          long MS = System.currentTimeMillis() - ((Long)this.lastTimer.get(player.getUniqueId())).longValue();
          
          List<Long> List = new ArrayList();
          if (this.MS.containsKey(player.getUniqueId())) {
            List = (List)this.MS.get(player.getUniqueId());
          }
          List.add(Long.valueOf(MS));
          if (List.size() == 20)
          {
            boolean doeet = true;
            for (Long ListMS : List) {
              if (ListMS.longValue() < 1L) {
                doeet = false;
              }
            }
            Long average = Long.valueOf(UtilMath.averageLong(List));
            dumplog(player, "Average MS for 20 ticks: " + average);
            if ((average.longValue() < 48L) && (doeet))
            {
              Count++;
              dumplog(player, "New Count: " + Count);
            }
            else
            {
              Count = 0;
            }
            this.MS.remove(player.getUniqueId());
          }
          else
          {
            this.MS.put(player.getUniqueId(), List);
          }
        }
        if (Count > 4)
        {
          dumplog(player, "Logged for timer. Count: " + Count);
          Count = 0;
          
          getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[0]);
        }
        this.lastTimer.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        this.timerTicks.put(player.getUniqueId(), Integer.valueOf(Count));
    }
}