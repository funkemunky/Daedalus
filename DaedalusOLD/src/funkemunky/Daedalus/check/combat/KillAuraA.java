package funkemunky.Daedalus.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class KillAuraA extends Check
{
    public static Map<UUID, Long> LastMS;
    public static Map<UUID, List<Long>> Clicks;
    public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public KillAuraA(final Daedalus Daedalus) {
        super("KillAuraA", "Kill Aura (Click Pattern)", Daedalus);
        this.LastMS = new HashMap<UUID, Long>();
        this.Clicks = new HashMap<UUID, List<Long>>();
        this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        
        this.setEnabled(true);
        this.setBannable(true);
        this.setViolationResetTime(300000);
        this.setMaxViolations(7);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	if(LastMS.containsKey(uuid)) {
    		LastMS.remove(uuid);
    	}
    	if(Clicks.containsKey(uuid)) {
    		Clicks.remove(uuid);
    	}
    	if(ClickTicks.containsKey(uuid)) {
    		ClickTicks.remove(uuid);
    	}
    }

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        if (!((e.getAttacked()) instanceof Player)) {
            return;
        }
        final Player damager = e.getAttacker();
	     if(damager.hasPermission("daedalus.bypass")) {
	         return;
	     }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.ClickTicks.containsKey(damager.getUniqueId())) {
            Count = this.ClickTicks.get(damager.getUniqueId()).getKey();
            Time = this.ClickTicks.get(damager.getUniqueId()).getValue();
        }
        if (this.LastMS.containsKey(damager.getUniqueId())) {
            final long MS = UtilTime.nowlong() - this.LastMS.get(damager.getUniqueId());
            if (MS > 500L || MS < 5L) {
                this.LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
                return;
            }
            if (this.Clicks.containsKey(damager.getUniqueId())) {
                final List<Long> Clicks = this.Clicks.get(damager.getUniqueId());
                if (Clicks.size() == 10) {
                    this.Clicks.remove(damager.getUniqueId());
                    Collections.sort(Clicks);
                    final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
                    if (Range < 30L) {
                        ++Count;
                        Time = System.currentTimeMillis();
                        this.dumplog(damager, "New Range: " + Range);
                        this.dumplog(damager, "New Count: " + Count);
                    }
                }
                else {
                    Clicks.add(MS);
                    this.Clicks.put(damager.getUniqueId(), Clicks);
                }
            }
            else {
                final List<Long> Clicks = new ArrayList<Long>();
                Clicks.add(MS);
                this.Clicks.put(damager.getUniqueId(), Clicks);
            }
        }
        if (this.ClickTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if ((Count > 0 && this.getDaedalus().getLag().getPing(damager) < 100) || (Count > 2 && this.getDaedalus().getLag().getPing(damager) < 200)) {
            this.dumplog(damager, "Logged. Count: " + Count);
            Count = 0;
            this.getDaedalus().logCheat(this, damager, null, Chance.HIGH, new String[0]);
            ClickTicks.remove(damager.getUniqueId());
        } else if(this.getDaedalus().getLag().getPing(damager) > 250) {
        	this.dumplog(damager, "Would set off Killaura (Click Pattern) but latency is too high!");
        }
        this.LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
        this.ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }
}